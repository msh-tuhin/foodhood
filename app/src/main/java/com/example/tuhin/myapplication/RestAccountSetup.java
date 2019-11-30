package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RestAccountSetup extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_account_setup);

        addNameAndEmailToDB(mAuth.getCurrentUser());
        Map<String, Object> map = new HashMap<>();
        map.put("a", new ArrayList<String>());
        String currentUserId = mAuth.getCurrentUser().getUid();
        db.collection("followers").document(currentUserId).set(map);
        db.collection("feedbacks_list").document(currentUserId).set(map);
        db.collection("own_activities").document(currentUserId)
                .set(new HashMap<>());
        db.collection("notifications").document(currentUserId)
                .set(new HashMap<>());

        Map<String, Object> confirmationMap = new HashMap<>();
        confirmationMap.put("a", true);
        db.collection("profile_created").document(currentUserId)
                .set(confirmationMap);

        Intent intent = new Intent(this, RestaurantHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void addNameAndEmailToDB(FirebaseUser user){
        if(user==null) return;

        String name = user.getDisplayName();
        String emailString = user.getEmail();

        Map<String, String> restVital = new HashMap<>();
        if(name!=null){
            restVital.put("n", name);
        }
        if(emailString!=null){
            restVital.put("e", emailString);
        }

        FirebaseFirestore.getInstance()
                .collection("rest_vital").document(user.getUid())
                .set(restVital, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
