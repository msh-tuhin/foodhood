package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLEngineResult;

public class CreateRestFeedPreview extends AppCompatActivity {

    Bundle post;
    String caption;
    ArrayList<String> imagesUri;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_rest_feed_preview);

        post = getIntent().getExtras();
        caption = post.getString("caption");
        imagesUri =  post.getStringArrayList("imageSringUris");

        progressBar = findViewById(R.id.progressBar);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Map<String, Object> restFeedMap = new HashMap<>();
        Map<String, String> who = new HashMap<>();
        who.put("l", currentUserLink);

        restFeedMap.put("ts", new Timestamp(new Date()));
        restFeedMap.put("w", who);
        restFeedMap.put("c", caption);
        restFeedMap.put("l", new ArrayList<String>());
        restFeedMap.put("coms", new ArrayList<String>());
        restFeedMap.put("cb", new ArrayList<String>());

        List<UploadTask> uploadTasks = new ArrayList<>();
        final List<StorageReference> references = new ArrayList<>();

        for(String stringUri : imagesUri){
            StorageReference reference = storage.getReference()
                    .child("post-images/" + Uri.parse(stringUri).getLastPathSegment());
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

                uploadTask.addOnProgressListener(CreateRestFeedPreview.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                restFeedMap.put("i", stringDownloadUris);
                final DocumentReference newRestFeedRef = db.collection("rest_feed").document();
                newRestFeedRef.set(restFeedMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("rest-feed-creation", "successful");
                                addNewRestFeedActivity(newRestFeedRef.getId());
                                CreateRestFeedPreview.this.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("rest-feed-creation", "failed");
                                CreateRestFeedPreview.this.finish();
                            }
                        });
            }
        });
    }

    private void addNewRestFeedActivity(String restFeedLink){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        Map<String, Object> newActivity = new HashMap<>();
        newActivity.put("t", 2);
        newActivity.put("w", who);
        newActivity.put("wh", restFeedLink);

        FirebaseFirestore.getInstance().collection("activities").add(newActivity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                    }
                });
    }
}
