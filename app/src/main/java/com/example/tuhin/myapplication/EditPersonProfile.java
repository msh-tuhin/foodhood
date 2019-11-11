package com.example.tuhin.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AccountTypes;
import myapp.utils.AdapterCreator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EditPersonProfile extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView rv;
    FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public AppBarLayout appBarLayout;
    ImageView coverPhoto;
    CircleImageView personAvatar;
    Animation scaleAnimation;
    float prev = 1f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person_profile);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        // collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        toolbar = findViewById(R.id.toolbar);
        coverPhoto = findViewById(R.id.cover_photo);

        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        appBarLayout = findViewById(R.id.appBarLayout);
        personAvatar = findViewById(R.id.person_avatar);
        rv = findViewById(R.id.rv);

        db.collection("person_vital")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitslSnapshot) {
                        bindProfilePicture(personVitslSnapshot);
                        bindCoverPhoto(personVitslSnapshot);
                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        String personLink = mAuth.getCurrentUser().getUid();
        adapter = AdapterCreator.getEditPersonProfileAdapter(this, this, personLink);
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

        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPersonProfile.this, ChangeRestCoverPhoto.class);
                intent.putExtra("entity", AccountTypes.PERSON);
                startActivity(intent);
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

    private void bindProfilePicture(DocumentSnapshot personVitslSnapshot){
        if(personVitslSnapshot == null) return;
        String profilePictureLink = personVitslSnapshot.getString("pp");
        if(profilePictureLink != null && !profilePictureLink.equals("")){
            Picasso.get().load(profilePictureLink)
                    .placeholder(R.drawable.ltgray)
                    .error(R.drawable.ltgray)
                    .into(personAvatar);
        }
    }

    private void bindCoverPhoto(DocumentSnapshot personVitslSnapshot){
        if(personVitslSnapshot == null) return;
        String coverPhotoLink = personVitslSnapshot.getString("cp");
        if(coverPhotoLink != null && !coverPhotoLink.equals("")){
            Picasso.get().load(coverPhotoLink)
                    .placeholder(R.drawable.gray)
                    .error(R.drawable.gray)
                    .into(coverPhoto);
        }
    }
}
