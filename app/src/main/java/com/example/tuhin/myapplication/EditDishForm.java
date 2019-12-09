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
import myapp.utils.EditDishFormSource;
import myapp.utils.NullStrings;
import myapp.utils.PictureBinder;
import myapp.utils.RealPathUtil;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditDishForm extends AppCompatActivity {

    final int IMAGE_CHOOSE_REQUEST_CODE = 1;
    final int REQUEST_EXTERNAL_STORAGE_READ_PERM = 1;

    Toolbar toolbar;
    LinearLayout formLayout;
    LinearLayout progressLayout;
    LinearLayout nameLinearLayout;
    TextInputEditText nameEditText;
    TextInputEditText descriptionEditText;
    TextInputLayout priceTextInputLayout;
    TextInputEditText priceEditText;
    ImageView enableNameEditIV;
    ImageView enableDescriptionEditIV;
    ImageView enablePriceEditIV;
    ConstraintLayout imageSourceChooser;
    ImageButton captureImage;
    ImageButton chooseFrom;
    ImageButton deleteImage;
    ImageView coverPhotoIV;
    Spinner dishCategorySpinner;
    Button saveButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mDishlink;
    private Uri uploadUri = null;
    private int mSource;
    private String oldName = "";
    private String mName = "";
    private String oldDescription = "";
    private String mDescription = "";
    private boolean imagePrevious = false;
    private boolean imageCurrent = false;
    private boolean imageChanged = false;
    private Double oldPrice = -1.0;
    private Double mPrice = -1.0;
    private String oldCoverPhotoLink = "";
    private String oldCategory = NullStrings.NULL_CATEGORY_STRING;
    private String mCategory = NullStrings.NULL_CATEGORY_STRING;
    private  SaveButtonController saveButtonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dish_form);

        mSource = getIntent().getIntExtra("source", EditDishFormSource.NEW_DISH);
        mDishlink = getIntent().getStringExtra("dishLink");

        toolbar = findViewById(R.id.toolbar);
        formLayout = findViewById(R.id.form_layout);
        progressLayout = findViewById(R.id.progress_layout);
        nameLinearLayout = findViewById(R.id.add_name_layout);
        nameEditText = findViewById(R.id.name);
        descriptionEditText = findViewById(R.id.description);
        priceEditText = findViewById(R.id.price);
        priceTextInputLayout = findViewById(R.id.price_textinputlayout);
        enableNameEditIV = findViewById(R.id.enable_name_edit);
        enableDescriptionEditIV = findViewById(R.id.enable_description_edit);
        enablePriceEditIV = findViewById(R.id.enable_price_edit);
        captureImage = findViewById(R.id.camera);
        chooseFrom = findViewById(R.id.gallery);
        deleteImage = findViewById(R.id.delete_image);
        imageSourceChooser = findViewById(R.id.image_source_chooser);
        coverPhotoIV = findViewById(R.id.cover_photo);
        saveButton = findViewById(R.id.save_button);
        dishCategorySpinner = findViewById(R.id.food_category_spinner);

        String title = mSource==EditDishFormSource.NEW_DISH ? "Add Dish": "Edit Dish";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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

        if(mSource==EditDishFormSource.NEW_DISH){
            nameLinearLayout.setVisibility(View.VISIBLE);
            bindCategorySpinner(null);
        }

        if(mSource==EditDishFormSource.EDIT_DISH){
            bindData();
        }

        saveButtonController = new SaveButtonController(saveButton, mSource);

        enableNameEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setEnabled(true);
                nameEditText.requestFocus();
            }
        });

        enableDescriptionEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionEditText.setEnabled(true);
                descriptionEditText.requestFocus();
            }
        });

        enablePriceEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEditText.setEnabled(true);
                priceEditText.requestFocus();
            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mName = s.toString().trim();
                saveButtonController.shouldNameBeSaved = shouldNameBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDescription = s.toString().trim();
                saveButtonController.shouldDescriptionBeSaved = shouldDescriptionBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }
        });

        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String priceString = s.toString().trim();
                try{
                    mPrice = Double.valueOf(priceString);
                    saveButtonController.shouldPriceBeSaved = shouldPriceBeSaved();
                    priceTextInputLayout.setError(null);
                }catch (Exception e){
                    priceTextInputLayout.setError("Please enter a valid price");
                    saveButtonController.shouldPriceBeSaved = false;
                }
                saveButtonController.enableOrDisableSaveButton();
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
                deleteImage.setVisibility(View.INVISIBLE);
                imageSourceChooser.setVisibility(View.VISIBLE);
                imageChanged = true;
                imageCurrent = false;
                saveButtonController.shouldCoverPhotoBeSaved = shoulCoverPhotoBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("saving", "clicked");
                saveButton.setEnabled(false);
                if(mSource==EditDishFormSource.NEW_DISH){
                    Log.i("saving", "new_dish");
                    if(shoulCoverPhotoBeSaved()){
                        Log.i("saving", "with_photo");
                        uploadPhotoAndUpdateDB(uploadUri, true);
                    }else{
                        Log.i("saving", "without_photo");
                        createVital(null);
                    }
                }else{
                    Log.i("updating", "dish");
                    if(shoulCoverPhotoBeSaved()){
                        if(!imageCurrent && imagePrevious){
                            deletePhotoAndUpdateDB();
                        }else{
                            Log.i("updating", "with_photo");
                            uploadPhotoAndUpdateDB(uploadUri, false);
                        }
                    }else{
                        Log.i("updating", "without_photo");
                        updateVital(null);
                    }
                }
