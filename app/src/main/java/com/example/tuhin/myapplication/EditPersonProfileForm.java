package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.InputValidator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPersonProfileForm extends AppCompatActivity {
    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String mBio = "";
    private String mPhone = "";
    private String mCurrentTown = "Select Current Town";
    private String mHomeTown = "Select Home Town";
    private String mYearString = "Year";
    private String mMonthString = "Month";
    private String mDateString = "Date";
    private String oldBio = "";
    private String oldPhone = "";
    private String oldHomeTown = "Select Home Town";
    private String oldCurrentTown = "Select Current Town";
    private String oldYearString = "Year";
    private String oldMonthString = "Month";
    private String oldDateString = "Date";

    SaveButtonController saveButtonController;
    private String mPersonLink;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Toolbar toolbar;
    ScrollView formLayout;
    LinearLayout progressLayout;
    ImageView enableBioEdit;
    TextInputEditText bioEditText;
    ImageView enablePhoneEdit;
    TextInputLayout phoneLayout;
    TextInputEditText phoneEditText;
    Spinner currentTownSpinner;
    Spinner homeTownSpinner;
    TextView birthdateTV;
    ImageView enableBirthdateEdit;
    LinearLayout yearLayout;
    LinearLayout monthLayout;
    LinearLayout dateLayout;
    Spinner yearSpinner;
    Spinner monthSpinner;
    Spinner dateSpinner;
    Button saveButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_edit_person_profile);

        // mPersonLink = getIntent().getStringExtra("personLink");
        mPersonLink = mAuth.getCurrentUser().getUid();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        formLayout = findViewById(R.id.form_layout);
        progressLayout = findViewById(R.id.progress_layout);
        enableBioEdit = findViewById(R.id.enable_bio_edit);
        bioEditText = findViewById(R.id.bio);
        phoneLayout = findViewById(R.id.phone_layout);
        phoneEditText = findViewById(R.id.phone);
        enablePhoneEdit = findViewById(R.id.enable_phone_edit);
        currentTownSpinner = findViewById(R.id.current_town_spinner);
        homeTownSpinner = findViewById(R.id.home_town_spinner);
        birthdateTV = findViewById(R.id.birthdate_textView);
        enableBirthdateEdit = findViewById(R.id.enable_birthdate_edit);
        yearLayout = findViewById(R.id.year_layout);
        yearSpinner = findViewById(R.id.year_spinner);
        monthLayout = findViewById(R.id.month_layout);
        monthSpinner = findViewById(R.id.month_spinner);
        dateLayout = findViewById(R.id.date_layout);
        dateSpinner = findViewById(R.id.date_spinner);
        saveButton = findViewById(R.id.save_button);

        saveButtonController = new SaveButtonController(saveButton);
        db.collection("person_vital").document(mPersonLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personInfo = task.getResult();
                    if(personInfo.exists()){
                        String personName = personInfo.getString("n");
                        String personBio = personInfo.getString("bio");
                        Timestamp personBirthdate = personInfo.getTimestamp("b");
                        String personHometown = personInfo.getString("ht");
                        String personCurrentLocation = personInfo.getString("ct");
                        String personPhone = personInfo.getString("p");

                        if(personBio != null){
                            oldBio = personBio;
                        }
                        if(personPhone != null){
                            oldPhone = personPhone;
                        }
                        if(personCurrentLocation != null){
                            oldCurrentTown = personCurrentLocation;
                        }
                        if(personHometown != null){
                            oldHomeTown = personHometown;
                        }
                        setOldBirthDate(personBirthdate);

                        bindBio(personBio);
                        bindPhone(personPhone);
                        bindCurrentTownSpinner(personCurrentLocation);
                        bindHomeTownSpinner(personHometown);
                        bindBirthdate(personBirthdate);
                    }
                }
            }
        });

        bioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBio = s.toString();
                Log.i("editing_bio", mBio);
                saveButtonController.shouldBioBeSaved = shouldBioBeSaved();
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

        enableBioEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bioEditText.setEnabled(true);
                bioEditText.requestFocus();
            }
        });

        enablePhoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEditText.setEnabled(true);
                phoneEditText.requestFocus();
            }
        });

        enableBirthdateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearLayout.setVisibility(View.VISIBLE);
                bindYearSpinner();
                monthSpinner.setEnabled(false);
                monthLayout.setVisibility(View.VISIBLE);
                bindMonthSpinner();
                dateSpinner.setEnabled(false);
                dateLayout.setVisibility(View.VISIBLE);
                bindDateSpinner();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setEnabled(false);
                Map<String, Object>  personVital = new HashMap<>();
                if(saveButtonController.shouldBioBeSaved){
                    personVital.put("bio", mBio);
                    oldBio = mBio;
                }
                if(saveButtonController.shouldPhoneBeSaved){
                    personVital.put("p", mPhone);
                    oldPhone = mPhone;
                }
                if(saveButtonController.shouldCurrentTownBeSaved){
                    personVital.put("ct", mCurrentTown);
                    oldCurrentTown = mCurrentTown;
                }
                if(saveButtonController.shouldHomeTownBeSaved){
                    personVital.put("ht", mHomeTown);
                    oldHomeTown = mHomeTown;
                }
                if(saveButtonController.shouldBirthDateBeSaved){
                    personVital.put("b", getBirthDate());
                    oldYearString = mYearString;
                    oldMonthString = mMonthString;
                    oldDateString = mDateString;
                }

                formLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);

                db.collection("person_vital")
                        .document(mPersonLink)
                        .update(personVital)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("profile_update", "successful");
                                // saveButtonController.refresh();
                                EditPersonProfileForm.this.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("profile_update", "failed");
                                saveButton.setEnabled(true);
                                formLayout.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.INVISIBLE);
                                Toast.makeText(EditPersonProfileForm.this, "Update failed!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                bioEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void bindBio(String bio){
        if(bio != null){
            bioEditText.setText(bio);
        }
    }

    private void bindPhone(String phone){
        if(phone == null) return;
        phoneEditText.setText(phone);
    }

    private void bindCurrentTownSpinner(String currentTown){
        String[] towns = getResources().getStringArray(R.array.towns_bd);
        ArrayList<String> townsArrayList = new ArrayList<>(Arrays.asList(towns));
        int position = 0;
        if(currentTown != null){
            position = townsArrayList.indexOf(currentTown);
        }

        ArrayAdapter<String> townAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, townsArrayList);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentTownSpinner.setAdapter(townAdapter);
        currentTownSpinner.setSelection(position);

        currentTownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("current_town", parent.getItemAtPosition(position).toString());
                mCurrentTown = parent.getItemAtPosition(position).toString();
                saveButtonController.shouldCurrentTownBeSaved = shouldCurrentTownBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindHomeTownSpinner(String homeTown){
        String[] towns = getResources().getStringArray(R.array.towns_bd);
        ArrayList<String> townsArrayList = new ArrayList<>(Arrays.asList(towns));
        int position = 0;
        townsArrayList.remove("Select Current Town");
        townsArrayList.add(0, "Select Home Town");
        if(homeTown != null){
            position = townsArrayList.indexOf(homeTown);
        }

        ArrayAdapter<String> townAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, townsArrayList);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeTownSpinner.setAdapter(townAdapter);
        homeTownSpinner.setSelection(position);

        homeTownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("home_town", parent.getItemAtPosition(position).toString());
                mHomeTown = parent.getItemAtPosition(position).toString();
                saveButtonController.shouldHomeTownBeSaved = shouldHomeTownBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindBirthdate(Timestamp birthdateTS){
        String birthdate;
        if(birthdateTS == null){
            birthdate = "not set";
        }else{
            birthdate = getBirthDateString(birthdateTS);
            Log.i("old_birthdate", birthdate);
        }
        birthdateTV.setText(birthdate);
    }

    private void bindYearSpinner(){
        ArrayList<String> yearArrayList = new ArrayList<>();
        yearArrayList.add("Year");
        for(int i=2019; i>=1901; i--){
            yearArrayList.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, yearArrayList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("year", parent.getItemAtPosition(position).toString());
                mYearString = parent.getItemAtPosition(position).toString();
                if(mYearString.equals("Year")){
                    bindMonthSpinner();
                    monthSpinner.setEnabled(false);
                    bindDateSpinner();
                    dateSpinner.setEnabled(false);
                }else{
                    monthSpinner.setEnabled(true);
                    bindMonthSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindMonthSpinner(){
        // monthLayout.setEnabled(false);
        ArrayList<String> monthArrayList = new ArrayList<>(Arrays.asList(months));
        monthArrayList.add(0, "Month");
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, monthArrayList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("month", parent.getItemAtPosition(position).toString());
                mMonthString = parent.getItemAtPosition(position).toString();
                if(mMonthString.equals("Month")){
                    bindDateSpinner();
                    dateSpinner.setEnabled(false);
                }else{
                    dateSpinner.setEnabled(true);
                    bindDateSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindDateSpinner(){
        ArrayList<String> dateArrayList = new ArrayList<>();
        dateArrayList.add("Date");
        if(!mYearString.equals("Year") && !mMonthString.equals("Month")){
            for(int i=1; i<=28; i++){
                dateArrayList.add(Integer.toString(i));
            }

            if(mMonthString.equals("Feb")){
                if(isLeapYear(Integer.valueOf(mYearString))){
                    dateArrayList.add("29");
                }
            }else{
                switch(mMonthString){
                    case "Jan":
                    case "March":
                    case "May":
                    case "July":
                    case "Aug":
                    case "Oct":
                    case "Dec":
                        dateArrayList.add("29");
                        dateArrayList.add("30");
                        dateArrayList.add("31");
                        break;
                    default:
                        dateArrayList.add("29");
                        dateArrayList.add("30");
                        break;
                }
            }
        }
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dateArrayList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("date", parent.getItemAtPosition(position).toString());
                mDateString = parent.getItemAtPosition(position).toString();
                saveButtonController.shouldBirthDateBeSaved = shouldBirthDateBeSaved();
                if(saveButtonController.shouldBirthDateBeSaved){
                    birthdateTV.setText(mDateString + " " + mMonthString + ", " + mYearString);
                }
                saveButtonController.enableOrDisableSaveButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean shouldBioBeSaved(){
        return !oldBio.equals(mBio);
    }

    private boolean shouldPhoneBeSaved(){
        if(mPhone == null) return false;
        return !oldPhone.equals(mPhone);
    }

    private boolean shouldHomeTownBeSaved(){
        return !oldHomeTown.equals(mHomeTown);
    }

    private boolean shouldCurrentTownBeSaved(){
        return !oldCurrentTown.equals(mCurrentTown);
    }

    private boolean shouldBirthDateBeSaved(){
        return !(mYearString.equals("Year") || mMonthString.equals("Month") || mDateString.equals("Date"));
    }

    private SpannableStringBuilder getSpannedBirthdate(String text){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        int start = text.indexOf("Birthdate");
        int end = start + "Birthdate".length();
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // spannableStringBuilder.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private boolean isLeapYear(int year){
        return ((year%4 == 0 && year%100 != 0) || (year%4 == 0 && year%100 == 0 && year%400 == 0));
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

    private int getMonthNumber(String month){
        ArrayList<String> monthArrayList = new ArrayList<>(Arrays.asList(months));
        return monthArrayList.indexOf(month);
    }

    private void setOldBirthDate(Timestamp birthDate){
        if(birthDate == null) return;
        Date dateObj = birthDate.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        oldYearString = Integer.toString(year);
        oldMonthString = months[month];
        oldDateString = Integer.toString(date);
    }

    private String getBirthDateString(Timestamp birthDate){
        if(birthDate == null) return null;
        Date dateObj = birthDate.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        String yearString = Integer.toString(year);
        String monthString = months[month];
        String dateString = Integer.toString(date);
        return dateString + " " + monthString + ", " + yearString;
    }

    private Timestamp getBirthDate(){
        Calendar cal = Calendar.getInstance();
        int year = Integer.valueOf(mYearString);
        int month = getMonthNumber(mMonthString);
        int date = Integer.valueOf(mDateString);
        cal.set(year, month, date);
        Date dateObj = cal.getTime();
        return new Timestamp(dateObj);
    }

    private class SaveButtonController{
        boolean shouldBioBeSaved = false;
        boolean shouldPhoneBeSaved = false;
        boolean shouldCurrentTownBeSaved = false;
        boolean shouldHomeTownBeSaved = false;
        boolean shouldBirthDateBeSaved = false;
        Button saveButton;

        SaveButtonController(Button saveButton){
            this.saveButton = saveButton;
        }

        void enableOrDisableSaveButton(){
            if(shouldBioBeSaved || shouldPhoneBeSaved || shouldHomeTownBeSaved
                    || shouldCurrentTownBeSaved || shouldBirthDateBeSaved){
                saveButton.setEnabled(true);
            }else{
                saveButton.setEnabled(false);
            }
        }

        void refresh(){
            saveButton.setEnabled(false);
            shouldBioBeSaved = false;
            shouldPhoneBeSaved = false;
            shouldCurrentTownBeSaved = false;
            shouldHomeTownBeSaved = false;
            shouldBirthDateBeSaved = false;
        }
    }
}
