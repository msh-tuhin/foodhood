package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AlgoliaAttributeNames;
import myapp.utils.AlgoliaIndexNames;
import myapp.utils.AnimationUtils;
import myapp.utils.SearchHitBinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.algolia.search.saas.AbstractQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import org.json.JSONException;

import java.util.ArrayList;

public class AdvancedSearchDish extends AppCompatActivity{

    private final int REQUEST_LOCATION_PERMISSION = 1;
    private final int REQUEST_CHECK_SETTINGS = 1;
    private boolean locationPermissionGranted = false;
    private boolean isGoogleApiAvailable = false;
    private boolean isLocationSettingsEnabled = false;
    private boolean isFilterOptionsCollapsed = false;
    private LocationRequest locationRequest;
    private String selectedDistrict, selectedCategory;

    boolean isMaxPriceEmpty;
    double minPrice = 0.0, maxPrice = 0.0, rating = 0.0;
    boolean previousCheckboxChecked = false, previousCategoryCheckboxChecked = false;
    String previousSelectedDistrict, previousSelectedCategory="none";
    Double previousLatitude=0.0, previousLongitude=0.0;
    NumericRefinement minPriceFilter, maxPriceFilter, ratingFilter;
    Double previousMinPrice = 0.0, previousMaxPrice = 0.0, previousRating = 0.0;

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    LinearLayout filterLayout, searchLayout;
    Searcher searcher;
    InstantSearch instantSearch;
    SearchBox searchBox;
    Hits hits;

