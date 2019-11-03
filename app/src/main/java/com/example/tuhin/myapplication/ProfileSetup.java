package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;
import models.PersonInfo;

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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// expects to receive a bundle with profile picture url
// and some vital personal information provided by the user
// checks which of the expected data are received and
// make changes to the database accordingly
public class ProfileSetup extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();;
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

        Log.i("new_user_name", newUserName);
        Log.i("new_user_email", newUserEmail);
        Log.i("new_user_uid", newUserUid);
        if(newUserPhone != null) Log.i("new_user_phone", newUserPhone);
        if(newUserCurrentTown != null) Log.i("new_user_town", newUserCurrentTown);
        if(photoPath != null) Log.i("new_user_photo_path", photoPath);

        Uri uploadUri = compressImage(photoPath);
        uploadPhotoAndAddUrlToDB(uploadUri);

        addPersonInfo();
        setupProfileCloud();
        // this is done from cloud side too
        // but it takes a bit of time to finish in cloud
        // that's why it is also done here, maybe not really needed
        markProfileCreated();
    }

    private void uploadPhotoAndAddUrlToDB(Uri uploadUri){
        if(uploadUri != null){
            Log.i("upload_uri", "not null");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference()
                    .child("profile-pictures").child(uploadUri.getLastPathSegment());
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
            }).continueWithTask(new Continuation<Uri, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<Uri> task) throws Exception {
                    Uri uri = task.getResult();
                    Log.i("download_uri", uri.toString());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user == null){
                        throw new Exception("User null");
                    }
                    Log.i("current_user", user.getEmail());
                    addPersonVital(uri);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();
                    return user.updateProfile(profileUpdates);
                }
            }).addOnSuccessListener(ProfileSetup.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("photo_uri", "updated");
                    Intent intent = new Intent(ProfileSetup.this, home.class);
                    startActivity(intent);
                    ProfileSetup.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // TODO
                    Log.e("error", e.getMessage());
                }
            });
        } else {
            Log.i("upload_uri", "null");
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

    private void addPersonVital(Uri photoUri){
        String newUserUid = mAuth.getCurrentUser().getUid();
        String newUserName = mAuth.getCurrentUser().getDisplayName();

        Map<String, Object> personVital = new HashMap<>();
        personVital.put("n", newUserName);
        personVital.put("pp", photoUri.toString());
        Log.i("new_user", newUserUid);

        db.collection("person_vital").document(newUserUid).set(personVital)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("person_vital", e.getMessage());
                    }
                });
    }

    private void addPersonInfo(){
        String newUserUid = mAuth.getCurrentUser().getUid();
        String newUserEmail = mAuth.getCurrentUser().getEmail();
        String newUserPhone = personDatabundle.getString("phone");

        PersonInfo personInfo = new PersonInfo();
        personInfo.setEmail(newUserEmail);
        if(newUserPhone != null){
            personInfo.setPhone(newUserPhone);
        }
        db.collection("person_info").document(newUserUid).set(personInfo)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("person_info", e.getMessage());
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
        data.put("a", false);
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
