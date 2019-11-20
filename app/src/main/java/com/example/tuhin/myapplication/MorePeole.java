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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_peole);

        ArrayList<String> links = getIntent().getStringArrayListExtra("personsList");
        int source = getIntent().getIntExtra("source", SourceMorePeople.UNKNOWN);
        String personLink = getIntent().getStringExtra("personLink");

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
        populateAdapter(links, collectionName, personLink);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateAdapter(ArrayList<String> personsList,
                                 String collectionName,
                                 String personLink){
        if(personsList != null){
            adapter.persons.addAll(personsList);
            adapter.notifyDataSetChanged();
        }else{
            if(personLink==null) return;
            db.collection(collectionName)
                    .document(personLink)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                try{
                                    ArrayList<String> links = (ArrayList)documentSnapshot.get("a");
                                    adapter.persons.addAll(links);
                                    adapter.notifyDataSetChanged();
                                }catch (NullPointerException e){
                                    Log.e("error", e.getMessage());
                                }
                            }
                        }
                    });
        }
    }

    // the adapter
    // might use a different adapter later
    private class PersonsAdapter extends RecyclerView.Adapter<MorePeopleItemHolder>{
        ArrayList<String> persons;
        PersonsAdapter(ArrayList<String> persons){
            this.persons = persons;
        }
        @Override
        public void onBindViewHolder(@NonNull MorePeopleItemHolder personHolder, int i) {
            personHolder.bindTo(MorePeole.this, persons.get(i));
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
