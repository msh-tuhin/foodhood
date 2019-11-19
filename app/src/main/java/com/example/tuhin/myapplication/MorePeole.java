package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import myviewholders.MorePeopleItemHolder;


// receives explicit intent with StringArraylistExtra : personsList
public class MorePeole extends AppCompatActivity {

    RecyclerView rv;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_peole);

        ArrayList<String> links = getIntent().getStringArrayListExtra("personsList");

        rv = findViewById(R.id.rv);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("All People");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        PersonsAdapter adapter = new PersonsAdapter(links);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
