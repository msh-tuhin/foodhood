package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myviewholders.FullPostCommentHolder;
import myviewholders.FullPostHeaderHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;
import java.util.List;

/*
    can have multiple entry points =>
    1) home_feed -> see feedbacks -> this page
    2) home_feed -> write_comment -> this page
    3) like_post and comment_post notification
    4) a post link anywhere (can be treated same as 1)
*/

public class FullPost extends AppCompatActivity {

    public final int REQUEST_COMMENT = 0;

    LinearLayoutManager mLinearLayoutManager;
    private int mEntryPoint;
    private String mPostLink;
    private String mCommentLink;
    private Task<DocumentSnapshot> mTaskPost;
    private CommentIntentExtra mCommentIntentExtra;

    public RecyclerView rv;
//    FirestorePagingAdapter<CommentModel, RecyclerView.ViewHolder> adapter;
    Toolbar toolbar;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);

        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.post_comment_rv);

        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        setmCommentIntentExtra();
        setmEntryPoint();
        setmCommentLink();
        setmPostLink();
        Log.i("postLink-full", mPostLink);
        setmTaskPost();

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
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
                    EntryPoints.CLICKED_GO_TO_FULL_POST);

        }

    }

    private void setmPostLink() {
        if(mCommentIntentExtra != null){
            mPostLink = mCommentIntentExtra.getPostLink();
        } else {
            mPostLink = getIntent().getStringExtra("postLink");
        }
    }

    private void setmCommentLink() {
        if(mCommentIntentExtra != null){
            mCommentLink = mCommentIntentExtra.getCommentLink();
        }
    }

    private void setmTaskPost(){
        if(mPostLink == null){
            setmPostLink();
        }
        DocumentReference postRef = FirebaseFirestore.getInstance()
                .collection("posts")
                .document(mPostLink);
        mTaskPost = postRef.get();
    }

    private void initializeAdapter(){
        adapter = new MyAdapter(FullPost.this, mTaskPost, mPostLink);
        switch(mEntryPoint){
            case EntryPoints.NOTIF_COMMENT_POST:
                adapter.commentLinks.add(mCommentLink);
                break;
        }
    }

    private void populateAdapter(){
//        switch(mEntryPoint){
//            case EntryPoints.NOTIF_LIKE_POST:
//            case EntryPoints.CLICKED_GO_TO_FULL_POST:
//            case EntryPoints.COMMENT_ON_HOME_POST:
//                mTaskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            DocumentSnapshot post = task.getResult();
//                            try{
//                                Log.i("comments", "downloaded");
//                                List<String> comments = (List<String>)post.get("coms");
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
        mTaskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot post = task.getResult();
                    try{
                        Log.i("comments", "downloaded");
                        List<String> comments = (List<String>)post.get("coms");
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

    private void addItemDecorationToRV(){
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                mLinearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
    }

    private void setStackFromEnd(){
        mTaskPost.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(mEntryPoint == EntryPoints.NOTIF_COMMENT_POST ||
                        mEntryPoint == EntryPoints.COMMENT_ON_HOME_POST){
                    mLinearLayoutManager.setStackFromEnd(true);
                }
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private final int FULL_POST = 0;
        private final int COMMENT = 1;
        private Task<DocumentSnapshot> taskPost;
        private String postLink;
        private Context context;

        ArrayList<String> commentLinks = new ArrayList<>();

        public MyAdapter(Context context, Task<DocumentSnapshot> taskPost, String postLink){
            commentLinks.add(postLink);
            this.context = context;
            this.postLink = postLink;
            this.taskPost = taskPost;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view;
            RecyclerView.ViewHolder viewHolder;
            switch (viewType){
                case FULL_POST:
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.full_post, viewGroup, false);
                    viewHolder = new FullPostHeaderHolder(view);
                    break;
                default:
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.comment_fp, viewGroup, false);
                    viewHolder = new FullPostCommentHolder(view);
                    break;

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof FullPostHeaderHolder){
                Log.i("time", "header");
                ((FullPostHeaderHolder) holder).bindTo(context, taskPost, postLink);
            }
            else{
                Log.i("time", "comment");
                ((FullPostCommentHolder)holder).bindTo(context, postLink, commentLinks.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return FULL_POST;
            }
            return COMMENT;
        }

        @Override
        public int getItemCount() {
            return commentLinks.size();
        }
    }

//    private void getAdapter(){
//        final int FULL_POST = 0;
//        final int COMMENT = 1;
//
//        // needs composite index
//        // TODO maybe separate dish and restaurant feedbacks in firebase
//        Query bQuery = FirebaseFirestore.getInstance().collection("comments").whereEqualTo("wh", postLink)
//                .orderBy("ts", Query.Direction.ASCENDING);
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setEnablePlaceholders(false)
//                .setPrefetchDistance(10)
//                .setPageSize(10).build();
//        FirestorePagingOptions<CommentModel> options = new FirestorePagingOptions.Builder<CommentModel>()
//                .setLifecycleOwner(this)
//                .setQuery(bQuery, config, CommentModel.class).build();
//
//        adapter = new FirestorePagingAdapter<CommentModel, RecyclerView.ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull CommentModel model) {
////                if(position == getItemCount()-1) return;
//                if(holder instanceof FullPostHeaderHolder){
//                    ((FullPostHeaderHolder) holder).bindTo(postLink);
//                    return;
//                }
//                CommentModel mModel = this.getCurrentList().get(position-1).toObject(CommentModel.class);
////                if(holder instanceof FeedbackWithoutReviewHolder) {
////                    ((FeedbackWithoutReviewHolder) holder).bindTo(mModel);
////                    return;
////                }
//                ((FullPostCommentHolder) holder).bindTo(mModel);
//            }
//
//            @NonNull
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view;
//                RecyclerView.ViewHolder viewHolder;
//                switch (i){
//                    case FULL_POST:
//                        view = LayoutInflater.from(viewGroup.getContext())
//                                .inflate(R.layout.full_post, viewGroup, false);
//                        viewHolder = new FullPostHeaderHolder(view);
//                        break;
//                    default:
//                        view = LayoutInflater.from(viewGroup.getContext())
//                                .inflate(R.layout.a_comment, viewGroup, false);
//                        viewHolder = new FullPostCommentHolder(view);
//                        break;
//
//                }
//                return viewHolder;
//            }
//
//            @Override
//            public int getItemViewType(int position) {
//                if(position == 0) return FULL_POST;
////                if(this.getCurrentList().get(position-1).getBoolean("hre")) return FEEDBACK;
//                return COMMENT;
//            }
//        };
//    }



}
