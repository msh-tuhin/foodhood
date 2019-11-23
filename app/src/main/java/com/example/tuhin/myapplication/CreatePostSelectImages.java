package com.example.tuhin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import myapp.utils.ImagesAdapter;
import myapp.utils.RealPathUtil;


// receives explicit intent with bundle extra(keys="caption":str,)
public class CreatePostSelectImages extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    Toolbar toolbar;
    ImageButton addImagesButton, captureImage, chooseFrom, deleteImage;
    ConstraintLayout imageSourceChooser;
    TextView noImages;
    ViewPager viewPager;
    Button cancel, next;

    Bundle post;
    Uri uri;
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> stringUris = new ArrayList<>();
    ImagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_select_images);

        post = getIntent().getExtras();

        // checking if data received correctly
        String caption = post.getString("caption");
        if(caption!=null)Log.i("post-caption", "not null");
        Log.i("Bundle-Caption", caption);

        toolbar = findViewById(R.id.toolbar);
        addImagesButton = findViewById(R.id.add_images);
        imageSourceChooser = findViewById(R.id.image_source_chooser);
        captureImage = findViewById(R.id.camera);
        chooseFrom = findViewById(R.id.gallery);
        noImages = findViewById(R.id.no_images);
        viewPager = findViewById(R.id.viewPager);
        deleteImage = findViewById(R.id.deleteImage);
        cancel = findViewById(R.id.cancel);
        next = findViewById(R.id.next);

        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        // TODO add api version (>=23) check
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("Permission", "Denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_READ_PERM);
        }else{
            // permission granted
            // TODO maybe nothing
        }

        addImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MORE_IMAGES", "clicked");
                imageSourceChooser.setVisibility(View.VISIBLE);
//                noImages.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                deleteImage.setVisibility(View.GONE);
                deleteImage.setClickable(false);
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
                if(Build.VERSION.SDK_INT >= 18) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_CHOOSE_REQUEST_CODE);
                }
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexToBeDeleted = viewPager.getCurrentItem();
                int viewpagerNewPosition = indexToBeDeleted == adapter.imageUris.size()-1 ? indexToBeDeleted-1 : indexToBeDeleted;
                Log.i("Image-Position", Integer.toString(viewPager.getCurrentItem()));
                adapter.imageUris.remove(indexToBeDeleted);
                stringUris.remove(indexToBeDeleted);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(viewpagerNewPosition);
                addImagesButton.setClickable(true);
                if(adapter.imageUris.size() == 0){
                    viewPager.setVisibility(View.GONE);
                    deleteImage.setVisibility(View.GONE);
                    deleteImage.setClickable(false);
                    imageSourceChooser.setVisibility(View.GONE);
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSourceChooser.setVisibility(View.GONE);
                if(adapter.imageUris.size() != 0){
                    viewPager.setVisibility(View.VISIBLE);
                    deleteImage.setVisibility(View.VISIBLE);
                    deleteImage.setClickable(true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.putStringArrayList("imageSringUris", stringUris);
                Intent intent = new Intent(CreatePostSelectImages.this, CreatePostSelectPlace.class);
                intent.putExtras(post);
                startActivity(intent);
            }
        });

        adapter = new ImagesAdapter(this);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // TODO currently there is no restriction to the number of selected images
        // TODO maybe implement restriction to 3 images

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == IMAGE_CHOOSE_REQUEST_CODE){

                if(data.getClipData() != null){
                    Log.i("image-source", "clipdata");
                    ClipData clipData = data.getClipData();
                    for(int i=0; i<clipData.getItemCount(); i++){
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri contentUri = item.getUri();
                        Log.i("Content-Uri", contentUri.toString());
                        String path = getPathFromContentUri(contentUri);
                        uri = Uri.fromFile(new File(path));
                        Log.i("Uri", uri.toString());
                        try{
                            File compressedFile = new Compressor(this).compressToFile(new File(path));
                            Uri compressedFileUri = Uri.fromFile(compressedFile);
                            Log.i("compressed_uri", compressedFileUri.toString());
                            adapter.imageUris.add(compressedFileUri);
                            stringUris.add(uri.toString());
                        }catch (IOException e){
                            Log.e("error", e.getMessage());
                            Toast.makeText(CreatePostSelectImages.this,
                                    "Had a problem while trying to compress image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Log.i("image-source", "data");
                    Uri contentUri = data.getData();
                    Log.i("Content-Uri", contentUri.toString());
                    String path = getPathFromContentUri(contentUri);
                    uri = Uri.fromFile(new File(path));
                    Log.i("Uri", uri.toString());
                    try{
                        File compressedFile = new Compressor(this).compressToFile(new File(path));
                        Uri compressedFileUri = Uri.fromFile(compressedFile);
                        Log.i("compressed_uri", compressedFileUri.toString());
                        adapter.imageUris.add(compressedFileUri);
                        stringUris.add(uri.toString());
                    }catch (IOException e){
                        Log.e("error", e.getMessage());
                        Toast.makeText(CreatePostSelectImages.this,
                                "Had a problem while trying to compress image", Toast.LENGTH_SHORT).show();
                    }
                    // limiting image selection to 3 images
//                    if(adapter.imageUris.size()==3){
//                        addImagesButton.setClickable(false);
//                    }
                }
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.imageUris.size()-1);
                viewPager.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);
                deleteImage.setClickable(true);

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
