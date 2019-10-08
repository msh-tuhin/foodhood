package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.EntryPoints;
import myviewholders.CommentDetailHolder;
import myviewholders.CommentDetailReplyHolder;
import myviewholders.FullPostCommentHolder;
import myviewholders.FullPostHeaderHolder;

import android.content.Context;
import android.content.Intent;
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
import java.util.Map;

/*
    can come to this from multiple sources and different actions
    1) from home_feed_post_comment when user replies to a comment(implemented)
                home_feed => write_comment => this page
    2) from home_feed_post_comment when user goes to comment_detail(implemented)
                home_feed => this page
    3) from full_post when user replies to a comment(implemented)
                full_post => write_comment => this page
    4) from full_post when user goes to comment_detail(implemented)
                full_post => this page
    5) from home_feed_post_reply when user goes to comment_detail(implemented)
                home_feed => this page
    6) from home_feed_post_reply when user replies to a comment/reply(implemented)
                home_feed => write_comment => this page

    from this page (user is replying to comment/reply) => WriteComment => this page (implemented)
*/
public class CommentDetail extends AppCompatActivity{

    public final int REQUEST_REPLY = 0;
    public final int REQUEST_REPLY_TO_REPLY = 1;

    private int mEntryPoint;
    private String mPostLink;
    private String mCommentLink;
    private String mReplyLink;
    private String mReplyToReplyLink;
    private Task<DocumentSnapshot> mTaskComment;

    Toolbar toolbar;
    MyAdapter adapter;
    public RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.post_comment_rv);

        toolbar.setTitle("Comment");
        setSupportActionBar(toolbar);

        mEntryPoint = getIntent().getIntExtra("entry_point", EntryPoints.HOME_PAGE);
        mPostLink = getIntent().getStringExtra("postLink");
        mCommentLink = getIntent().getStringExtra("commentLink");
        mReplyLink = getIntent().getStringExtra("replyLink");
        mReplyToReplyLink = getIntent().getStringExtra("replyToReplyLink");

        DocumentReference commentRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mCommentLink);
        mTaskComment = commentRef.get();

        initializeAdapter();

        populateAdapter();
        decorateRecyclerView();
    }

    private void initializeAdapter(){
        // adapter = new MyAdapter();
        // adapter.notifyDataSetChanged();
        adapter = new MyAdapter(this, mTaskComment, mPostLink, mCommentLink);
        adapter.replyLinks.add(0, mCommentLink);

        switch (mEntryPoint){
            case EntryPoints.NOTIF_REPLY_COMMENT:
            case EntryPoints.REPLY_TO_COMMENT:
            case EntryPoints.CD_FROM_HOME_COMMENT_REPLY:
                adapter.replyLinks.add(1, mReplyLink);
                break;
            case EntryPoints.NOTIF_REPLY_REPLY:
            case EntryPoints.REPLY_TO_REPLY_HOME:
                adapter.replyLinks.add(1, mReplyLink);
                adapter.replyLinks.add(2, mReplyToReplyLink);
                break;
        }
    }

    private void populateAdapter(){
        mTaskComment.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot comment = task.getResult();
                    try{
                        Log.i("replies", "downloaded");
                        List<String> replies = (List<String>)comment.get("r");

                        switch (mEntryPoint){
                            case EntryPoints.NOTIF_REPLY_COMMENT:
                            case EntryPoints.REPLY_TO_COMMENT:
                            case EntryPoints.CD_FROM_HOME_COMMENT_REPLY:
                                replies.remove(mReplyLink);
                                break;
                            case EntryPoints.NOTIF_REPLY_REPLY:
                            case EntryPoints.REPLY_TO_REPLY_HOME:
                                replies.remove(mReplyLink);
                                replies.remove(mReplyToReplyLink);
                                break;
                        }
                        adapter.replyLinks.addAll(replies);
                    }catch (NullPointerException e){
                        Log.i("ERROR", e.getMessage());
                    }
                }
            }
        });
    }

    private void decorateRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(adapter);
//        if(entryPoint == EntryPoints.NOTIF_REPLY_REPLY){
//            layoutManager.smoothScrollToPosition(rv, null, 2);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_REPLY:
                    String replyLink = data.getStringExtra("replyLink");
                    adapter.replyLinks.add(1, replyLink);
                    adapter.notifyItemInserted(1);
                    break;
                case REQUEST_REPLY_TO_REPLY:
                    String replyToReplyLink = data.getStringExtra("replyLink");
                    int newReplyPosition = data.getIntExtra("newReplyPosition", 1);
                    adapter.replyLinks.add(newReplyPosition+1, replyToReplyLink);
                    adapter.notifyItemInserted(newReplyPosition+1);
                    break;
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private final int COMMENT = 0;
        private final int REPLY = 1;

        private Task<DocumentSnapshot> taskComment;
        private String postLink;
        private String commentLink;
        private Context context;

        ArrayList<String> replyLinks = new ArrayList<>();

        public MyAdapter(Context context, Task<DocumentSnapshot> taskComment, String postLink, String commentLink){
            this.context = context;
            this.taskComment = taskComment;
            this.postLink = postLink;
            this.commentLink = commentLink;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view;
            RecyclerView.ViewHolder viewHolder;
            switch (viewType){
                case COMMENT:
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.comment_big, viewGroup, false);
                    viewHolder = new CommentDetailHolder(view);
                    break;
                default:
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.a_reply, viewGroup, false);
                    viewHolder = new CommentDetailReplyHolder(view);
                    break;

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof CommentDetailHolder){
                Log.i("time", "header");
                ((CommentDetailHolder) holder).bindTo(context, taskComment, postLink, commentLink);
            }
            else{
                Log.i("time", "comment");
                ((CommentDetailReplyHolder)holder).bindTo(context, postLink, commentLink, replyLinks.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return COMMENT;
            }
            return REPLY;
        }

        @Override
        public int getItemCount() {
            return replyLinks.size();
        }
    }
}
