package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;
import myapp.utils.AccountTypes;
import myapp.utils.RealPathUtil;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChangeRestCoverPhoto extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    private String mPersonOrRestaurantLink;
    private String mCollectonName;
    private String mChangeable;
    Uri uploadUri = null;
    String photoPath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean imageChanged = false;
    private boolean imagePrevious = false;
    private boolean imageCurrent = false;
    private String oldCoverPhotoLink = "";

    Toolbar toolbar;
    LinearLayout addImageLayout;
    ImageButton addImageButton;
    ImageButton captureImage;
    ImageButton chooseFrom;
    ImageButton deleteImage;
    ConstraintLayout imageSourceChooser;
    ImageView coverPhotoIV;
    Button saveButton;
    ProgressBar progressBar;
    TextView progressTV;
    FrameLayout frameLayoutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_rest_cover_photo);

        toolbar = findViewById(R.id.toolbar);
        addImageLayout = findViewById(R.id.add_image_layout);
        addImageButton = findViewById(R.id.add_image);
        captureImage = findViewById(R.id.camera);
        chooseFrom = findViewById(R.id.gallery);
        deleteImage = findViewById(R.id.delete_image);
        imageSourceChooser = findViewById(R.id.image_source_chooser);
        coverPhotoIV = findViewById(R.id.cover_photo);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progressBar);
        progressTV = findViewById(R.id.progress_text);
        frameLayoutImage = findViewById(R.id.framelayout_image);

        mPersonOrRestaurantLink = mAuth.getCurrentUser().getUid();

        toolbar.setTitle("Change Photo");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        mChangeable = getIntent().getStringExtra("changeable");
        mCollectonName = mChangeable.equals("restaurant_cover_pic") ? "rest_vital":"person_vital";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("Permission", "External storage : denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_READ_PERM);
        }else{
            // permission granted
            // TODO maybe nothing
        }

        db.collection(mCollectonName)
                .document(mPersonOrRestaurantLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot vitalSnapshot) {
                        if(vitalSnapshot.exists()){
                            bindImage(vitalSnapshot);
                        }
                    }
                });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Camera", "Capture Image");
                // TODO implement image capture by camera
            }
        });

        chooseFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Gallery", "Choose From Here");
                sendIntentForImage();

            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntentForImage();
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUri = null;
                coverPhotoIV.setImageResource(R.drawable.ltgray);
                deleteImage.setClickable(false);
                deleteImage.setVisibility(View.INVISIBLE);
                //imageSourceChooser.setVisibility(View.VISIBLE);
                addImageLayout.setVisibility(View.VISIBLE);
                imageChanged = true;
                imageCurrent = false;
                enableOrDisableSaveButton();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setEnabled(false);
                if(imagePrevious && !imageCurrent){
                    deletePhotoAndUpdateDB();
                }else{
                    uploadPhotoAndAddUrlToDB(uploadUri);
                }
                frameLayoutImage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressTV.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // return super.onSupportNavigateUp();
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri contentUri;
        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == IMAGE_CHOOSE_REQUEST_CODE){
                if(data.getClipData() != null){
                    Log.i("image-source", "clipdata");
                    ClipData clipData = data.getClipData();
                    ClipData.Item item = clipData.getItemAt(0);
                    contentUri = item.getUri();
                    Log.i("Content-Uri", contentUri.toString());
                }else {
                    Log.i("image-source", "data");
                    contentUri = data.getData();
                    Log.i("Content-Uri", contentUri.toString());
                }
                String path = getPathFromContentUri(contentUri);
                photoPath = path;
                Uri uri = Uri.fromFile(new File(path));
                Log.i("Uri", uri.toString());

                try{
                    File compressedFile = new Compressor(this).compressToFile(new File(path));
                    Uri compressedFileUri = Uri.fromFile(compressedFile);
                    Log.i("compressed_uri", compressedFileUri.toString());
                    uploadUri = compressedFileUri;
                    coverPhotoIV.setImageURI(compressedFileUri);

                    //imageSourceChooser.setVisibility(View.INVISIBLE);
                    addImageLayout.setVisibility(View.INVISIBLE);
                    deleteImage.setClickable(true);
                    deleteImage.setVisibility(View.VISIBLE);
                    imageChanged = true;
                    imageCurrent = true;
                    enableOrDisableSaveButton();
                }catch (IOException e){

                    // TODO handle the unsuccessful image compression
                    // TODO maybe show a dialog(cant compress the selected image)

                    Log.e("compression-error", e.getMessage());
                    // profilePicture.setImageURI(uri);
                }

                String msg = data.getClipData() != null ? "not null" : "null";
                Log.i("CLIPDATA", msg);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE_READ_PERM){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // permission granted
                // TODO maybe nothing
            }else{
                // permission denied
                // TODO launch next activity
            }
        }
    }

    private String getPathFromContentUri(Uri contentUri){
        String path = RealPathUtil.getRealPath(this, contentUri);
        Log.i("PATH", path);
        return path;
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
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(imagePrevious && imageCurrent){
                        FirebaseStorage.getInstance().getReferenceFromUrl(oldCoverPhotoLink)
                                .delete()
                                .addOnCompleteListener(ChangeRestCoverPhoto.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.i("image-delete", "successful");
                                        }else{
                                            Log.i("image-delete", "failed");
                                        }
                                    }
                                });
                    }
                    updateVital(uri);
                    if(mChangeable.equals("person_profile_pic") ||
                            mChangeable.equals("restaurant_cover_pic")){
                        updatePhotoUri(uri);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressTV.setVisibility(View.INVISIBLE);
                    frameLayoutImage.setVisibility(View.VISIBLE);
                    Toast.makeText(ChangeRestCoverPhoto.this, "Couldn't upload image!",
                            Toast.LENGTH_LONG).show();
                    enableOrDisableSaveButton();
                }
            });
        } else {
            Log.i("upload_uri", "null");
            progressBar.setVisibility(View.INVISIBLE);
            progressTV.setVisibility(View.INVISIBLE);
            frameLayoutImage.setVisibility(View.VISIBLE);
            Toast.makeText(ChangeRestCoverPhoto.this, "Update Failed!",
                    Toast.LENGTH_LONG).show();
            enableOrDisableSaveButton();
        }
    }

    private void deletePhotoAndUpdateDB(){
        updateVital(null);
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldCoverPhotoLink);
        photoRef.delete()
                .addOnCompleteListener(ChangeRestCoverPhoto.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i("image-delete", "successful");
                        }else{
                            Log.i("image-delete", "failed");
                        }
                    }
                });
        updatePhotoUri(null);
    }

    private void updateVital(Uri uri){
        Map<String, Object> restOrPersonVital = new HashMap<>();
        if(mChangeable.equals("person_profile_pic")){
            if(uri != null){
                restOrPersonVital.put("pp", uri.toString());
            }else{
                restOrPersonVital.put("pp", "");
            }
        }else{
            if(uri != null){
                restOrPersonVital.put("cp", uri.toString());
            }else{
                restOrPersonVital.put("cp", "");
            }
        }

        db.collection(mCollectonName)
                .document(mPersonOrRestaurantLink).update(restOrPersonVital)
                .addOnSuccessListener(ChangeRestCoverPhoto.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ChangeRestCoverPhoto.this.finish();
                    }
                })
                .addOnFailureListener(ChangeRestCoverPhoto.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("vital_info_update", e.getMessage());
//                        progressBar.setVisibility(View.INVISIBLE);
//                        progressTV.setVisibility(View.INVISIBLE);
//                        frameLayoutImage.setVisibility(View.VISIBLE);
//                        Toast.makeText(ChangeRestCoverPhoto.this, "Update Failed!",
//                                Toast.LENGTH_LONG).show();
//                        enableOrDisableSaveButton();
                        ChangeRestCoverPhoto.this.finish();
                    }
                });
    }

    private void updatePhotoUri(Uri uri){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null) return;
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(profileUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("photo_uri", "updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("photo_uri", e.getMessage());
                    }
                });
    }

    private void bindImage(DocumentSnapshot vitalSnapshot){
        if(vitalSnapshot == null) return;
        String coverPhotoLink;
        if(mChangeable.equals("person_profile_pic")){
            coverPhotoLink = vitalSnapshot.getString("pp");
        }else {
            coverPhotoLink = vitalSnapshot.getString("cp");
        }

        if(coverPhotoLink == null || coverPhotoLink.equals("")){
            deleteImage.setVisibility(View.INVISIBLE);
            //imageSourceChooser.setVisibility(View.VISIBLE);
            addImageLayout.setVisibility(View.VISIBLE);
        }else{
            oldCoverPhotoLink = coverPhotoLink;
            imageCurrent = true;
            imagePrevious = true;
            Picasso.get().load(coverPhotoLink)
                    .placeholder(R.drawable.gray)
                    .error(R.drawable.gray)
                    .into(coverPhotoIV);
            deleteImage.setVisibility(View.VISIBLE);
        }
    }

    private void enableOrDisableSaveButton(){
        if(imageChanged){
            saveButton.setEnabled(imagePrevious || imageCurrent);
        }else{
            saveButton.setEnabled(false);
        }
    }

    private void sendIntentForImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // choose only from local images
        // not working!
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if(Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_CHOOSE_REQUEST_CODE);
        }
    }
}
