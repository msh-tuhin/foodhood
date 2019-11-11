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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChangeRestCoverPhoto extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    private String mPersonOrRestaurantLink;
    private String mCollectonName;
    private int mEntity;
    Uri uploadUri = null;
    String photoPath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Toolbar toolbar;
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

        toolbar.setTitle("Cover Photo");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        mEntity = getIntent().getIntExtra("entity", AccountTypes.PERSON);
        mCollectonName = mEntity==AccountTypes.PERSON ? "person_vital":"rest_vital";

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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                // choose only from local images
                // not working!
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_CHOOSE_REQUEST_CODE);
                }
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUri = null;
                coverPhotoIV.setImageURI(null);
                deleteImage.setClickable(false);
                imageSourceChooser.setVisibility(View.VISIBLE);
                saveButton.setEnabled(false);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoAndAddUrlToDB(uploadUri);
                frameLayoutImage.setVisibility(View.INVISIBLE);
                saveButton.setVisibility(View.INVISIBLE);
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

                    imageSourceChooser.setVisibility(View.INVISIBLE);
                    deleteImage.setClickable(true);
                    saveButton.setEnabled(true);
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
                    frameLayoutImage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressTV.setVisibility(View.INVISIBLE);
                    saveButton.setEnabled(false);
                    saveButton.setVisibility(View.VISIBLE);
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
                    updateVital(uri);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();
                    return user.updateProfile(profileUpdates);
                }
            }).addOnSuccessListener(ChangeRestCoverPhoto.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("photo_uri", "updated");
                }
            }).addOnFailureListener(ChangeRestCoverPhoto.this, new OnFailureListener() {
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

    private void updateVital(Uri uri){
        Map<String, Object> restOrPersonVital = new HashMap<>();
        restOrPersonVital.put("cp", uri.toString());

        db.collection(mCollectonName)
                .document(mPersonOrRestaurantLink).update(restOrPersonVital)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("vital_info_update", e.getMessage());
                    }
                });
    }

    private void bindImage(DocumentSnapshot vitalSnapshot){
        if(vitalSnapshot == null) return;
        String coverPhotoLink = vitalSnapshot.getString("cp");
        if(coverPhotoLink == null || coverPhotoLink.equals("")){
            deleteImage.setVisibility(View.VISIBLE);
            imageSourceChooser.setVisibility(View.VISIBLE);
        }else{
            Picasso.get().load(coverPhotoLink)
                    .placeholder(R.drawable.gray)
                    .error(R.drawable.gray)
                    .into(coverPhotoIV);
            deleteImage.setVisibility(View.VISIBLE);
        }
    }
}
