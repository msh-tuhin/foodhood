package com.example.tuhin.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AdapterCreator;

// receives intent with string extra : personLink
public class PersonDetail extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView rv;
    FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
    AppBarLayout appBarLayout;
    CircleImageView personAvatar;
    Animation scaleAnimation;
    float prev = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        String personLink = getIntent().getStringExtra("personLink");

        db = FirebaseFirestore.getInstance();
        appBarLayout = findViewById(R.id.appBarLayout);
        personAvatar = findViewById(R.id.person_avatar);
        rv = findViewById(R.id.rv);

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
            personAvatar.setAlpha(avatarAlpha);
        } else{
            personAvatar.setAlpha(0f);
        }

        float personAvatarHeight = personAvatar.getHeight();
        Log.i("personAvatarHeight", Float.toString(personAvatarHeight));
        if(absoluteVerticalOffset<=personAvatarHeight/2){
            float nextVal = 1-absoluteVerticalOffset/personAvatarHeight;
            scaleAnimation = new ScaleAnimation(prev, nextVal, prev, nextVal);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setDuration(1);
            personAvatar.startAnimation(scaleAnimation);
            prev = nextVal;
        }
    }

}