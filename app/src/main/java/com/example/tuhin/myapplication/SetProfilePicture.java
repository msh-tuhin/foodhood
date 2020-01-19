package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;

public class SetProfilePicture extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Uri uploadUri = null;
    String photoPath;
    Toolbar toolbar;
    LinearLayout addImageLayout;
    ImageButton addImageButton;
    ImageButton captureImage, chooseFrom, deleteImage;
    ConstraintLayout imageSourceChooser;
    ImageView profilePicture;
    TextView skipOrNext;
    Bundle personDataBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_picture);

        toolbar = findViewById(R.id.toolbar);
        addImageLayout = findViewById(R.id.add_image_layout);
        addImageButton = findViewById(R.id.add_image);
        captureImage = findViewById(R.id.camera);
        chooseFrom = findViewById(R.id.gallery);
        deleteImage = findViewById(R.id.delete_image);
        imageSourceChooser = findViewById(R.id.image_source_chooser);
        profilePicture = findViewById(R.id.profile_picture);
        skipOrNext = findViewById(R.id.skip_or_next);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        // ActionBar actionBar = getSupportActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

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
                profilePicture.setImageResource(R.drawable.ltgray);
                deleteImage.setClickable(false);
                deleteImage.setVisibility(View.INVISIBLE);
                //imageSourceChooser.setVisibility(View.VISIBLE);
                addImageLayout.setVisibility(View.VISIBLE);
                skipOrNext.setText("Skip");
            }
        });

        skipOrNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("skip_next", "clicked");
                Intent intent = new Intent(SetProfilePicture.this, SetPhone.class);
                if(((TextView)v).getText() == "Next"){
                    personDataBundle.putString("photo_path", photoPath);
                }
                intent.putExtras(personDataBundle);
                startActivity(intent);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(SetProfilePicture.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_only_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
                    profilePicture.setImageURI(compressedFileUri);

                    //imageSourceChooser.setVisibility(View.INVISIBLE);
                    addImageLayout.setVisibility(View.INVISIBLE);
                    deleteImage.setClickable(true);
                    deleteImage.setVisibility(View.VISIBLE);
                    skipOrNext.setText("Next");
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
