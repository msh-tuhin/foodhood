package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInAfterPasswordRecovery extends AppCompatActivity {

    TextView errorMessageTextView;
    EditText emailEditText, passwordEditText;
    Button signInButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_after_password_recovery);

        String email = getIntent().getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();

        errorMessageTextView = findViewById(R.id.error_message);
        errorMessageTextView.setVisibility(View.INVISIBLE);

        emailEditText = findViewById(R.id.email);
        emailEditText.setText(email);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signInWithEmailAndPassword(email, password);
            }
        });

    }

    private void signInWithEmailAndPassword(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        if(user != null){
                            if(user.isEmailVerified()){
                                Log.i("sign_in", "Successful");
                                // TODO send to welcome/ProfileSetup page if new user
                                Intent intent = new Intent(SignInAfterPasswordRecovery.this, home.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Log.i("sign_in", "Email not verified");
                                user.sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("email_verification", "sent");
                                                Intent intent = new Intent(SignInAfterPasswordRecovery.this, EmailVerification.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("email_verification", e.getMessage());
                                                Toast.makeText(SignInAfterPasswordRecovery.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }else{
                            // maybe this never happens
                            Log.i("sign_in", "User is null");
                            errorMessageTextView.setText("User not found");
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("sign_in", e.getMessage());
//                        errorMessageTextView.setText(e.getMessage());
                        errorMessageTextView.setText("Email or Password is wrong.");
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
