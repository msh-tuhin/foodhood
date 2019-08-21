package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.InputValidator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {

    TextInputLayout nameLayout, emailLayout, passwordLayout, passwordConfirmLayout;
    TextInputEditText emailEditText, passwordEditText, passwordConfirmEditText, nameEditText;
    TextView emailErrorMessageTextview, passwordErrorMessageTextview, passwordConfirmErrorMessageTextview, nameErrorMessageTextview;
    Button signUp;
    FirebaseAuth mAuth;

    SignUpButtonController signUpButtonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        nameLayout = findViewById(R.id.input_layout_name);
        emailLayout = findViewById(R.id.input_layout_email);
        passwordLayout = findViewById(R.id.input_layout_password);
        passwordConfirmLayout = findViewById(R.id.input_layout_password_confirm);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        passwordConfirmEditText = findViewById(R.id.password_confirm);
        signUp = findViewById(R.id.sign_up_button);

        emailErrorMessageTextview = findViewById(R.id.email_error_message);
        passwordErrorMessageTextview = findViewById(R.id.password_error_message);
        passwordConfirmErrorMessageTextview = findViewById(R.id.password_confirm_error_message);
        nameErrorMessageTextview = findViewById(R.id.name_error_message);
        nameErrorMessageTextview.setVisibility(View.INVISIBLE);

        signUpButtonController = new SignUpButtonController(signUp);

        nameEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        emailEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        passwordEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        passwordConfirmEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
        addTextChangedListeners();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = nameEditText.getText().toString();
                String emailText = emailEditText.getText().toString();
                String passwordText = passwordEditText.getText().toString();
                createUserWithEmailAndPassword(emailText, passwordText, nameText);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void createUserWithEmailAndPassword(final String email, final String password, final String name){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        user.updateProfile(userProfileChangeRequest)
                                .addOnSuccessListener(SignUp.this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("name", "added");
                                    }
                                })
                                .addOnFailureListener(SignUp.this, new OnFailureListener() {
                                    @Override
                                    // the chances for this failure are very thin
                                    public void onFailure(@NonNull Exception e) {
                                        if(e instanceof FirebaseAuthInvalidUserException){
                                            Log.i("error", e.getMessage());
                                        }
                                    }
                                });
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.i("email_verification", "sent");
                                            Intent intent = new Intent(SignUp.this, EmailVerification.class);
                                            // maybe not needed
                                            intent.putExtra("name", name);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
//                                            finish();
                                        }else{
                                            // TODO show a dialog with the error message
                                            // TODO there are some quota limitations
                                            // TODO handle those
                                            // TODO add a firestore collection for saving
                                            // TODO verification email not sent error messages
                                            // TODO so that they can be checked by an admin later
                                            Log.i("email_verification", task.getException().getMessage());
                                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Exception exception = e;
                        if(exception instanceof FirebaseAuthWeakPasswordException){
//                            passwordErrorMessageTextview.setText(((FirebaseAuthWeakPasswordException) exception).getReason());
//                            passwordErrorMessageTextview.setVisibility(View.VISIBLE);
//                            passwordEditText.setText("");
//                            passwordConfirmEditText.setText("");
                            passwordLayout.setError(((FirebaseAuthWeakPasswordException) exception).getReason());
                        } else{
                             // FirebaseAuthInvalidCredentialsException
                             // FirebaseAuthUserCollisionException
//                            emailErrorMessageTextview.setText(exception.getMessage());
//                            emailErrorMessageTextview.setVisibility(View.VISIBLE);
                            emailLayout.setError(exception.getMessage());
                        }
                    }
                });
    }

    private void addTextChangedListeners(){
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkNameEditText(s.toString());
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEmailEditText(s.toString());
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordEditText(s.toString());
            }
        });

        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordConfirmEditText(passwordEditText.getText().toString(), s.toString());
            }
        });
    }

    private void checkNameEditText(String name){
//        if(name.trim().isEmpty()){
//            nameLayout.setError(null);
//            nameLayout.setError("Name is required");
//            return;
//        }
        boolean validated = InputValidator.validateName(name);
        nameLayout.setError(null);
        if(!validated){
            nameLayout.setError("Name is required");
        }
        signUpButtonController.isNameValid = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private void checkEmailEditText(String email){
//        if(email.trim().isEmpty()){
//            emailLayout.setError(null);
//            emailLayout.setError("Email is required");
//            return;
//        }
        boolean validated = InputValidator.validateEmail(email);
        emailLayout.setError(null);
        if(!validated){
            emailLayout.setError("Please type a valid email");
        }
        signUpButtonController.isEmailValid = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private void checkPasswordEditText(String password){
//        if(password.trim().isEmpty()){
//            passwordLayout.setError(null);
//            passwordLayout.setError("Password is required");
//            return;
//        }
        boolean validated = InputValidator.validatePassword(password);
        passwordConfirmEditText.setEnabled(false);
        passwordLayout.setError(null);
        if(!validated){
            passwordLayout.setError("Password should be at least 6 characters");
        } else{
            passwordConfirmEditText.setEnabled(true);
        }
        signUpButtonController.isPasswordValid = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private void checkPasswordConfirmEditText(String password, String passwordAgain){
        boolean validated = InputValidator.confirmPassword(password, passwordAgain);
        passwordConfirmLayout.setError(null);
        if(!validated){
            passwordConfirmLayout.setError("Passwords don't match");
        }
        signUpButtonController.isPasswordConfirmed = validated;
        signUpButtonController.enableOrDisableSignUpButton();
    }

    private class SignUpButtonController{
        boolean isNameValid = false;
        boolean isEmailValid = false;
        boolean isPasswordValid = false;
        boolean isPasswordConfirmed = false;
        Button signUpButton;

        SignUpButtonController(Button signUpButton){
            this.signUpButton = signUpButton;
        }
        void enableOrDisableSignUpButton(){
            if(isNameValid && isEmailValid && isPasswordValid && isPasswordConfirmed){
                signUpButton.setEnabled(true);
            } else{
                signUpButton.setEnabled(false);
            }
        }
    }

    private class MyOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.name:
                    if(hasFocus) {
                        Log.i("focus", "got by 'name'");
                    } else{
                        Log.i("focus", "lost by 'name'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_name)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_name)).setError("Name is required");
                        }
                    }
                    break;
                case R.id.email:
                    if(hasFocus) {
                        Log.i("focus", "got by 'email'");
                    } else{
                        Log.i("focus", "lost by 'email'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_email)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_email)).setError("Email is required");
                        }
                    }
                    break;
                case R.id.password:
                    if(hasFocus) {
                        Log.i("focus", "got by 'password'");
                    } else{
                        Log.i("focus", "lost by 'password'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_password)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_password)).setError("Password is required");
                        }
                    }
                    break;
                case R.id.password_confirm:
                    if(hasFocus){
                        Log.i("focus", "got by 'password_confirm'");
                    } else{
                        Log.i("focus", "lost by 'password_confirm'");
                        if (((TextInputEditText) v).getText().toString().trim().isEmpty()) {
                            ((TextInputLayout) findViewById(R.id.input_layout_password_confirm)).setError(null);
                            ((TextInputLayout) findViewById(R.id.input_layout_password_confirm)).setError("Please confirm your password");
                        }
                    }
                    break;
            }
        }
    }
}
