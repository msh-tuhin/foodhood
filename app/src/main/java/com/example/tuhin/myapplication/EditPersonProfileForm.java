package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class EditPersonProfileForm extends AppCompatActivity {

    private String mCurrentTown;
    private String mHomeTown;
    private String mPersonLink;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Toolbar toolbar;
    ImageView enablePhoneEdit;
    TextInputLayout phoneLayout;
    TextInputEditText phoneEditText;
    Spinner currentTownSpinner;
    Spinner homeTownSpinner;
    TextView birthdateTV;
    ImageView enableBirthdateEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        phoneLayout = findViewById(R.id.phone_layout);
        phoneEditText = findViewById(R.id.phone);
        enablePhoneEdit = findViewById(R.id.enable_phone_edit);
        currentTownSpinner = findViewById(R.id.current_town_spinner);
        homeTownSpinner = findViewById(R.id.home_town_spinner);
        birthdateTV = findViewById(R.id.birthdate_textView);
        enableBirthdateEdit = findViewById(R.id.enable_birthdate_edit);

        db.collection("person_vital").document(mPersonLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personInfo = task.getResult();
                    if(personInfo.exists()){
                        String personName = personInfo.getString("n");
                        Timestamp personBirthdate = personInfo.getTimestamp("b");
                        String personHometown = personInfo.getString("ht");
                        String personCurrentLocation = personInfo.getString("ct");
                        String personPhone = personInfo.getString("p");
//                        birthdate.setText(personBirthdate.toString());
//                        currentLocation.setText(personCurrentLocation);
//                        hometown.setText(personHometown);
                        bindPhone(personPhone);
                        bindCurrentTownSpinner(personCurrentLocation);
                        bindHomeTownSpinner(personHometown);
                        bindBirthdate(personBirthdate);
                    }
                }
            }
        });

        enablePhoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEditText.setEnabled(true);
            }
        });

        enableBirthdateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void bindPhone(String phone){
        if(phone == null) return;
        phoneEditText.setText(phone);
    }

    private void bindCurrentTownSpinner(String currentTown){
        String[] towns = getResources().getStringArray(R.array.towns_bd);
        ArrayList<String> townsArrayList = new ArrayList<>(Arrays.asList(towns));
        if(currentTown != null){
            townsArrayList.remove(currentTown);
            townsArrayList.remove("Select Current Town");
            townsArrayList.add(0, currentTown);
        }

        ArrayAdapter<String> townAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, townsArrayList);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentTownSpinner.setAdapter(townAdapter);

        currentTownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("current_town", parent.getItemAtPosition(position).toString());
                mCurrentTown = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindHomeTownSpinner(String homeTown){
        String[] towns = getResources().getStringArray(R.array.towns_bd);
        ArrayList<String> townsArrayList = new ArrayList<>(Arrays.asList(towns));
        if(homeTown != null){
            townsArrayList.remove(homeTown);
            townsArrayList.remove("Select Current Town");
            townsArrayList.add(0, homeTown);
        }else{
            townsArrayList.remove("Select Current Town");
            townsArrayList.add(0, "Select Home Town");
        }

        ArrayAdapter<String> townAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, townsArrayList);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeTownSpinner.setAdapter(townAdapter);

        homeTownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("home_town", parent.getItemAtPosition(position).toString());
                mHomeTown = parent.getItemAtPosition(position).toString();
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
            birthdate = birthdateTS.toString();
        }
        String text = "Birthdate: " + birthdate;
        SpannableStringBuilder spannableStringBuilder = getSpannedBirthdate(text);
        birthdateTV.setText(spannableStringBuilder);
    }

    private SpannableStringBuilder getSpannedBirthdate(String text){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        int start = text.indexOf("Birthdate");
        int end = start + "Birthdate".length();
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
