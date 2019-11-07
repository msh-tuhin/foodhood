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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private Task<DocumentSnapshot> mEmailTypeTask;
    TextView errorMessageTextView, signUpTextView, forgotPasswordTextView;
    TextView createBusinessAccountTV;
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
        createBusinessAccountTV = findViewById(R.id.create_business_account_tv);
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
        if(user != null && user.isEmailVerified()){
            chooseAndLaunchHome(user);
        }
    }

    private void signInWithEmailAndPassword(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser user = authResult.getUser();
                        if(user != null){
                             chooseAndLaunchHome(user);
//                            if(user.isEmailVerified()){
//                                Log.i("sign_in", "Successful");
//                                chooseAndLaunchHome(user);
//                            }else{
//                                Log.i("sign_in", "Email not verified");
//                                sendEmailVerification(user, email);
//                            }
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

    private void chooseAndLaunchHome(FirebaseUser user){
        SharedPreferences sPref = getSharedPreferences(getString(R.string.account_type),
                Context.MODE_PRIVATE);
        int accountType = sPref.getInt(user.getEmail(), AccountTypes.UNSET);
        if(accountType == AccountTypes.UNSET){
            getAccountTypeFromDB(user);
        }else if(accountType == AccountTypes.PERSON){
            // TODO send to Welcome/ProfileSetup page if user is new
            chooseHomeOrProfileCreation(user);
        }else{
            Intent intent = new Intent(MainActivity.this, RestaurantHome.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

    }

    private void getAccountTypeFromDB(final FirebaseUser user){
        FirebaseFirestore.getInstance()
                .collection("email_type")
                .document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i("account_type_SP", "being added");
                        SharedPreferences sPref = getSharedPreferences(getString(R.string.account_type),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sPref.edit();
                        if(documentSnapshot.exists()){
                            Boolean forPerson = documentSnapshot.getBoolean("forPerson");
                            Intent intent;
                            if(forPerson){
                                editor.putInt(user.getEmail(), AccountTypes.PERSON);
                                editor.apply();
                                chooseHomeOrProfileCreation(user);
                            }else{
                                editor.putInt(user.getEmail(), AccountTypes.RESTAURANT);
                                editor.apply();
                                intent = new Intent(MainActivity.this, RestaurantHome.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }
                        }
                    }
                });
    }

    private void launchEmailVerification(FirebaseUser user, final String emailString){
        FirebaseFirestore.getInstance()
                .collection("email_type")
                .document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Boolean forPerson = documentSnapshot.getBoolean("forPerson");
                            Intent intent = new Intent(MainActivity.this, EmailVerification.class);
                            intent.putExtra("email", emailString);
                            intent.putExtra("for_person", forPerson);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }
                });
    }

    private void sendEmailVerification(final FirebaseUser user, final String emailString){
        user.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("email_verification", "sent");
                        launchEmailVerification(user, emailString);
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

    private void chooseHomeOrProfileCreation(FirebaseUser user){
        String uid = user.getUid();
        FirebaseFirestore.getInstance().collection("profile_created")
                .document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Intent intent = new Intent(MainActivity.this, home.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }else{
                            Intent intent = new Intent(MainActivity.this, SetProfilePicture.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }
                });
    }
}
