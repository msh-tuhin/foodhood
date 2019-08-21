package com.example.tuhin.myapplication;

import androidx.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import myapp.utils.AdapterCreator;

public class HomeFeedFragment extends Fragment {

    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db;
    FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;

    public HomeFeedFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_feed, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //  ?? j code gula ekhane likhlam ogula ki asholei ekhane thakbe ??
        // naki onno kono method e thakle valo, like onCreate ??
        // adapter ta maybe onCreate e banale valo

        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.home_feed_rv);
        // getActivity().getApplicationContext() also works, maybe preferable
        linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = AdapterCreator.getHomeFeedAdapter(this, getActivity(), "YdnyQzx5XIMD0N2s8FDRPFVKRSo1");
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FAB", "clicked");
                Intent intent = new Intent(getActivity(), CreatePost.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
