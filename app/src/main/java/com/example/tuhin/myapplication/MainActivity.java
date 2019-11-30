package com.example.tuhin.myapplication;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.AccountTypes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private Task<DocumentSnapshot> mEmailTypeTask;

    LinearLayout mainLayout;
    LinearLayout progressLayout;
    TextView errorMessageTextView, signUpTextView, forgotPasswordTextView;
    TextView createBusinessAccountTV;
    EditText email, password;
    Button signIn, signUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.main_layout);
        progressLayout = findViewById(R.id.progress_layout);
        errorMessageTextView = findViewById(R.id.error_message);
        errorMessageTextView.setVisibility(View.INVISIBLE);

        forgotPasswordTextView = findViewById(R.id.forgot_password);
        signUpTextView = findViewById(R.id.sign_up_textview);
        createBusinessAccountTV = findViewById(R.id.create_business_account_tv);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.button);
        signUp = findViewById(R.id.sign_up_button);

        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setEnabled(false);
                signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
                mainLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                intent.putExtra("entity", "person");
                startActivity(intent);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                intent.putExtra("entity", "person");
                startActivity(intent);
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordRecovery.class));
            }
        });

        createBusinessAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                intent.putExtra("entity", "business");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    private void signInWithEmailAndPassword(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser user = authResult.getUser();
                        if(user != null){
                            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }else{
                            // maybe this never happens
                            Log.i("sign_in", "User is null");
                            progressLayout.setVisibility(View.INVISIBLE);
                            mainLayout.setVisibility(View.VISIBLE);
                            signIn.setEnabled(true);
                            errorMessageTextView.setText("User not found");
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("sign_in", "failed");
                        progressLayout.setVisibility(View.INVISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                        signIn.setEnabled(true);
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Log.i("error", "FirebaseAuthInvalidUserException");
                            errorMessageTextView.setText("The email is disabled or doesn't exist!");
                        }
                        else if(e instanceof FirebaseAuthInvalidCredentialsException){
                            Log.i("error", "FirebaseAuthInvalidCredentialsException");
                            errorMessageTextView.setText("Email or password is wrong!");
                        }
                        else{
                            errorMessageTextView.setText("Couldn't sign in!");
                        }
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

}
