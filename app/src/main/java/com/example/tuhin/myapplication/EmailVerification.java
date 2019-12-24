package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.OrphanUtilityMethods;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmailVerification extends AppCompatActivity {

    private String mEmail;
    LinearLayout mainLayout;
    LinearLayout progressLayout;
    TextView emailVerifiedTextView, resendEmailTextView, emailNotVerifiedTextView;
    EditText emailEditText, passwordEditText;
    Button signInButton;
    FirebaseAuth mAuth;
    private final String TAG = "EMAILVERIFICATION_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // maybe name, email not needed from intent
        // could be acquired from the FirebaseUser object
        mEmail = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();

        mainLayout = findViewById(R.id.main_layout);
        progressLayout = findViewById(R.id.progress_layout);
        emailVerifiedTextView = findViewById(R.id.email_verified_textview);
        emailNotVerifiedTextView = findViewById(R.id.not_verified_text);
        emailNotVerifiedTextView.setVisibility(View.INVISIBLE);
        resendEmailTextView = findViewById(R.id.resend_email_textview);

        emailEditText = findViewById(R.id.email);
        emailEditText.setText(mEmail);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);

        mAuth.signOut();

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    signInButton.setEnabled(false);
                }else{
                    signInButton.setEnabled(true);
                }
            }
        });
        emailVerifiedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                if(user.isEmailVerified()){
                                    Log.i(TAG+"email", "verified");
                                    startActivity(new Intent(EmailVerification.this, ProfileSetup.class));
                                    finish();
                                }else{
                                    Log.i(TAG+"email", "not verified");
                                    emailNotVerifiedTextView.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Log.i(TAG+"error", task.getException().getMessage());
                            }
                        }
                    });

                }else{
                    Log.i(TAG+"user", "null");
                }
            }
        });

        resendEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton.setEnabled(false);
                mainLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
                emailNotVerifiedTextView.setVisibility(View.INVISIBLE);
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInWithEmailAndPassword(email, password);
            }
        });
    }

    @Override
    protected void onStart() {
        OrphanUtilityMethods.checkUpdateMust(this);
        super.onStart();
    }

    private void signInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        if(user != null){
                            if(user.isEmailVerified()){
                                Log.i("sign_in", "Successful");
                                launchPersonOrRestProfileSetup(user);
                            }else{
                                Log.i("sign_in", "Email not verified");
                                progressLayout.setVisibility(View.INVISIBLE);
                                mainLayout.setVisibility(View.VISIBLE);
                                signInButton.setEnabled(true);
                                // showDialog();
                                emailNotVerifiedTextView.setText("Sorry! Email is not verified!");
                                emailNotVerifiedTextView.setVisibility(View.VISIBLE);
                            }
                        }else{
                            // maybe this never happens
                            Log.i("sign_in", "User is null");
                            progressLayout.setVisibility(View.INVISIBLE);
                            mainLayout.setVisibility(View.VISIBLE);
                            signInButton.setEnabled(true);
                            emailNotVerifiedTextView.setText("User not found");
                            emailNotVerifiedTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("sign_in", "failed");
                        progressLayout.setVisibility(View.INVISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                        signInButton.setEnabled(true);
                        // the error message doesn't show in emailNotVerifiedTextView
                        // don't know why
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Log.i("error", "FirebaseAuthInvalidUserException");
                            emailNotVerifiedTextView.setText("The email is disabled or doesn't exist!");
                        }
                        else if(e instanceof FirebaseAuthInvalidCredentialsException){
                            Log.i("error", "FirebaseAuthInvalidCredentialsException");
                            emailNotVerifiedTextView.setText("Email or password is wrong!");
                        }
                        else{
                            emailNotVerifiedTextView.setText("Couldn't sign in!");
                        }
                    }
                });
    }



    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Email is not verified!");
        View view = LayoutInflater.from(this).inflate(R.layout.email_not_verified_dialog,null);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();
        view.findViewById(R.id.resend_email_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
                dialog.cancel();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    private void launchPersonOrRestProfileSetup(final FirebaseUser user){
        if(user==null) return;
        FirebaseFirestore.getInstance()
                .collection("email_type")
                .document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Boolean forPerson = documentSnapshot.getBoolean("forPerson");
                            Intent intent;
                            if(forPerson){
                                intent = new Intent(EmailVerification.this, SetProfilePicture.class);
                            } else{
                                intent = new Intent(EmailVerification.this, RestAccountSetup.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void resendVerificationEmail(){
        emailNotVerifiedTextView.setVisibility(View.INVISIBLE);
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i(TAG+"ver_e", "sent");
                        String message = "Verification Email sent to " + user.getEmail();
                        Toast.makeText(EmailVerification.this, message, Toast.LENGTH_LONG).show();
                    }else{
                        // TODO show a dialog with the error message
                        // TODO there are some quota limitations
                        // TODO handle those
                        // TODO add a firestore collection for saving
                        // TODO verification email not sent error messages
                        // TODO so that they can be checked by an admin later
                        Log.i(TAG+"error", task.getException().getMessage());
                        Toast.makeText(EmailVerification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Log.i("user", "null");
        }
    }

}
