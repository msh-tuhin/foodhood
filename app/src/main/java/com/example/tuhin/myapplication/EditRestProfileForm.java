package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import myapp.utils.CityMapping;
import myapp.utils.InputValidator;
import myapp.utils.NullStrings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditRestProfileForm extends AppCompatActivity {

    private final int REQUEST_LOCATION_PERMISSION = 1;
    private final int REQUEST_CHECK_SETTINGS = 1;

    private boolean locationPermissionGranted = false;
    private LocationRequest locationRequest;
    private boolean isLocationSettingsEnabled = false;
    private boolean isGoogleApiAvailable = false;

    private Double mLatitudeValue = 0.0;
    private Double mLongitudeValue = 0.0;
    private String mAddress = "";
    private String mPhone = "";
    private String mWebsite = "";
    private String mTown = "Select a city/thana";
    private String oldAddress = "";
    private String oldPhone = "";
    private String oldWebsite = "";
    private Double oldLatitudeValue = null;
    private Double oldLongitudeValue = null;
    private String oldTown = "Select a city/thana";

    SaveButtonController saveButtonController;
    private String mRestaurantLink;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Toolbar toolbar;
    ScrollView formLayout;
    LinearLayout progressLayout;
    ImageView enableAddressEdit;
    ImageView enablePhoneEdit;
    ImageView enableWebsiteEdit;
    TextInputLayout addressLayout;
    TextInputEditText addressEditText;
    TextInputLayout phoneLayout;
    TextInputEditText phoneEditText;
    TextInputLayout websiteLayout;
    TextInputEditText websiteEditText;
    CheckBox locationChechbox;
    TextView latitudeTV;
    TextView longitudeTV;
    Spinner townSpinner;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rest_profile_form);

        mRestaurantLink = mAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        formLayout = findViewById(R.id.form_layout);
        progressLayout = findViewById(R.id.progress_layout);
        enableAddressEdit = findViewById(R.id.enable_address_edit);
        enablePhoneEdit = findViewById(R.id.enable_phone_edit);
        enableWebsiteEdit = findViewById(R.id.enable_website_edit);
        addressLayout = findViewById(R.id.address_layout);
        addressEditText = findViewById(R.id.address);
        phoneLayout = findViewById(R.id.phone_layout);
        phoneEditText = findViewById(R.id.phone);
        websiteLayout = findViewById(R.id.website_layout);
        websiteEditText = findViewById(R.id.website);
        locationChechbox = findViewById(R.id.location_checkbox);
        latitudeTV = findViewById(R.id.lat_tv);
        longitudeTV = findViewById(R.id.lng_tv);
        townSpinner = findViewById(R.id.town_spinner);
        saveButton = findViewById(R.id.save_button);

        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        saveButtonController = new SaveButtonController(saveButton);

        db.collection("rest_vital")
                .document(mRestaurantLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot restVitalSnapshot) {
                        if(restVitalSnapshot.exists()){
                            bindAddress(restVitalSnapshot);
                            bindPhone(restVitalSnapshot);
                            bindWebsite(restVitalSnapshot);
                            bindLocation(restVitalSnapshot);
                            bindTownSpinner(restVitalSnapshot);
                        }
                    }
                });

        enableAddressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressEditText.setEnabled(true);
                addressEditText.requestFocus();
            }
        });

        enablePhoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEditText.setEnabled(true);
                phoneEditText.requestFocus();
            }
        });

        enableWebsiteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                websiteEditText.setEnabled(true);
                websiteEditText.requestFocus();
            }
        });

        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAddress = s.toString().trim();
                Log.i("editing_address", mAddress);
                saveButtonController.shouldAddressBeSaved = shouldAddressBeSaved();
                if(saveButtonController.shouldAddressBeSaved){
                    Log.i("bool", "true");
                }
                saveButtonController.enableOrDisableSaveButton();
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPhoneEditText(s.toString());
                mPhone = getNewPhoneNumber(s.toString());
                saveButtonController.shouldPhoneBeSaved = shouldPhoneBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }
        });

        websiteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mWebsite = s.toString().trim();
                Log.i("editing_website", mWebsite);
                saveButtonController.shouldWebsiteBeSaved = shouldWebsiteBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }
        });

        locationChechbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    calculateLocationAfterCheckingPermission();
                }else{
                    displayLocation(oldLatitudeValue, oldLongitudeValue);
                    saveButtonController.shouldLocationBeSaved = false;
                    saveButtonController.enableOrDisableSaveButton();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setEnabled(false);
                Map<String, Object> restaurantVital = new HashMap<>();
                if(saveButtonController.shouldAddressBeSaved){
                    restaurantVital.put("a", mAddress);
                    oldAddress = mAddress;
                }
                if(saveButtonController.shouldPhoneBeSaved){
                    restaurantVital.put("p", mPhone);
                    oldPhone = mPhone;
                }
                if(saveButtonController.shouldWebsiteBeSaved){
                    restaurantVital.put("w", mWebsite);
                    oldWebsite = mWebsite;
                }

                if(saveButtonController.shouldLocationBeSaved){
                    Map<String, Double> locationMap = new HashMap<>();
                    locationMap.put("lat", mLatitudeValue);
                    locationMap.put("lng", mLongitudeValue);
                    restaurantVital.put("loc", locationMap);
                    oldLatitudeValue = mLatitudeValue;
                    oldLongitudeValue = mLongitudeValue;
                }

                if(saveButtonController.shouldTownBeSaved){
                    restaurantVital.put("t", mTown);
                    oldTown = mTown;
                    //CityMapping cityMapping = new CityMapping();
                    //restaurantVital.put("div", cityMapping.getDivision(mTown));
                    //restaurantVital.put("dis", cityMapping.getDistrict(mTown));
                }

                formLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);

                db.collection("rest_vital")
                        .document(mRestaurantLink)
                        .update(restaurantVital)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("profile_update", "successful");
                                // saveButtonController.refresh();
                                EditRestProfileForm.this.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("profile_update", "failed");
                                saveButton.setEnabled(true);
                                formLayout.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditRestProfileForm.this, "Update failed!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                addressEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                websiteEditText.setEnabled(false);
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
                        if(locationChechbox.isChecked()){
                            calculateLocationAfterCheckingSettings();
                        }else{
                            checkChangeLocationSettings();
                        }
                        locationPermissionGranted = true;
                    } else {
                        // permission denied
                        Log.i("location", "permission denied");
                        locationChechbox.setChecked(false);
                        locationPermissionGranted = false;
                        // TODO
                    }
                }
                break;
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
                        if(locationChechbox.isChecked()){
                            calculateLocation();
                        }
                        // TODO
                        break;
                    case Activity.RESULT_CANCELED:
                        isLocationSettingsEnabled = false;
                        locationChechbox.setChecked(false);
                        Log.i("location_settings", "ignored by user");
                        // TODO
                        break;
                }
                break;
        }
    }

    private void bindAddress(DocumentSnapshot restVitalSnapshot){
        String address = restVitalSnapshot.getString("a");
        if(address != null){
            Log.i("address", address);
            oldAddress = address;
            addressEditText.setText(address);
        }
    }

    private void bindPhone(DocumentSnapshot restVitalSnapshot){
        String phone = restVitalSnapshot.getString("p");
        if(phone != null){
            oldPhone = phone;
            phoneEditText.setText(phone);
        }
    }

    private void bindWebsite(DocumentSnapshot restVitalSnapshot){
        String web = restVitalSnapshot.getString("w");
        if(web != null){
            oldWebsite  = web;
            websiteEditText.setText(web);
        }
    }

    private void bindLocation(DocumentSnapshot restVitalSnapshot){
        Map<String, Double> location = (Map<String, Double>) restVitalSnapshot.get("loc");
        if(location==null) return;
        Double latitude = location.get("lat");
        if(latitude!=null){
            latitudeTV.setText("Latitude: " + Double.toString(latitude));
            oldLatitudeValue = latitude;
        }

        Double longitude = location.get("lng");
        if(longitude!=null){
            longitudeTV.setText("Longitude: " + Double.toString(longitude));
            oldLongitudeValue = longitude;
        }
    }

    private void bindTownSpinner(DocumentSnapshot restVitalSnapshot){
        String[] towns = getResources().getStringArray(R.array.towns_bd);
        ArrayList<String> townsArrayList = new ArrayList<>(Arrays.asList(towns));
        int position = 0;

        String townString = restVitalSnapshot.getString("t");
        if(!(townString == null || townString.equals("") || townString.equals(NullStrings.NULL_TOWN_STRING))){
            oldTown = townString;
            townsArrayList.remove(0);
            position = townsArrayList.indexOf(townString);
        }
        position = position<0 ? 0:position;

        ArrayAdapter<String> townAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, townsArrayList);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        townSpinner.setAdapter(townAdapter);
        townSpinner.setSelection(position);

        townSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("town", parent.getItemAtPosition(position).toString());
                mTown = parent.getItemAtPosition(position).toString();
                saveButtonController.shouldTownBeSaved = shouldTownBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean shouldAddressBeSaved(){
        return !oldAddress.equals(mAddress);
    }

    private boolean shouldPhoneBeSaved(){
        if(mPhone == null) return false;
        return !oldPhone.equals(mPhone);
    }

    private boolean shouldWebsiteBeSaved(){
        return !oldWebsite.equals(mWebsite);
    }

    private boolean shouldLocationBeSaved(Double latitude, Double longitude){
        if(latitude==null || longitude==null) return false;
        if(oldLatitudeValue==null || oldLongitudeValue==null) return false;
        return (!latitude.equals(oldLatitudeValue) || !longitude.equals(oldLongitudeValue));
    }

    private boolean shouldTownBeSaved(){
        return !(mTown==null || mTown.equals("Select a city/thana") || mTown.equals(oldTown));
    }

    private void checkPhoneEditText(String phoneNumber){
        phoneLayout.setError(null);
        if(phoneNumber.trim().isEmpty()){
            // phoneLayout.setError("Please type a valid phone number");
            return;
        }

        boolean validated = InputValidator.validatePhone(phoneNumber);
        if(!validated){
            phoneLayout.setError("Please type a valid phone number");
        }
    }

    private String getNewPhoneNumber(String phoneNumber){
        if(phoneNumber.trim().isEmpty()){
            return "";
        }
        boolean validated = InputValidator.validatePhone(phoneNumber);
        if(validated){
            return sanitizePhone(phoneNumber);
        }else return null;
    }

    private String sanitizePhone(String phoneNumber){
        phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
        String regex = "\\A\\+88";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if(matcher.find()){
            phoneNumber = matcher.replaceFirst("");
        } else {
            Log.i("match", "not found");
        }
        Log.i("new_phone_number", phoneNumber);
        return phoneNumber;
    }

    private class SaveButtonController{
        boolean shouldAddressBeSaved = false;
        boolean shouldPhoneBeSaved = false;
        boolean shouldWebsiteBeSaved = false;
        boolean shouldLocationBeSaved = false;
        boolean shouldTownBeSaved = false;
        Button saveButton;

        SaveButtonController(Button saveButton){
            this.saveButton = saveButton;
        }

        void enableOrDisableSaveButton(){
            if(shouldAddressBeSaved || shouldPhoneBeSaved || shouldWebsiteBeSaved ||
                    shouldLocationBeSaved || shouldTownBeSaved){
                saveButton.setEnabled(true);
            }else{
                saveButton.setEnabled(false);
            }
        }

        void refresh(){
            saveButton.setEnabled(false);
            shouldAddressBeSaved = false;
            shouldPhoneBeSaved = false;
            shouldWebsiteBeSaved = false;
            shouldLocationBeSaved = false;
            shouldTownBeSaved = false;
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

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void createLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                        resolvable.startResolutionForResult(EditRestProfileForm.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.i("location_settings", "asking dialog failed");
                    }
                }
            }
        });
    }

    private Task<LocationSettingsResponse> getLocationSettingsResponseTask(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        return client.checkLocationSettings(builder.build());
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

    @SuppressLint("MissingPermission")
    private void calculateLocation(){
        if(isGoogleApiAvailable){
            FusedLocationProviderClient fusedLocationClient = LocationServices.
                    getFusedLocationProviderClient(EditRestProfileForm.this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(EditRestProfileForm.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                mLatitudeValue = location.getLatitude();
                                mLongitudeValue = location.getLongitude();
                                saveButtonController.shouldLocationBeSaved = shouldLocationBeSaved(mLatitudeValue,
                                        mLongitudeValue);
                                saveButtonController.enableOrDisableSaveButton();
                                displayLocation(mLatitudeValue, mLongitudeValue);
                            } else{
                                Log.i("location", "null");
                                Toast.makeText(EditRestProfileForm.this, "Couldn't read location! Please try again.",
                                        Toast.LENGTH_SHORT).show();
                                locationChechbox.setChecked(false);
                            }
                        }
                    }).addOnFailureListener(EditRestProfileForm.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditRestProfileForm.this, "Couldn't read location! Please try again.",
                            Toast.LENGTH_SHORT).show();
                    locationChechbox.setChecked(false);
                }
            });
        } else{
            // no location
            // TODO maybe get location by legacy methods
            locationChechbox.setChecked(false);
        }
    }

    private void calculateLocationAfterCheckingPermission(){
        if(checkLocationPermissions()){
            calculateLocationAfterCheckingSettings();
        } else{
            // no location
            Log.i("location", "permission denied");
            checkAskLocationPermissions();
        }
    }

    private void calculateLocationAfterCheckingSettings(){
        Task<LocationSettingsResponse> task = getLocationSettingsResponseTask();
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if(locationSettingsResponse.getLocationSettingsStates().isLocationUsable()){
                    Log.i("location", "usable");
                    isLocationSettingsEnabled = true;
                    calculateLocation();
                }else{
                    Log.i("location", "not usable");
                    locationChechbox.setChecked(false);
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
                        resolvable.startResolutionForResult(EditRestProfileForm.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.i("location_settings", "asking dialog failed");
                    }
                }
            }
        });
    }

    private void displayLocation(Double latitude, Double longitude){
        String latitudeText = "Latitude: ";
        latitudeText += latitude==null ? "not found": Double.toString(latitude);
        latitudeTV.setText(latitudeText);
        String longitudeText = "Longitude: ";
        longitudeText += longitude==null ? "not found": Double.toString(longitude);
        longitudeTV.setText(longitudeText);

    }
}
