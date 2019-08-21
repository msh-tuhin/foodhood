package com.example.tuhin.myapplication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    TextView errorMessageTextView, signUpTextView, forgotPasswordTextView;
    EditText email, password;
    Button signIn, signUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorMessageTextView = findViewById(R.id.error_message);
        errorMessageTextView.setVisibility(View.INVISIBLE);

        forgotPasswordTextView = findViewById(R.id.forgot_password);
        signUpTextView = findViewById(R.id.sign_up_textview);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.button);
        signUp = findViewById(R.id.sign_up_button);

        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordRecovery.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null && user.isEmailVerified()){
            // TODO send to Welcome/ProfileSetup page if user is new
            Intent intent = new Intent(MainActivity.this, home.class);
            startActivity(intent);
        }
    }

    private void signInWithEmailAndPassword(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        if(user != null){
                            if(user.isEmailVerified()){
                                Log.i("sign_in", "Successful");
                                // TODO send to welcome/ProfileSetup page if new user
                                Intent intent = new Intent(MainActivity.this, home.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Log.i("sign_in", "Email not verified");
                                user.sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("email_verification", "sent");
                                                Intent intent = new Intent(MainActivity.this, EmailVerification.class);
                                                // maybe not needed
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // TODO show a dialog with the error message
                                                // TODO there are some quota limitations
                                                // TODO handle those
                                                // TODO add a firestore collection for saving
                                                // TODO verification email not sent error messages
                                                // TODO so that they can be checked by an admin later
                                                Log.i("email_verification", e.getMessage());
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
