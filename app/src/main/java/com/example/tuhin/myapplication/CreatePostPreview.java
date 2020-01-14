package com.example.tuhin.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import models.DishFeedback;
import models.PostModel;
import models.RestaurantFeedback;
import models.SelectedPerson;
import models.FeedbackModel;
import myapp.utils.FeedbackTypes;
import myapp.utils.ImagesAdapter;
import myapp.utils.OrphanUtilityMethods;
import myapp.utils.PictureBinder;
import myapp.utils.SourceHomePage;

// receives explicit intent with bundle extra
// the keys =>
//     "caption": str
//     "imageSringUris": ArrayList<String>
//     "restaurantFeedback": RestaurantFeedback
//     "dishFeedbacks": ArrayList<DishFeedback>
//     *** not used anymore "addedPeople": ArrayList<String>     // these are links
//     "addedPeopleList": ArrayList<SelectedPerson>
public class CreatePostPreview extends AppCompatActivity {

    Bundle post;
    Toolbar toolbar;

    AppBarLayout appBarLayout;
    ScrollView postPreviewLayout;
    LinearLayout progressLayout;
    CircleImageView dishOrRestaurantAvatar;
    TextView captionTextView, dishOrRestaurantNameTextView, reviewTextView;
    TextView viewPagerCurrentPositionTV;
    TextView captionHeaderTextView, imagesHeaderTextView, peopleHeaderTextView;
    RatingBar ratingBar;
    ViewPager viewPager;
    ImagesAdapter adapter;
    LinearLayout dishReviewsLinearLayout, mainLinearLayout, taggedPeopleLayout;

    ArrayList<SelectedPerson> addedPeopleList;
    String caption;
    ArrayList<String> imagesUri;
    RestaurantFeedback restaurantFeedback;
    ArrayList<DishFeedback> dishFeedbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_preview);

        post = getIntent().getExtras();

        // checking if data received correctly
//        ArrayList<String> addedPeople = post.getStringArrayList("taggedPeople");
        addedPeopleList = (ArrayList<SelectedPerson>) post.getSerializable("taggedPeople");
        caption = post.getString("caption");
        imagesUri =  post.getStringArrayList("imageSringUris");
        restaurantFeedback = (RestaurantFeedback) post.getSerializable("restaurantFeedback");
        dishFeedbacks = (ArrayList<DishFeedback>) post.getSerializable("dishFeedbacks");

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        postPreviewLayout = findViewById(R.id.post_preview_layout);
        progressLayout = findViewById(R.id.progress_layout);
        captionTextView = findViewById(R.id.caption);
        captionHeaderTextView = findViewById(R.id.caption_header);
        imagesHeaderTextView = findViewById(R.id.images_header);
        peopleHeaderTextView = findViewById(R.id.people_header);
        viewPagerCurrentPositionTV = findViewById(R.id.current_image_position);
        viewPager = findViewById(R.id.viewPager);
        dishOrRestaurantAvatar = findViewById(R.id.dish_avatar);
        dishOrRestaurantNameTextView = findViewById(R.id.dish_name);
        reviewTextView = findViewById(R.id.review);
        ratingBar = findViewById(R.id.dish_ratingBar);
        dishReviewsLinearLayout = findViewById(R.id.dish_reviews);
        mainLinearLayout = findViewById(R.id.main_layout);
        taggedPeopleLayout = findViewById(R.id.tagged_people_layout);

        toolbar.setTitle("Post Preview");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