    CheckBox checkBox, foodCategoryCheckbox;
    Spinner districtSpinner, foodCategorySpinner;
    Button searchButton;
    EditText minPriceEditText, maxPriceEditText, ratingEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_dish);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        filterLayout = findViewById(R.id.filter_layout);
        searchLayout = findViewById(R.id.search_layout);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        foodCategoryCheckbox = findViewById(R.id.food_category_checkbox);
        foodCategorySpinner = findViewById(R.id.food_category_spinner);
        districtSpinner = findViewById(R.id.district_spinner);
        checkBox = findViewById(R.id.checkbox);
        searchButton = findViewById(R.id.search);
        minPriceEditText = findViewById(R.id.min_price_edittext);
        maxPriceEditText = findViewById(R.id.max_price_edittext);
        ratingEditText = findViewById(R.id.rating_edittext);

        hits = findViewById(R.id.search_hits);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

        //searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY,
          //      AlgoliaIndexNames.INDEX_RATING_DESC);
        //searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 1));
        //instantSearch = new InstantSearch(this, searcher);
        initiateSearch();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    districtSpinner.setEnabled(true);
                } else{
                    districtSpinner.setEnabled(false);
                }
            }
        });

        foodCategoryCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    foodCategorySpinner.setEnabled(true);
                } else{
                    foodCategorySpinner.setEnabled(false);
                }
            }
        });

        districtSpinner.setEnabled(false);
        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this,
                R.array.towns_bd, android.R.layout.simple_spinner_item);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("district", parent.getItemAtPosition(position).toString());
                selectedDistrict = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        foodCategorySpinner.setEnabled(false);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodCategorySpinner.setAdapter(categoryAdapter);
        foodCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("category", parent.getItemAtPosition(position).toString());
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                SearchHitBinder.refreshView(view);
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));

                SearchHitBinder searchHitBinder = new SearchHitBinder(hits, position, view);
                searchHitBinder.bind(true, true, true, true,
                        true, false, false, false,
                        false, false, true);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }

        });

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                try{
                    String id = hits.get(position).getString(AlgoliaAttributeNames.ID);
                    Intent intent;
                    intent = new Intent(AdvancedSearchDish.this, DishDetail.class);
                    intent.putExtra("dishLink", id);
                    startActivity(intent);
                }catch (JSONException e){
                    Log.i("object_id", "no value found");
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        createLocationRequest();
        if(checkLocationPermissions()){
            checkChangeLocationSettings();
        }

        checkAskLocationPermissions();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if(searcher!=null){
            searcher.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGoogleApiAvailability();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.advanced_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter_options:
                if(isFilterOptionsCollapsed){
                    expandFilterOptions();
                } else{
                    collapseFilterOptions();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode){
                    case Activity.RESULT_OK:
                        isLocationSettingsEnabled = true;
                        Log.i("location_settings", "enabled by user");
                        // TODO
                        break;
                    case Activity.RESULT_CANCELED:
                        isLocationSettingsEnabled = false;
                        Log.i("location_settings", "ignored by user");
                        // TODO
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    // although there are two different location permissions(coarse and fine) asked,
                    // user gives location permission and both are granted
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission granted
                        Log.i("location", "permission granted");
                        checkChangeLocationSettings();
                        // TODO
                        locationPermissionGranted = true;
                    } else {
                        // permission denied
                        Log.i("location", "permission denied");
                        locationPermissionGranted = false;
                        // TODO
                    }
                }
                break;
        }
    }

    private void checkGoogleApiAvailability(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayAvailability = googleApiAvailability.isGooglePlayServicesAvailable(this);
        switch (googlePlayAvailability) {
            case ConnectionResult.SUCCESS:
                Log.i("Google_Play", "Up to date");
                isGoogleApiAvailable = true;
                break;
            default:
                // TODO google play not available/updated
                // TODO show dialog with action
                // use getErrorDialog()
                Log.i("Google_Play", "Some Problem");
                isGoogleApiAvailable = false;
        }
    }

    private void checkAskLocationPermissions(){
        String[] permissionsArray = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        ArrayList<String> permissionsArrayList = new ArrayList<>();

        for (String permission : permissionsArray) {
            if (!hasPermission(permission)) {
                permissionsArrayList.add(permission);
            }
        }

        if (permissionsArrayList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsArrayList.toArray(new String[0]), REQUEST_LOCATION_PERMISSION);
        } else{
            Log.i("location", "permission pre exists");
            locationPermissionGranted = true;
        }
    }

    private boolean checkLocationPermissions(){
        String[] permissionsArray = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        for (String permission : permissionsArray) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private Location getLocationLegacyMethod(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }

    private void createLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Task<LocationSettingsResponse> getLocationSettingsResponseTask(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        return client.checkLocationSettings(builder.build());
    }

    private void checkChangeLocationSettings(){
        Task<LocationSettingsResponse> task = getLocationSettingsResponseTask();
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if(locationSettingsResponse.getLocationSettingsStates().isLocationUsable()){
                    Log.i("location_settings", "enabled from before");
                    isLocationSettingsEnabled = true;
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(AdvancedSearchDish.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.i("location_settings", "asking dialog failed");
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void search(){
        // hiding the soft keyboard
        hideKeyboard();

        clearRefinements();

        try {
            setNumericValues();
        } catch (Exception e){
            showToast(e.getMessage(), true);
            return;
        }

        try{
            validateNumericValues();
        } catch (MyInvalidInputException e){
            showToast(e.getMessage(), true);
            return;
        }

        addRefinements();

        boolean allZero = minPrice == 0.0 && maxPrice == 0.0 && rating == 0.0;
//        allZero = allZero && !foodCategoryCheckbox.isChecked();

        // don't delete this code
//        if(allZero){
//            hits.clear();
//            // TODO evaluate if this is needed or not
//            // searcher.reset();
//            return;
//        }

        if(checkBox.isChecked()){
            Log.i("district_selected", selectedDistrict);
            searchWithoutLocation();
        } else{
            if(checkLocationPermissions()){
                Task<LocationSettingsResponse> task = getLocationSettingsResponseTask();
                task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("location_settings", "enabled from before");
                        isLocationSettingsEnabled = true;
                        if(isGoogleApiAvailable){
                            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(AdvancedSearchDish.this);
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(AdvancedSearchDish.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            if(location != null){
                                                searchWithLocation(location);
                                            } else{
                                                Log.i("location", "null");
                                                // no location
                                                handleNoLocation();
                                            }
                                        }
                                    }).addOnFailureListener(AdvancedSearchDish.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    // no location
                                    handleNoLocation();
                                }
                            });
                        } else{
                            // no location
                            // TODO maybe get location by legacy methods
                            handleNoLocation();
                        }
                    }
                });
                task.addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("location", "settings disabled");
                        handleNoLocation();
                    }
                });
            } else{
                // no location
                Log.i("location", "permission denied");
                handleNoLocation();
            }
        }
    }

    private void clearRefinements(){
        if(searcher==null) return;
        searcher.clearFacetRefinements();
        searcher.removeNumericRefinement(AlgoliaAttributeNames.PRICE);
        searcher.removeNumericRefinement(AlgoliaAttributeNames.RATING);
        searcher.getQuery().setAroundLatLng(null);
    }

    private void setNumericValues(){
        String minPriceString = minPriceEditText.getText().toString();
        if (!minPriceString.isEmpty()) {
            minPrice = Double.valueOf(minPriceString);
        } else {
            minPrice = 0.0;
        }

        String maxPriceString = maxPriceEditText.getText().toString();
        if (!maxPriceString.isEmpty()) {
            maxPrice = Double.valueOf(maxPriceString);
            isMaxPriceEmpty = false;
        } else {
            maxPrice = 0.0;
            isMaxPriceEmpty = true;
        }

        String ratingString = ratingEditText.getText().toString();
        if (!ratingString.isEmpty()) {
            rating = Double.valueOf(ratingString);
        } else {
            rating = 0.0;
        }
    }

    private void validateNumericValues () throws MyInvalidInputException{
        if(minPrice < 0.0){
            throw new MyInvalidInputException("Invalid minimum price");
        }

        if(!isMaxPriceEmpty && maxPrice <= 0.0){
            throw new MyInvalidInputException("Invalid maximum price");
        }

        if(!isMaxPriceEmpty && maxPrice < minPrice){
            throw new MyInvalidInputException("Maximum price should be greater than " +
                    "minimum price");
        }

        if(rating < 0.0 || rating > 5.0){
            throw new MyInvalidInputException("Rating should be between 0 and 5");
        }
    }

    private void addRefinements(){
        if(searcher==null) return;
        if (minPrice > 0.0) {
            minPriceFilter = new NumericRefinement(AlgoliaAttributeNames.PRICE, 4, minPrice);
            searcher.addNumericRefinement(minPriceFilter);
        }

        if (maxPrice > 0.0) {
            maxPriceFilter = new NumericRefinement(AlgoliaAttributeNames.PRICE, 1, maxPrice);
            searcher.addNumericRefinement(maxPriceFilter);
        }

        if (rating > 0.0) {
            ratingFilter = new NumericRefinement(AlgoliaAttributeNames.RATING, 4, rating);
            searcher.addNumericRefinement(ratingFilter);
        }

        if(checkBox.isChecked()){
            searcher.addFacetRefinement(AlgoliaAttributeNames.DISTRICT, selectedDistrict);
        }

        if(foodCategoryCheckbox.isChecked()){
            searcher.addFacetRefinement(AlgoliaAttributeNames.CATEGORY, selectedCategory);
        }
    }

    private boolean getShouldSearch(){
        boolean shouldSearch = false;
        if (!previousMinPrice.equals(minPrice)) {
            previousMinPrice = minPrice;
            shouldSearch = true;
        }

        if (!previousMaxPrice.equals(maxPrice)) {
            previousMaxPrice = maxPrice;
            shouldSearch = true;
        }

        if (!previousRating.equals(rating)) {
            previousRating = rating;
            shouldSearch = true;
        }

        if(foodCategoryCheckbox.isChecked()){
            if(previousCategoryCheckboxChecked){
                if(!previousSelectedCategory.equals(selectedCategory)){
                    previousSelectedCategory = selectedCategory;
                    shouldSearch = true;
                }
            } else{
                shouldSearch = true;
                previousSelectedCategory = selectedCategory;
                previousCategoryCheckboxChecked = true;
            }
        } else{
            if(previousCategoryCheckboxChecked){
                shouldSearch = true;
                previousCategoryCheckboxChecked = false;
            }
        }

        return shouldSearch;
    }

    private void searchWithoutLocation(){
        collapseFilterOptions();
        boolean shouldSearch = getShouldSearch();
        if(previousCheckboxChecked){ // because of this check previousSelectedDistrict is never null
            // so no null check here for previousSelectedDistrict
            if(!previousSelectedDistrict.equals(selectedDistrict)){
                previousSelectedDistrict = selectedDistrict;
                Log.i("search", "yes");
                if(instantSearch!=null){
                    instantSearch.search();
                }
            } else if(shouldSearch){
                Log.i("search", "yes");
                if(instantSearch!=null){
                    instantSearch.search();
                }
            }
        } else{
            previousCheckboxChecked = true;
            previousSelectedDistrict = selectedDistrict;
            Log.i("search", "yes");
            if(instantSearch!=null){
                instantSearch.search();
            }
        }
    }

    private void searchWithLocation(Location location){
        collapseFilterOptions();
        boolean shouldSearch = getShouldSearch();
        Log.i("Provider", location.getProvider());
        Log.i("Latitude", String.valueOf(location.getLatitude()));
        Log.i("Longitude", String.valueOf(location.getLongitude()));
        AbstractQuery.LatLng userLocation = new AbstractQuery.LatLng(location.getLatitude(), location.getLongitude());
//        AbstractQuery.LatLng userLocation = new AbstractQuery.LatLng(40.71, -74.01);
        if(searcher!=null){
            searcher.getQuery().setAroundLatLng(userLocation).setAroundRadius(2000); // 2km
        }
        if(!(previousLatitude.equals(location.getLatitude())
                && previousLongitude.equals(location.getLongitude()))){
            Log.i("location", "new");
            previousLatitude = location.getLatitude();
            previousLongitude = location.getLongitude();
            Log.i("search", "yes");
            if(instantSearch!=null){
                instantSearch.search();
            }
        } else if(shouldSearch){
            Log.i("search", "yes");
            if(instantSearch!=null){
                instantSearch.search();
            }
        } else if(previousCheckboxChecked){
            Log.i("search", "yes");
            if(instantSearch!=null){
                instantSearch.search();
            }
        }
        previousCheckboxChecked = false;
    }

    private void handleNoLocation(){
        // TODO maybe show a dialog
//        Toast.makeText(this, "Can't use precise location\n" +
//                "Please select district instead", Toast.LENGTH_LONG).show();
//        checkBox.setChecked(true);
//        districtSpinner.setEnabled(true);

        final Snackbar snackbar = Snackbar.make(coordinatorLayout,
                "Can't use precise location.\n" +
                        "Select city/upazila instead?", Snackbar.LENGTH_LONG);
        snackbar.setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(true);
                districtSpinner.setEnabled(true);
                snackbar.dismiss();
            }
        });
        snackbar.show();
        checkBox.setChecked(true);
        districtSpinner.setEnabled(true);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try{
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e){
//            e.printStackTrace();
        }
    }

    private void collapseFilterOptions(){
        AnimationUtils.collapse(filterLayout);
        searchLayout.setVisibility(View.VISIBLE);
        isFilterOptionsCollapsed = true;
    }

    private void expandFilterOptions(){
//        searchLayout.setVisibility(View.GONE);
        AnimationUtils.expand(filterLayout);
        isFilterOptionsCollapsed = false;
    }

    private void showToast(String message, boolean onTop){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, getSupportActionBar().getHeight() + 16);
        toast.show();

    }

    private class MyInvalidInputException extends Exception{
        MyInvalidInputException(String message){
            super(message);
        }
    }

    private void initiateSearch(){
        FirebaseFirestore.getInstance().collection("acr")
                .document("a").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            searcher = Searcher.create(documentSnapshot.getString("id"),
                                    documentSnapshot.getString("k"),
                                    AlgoliaIndexNames.INDEX_RATING_DESC);
                            searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 1));
                            instantSearch = new InstantSearch(AdvancedSearchDish.this, searcher);
                        }
                    }
                });
    }
}
