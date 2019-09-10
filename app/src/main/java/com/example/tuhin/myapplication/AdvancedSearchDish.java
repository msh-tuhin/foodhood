package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AlgoliaCredentials;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AdvancedSearchDish extends AppCompatActivity{

    private final int REQUEST_LOCATION_PERMISSION = 1;
    private final int REQUEST_CHECK_SETTINGS = 1;
    private boolean locationPermissionGranted = false;
    private boolean isGoogleApiAvailable = false;
    private boolean isLocationSettingsEnabled = false;
    private LocationRequest locationRequest;
    private String selectedDistrict;

    boolean previousCheckboxChecked = false;
    String previousSelectedDistrict;
    Double previousLatitude=0.0, previousLongitude=0.0;
    NumericRefinement minPriceFilter, maxPriceFilter, ratingFilter;
    Double previousMinPrice = 0.0, previousMaxPrice = 0.0, previousRating = 0.0;

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    Searcher searcher;
    InstantSearch instantSearch;
    SearchBox searchBox;
    Hits hits;

    CheckBox checkBox;
    Spinner districtSpinner;
    Button searchButton;
    EditText minPriceEditText, maxPriceEditText, ratingEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_dish);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Food Frenzy");
        setSupportActionBar(toolbar);

        districtSpinner = findViewById(R.id.district_spinner);
        checkBox = findViewById(R.id.checkbox);
        searchButton = findViewById(R.id.search);
        minPriceEditText = findViewById(R.id.min_price_edittext);
        maxPriceEditText = findViewById(R.id.max_price_edittext);
        ratingEditText = findViewById(R.id.rating_edittext);

        hits = findViewById(R.id.search_hits);
        final String ALGOLIA_INDEX_NAME = "Dishes";
        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        instantSearch = new InstantSearch(this, searcher);

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

        districtSpinner.setEnabled(false);
        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this,
                R.array.districts_bd, android.R.layout.simple_spinner_item);
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

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));
                String name = "";
                try {
                    name = hits.get(position).getString("name");
                    ((TextView) view.findViewById(R.id.restaurant_name)).setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

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
    protected void onResume() {
        super.onResume();
        checkGoogleApiAvailability();
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
                Log.i("location_settings", "enabled from before");
                isLocationSettingsEnabled = true;
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

        boolean shouldSearch = false;
        double minPrice = 0.0, maxPrice = 0.0, rating = 0.0;
        searcher.clearFacetRefinements();
        searcher.removeNumericRefinement("price");
        searcher.removeNumericRefinement("rating");
        searcher.getQuery().setAroundLatLng(null);

        String minPriceString = minPriceEditText.getText().toString();
        if (!minPriceString.isEmpty()) {
            minPrice = Double.valueOf(minPriceString);
            if (minPrice != 0.0) {
                minPriceFilter = new NumericRefinement("price", 4, minPrice);
                searcher.addNumericRefinement(minPriceFilter);
            } else {
                minPrice = 0.0;
            }

        } else {
            minPrice = 0.0;
        }

        if (!previousMinPrice.equals(minPrice)) {
            previousMinPrice = minPrice;
            shouldSearch = true;
        }

        String maxPriceString = maxPriceEditText.getText().toString();
        if (!maxPriceString.isEmpty()) {
            maxPrice = Double.valueOf(maxPriceString);
            if (maxPrice != 0.0) {
                maxPriceFilter = new NumericRefinement("price", 1, maxPrice);
                searcher.addNumericRefinement(maxPriceFilter);
            } else {
                maxPrice = 0.0;
            }
        } else {
            maxPrice = 0.0;
        }
        if (!previousMaxPrice.equals(maxPrice)) {
            previousMaxPrice = maxPrice;
            shouldSearch = true;
        }

        String ratingString = ratingEditText.getText().toString();
        if (!ratingString.isEmpty()) {
            rating = Double.valueOf(ratingString);
            if (rating != 0.0) {
                ratingFilter = new NumericRefinement("rating", 4, rating);
                searcher.addNumericRefinement(ratingFilter);
            } else {
                rating = 0.0;
            }
        } else {
            rating = 0.0;
        }
        if (!previousRating.equals(rating)) {
            previousRating = rating;
            shouldSearch = true;
        }

        final boolean allZero = minPrice == 0.0 && maxPrice == 0.0 && rating == 0.0;
        shouldSearch = shouldSearch && !allZero;
        final boolean finalShouldSearch = shouldSearch;

        if(allZero){
            hits.clear();
            // TODO evaluate if this is needed or not
            // searcher.reset();
            return;
        }

        if(checkBox.isChecked()){
            Log.i("district_selected", selectedDistrict);
            searchWithoutLocation(shouldSearch);
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
                                                searchWithLocation(location, finalShouldSearch);
                                            } else{
                                                Log.i("location", "null");
                                                // TODO no location
                                                handleNoLocation();
                                            }
                                        }
                                    }).addOnFailureListener(AdvancedSearchDish.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    // TODO no location
                                    handleNoLocation();
                                }
                            });
                        } else{
                            // TODO no location
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
                // TODO no location
                Log.i("location", "permission denied");
                handleNoLocation();
            }
        }
    }

    private void searchWithoutLocation(boolean shouldSearch){
        if(previousCheckboxChecked){ // because of this check previousSelectedDistrict is never null
            // so no null check here for previousSelectedDistrict
            if(!previousSelectedDistrict.equals(selectedDistrict)){
                previousSelectedDistrict = selectedDistrict;
                Log.i("search", "yes");
                instantSearch.search();
            } else if(shouldSearch){
                Log.i("search", "yes");
                instantSearch.search();
            }
        } else{
            previousCheckboxChecked = true;
            previousSelectedDistrict = selectedDistrict;
            Log.i("search", "yes");
            instantSearch.search();
        }
    }

    private void searchWithLocation(Location location, boolean finalShouldSearch){
        Log.i("Provider", location.getProvider());
        Log.i("Latitude", String.valueOf(location.getLatitude()));
        Log.i("Longitude", String.valueOf(location.getLongitude()));
//        AbstractQuery.LatLng userLocation = new AbstractQuery.LatLng(location.getLatitude(), location.getLongitude());
//        searcher.getQuery().setAroundLatLng(userLocation).setAroundRadius(1000000);
        AbstractQuery.LatLng userLocation = new AbstractQuery.LatLng(40.71, -74.01);
        searcher.getQuery().setAroundLatLng(userLocation).setAroundRadius(1000000);
        if(!(previousLatitude.equals(location.getLatitude())
                && previousLongitude.equals(location.getLongitude()))){
            Log.i("location", "new");
            previousLatitude = location.getLatitude();
            previousLongitude = location.getLongitude();
            Log.i("search", "yes");
            previousCheckboxChecked = false;
            instantSearch.search();
        } else if(finalShouldSearch){
            Log.i("search", "yes");
            previousCheckboxChecked = false;
            instantSearch.search();
        } else if(previousCheckboxChecked){
            Log.i("search", "yes");
            previousCheckboxChecked = false;
            instantSearch.search();
        }
    }

    private void handleNoLocation(){
        // TODO maybe show a dialog
//        Toast.makeText(this, "Can't use precise location\n" +
//                "Please select district instead", Toast.LENGTH_LONG).show();
//        checkBox.setChecked(true);
//        districtSpinner.setEnabled(true);

        final Snackbar snackbar = Snackbar.make(coordinatorLayout,
                "Can't use precise location\n" +
                        "Select district instead?", Snackbar.LENGTH_LONG);
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
            e.printStackTrace();
        }
    }
}
