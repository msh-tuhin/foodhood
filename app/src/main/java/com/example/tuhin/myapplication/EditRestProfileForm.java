package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.InputValidator;

import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditRestProfileForm extends AppCompatActivity {

    private String mAddress = "";
    private String mPhone = "";
    private String mWebsite = "";
    private String oldAddress = "";
    private String oldPhone = "";
    private String oldWebsite = "";

    SaveButtonController saveButtonController;
    private String mRestaurantLink;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Toolbar toolbar;
    ImageView enableAddressEdit;
    ImageView enablePhoneEdit;
    ImageView enableWebsiteEdit;
    TextInputLayout addressLayout;
    TextInputEditText addressEditText;
    TextInputLayout phoneLayout;
    TextInputEditText phoneEditText;
    TextInputLayout websiteLayout;
    TextInputEditText websiteEditText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rest_profile_form);


        mRestaurantLink = mAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.toolbar);
        enableAddressEdit = findViewById(R.id.enable_address_edit);
        enablePhoneEdit = findViewById(R.id.enable_phone_edit);
        enableWebsiteEdit = findViewById(R.id.enable_website_edit);
        addressLayout = findViewById(R.id.address_layout);
        addressEditText = findViewById(R.id.address);
        phoneLayout = findViewById(R.id.phone_layout);
        phoneEditText = findViewById(R.id.phone);
        websiteLayout = findViewById(R.id.website_layout);
        websiteEditText = findViewById(R.id.website);
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                saveButtonController.refresh();

                db.collection("rest_vital")
                        .document(mRestaurantLink)
                        .update(restaurantVital)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("profile_update", "successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("profile_update", "failed");
                            }
                        });

                addressEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                websiteEditText.setEnabled(false);
            }
        });
    }

    private void bindAddress(DocumentSnapshot restVitalSnapshot){
        String address = restVitalSnapshot.getString("a");
        Log.i("address", address);
        if(address != null){
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
        Button saveButton;

        SaveButtonController(Button saveButton){
            this.saveButton = saveButton;
        }

        void enableOrDisableSaveButton(){
            if(shouldAddressBeSaved || shouldPhoneBeSaved || shouldWebsiteBeSaved){
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
        }
    }
}
