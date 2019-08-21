package com.example.tuhin.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import myapp.utils.AdapterCreator;

// receives intent with string extra : personLink
public class PersonDetail extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView rv;
    FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        String personLink = getIntent().getStringExtra("personLink");

        db = FirebaseFirestore.getInstance();
        rv = findViewById(R.id.rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = AdapterCreator.getPersonDetailAdapter(this, this, personLink);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}