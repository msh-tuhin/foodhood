package com.example.tuhin.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import models.ActivityResponse;
import myapp.utils.AdapterCreator;

// receives intent with string extra : personLink
public class PersonDetail extends AppCompatActivity {

    float prev = 1f;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;

    public CollapsingToolbarLayout collapsingToolbarLayout;
    public AppBarLayout appBarLayout;
    Toolbar toolbar;
    public CircleImageView profilePicture;
    public ImageView coverPhoto;
    Animation scaleAnimation;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        String personLink = getIntent().getStringExtra("personLink");

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        profilePicture = findViewById(R.id.profile_picture);
        coverPhoto = findViewById(R.id.cover_photo);
        rv = findViewById(R.id.rv);

        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        //toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = AdapterCreator.getPersonDetailAdapter(this, this, personLink);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

        Log.i("total", Integer.toString(appBarLayout.getTotalScrollRange()));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.i("offset", Integer.toString(verticalOffset));
                Log.i("total", Integer.toString(appBarLayout.getTotalScrollRange()));
                animatePersonAvatar(appBarLayout, verticalOffset);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void animatePersonAvatar(AppBarLayout appBarLayout, int verticalOffset){
        float absoluteVerticalOffset = Math.abs(verticalOffset);
        Log.i("abs_offset", Float.toString(absoluteVerticalOffset));
        float range = appBarLayout.getTotalScrollRange();
        if(absoluteVerticalOffset <= range/2){
            float avatarAlpha = 1 - absoluteVerticalOffset/(range/2);
            Log.i("avatarAlpha", Float.toString(avatarAlpha));
            profilePicture.setAlpha(avatarAlpha);
        } else{
            profilePicture.setAlpha(0f);
        }

        float personAvatarHeight = profilePicture.getHeight();
        Log.i("personAvatarHeight", Float.toString(personAvatarHeight));
        if(absoluteVerticalOffset<=personAvatarHeight/2){
            float nextVal = 1-absoluteVerticalOffset/personAvatarHeight;
            scaleAnimation = new ScaleAnimation(prev, nextVal, prev, nextVal);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setDuration(1);
            profilePicture.startAnimation(scaleAnimation);
            prev = nextVal;
        }
    }

}