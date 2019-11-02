package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import models.PersonInfo;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;


// expects to receive a bundle with profile picture url
// and some vital personal information provided by the user
// checks which of the expected data are received and
// make changes to the database accordingly
public class ProfileSetup extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView nameTextView;
    Bundle personDatabundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        personDatabundle = getIntent().getExtras();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        nameTextView = findViewById(R.id.name);

        nameTextView.setText(currentUser.getDisplayName());

        String newUserName = currentUser.getDisplayName();
        String newUserEmail = currentUser.getEmail();
        String newUserUid = currentUser.getUid();
        Uri newUserPhotoUri = currentUser.getPhotoUrl();
        String newUserPhone = personDatabundle.getString("phone");
        String newUserCurrentTown = personDatabundle.getString("current_town");

        Log.i("new_user_name", newUserName);
        Log.i("new_user_email", newUserEmail);
        Log.i("new_user_uid", newUserUid);
        Log.i("new_user_photo", newUserPhotoUri.toString());
        Log.i("new_user_phone", newUserPhone);
        Log.i("new_user_town", newUserCurrentTown);

        Map<String, Object> personVital = new HashMap<>();
        personVital.put("n", newUserName);
        Log.i("new_user", newUserUid);

//        db.collection("person_vital").document(newUserUid).set(personVital)
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("person_vital", e.getMessage());
//                    }
//                });
//
//        PersonInfo personInfo = new PersonInfo();
//        personInfo.setEmail(newUserEmail);
//
//        db.collection("person_info").document(newUserUid).set(personInfo)
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("person_info", e.getMessage());
//                    }
//                });
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("uid", newUserUid);
//
//        FirebaseFunctions.getInstance().getHttpsCallable("setupProfile").call(data)
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i("function_call", e.getMessage());
//                    }
//                })
//                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
//                    @Override
//                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
//                        Log.i("function_call", "successful");
//                    }
//                });
    }
}
