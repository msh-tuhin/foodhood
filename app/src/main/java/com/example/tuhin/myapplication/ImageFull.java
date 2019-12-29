package com.example.tuhin.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import myapp.utils.PostImagesAdapter;

import android.os.Bundle;

import java.util.ArrayList;

public class ImageFull extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        int position = getIntent().getIntExtra("position", 0);
        ArrayList<String> imageUris = getIntent().getStringArrayListExtra("imageUris");

        viewPager.setOffscreenPageLimit(2);
        PostImagesAdapter adapter = new PostImagesAdapter(this, imageUris);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
