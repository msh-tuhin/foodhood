package com.example.tuhin.myapplication;

import androidx.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import models.PostModel;
import myapp.utils.AdapterCreator;
import myapp.utils.CityMapping;
import myviewholders.AlternatePostHolder;

public class HomeFeedFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
    FirestorePagingAdapter<PostModel, AlternatePostHolder> alternateAdapter;

    private boolean isTimelineAltered = false;
    private String mCurrentUserLink;

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
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //  ?? j code gula ekhane likhlam ogula ki asholei ekhane thakbe ??
        // naki onno kono method e thakle valo, like onCreate ??
        // adapter ta maybe onCreate e banale valo

        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rv = view.findViewById(R.id.home_feed_rv);
        // getActivity().getApplicationContext() also works, maybe preferable
        linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        mCurrentUserLink = mAuth.getCurrentUser().getUid();
        adapter = AdapterCreator.getHomeFeedAdapter(this, getActivity(), mCurrentUserLink);
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // this nesting is redundant
                if(!isTimelineAltered){
                    if(adapter != null){
                        adapter.refresh();
                    }
                }else{
                    if(alternateAdapter != null){
                        alternateAdapter.refresh();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
        if(alternateAdapter!=null){
            alternateAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
        if(alternateAdapter!=null){
            alternateAdapter.stopListening();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.alter_timeline:
                Log.i("timeline", "please alter");
                if(isTimelineAltered){
                    isTimelineAltered = false;
                    if(alternateAdapter!=null){
                        alternateAdapter.stopListening();
                    }
                    alternateAdapter = null;
                    adapter = AdapterCreator.getHomeFeedAdapter(this, getActivity(), mCurrentUserLink);
                    adapter.startListening();
                    rv.setAdapter(adapter);
                    Toast.makeText(getActivity(), "Showing reviews from your followings",
                            Toast.LENGTH_SHORT).show();
                }else{
                    db.collection("person_vital").document(mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(HomeFeedFragment.this.getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        String currentTown = documentSnapshot.getString("ct");
                                        if(currentTown==null || currentTown.equals("")) {
                                            Toast.makeText(getActivity(),
                                                    "You need to set your hometown/upozilla",
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        isTimelineAltered = true;
                                        if(adapter!=null) {
                                            adapter.stopListening();
                                        }
                                        adapter = null;
                                        CityMapping cityMapping = new CityMapping();
                                        Log.i("current_town", currentTown);
                                        String district = cityMapping.getDistrict(currentTown);
                                        if(district!=null){
                                            Log.i("district", district);
                                            alternateAdapter = AdapterCreator.getHomeFeedAlternativeAdapter(
                                                    HomeFeedFragment.this,
                                                    HomeFeedFragment.this.getActivity(),
                                                    district);
                                            alternateAdapter.startListening();
                                        }else{
                                            Log.i("district", "null");
                                            alternateAdapter = null;
                                        }
                                        rv.setAdapter(alternateAdapter);
                                        Toast.makeText(getActivity(),
                                                "Showing reviews about restaurants from your district",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
