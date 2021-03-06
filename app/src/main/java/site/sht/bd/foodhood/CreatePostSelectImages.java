package site.sht.bd.foodhood;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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

import site.sht.bd.foodhood.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import myapp.utils.ImagesAdapter;
import myapp.utils.PostTypes;
import myapp.utils.RealPathUtil;


// receives explicit intent with bundle extra(keys="caption":str,)
public class CreatePostSelectImages extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    Toolbar toolbar;
    ImageButton addImagesButton, captureImage, chooseFrom, deleteImage;
    ConstraintLayout imageSourceChooser;
    TextView noImages;
    TextView limitationCountTV;
    ViewPager viewPager;
    Button cancel, next;

    int postType;
    Bundle post;
    Uri uri;
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> stringUris = new ArrayList<>();
    ImagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_select_images);

        postType = getIntent().getIntExtra("post_type", PostTypes.POST);
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
        limitationCountTV = findViewById(R.id.limitation_count);
        viewPager = findViewById(R.id.viewPager);
        deleteImage = findViewById(R.id.deleteImage);
        cancel = findViewById(R.id.cancel);
        next = findViewById(R.id.next);

        if(postType==PostTypes.REST_FEED){
            //next.setEnabled(false);
        }

        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        // TODO add api version (>=23) check
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("Permission", "Denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_READ_PERM);
        }else{
            // permission granted
            // TODO maybe nothing
        }

        if(postType==PostTypes.REST_FEED){
            next.setText("Post");
        }

        addImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MORE_IMAGES", "clicked");
                // imageSourceChooser.setVisibility(View.VISIBLE);
//                noImages.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                deleteImage.setVisibility(View.GONE);
                deleteImage.setClickable(false);
                sendIntentForImage();
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

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexToBeDeleted = viewPager.getCurrentItem();
                int viewpagerNewPosition = indexToBeDeleted == adapter.imageUris.size()-1 ? indexToBeDeleted-1 : indexToBeDeleted;
                Log.i("Image-Position", Integer.toString(viewPager.getCurrentItem()));
                adapter.imageUris.remove(indexToBeDeleted);
                stringUris.remove(indexToBeDeleted);
                //enableDisableNextButton();
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(viewpagerNewPosition);
                addImagesButton.setClickable(true);
                limitationCountTV.setText(Integer.toString(adapter.imageUris.size())+"/3");
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
                Intent intent;
                if(postType==PostTypes.POST){
                    intent = new Intent(CreatePostSelectImages.this, CreatePostSelectPlace.class);
                }else{
                    intent = new Intent(CreatePostSelectImages.this, CreateRestFeedPreview.class);
                }
                intent.putExtras(post);
                startActivity(intent);
            }
        });

        adapter = new ImagesAdapter(this);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                            //enableDisableNextButton();
                            limitationCountTV.setText(Integer.toString(adapter.imageUris.size())+"/3");
                            if(adapter.imageUris.size()==3){
                               addImagesButton.setClickable(false);
                               break;
                            }
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
                        //enableDisableNextButton();
                        limitationCountTV.setText(Integer.toString(adapter.imageUris.size())+"/3");
                        if(adapter.imageUris.size()==3){
                            addImagesButton.setClickable(false);
                        }
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

    private void enableDisableNextButton(){
        if(postType==PostTypes.POST) return;
        if(stringUris.size()>0){
            next.setEnabled(true);
        }else{
            next.setEnabled(false);
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
