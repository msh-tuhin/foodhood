package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.EntryPoints;
import myviewholders.FullPostCommentHolder;
import myviewholders.FullPostHeaderHolder;
import myviewholders.FullRestFeedCommentHolder;
import myviewholders.FullRestFeedHeaderHolder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FullRestFeed extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rv;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_rest_feed);

        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.post_comment_rv);

        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);

        int entryPoint = getIntent().getIntExtra("entry_point", EntryPoints.HOME_PAGE_RF);

        // TODO this activity might receive a Task<DocumentSnapshot> instead
        String restFeedLink="";
        restFeedLink = getIntent().getStringExtra("restFeedLink");
        DocumentReference restFeedRef = FirebaseFirestore.getInstance().collection("rest_feed").document(restFeedLink);
        Task<DocumentSnapshot> taskPost = restFeedRef.get();
        adapter = new MyAdapter(FullRestFeed.this, taskPost, restFeedLink);

        // fetch the comment links to add to adapter
        switch(entryPoint){
            case EntryPoints.HOME_PAGE_RF:
                taskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot restFeed = task.getResult();
                            try{
                                Log.i("comments", "downloaded");
                                List<String> comments = (List<String>)restFeed.get("coms");
                                adapter.commentLinks.addAll(comments);
                            }catch (NullPointerException e){
                                Log.i("ERROR", e.getMessage());
                            }
                        }
                    }
                });
                break;
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(adapter);
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private final int FULL_REST_FEED = 0;
        private final int COMMENT = 1;
        private Task<DocumentSnapshot> taskPost;
        private String restFeedLink;
        private Context context;

        ArrayList<String> commentLinks = new ArrayList<>();

        public MyAdapter(Context context, Task<DocumentSnapshot> taskPost, String restFeedLink){
            commentLinks.add(restFeedLink);
            this.context = context;
            this.restFeedLink = restFeedLink;
            this.taskPost = taskPost;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view;
            RecyclerView.ViewHolder viewHolder;
            switch (viewType){
                case FULL_REST_FEED:
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.full_rest_feed, viewGroup, false);
                    viewHolder = new FullRestFeedHeaderHolder(view);
                    break;
                default:
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.a_comment, viewGroup, false);
                    viewHolder = new FullRestFeedCommentHolder(view);
                    break;

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof FullRestFeedHeaderHolder){
                Log.i("time", "header");
                ((FullRestFeedHeaderHolder) holder).bindTo(context, taskPost, restFeedLink);
            }
            else{
                Log.i("time", "comment");
                ((FullRestFeedCommentHolder)holder).bindTo(context, restFeedLink, commentLinks.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return FULL_REST_FEED;
            }
            return COMMENT;
        }

        @Override
        public int getItemCount() {
            return commentLinks.size();
        }
    }
}
