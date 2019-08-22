package com.example.tuhin.myapplication;

import android.net.Uri;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import id.zelory.compressor.Compressor;
import models.DishFeedback;
import models.PostModel;
import models.RestaurantFeedback;
import models.SelectedPerson;
import models.FeedbackModel;
import myapp.utils.FeedbackTypes;
import myapp.utils.ImagesAdapter;

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
    TextView captionTextView, dishOrRestaurantNameTextView, reviewTextView;
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
        captionTextView = findViewById(R.id.caption);
        captionHeaderTextView = findViewById(R.id.caption_header);
        imagesHeaderTextView = findViewById(R.id.images_header);
        peopleHeaderTextView = findViewById(R.id.people_header);
        viewPager = findViewById(R.id.viewPager);
        dishOrRestaurantNameTextView = findViewById(R.id.dish_name);
        reviewTextView = findViewById(R.id.review);
        ratingBar = findViewById(R.id.dish_ratingBar);
        dishReviewsLinearLayout = findViewById(R.id.dish_reviews);
        mainLinearLayout = findViewById(R.id.main_layout);
        taggedPeopleLayout = findViewById(R.id.tagged_people_layout);

        toolbar.setTitle("Post Preview");
        setSupportActionBar(toolbar);

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
            adapter = new ImagesAdapter(this);
            for(String uriString : imagesUri){
                adapter.imageUris.add(Uri.parse(uriString));
            }
            viewPager.setAdapter(adapter);
        }

        dishOrRestaurantNameTextView.setText(restaurantFeedback.name);
        ratingBar.setRating(restaurantFeedback.rating);
        ratingBar.setIsIndicator(true);
        reviewTextView.setText(restaurantFeedback.review);

        for(DishFeedback dishFeedback : dishFeedbacks){
            View view = LayoutInflater.from(this).inflate(R.layout.feedback_preview, null);
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
                ((TextView) view.findViewById(R.id.restaurant_name)).setText(selectedPerson.name);
                taggedPeopleLayout.addView(view);
            }
        }

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

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();

                final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final PostModel postModel = new PostModel();

                Map<String, String> who = new HashMap<>();
                who.put("l", currentUserLink);
                postModel.setWho(who);

                postModel.setCaption(caption);
                postModel.setComments(new ArrayList<String>());
                postModel.setCommentsBy(new ArrayList<String>());
                postModel.setLikes(new ArrayList<String>());

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
                    StorageReference reference = storage.getReference()
                            .child("post-images/" + Uri.parse(stringUri).getLastPathSegment());
                    references.add(reference);
                    Log.i("reference", reference.toString());
                    String path = Uri.parse(stringUri).getPath();
                    Log.i("path", path);
                    try{
                        // throws IOException
                        File compressedFile = new Compressor(this).compressToFile(new File(path));
                        Uri compressedFileUri = Uri.fromFile(compressedFile);
                        UploadTask uploadTask = reference.putFile(compressedFileUri);
//                        UploadTask uploadTask = reference.putFile(Uri.parse(stringUri));
                        uploadTasks.add(uploadTask);

                        uploadTask.addOnProgressListener(CreatePostPreview.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("image-upload", "failed");
                        // TODO maybe delete the StorageReferences
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
        if(type == FeedbackTypes.DISH_FEEDBACK){
            DishFeedback dishFeedback = (DishFeedback)data;
            feedback.setRating(dishFeedback.rating);
            if(!TextUtils.isEmpty(dishFeedback.review)){
                feedback.setHasReview(true);
                feedback.setReview(dishFeedback.review);
            }
            feedback.setType(FeedbackTypes.DISH_FEEDBACK);
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
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                    }
                });
    }
}