//                nameEditText.setEnabled(false);
//                priceEditText.setEnabled(false);
//                descriptionEditText.setEnabled(false);
                formLayout.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
//                oldName = mName;
//                oldPrice = mPrice;
//                oldDescription = mDescription;
//                imagePrevious = imageCurrent;
//                imageCurrent = false;
//                imageChanged = false;
//                saveButtonController.refresh();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                Uri uri = Uri.fromFile(new File(path));
                Log.i("Uri", uri.toString());

                try{
                    File compressedFile = new Compressor(this).compressToFile(new File(path));
                    Uri compressedFileUri = Uri.fromFile(compressedFile);
                    Log.i("compressed_uri", compressedFileUri.toString());
                    uploadUri = compressedFileUri;
                    coverPhotoIV.setImageURI(compressedFileUri);
                    imageChanged = true;
                    imageCurrent = true;
                    saveButtonController.shouldCoverPhotoBeSaved = shoulCoverPhotoBeSaved();
                    saveButtonController.enableOrDisableSaveButton();

                    imageSourceChooser.setVisibility(View.INVISIBLE);
                    deleteImage.setClickable(true);
                    deleteImage.setVisibility(View.VISIBLE);
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

    private boolean shouldNameBeSaved(){
        return !mName.equals("");
    }

    private boolean shouldDescriptionBeSaved(){
        return !(mDescription.equals(oldDescription) || mDescription.equals(""));
    }

    private boolean shouldPriceBeSaved(){
        if(mPrice<0.0) return false;
        return !mPrice.equals(oldPrice);
    }

    private boolean shoulCoverPhotoBeSaved(){
        if(mSource==EditDishFormSource.NEW_DISH) {
            return imageCurrent;
        }
        if(imageChanged){
            return (imagePrevious || imageCurrent);
        }
        return false;
    }

    private boolean shouldCategoryBeSaved(){
        return !(mCategory==null || mCategory.equals(NullStrings.NULL_CATEGORY_STRING) || mCategory.equals(oldCategory));
    }

    private String getPathFromContentUri(Uri contentUri){
        String path = RealPathUtil.getRealPath(this, contentUri);
        Log.i("PATH", path);
        return path;
    }

    private void uploadPhotoAndUpdateDB(Uri uploadUri, final boolean creation){
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
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(creation){
                        createVital(uri);
                    }else {
                        if(imagePrevious && imageCurrent){
                            FirebaseStorage.getInstance().getReferenceFromUrl(oldCoverPhotoLink)
                                    .delete()
                                    .addOnCompleteListener(EditDishForm.this, new OnCompleteListener<Void>() {
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
                    }
                    updatePhotoUri(uri);
                }
            }).addOnFailureListener(EditDishForm.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressLayout.setVisibility(View.INVISIBLE);
                    formLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(EditDishForm.this, "Couldn't upload image!",
                            Toast.LENGTH_LONG).show();
                    saveButtonController.enableOrDisableSaveButton();
                }
            });
        } else {
            Log.i("upload_uri", "null");
            progressLayout.setVisibility(View.INVISIBLE);
            formLayout.setVisibility(View.VISIBLE);
            Toast.makeText(EditDishForm.this, "Update Failed", Toast.LENGTH_LONG).show();
            saveButtonController.enableOrDisableSaveButton();
        }
    }

    private void deletePhotoAndUpdateDB(){
        updateVital(null);
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldCoverPhotoLink);
        photoRef.delete()
                .addOnCompleteListener(EditDishForm.this, new OnCompleteListener<Void>() {
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

    private void createVital(Uri uri){
        Map<String, Object> dishVital = new HashMap<>();
        dishVital.put("n", mName);
        dishVital.put("p", mPrice);
        ArrayList<String> categories = new ArrayList<>();
        categories.add(mCategory);
        dishVital.put("c", categories);
        if(saveButtonController.shouldDescriptionBeSaved){
            dishVital.put("d", mDescription);
        }
        if(saveButtonController.shouldCoverPhotoBeSaved && uri!=null){
            dishVital.put("cp", uri.toString());
        }
        dishVital.put("npr", 0);
        dishVital.put("tr", 0);
        dishVital.put("nw", 0);
        Map<String, String> restaurantMap = new HashMap<>();
        restaurantMap.put("l", mAuth.getCurrentUser().getUid());
        dishVital.put("re", restaurantMap);
        final DocumentReference ref = db.collection("dish_vital").document();
        ref.set(dishVital)
        .addOnSuccessListener(EditDishForm.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("dish_creation", "successful");
                Log.i("new_dish", ref.getId());
                EditDishForm.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("dish_creation", "failed");
                EditDishForm.this.finish();
            }
        });
    }

    private void updateVital(Uri uri){
        Map<String, Object> dishVital = new HashMap<>();
        if(saveButtonController.shouldNameBeSaved){
            dishVital.put("n", mName);
        }
        if(saveButtonController.shouldPriceBeSaved){
            dishVital.put("p", mPrice);
        }
        if(saveButtonController.shouldDescriptionBeSaved){
            dishVital.put("d", mDescription);
        }
        if(uri!=null){
            dishVital.put("cp", uri.toString());
        }else{
            dishVital.put("cp", "");
        }
        if(saveButtonController.shouldCategoryBeSaved){
            ArrayList<String> categories = new ArrayList<>();
            categories.add(mCategory);
            dishVital.put("c", categories);
        }
        final DocumentReference ref = db.collection("dish_vital").document(mDishlink);
        ref.update(dishVital)
            .addOnSuccessListener(EditDishForm.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("dish_update", "successful");
                    EditDishForm.this.finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("dish_update", "failed");
                    EditDishForm.this.finish();
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
                .addOnSuccessListener(EditDishForm.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("photo_uri", "updated");
                    }
                })
                .addOnFailureListener(EditDishForm.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("photo_uri", e.getMessage());
                    }
                });
    }

    private void bindData(){
        db.collection("dish_vital").document(mDishlink)
                .get()
                .addOnCompleteListener(EditDishForm.this, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot dishInfo = task.getResult();
                            if(dishInfo.exists()){
                                bindDishImage(dishInfo);
                                bindDescription(dishInfo);
                                bindPrice(dishInfo);
                                bindCategorySpinner(dishInfo);
                            }
                        }
                    }
                });
    }

    private void bindDishImage(DocumentSnapshot dishVitalSnapshot){
        String coverPhotoLink = dishVitalSnapshot.getString("cp");
        if(coverPhotoLink==null || coverPhotoLink.equals("")){
            return;
        }
        oldCoverPhotoLink = coverPhotoLink;
        imagePrevious = true;
        imageCurrent = true;
        imageSourceChooser.setVisibility(View.INVISIBLE);
        deleteImage.setVisibility(View.VISIBLE);
        PictureBinder.bindCoverPicture(coverPhotoIV, dishVitalSnapshot);
    }

    private void bindDescription(DocumentSnapshot dishVitalSnapshot){
        String description = dishVitalSnapshot.getString("d");
        if(description==null || description.equals(""))return;
        oldDescription = description;
        descriptionEditText.setText(description);
    }

    private void bindPrice(DocumentSnapshot dishVitalSnapshot){
        Double price = dishVitalSnapshot.getDouble("p");
        if(price==null) return;
        oldPrice = price;
        priceEditText.setText(Double.toString(price));
    }

    private void bindCategorySpinner(DocumentSnapshot dishVitalSnapshot){
        String[] categories = getResources().getStringArray(R.array.food_categories);
        ArrayList<String> categoriesArrayList = new ArrayList<>(Arrays.asList(categories));
        int position = 0;

        if(dishVitalSnapshot!=null){
            ArrayList<String> dishCategories = (ArrayList<String>) dishVitalSnapshot.get("c");
            if(dishCategories!=null && dishCategories.size()>0){
                oldCategory = dishCategories.get(0);
                position = categoriesArrayList.indexOf(dishCategories.get(0));
            }else{
                categoriesArrayList.add(0, NullStrings.NULL_CATEGORY_STRING);
            }
            position = position<0 ? 0:position;
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoriesArrayList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishCategorySpinner.setAdapter(categoryAdapter);
        dishCategorySpinner.setSelection(position);

        dishCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("category", parent.getItemAtPosition(position).toString());
                mCategory = parent.getItemAtPosition(position).toString();
                saveButtonController.shouldCategoryBeSaved = shouldCategoryBeSaved();
                saveButtonController.enableOrDisableSaveButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class SaveButtonController{
        boolean shouldDescriptionBeSaved = false;
        boolean shouldNameBeSaved = false;
        boolean shouldPriceBeSaved = false;
        boolean shouldCoverPhotoBeSaved = false;
        boolean shouldCategoryBeSaved = false;
        Button saveButton;
        int source;
        boolean condition;

        SaveButtonController(Button saveButton, int source){
            this.saveButton = saveButton;
            this.source = source;
        }

        void enableOrDisableSaveButton(){
            if(source==EditDishFormSource.NEW_DISH){
                // description & cover photo are optional
                this.condition = shouldNameBeSaved && shouldPriceBeSaved && shouldCategoryBeSaved;
                // description & cover photo are mandatory
                 //this.condition = shouldNameBeSaved && shouldPriceBeSaved && shouldDescriptionBeSaved &&
                 //shouldCoverPhotoBeSaved && shouldCategoryBeSaved;
            }else{
                this.condition = shouldNameBeSaved ||
                        shouldDescriptionBeSaved ||
                        shouldCoverPhotoBeSaved ||
                        shouldPriceBeSaved || shouldCategoryBeSaved;
            }
            if(condition){
                saveButton.setEnabled(true);
            }else{
                saveButton.setEnabled(false);
            }
        }

        void refresh(){
            saveButton.setEnabled(false);
            shouldNameBeSaved = false;
            shouldCoverPhotoBeSaved = false;
            shouldDescriptionBeSaved = false;
            shouldPriceBeSaved = false;
            shouldCategoryBeSaved = false;
        }
    }
}
