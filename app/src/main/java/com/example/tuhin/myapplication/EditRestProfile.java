package com.example.tuhin.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditRestProfile extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Toolbar toolbar;
    TextView nameTV;
    TextView ratingTV;
    TextView phoneTV;
    TextView addressTV;
    TextView emailTV;
    TextView websiteTV;
    Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rest_profile);

        toolbar = findViewById(R.id.toolbar);
        nameTV = findViewById(R.id.restaurant_name);
        ratingTV = findViewById(R.id.rating);
        phoneTV = findViewById(R.id.phone);
        addressTV = findViewById(R.id.restaurant_address);
        emailTV = findViewById(R.id.email);
        websiteTV = findViewById(R.id.web);
        editProfileButton = findViewById(R.id.edit_profile);

        String displayName = mAuth.getCurrentUser().getDisplayName();
        if(displayName != null){
            toolbar.setTitle(displayName);
            nameTV.setText(displayName);
        }else{
            toolbar.setTitle("Profile");
        }
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        db.collection("rest_vital")
        .document(mAuth.getCurrentUser().getUid())
        .get()
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot restVitalSnapshot) {
                if(restVitalSnapshot.exists()){
                    bindName(restVitalSnapshot);
                    bindAddress(restVitalSnapshot);
                    bindPhone(restVitalSnapshot);
                    bindRating(restVitalSnapshot);
                    bindEmail(restVitalSnapshot);
                    bindWebsite(restVitalSnapshot);
                }
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRestProfile.this, EditRestProfileForm.class);
                startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot restVitalSnapshot){
        String name = restVitalSnapshot.getString("n");
        if(name != null){
            nameTV.setText(name);
        }
    }

    private void bindAddress(DocumentSnapshot restVitalSnapshot){
        String address = restVitalSnapshot.getString("a");
        if(address != null){
            addressTV.setText(address);
        }
    }

    private void bindPhone(DocumentSnapshot restVitalSnapshot){
        String phone = restVitalSnapshot.getString("p");
        if(phone != null){
            phoneTV.setText(phone);
        }
    }

    private void bindEmail(DocumentSnapshot restVitalSnapshot){
        String email = restVitalSnapshot.getString("e");
        if(email != null){
            emailTV.setText(email);
        }
    }

    private void bindWebsite(DocumentSnapshot restVitalSnapshot){
        String web = restVitalSnapshot.getString("w");
        if(web != null){
            websiteTV.setText(web);
        }
    }

    private void bindRating(DocumentSnapshot restVitalSnapshot){
        Double numberOfPeople = restVitalSnapshot.getDouble("npr");
        Double totalRating = restVitalSnapshot.getDouble("tr");
        if(numberOfPeople == null){
            return;
        }
        if(totalRating == null){
            return;
        }
        Double rating = totalRating/numberOfPeople;
        ratingTV.setText(Double.toString(rating));
    }
}
