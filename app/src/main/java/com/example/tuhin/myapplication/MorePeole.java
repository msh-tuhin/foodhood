package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import myapp.utils.SourceMorePeople;
import myviewholders.MorePeopleItemHolder;


// receives explicit intent with StringArraylistExtra : personsList
public class MorePeole extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PersonsAdapter adapter;
    RecyclerView rv;
    Toolbar toolbar;

    private ArrayList<String> links;
    private int source;
    private String personLink;
    private String postLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_peole);

        links = getIntent().getStringArrayListExtra("personsList");
        source = getIntent().getIntExtra("source", SourceMorePeople.UNKNOWN);
        personLink = getIntent().getStringExtra("personLink");
        postLink = getIntent().getStringExtra("postLink");

        rv = findViewById(R.id.rv);
        toolbar = findViewById(R.id.toolbar);

        String collectionName = "";
        String title = "All People";
        switch (source){
            case SourceMorePeople.FOLLOWINGS:
                title = "Follows";
                collectionName = "followings";
                break;
            case SourceMorePeople.FOLLOWERS:
                collectionName = "followers";
                title = "Followers";
                break;
            case SourceMorePeople.LIKERS_POST:
                collectionName = "posts";
                title = "Liked By";
                break;
            case SourceMorePeople.LIKERS_RF:
                collectionName = "rest_feed";
                title = "Liked By";
                break;
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new PersonsAdapter(new ArrayList<String>());
        rv.setAdapter(adapter);
        populateAdapter(collectionName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateAdapter(String collectionName){
        if(links != null){
            adapter.persons.addAll(links);
            adapter.notifyDataSetChanged();
        }else{
            if(source==SourceMorePeople.LIKERS_POST || source==SourceMorePeople.LIKERS_RF){
                if(postLink==null) return;
                db.collection(collectionName)
                        .document(postLink)
                        .get()
                        .addOnSuccessListener(MorePeole.this, new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {
                                    try {
                                        ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("l");
                                        adapter.persons.addAll(likes);
                                        adapter.notifyDataSetChanged();
                                    } catch (NullPointerException e) {
                                        Log.e("error", e.getMessage());
                                    } catch (Exception e){
                                        Log.e("error", e.getMessage());

                                    }
                                }
                            }
                        });
            }
            else{
                if(personLink==null) return;
                db.collection(collectionName)
                        .document(personLink)
                        .get()
                        .addOnSuccessListener(MorePeole.this, new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    try{
                                        ArrayList<String> links = (ArrayList)documentSnapshot.get("a");
                                        adapter.persons.addAll(links);
                                        adapter.notifyDataSetChanged();
                                    }catch (NullPointerException e){
                                        Log.e("error", e.getMessage());
                                    }catch (Exception e){
                                        Log.e("error", e.getMessage());

                                    }
                                }
                            }
                        });
            }
        }
    }

    // the adapter
    // might use a different adapter later
    private class PersonsAdapter extends RecyclerView.Adapter<MorePeopleItemHolder>{
        ArrayList<String> persons;
        Task<DocumentSnapshot> taskWithCurrentUserFollowings;
        PersonsAdapter(ArrayList<String> persons){
            this.persons = persons;
            taskWithCurrentUserFollowings = db.collection("followings")
                    .document(mAuth.getCurrentUser().getUid()).get();
        }
        @Override
        public void onBindViewHolder(@NonNull MorePeopleItemHolder personHolder, int i) {
            personHolder.bindTo(MorePeole.this, persons.get(i), taskWithCurrentUserFollowings);
        }
        @Override
        public int getItemCount() {
            return persons.size();
        }

        @NonNull
        @Override
        public MorePeopleItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.more_people_item, viewGroup, false);
            return new MorePeopleItemHolder(view);
        }
    }
}
