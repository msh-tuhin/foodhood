package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myviewholders.FullPostCommentHolder;
import myviewholders.FullPostHeaderHolder;
import myviewholders.FullRestFeedCommentHolder;
import myviewholders.FullRestFeedHeaderHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FullRestFeed extends AppCompatActivity {

    public final int REQUEST_COMMENT = 0;

    LinearLayoutManager mLinearLayoutManager;
    private CommentIntentExtra mCommentIntentExtra;
    private Task<DocumentSnapshot> mTaskRestFeed;
    private int mEntryPoint;
    private String mRestFeedLink;
    private String mCommentLink;

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

        setmCommentIntentExtra();
        setmEntryPoint();
        setmRestFeedLink();
        setmTaskRestFeed();
        setmCommentLink();

        mLinearLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLinearLayoutManager);
        addItemDecorationToRV();

        initializeAdapter();
        rv.setAdapter(adapter);
        populateAdapter();

        setStackFromEnd();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_COMMENT:
                    String commentLink = data.getStringExtra("commentLink");
                    adapter.commentLinks.add(1, commentLink);
                    adapter.notifyItemInserted(1);
                    break;
            }
        }
    }

    private void setmCommentIntentExtra() {
        mCommentIntentExtra = (CommentIntentExtra) getIntent()
                .getSerializableExtra("comment_extra");
    }

    private void setmEntryPoint() {
        if(mCommentIntentExtra != null){
            mEntryPoint = mCommentIntentExtra.getEntryPoint();
        } else {
            mEntryPoint = getIntent().getIntExtra("entry_point",
                    EntryPoints.CLICKED_GO_TO_FULL_RF);

        }

    }

    private void setmRestFeedLink() {
        if(mCommentIntentExtra != null){
            mRestFeedLink = mCommentIntentExtra.getPostLink();
        } else {
            mRestFeedLink = getIntent().getStringExtra("restFeedLink");
        }
    }

    private void setmCommentLink() {
        if(mCommentIntentExtra != null){
            mCommentLink = mCommentIntentExtra.getCommentLink();
        }
    }

    private void setmTaskRestFeed(){
        if(mRestFeedLink == null){
            setmRestFeedLink();
        }
        DocumentReference postRef = FirebaseFirestore.getInstance()
                .collection("rest_feed")
                .document(mRestFeedLink);
        mTaskRestFeed = postRef.get();
    }

    private void addItemDecorationToRV(){
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                mLinearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
    }

    private void initializeAdapter(){
        adapter = new MyAdapter(FullRestFeed.this, mTaskRestFeed, mRestFeedLink);
        switch(mEntryPoint){
            case EntryPoints.NOTIF_COMMENT_RF:
                adapter.commentLinks.add(mCommentLink);
                break;
        }
    }

    private void populateAdapter(){
        // fetch the comment links to add to adapter
//        switch(mEntryPoint){
//            case EntryPoints.NOTIF_LIKE_RF:
//            case EntryPoints.CLICKED_GO_TO_FULL_RF:
//            case EntryPoints.COMMENT_ON_HOME_RF:
//                mTaskRestFeed.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            DocumentSnapshot restFeed = task.getResult();
//                            try{
//                                Log.i("comments", "downloaded");
//                                List<String> comments = (List<String>)restFeed.get("coms");
//                                adapter.commentLinks.addAll(comments);
//                            }catch (NullPointerException e){
//                                Log.i("ERROR", e.getMessage());
//                            }
//                        }
//                    }
//                });
//                break;
//        }
//        adapter.notifyDataSetChanged();
        mTaskRestFeed.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restFeed = task.getResult();
                    try{
                        Log.i("comments", "downloaded");
                        List<String> comments = (List<String>)restFeed.get("coms");
                        switch(mEntryPoint){
                            case EntryPoints.NOTIF_COMMENT_RF:
                                comments.remove(mCommentLink);
                                break;
                        }
                        adapter.commentLinks.addAll(comments);
                        adapter.notifyDataSetChanged();
                    }catch (NullPointerException e){
                        Log.i("ERROR", e.getMessage());
                    }
                }
            }
        });
    }

    private void setStackFromEnd(){
        mTaskRestFeed.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(mEntryPoint == EntryPoints.COMMENT_ON_HOME_RF){
                    mLinearLayoutManager.setStackFromEnd(true);
                }
            }
        });
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