//        captionTextView.setText(caption);
        if(caption.isEmpty()){
            mainLinearLayout.removeView(captionHeaderTextView);
            mainLinearLayout.removeView(captionTextView);
        }else{
            captionTextView.setText(caption);
        }

        if(imagesUri.size() == 0){
            mainLinearLayout.removeView(imagesHeaderTextView);
            mainLinearLayout.removeView(viewPager);
        }else{
            viewPagerCurrentPositionTV.setText("1/" +
                    Integer.toString(imagesUri.size()));
            adapter = new ImagesAdapter(this);
            for(String uriString : imagesUri){
                //adapter.imageUris.add(Uri.parse(uriString));
                try{
                    // String path = Uri.parse(uriString).getPath();
                    // File compressedFile = new Compressor(this).compressToFile(new File(path));
                    URI uri = URI.create(uriString);
                    File compressedFile = new Compressor(this).compressToFile(new File(uri));
                    Uri compressedFileUri = Uri.fromFile(compressedFile);
                    adapter.imageUris.add(compressedFileUri);
                }catch (IOException e){
                    Log.e("error", e.getMessage());
                }
            }
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    viewPagerCurrentPositionTV.setText(Integer.toString(position+1)+"/" +
                            Integer.toString(adapter.imageUris.size()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setAdapter(adapter);
        }

        PictureBinder.bindPictureSearchResult(dishOrRestaurantAvatar, restaurantFeedback.imageUrl);
        dishOrRestaurantNameTextView.setText(restaurantFeedback.name);
        ratingBar.setRating(restaurantFeedback.rating);
        ratingBar.setIsIndicator(true);
        reviewTextView.setText(restaurantFeedback.review);

        for(DishFeedback dishFeedback : dishFeedbacks){
            View view = LayoutInflater.from(this).inflate(R.layout.feedback_preview, null);
            PictureBinder.bindPictureSearchResult((CircleImageView) view.findViewById(R.id.dish_avatar),
                    dishFeedback.imageUrl);
            ((TextView) view.findViewById(R.id.dish_name)).setText(dishFeedback.name);
            ((TextView) view.findViewById(R.id.review)).setText(dishFeedback.review);
            ((RatingBar) view.findViewById(R.id.dish_ratingBar)).setRating(dishFeedback.rating);
            ((RatingBar) view.findViewById(R.id.dish_ratingBar)).setIsIndicator(true);
            dishReviewsLinearLayout.addView(view);
        }

        if(addedPeopleList.size() == 0){
            mainLinearLayout.removeView(peopleHeaderTextView);
            mainLinearLayout.removeView(taggedPeopleLayout);
        }else{
            for(SelectedPerson selectedPerson : addedPeopleList){
                View view = LayoutInflater.from(this).inflate(R.layout.search_hit_item, null);
                PictureBinder.bindPictureSearchResult((CircleImageView)view.findViewById(R.id.avatar),
                        selectedPerson.imageUrl);
                ((TextView) view.findViewById(R.id.restaurant_name)).setText(selectedPerson.name);
                taggedPeopleLayout.addView(view);
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_post_preview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:
                Log.i("Menu", "working");

                appBarLayout.setVisibility(View.INVISIBLE);
                postPreviewLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();

                final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final PostModel postModel = new PostModel();

                Map<String, String> who = new HashMap<>();
                who.put("l", currentUserLink);
                who.put("n", OrphanUtilityMethods.getCurrentUserName(this));
                postModel.setWho(who);

                postModel.setCaption(caption);
                postModel.setComments(new ArrayList<String>());
                postModel.setCommentsBy(new ArrayList<String>());
                postModel.setLikes(new ArrayList<String>());
                postModel.setTimestamp(new Timestamp(new Date()));

                Map<String, String> taggedPeople = new HashMap<>();
                for(SelectedPerson person : addedPeopleList){
                    taggedPeople.put(person.id, person.name);
                }
                postModel.setTaggedPeople(taggedPeople);

                Map<String, String> restaurantMap = new HashMap<>();
                restaurantMap.put("l", restaurantFeedback.link);
                restaurantMap.put("n", restaurantFeedback.name);
                postModel.setRestaurant(restaurantMap);

                Map<String, String> dishesMap = new HashMap<>();
                for(DishFeedback dishFeedback : dishFeedbacks){
                    dishesMap.put(dishFeedback.link, dishFeedback.name);
                }
                postModel.setDishes(dishesMap);

                List<UploadTask> uploadTasks = new ArrayList<>();
                final List<StorageReference> references = new ArrayList<>();

                for(String stringUri : imagesUri){
                    Date now = new Date();
                    String timestampString = String.valueOf(now.getTime()) + "_";
                    StorageReference reference = storage.getReference()
                            .child("post-images/" + timestampString + Uri.parse(stringUri).getLastPathSegment());
                    references.add(reference);
                    Log.i("reference", reference.toString());
                    //String path = Uri.parse(stringUri).getPath();
                    //Log.i("path", path);
                    URI uri = URI.create(stringUri);
                    try{
                        // throws IOException
                        // File compressedFile = new Compressor(this).compressToFile(new File(path));
                        File compressedFile = new Compressor(this).compressToFile(new File(uri));
                        Uri compressedFileUri = Uri.fromFile(compressedFile);
                        UploadTask uploadTask = reference.putFile(compressedFileUri);
//                        UploadTask uploadTask = reference.putFile(Uri.parse(stringUri));
                        uploadTasks.add(uploadTask);

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.i("upload", "running");
                            }
                        });
                        uploadTask.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.i("upload", "paused");
                            }
                        });
                        uploadTask.addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.i("upload", "canceled");
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                // also works : final List<Task> uriTasks = new ArrayList<>();
                final List<Task<Uri>> uriTasks = new ArrayList<>();

                // Tasks.whenAllSuccess(uploadTasks.toArray(new Task[uploadTasks.size()]))
                // alternative
                Tasks.whenAllSuccess(uploadTasks)
                .continueWithTask(new Continuation<List<Object>, Task<List<Uri>>>() {
                    @Override
                    public Task<List<Uri>> then(@NonNull Task<List<Object>> task) throws Exception {
                        Log.i("upload", "successful");
                        for(StorageReference reference : references){
                            uriTasks.add(reference.getDownloadUrl());
                        }
                        // return Tasks.whenAllSuccess(uriTasks.toArray(new Task[uriTasks.size()]));
                        // alternative
                        return Tasks.whenAllSuccess(uriTasks);
                    }
                }).addOnSuccessListener(new OnSuccessListener<List<Uri>>() {
                    @Override
                    public void onSuccess(List<Uri> downloadUris) {
                        List<String> stringDownloadUris = new ArrayList<>();
                        for(Uri downloadUri : downloadUris){
                            Log.i("download-uri", downloadUri.toString());
                            stringDownloadUris.add(downloadUri.toString());
                        }
                        postModel.setImages(stringDownloadUris);

                        WriteBatch batch = db.batch();
                        List<Task> postTasks = new ArrayList<>();
                        List<String> dishFeedbackLinks = new ArrayList<>();
                        for(DishFeedback dishFeedback : dishFeedbacks){
                            DocumentReference documentReference = db.collection("feedbacks").document();
                            Log.i("dish-feedback", documentReference.getId());
                            dishFeedbackLinks.add(documentReference.getId());
                            FeedbackModel feedback = getFeedback(dishFeedback, FeedbackTypes.DISH_FEEDBACK);
//                            postTasks.add(documentReference.set(feedback));
                            batch.set(documentReference, feedback);
                        }
                        postModel.setFeedbacks(dishFeedbackLinks);

                        DocumentReference documentReference = db.collection("feedbacks").document();
                        Log.i("rest-feedback", documentReference.getId());
                        FeedbackModel feedback = getFeedback(restaurantFeedback, FeedbackTypes.RESTAURANT_FEEDBACK);
//                        postTasks.add(documentReference.set(feedback));
                        batch.set(documentReference, feedback);
                        postModel.setRestaurantFeedback(documentReference.getId());

                        final DocumentReference postReference = db.collection("posts").document();
                        Log.i("post", postReference.getId());
                        batch.set(postReference, postModel);
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.i("post-creation", "successful");
                                    addNewPostActivity(postReference.getId());
                                }else{
                                    Log.i("post-creation", "failed");
                                    Intent intent = new Intent(CreatePostPreview.this, home.class);
                                    intent.putExtra("source", SourceHomePage.POST_CREATION_FAILED);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                })
//                        .continueWithTask(new Continuation<List<Uri>, Task<Task>>() {
//                    @Override
//                    public Task<Task> then(@NonNull Task<List<Uri>> task) throws Exception {
//                        List<Uri> downloadUris = task.getResult();
//                        List<String> stringDownloadUris = new ArrayList<>();
//                        for(Uri downloadUri : downloadUris){
//                            Log.i("download-uri", downloadUri.toString());
//                            stringDownloadUris.add(downloadUri.toString());
//                        }
//                        postModel.setImages(stringDownloadUris);
//                        return null;
//                    }
//                })
                        .addOnFailureListener(CreatePostPreview.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("image-upload", "failed");
                        // TODO maybe delete the StorageReferences
                        Intent intent = new Intent(CreatePostPreview.this, home.class);
                        intent.putExtra("source", SourceHomePage.POST_CREATION_FAILED);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private FeedbackModel getFeedback(Serializable data, int type){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FeedbackModel feedback = new FeedbackModel();
        feedback.setTimestamp(new Timestamp(new Date()));
        // Map<String, String> whoMap = new HashMap<>();
        // whoMap.put("l", currentUserLink);
        if(type == FeedbackTypes.DISH_FEEDBACK){
            DishFeedback dishFeedback = (DishFeedback)data;
            feedback.setRating(dishFeedback.rating);
            if(!TextUtils.isEmpty(dishFeedback.review)){
                feedback.setHasReview(true);
                feedback.setReview(dishFeedback.review);
            }
            feedback.setType(FeedbackTypes.DISH_FEEDBACK);
            // feedback.setWho(whoMap);
            feedback.setWho(currentUserLink);
            feedback.setWhere(dishFeedback.link);
        }else{
            RestaurantFeedback restaurantFeedback = (RestaurantFeedback) data;
            feedback.setRating(restaurantFeedback.rating);
            if(!TextUtils.isEmpty(restaurantFeedback.review)){
                feedback.setHasReview(true);
                feedback.setReview(restaurantFeedback.review);
            }
            feedback.setType(FeedbackTypes.RESTAURANT_FEEDBACK);
            // feedback.setWho(whoMap);
            feedback.setWho(currentUserLink);
            feedback.setWhere(restaurantFeedback.link);
        }

        return feedback;
    }

    private void addNewPostActivity(String postLink){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> newActivity = new HashMap<>();
        Map<String, Object> who = new HashMap<>();

        who.put("l", currentUserLink);

        newActivity.put("t", 0);
        newActivity.put("w", who);
        newActivity.put("wh", postLink);

        FirebaseFirestore.getInstance().collection("activities").add(newActivity)
                .addOnSuccessListener(CreatePostPreview.this, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                        Intent intent = new Intent(CreatePostPreview.this, home.class);
                        intent.putExtra("source", SourceHomePage.POST_CREATION_SUCCESSFUL);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(CreatePostPreview.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Intent intent = new Intent(CreatePostPreview.this, home.class);
                        intent.putExtra("source", SourceHomePage.POST_CREATION_FAILED);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
    }

}
