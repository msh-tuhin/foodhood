package com.example.tuhin.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.InputValidator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SetPhone extends AppCompatActivity {

    Toolbar toolbar;
    TextInputLayout phoneInputLayout;
    TextInputEditText phoneEditText;
    TextView skipOrNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone);

        toolbar = findViewById(R.id.toolbar);
        phoneInputLayout = findViewById(R.id.input_layout_phone);
        phoneEditText = findViewById(R.id.phone_editText);
        skipOrNext = findViewById(R.id.skip_or_next);

        toolbar.setTitle("Food Frenzy");
        setSupportActionBar(toolbar);

        skipOrNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("skip_next_phone", "clicked");

                // TODO add phone number to firestore database
                // TODO launch new activity

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
}
