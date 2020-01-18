package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;
import models.PersonInfo;
import myapp.utils.SourceHomePage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


// expects to receive a bundle with profile picture url
// and some vital personal information provided by the user
// checks which of the expected data are received and
// make changes to the database accordingly
public class ProfileSetup extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    Bundle personDatabundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        personDatabundle = getIntent().getExtras();

        currentUser = mAuth.getCurrentUser();
        String newUserName = currentUser.getDisplayName();
        String newUserEmail = currentUser.getEmail();
        String newUserUid = currentUser.getUid();
        String newUserPhone = personDatabundle.getString("phone");
        String newUserCurrentTown = personDatabundle.getString("current_town");
        String photoPath = personDatabundle.getString("photo_path");

        if(newUserName != null) Log.i("new_user_name", newUserName);
        if(newUserEmail != null) Log.i("new_user_email", newUserEmail);
        Log.i("new_user_uid", newUserUid);
        if(newUserPhone != null) Log.i("new_user_phone", newUserPhone);
        if(newUserCurrentTown != null) Log.i("new_user_town", newUserCurrentTown);
        if(photoPath != null) Log.i("new_user_photo_path", photoPath);

        addPersonVital();
        setupProfileCloud();
        // this is done from cloud side too
        // but it takes a bit of time to finish in cloud
        // that's why it is also done here, maybe not really needed
        markProfileCreated();

        Uri uploadUri = compressImage(photoPath);
        uploadPhotoAndAddUrlToDB(uploadUri);
    }

    private void uploadPhotoAndAddUrlToDB(Uri uploadUri){
        if(uploadUri != null){
            Log.i("upload_uri", "not null");
            Date now = new Date();
            String timestampString = String.valueOf(now.getTime()) + "_";
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference()
                    .child("profile-pictures").child(timestampString + uploadUri.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(uploadUri);

            uploadTask.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.i("upload", "canceled");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("upload", "paused");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("upload", "running");
                }
            });

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            })
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    addPhotoUriToPersonVital(uri);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Log.i("current_user", user.getEmail());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();
                    user.updateProfile(profileUpdates);
                    Intent intent = new Intent(ProfileSetup.this, home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Intent intent = new Intent(ProfileSetup.this, home.class);
                    intent.putExtra("source", SourceHomePage.PHOTO_UPLOAD_FAILED);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        } else {
            Log.i("upload_uri", "null");
            Intent intent = new Intent(ProfileSetup.this, home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private Uri compressImage(String path){
        try{
            File compressedFile = new Compressor(this).compressToFile(new File(path));
            Uri compressedFileUri = Uri.fromFile(compressedFile);
            Log.i("compressed_uri", compressedFileUri.toString());
            return compressedFileUri;
        }catch(IOException e){
            Log.i("compression_error", e.getMessage());
            return null;
        }catch (Exception e){
            return null;
        }
    }

    private void addPersonVital(){
        String newUserUid = mAuth.getCurrentUser().getUid();
        String newUserName = mAuth.getCurrentUser().getDisplayName();
        String newUserEmail = mAuth.getCurrentUser().getEmail();
        String newUserPhone = personDatabundle.getString("phone");

        Map<String, Object> personVital = new HashMap<>();
        if(newUserName!=null){
            personVital.put("n", newUserName);
        }
        if(newUserEmail!=null){
            personVital.put("e", newUserEmail);
        }
        if(newUserPhone != null){
            personVital.put("p", newUserPhone);
        }
        Log.i("new_user", newUserUid);

        db.collection("person_vital").document(newUserUid)
                .set(personVital, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("person_vital", e.getMessage());
                    }
                });
    }

    private void addPhotoUriToPersonVital(Uri uri){
        if(uri==null) return;
        Map<String, Object> personVital = new HashMap<>();
        personVital.put("pp", uri.toString());
        db.collection("person_vital").document(mAuth.getCurrentUser().getUid())
                .set(personVital, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("person_vital", e.getMessage());
                    }
                });
    }

    private void setupProfileCloud(){
        String newUserUid = mAuth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("uid", newUserUid);
        FirebaseFunctions.getInstance().getHttpsCallable("setupProfile").call(data)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("function_call", e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Log.i("function_call", "successful");
                    }
                });
    }

    private void markProfileCreated(){
        Map<String, Object> data = new HashMap<>();
        data.put("a", true);
        db.collection("profile_created")
                .document(mAuth.getCurrentUser().getUid())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("profile_creation", "marked as done");
                    }
                });
    }

}
