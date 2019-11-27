package com.example.tuhin.myapplication;

import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.AccountTypes;
import myapp.utils.PostTypes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class CreatePost extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    Button next;
    //EditText captionEditText;
    TextInputLayout captionLayout;
    TextInputEditText captionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        final int postType = getIntent().getIntExtra("post_type", PostTypes.POST);

        toolbar = findViewById(R.id.toolbar);
        next = findViewById(R.id.next);
        //captionEditText = findViewById(R.id.caption);
        captionLayout = findViewById(R.id.caption_layout);
        captionEditText = findViewById(R.id.caption);

        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        if(postType==PostTypes.REST_FEED) {
            next.setEnabled(false);
        }
        // hide the keyboard
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        captionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(postType==PostTypes.POST) return;
                String text = s.toString().trim();
                if(text.equals("")){
                    captionLayout.setError("Please type something");
                    next.setEnabled(false);
                }else{
                    captionLayout.setError(null);
                    next.setEnabled(true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = captionEditText.getText().toString().trim();
                Bundle post = new Bundle();
                post.putString("caption", caption);
                Intent intent = new Intent(CreatePost.this, CreatePostSelectImages.class);
                intent.putExtra("post_type", postType);
                intent.putExtras(post);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
