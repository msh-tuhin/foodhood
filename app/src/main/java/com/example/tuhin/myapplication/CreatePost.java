package com.example.tuhin.myapplication;

import android.content.Intent;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class CreatePost extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    Button next;
    EditText captionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        toolbar = findViewById(R.id.toolbar);
        next = findViewById(R.id.next);
        captionEditText = findViewById(R.id.caption);

        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        // hide the keyboard
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = captionEditText.getText().toString();
                Bundle post = new Bundle();
                post.putString("caption", caption);
                Intent intent = new Intent(CreatePost.this, CreatePostSelectImages.class);
                intent.putExtras(post);
                startActivity(intent);
            }
        });
    }
}
