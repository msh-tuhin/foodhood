package com.example.tuhin.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.InputValidator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetPhone extends AppCompatActivity {

    String phoneNumber = null;
    Toolbar toolbar;
    TextInputLayout phoneInputLayout;
    TextInputEditText phoneEditText;
    TextView skipOrNext;
    Bundle personDataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone);

        personDataBundle = getIntent().getExtras();
        toolbar = findViewById(R.id.toolbar);
        phoneInputLayout = findViewById(R.id.input_layout_phone);
        phoneEditText = findViewById(R.id.phone_editText);
        skipOrNext = findViewById(R.id.skip_or_next);

        toolbar.setTitle("Food Frenzy");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        skipOrNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("skip_next_phone", "clicked");
                Intent intent = new Intent(SetPhone.this, SetCurrentTown.class);
                if(((TextView)v).getText() == "Next"){
                    phoneNumber = phoneEditText.getText().toString();
                    phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
                    String regex = "\\A\\+88";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(phoneNumber);
                    if(matcher.find()){
                        Log.i("group", matcher.group());
                        Log.i("match_start", Integer.toString(matcher.start()));
                        Log.i("match_end", Integer.toString(matcher.end()));
                        phoneNumber = matcher.replaceFirst("");
                    } else {
                        Log.i("match", "not found");
                    }
                    Log.i("new_phone_number", phoneNumber);
                    personDataBundle.putString("phone", phoneNumber);
                }
                intent.putExtras(personDataBundle);
                startActivity(intent);
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
            }
        });

    }

    private void checkPhoneEditText(String phoneNumber){
        phoneInputLayout.setError(null);
        if(phoneNumber.trim().isEmpty()){
            skipOrNext.setText("Skip");
            return;
        }

        skipOrNext.setText("Next");
        boolean validated = InputValidator.validatePhone(phoneNumber);
        if(!validated){
            phoneInputLayout.setError("Please type a valid phone number");
            skipOrNext.setText("Skip");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
