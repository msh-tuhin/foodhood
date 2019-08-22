package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import id.zelory.compressor.Compressor;
import myapp.utils.RealPathUtil;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class SetProfilePicture extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    Uri uri = null;
    Toolbar toolbar;
    ImageButton captureImage, chooseFrom, deleteImage;
    ConstraintLayout imageSourceChooser;
    ImageView profilePicture;
    TextView skipOrNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_picture);

        toolbar = findViewById(R.id.toolbar);
        captureImage = findViewById(R.id.camera);
        chooseFrom = findViewById(R.id.gallery);
        deleteImage = findViewById(R.id.delete_image);
        imageSourceChooser = findViewById(R.id.image_source_chooser);
        profilePicture = findViewById(R.id.profile_picture);
        skipOrNext = findViewById(R.id.skip_or_next);

        toolbar.setTitle("Food Frenzy");
        setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("Permission", "External storage : denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_READ_PERM);
        }else{
            // permission granted
            // TODO maybe nothing
        }

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
                uri = null;
                profilePicture.setImageURI(null);
                deleteImage.setClickable(false);
                imageSourceChooser.setVisibility(View.VISIBLE);
                skipOrNext.setText("Skip");
            }
        });

        skipOrNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO upload photo
                // TODO launch next activity

                Log.i("skip_next", "clicked");
            }
        });

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
                uri = Uri.fromFile(new File(path));
                Log.i("Uri", uri.toString());
                try{
                    File compressedFile = new Compressor(this).compressToFile(new File(path));
                    Uri compressedFileUri = Uri.fromFile(compressedFile);
                    Log.i("compressed_uri", compressedFileUri.toString());
                    profilePicture.setImageURI(compressedFileUri);
                }catch (IOException e){
                    Log.e("error", e.getMessage());
                    profilePicture.setImageURI(uri);
                }
                imageSourceChooser.setVisibility(View.INVISIBLE);
                deleteImage.setClickable(true);
                skipOrNext.setText("Next");

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
}
