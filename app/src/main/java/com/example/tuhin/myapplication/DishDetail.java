package com.example.tuhin.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.ActionMode;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import models.DishFeedback;
import models.FeedbackModel;
import myviewholders.DishDetailHeaderHolder;
import myviewholders.FeedbackHolder;
import myviewholders.FeedbackHolderCommon;
import myviewholders.FeedbackWithoutReviewHolder;

// receives explicit intent with String extra : dishLink
public class DishDetail extends AppCompatActivity {
    public AppBarLayout appBarLayout;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public Toolbar toolbar;
    RecyclerView rv;
    public ImageView coverPhoto;
    // FirestorePagingAdapter<FeedbackModel, RecyclerView.ViewHolder> adapter;
    MyFeedbackAdapter adapter;
    String dishLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);
        dishLink = getIntent().getExtras().getString("dishLink");

        coverPhoto = findViewById(R.id.cover_photo);
        appBarLayout = findViewById(R.id.appBarLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.dish_detail_rv);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        // adapter = getAdapter();
        adapter = new MyFeedbackAdapter(this);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
        FirebaseFirestore.getInstance().collection("feedbacks_list")
                .document(dishLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot feedbacksArraySnapshot) {
                        if(feedbacksArraySnapshot.exists()){
                            try{
                                ArrayList<String> feedbacksList = (ArrayList<String>)feedbacksArraySnapshot.get("a");
                                adapter.feedbacksLinks.addAll(feedbacksList);
                                adapter.notifyDataSetChanged();
                            }catch (NullPointerException e){
                                Log.e("error", e.getMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // adapter.stopListening();
    }

    private FirestorePagingAdapter<FeedbackModel, RecyclerView.ViewHolder> getAdapter(){
        final int HEADER = 0;
        final int FEEDBACK_WITHOUT_REVIEW = 1;
        final int FEEDBACK = 2;

        // needs composite index
        // TODO maybe separate dish and restaurant feedbacks in firebase
        Query bQuery = FirebaseFirestore.getInstance().collection("feedbacks")
                .whereEqualTo("t", 0);
//                .orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<FeedbackModel> options = new FirestorePagingOptions.Builder<FeedbackModel>()
                .setLifecycleOwner(this)
                .setQuery(bQuery, config, FeedbackModel.class).build();

        return new FirestorePagingAdapter<FeedbackModel, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull FeedbackModel model) {
//                if(position == getItemCount()-1){
//                    Log.i("hoorah", "end reached");
//                    return;
//                }
                if(holder instanceof DishDetailHeaderHolder){
                    ((DishDetailHeaderHolder) holder).bindTo(DishDetail.this, dishLink);
                    return;
                }
                FeedbackModel mModel = this.getCurrentList().get(position-1).toObject(FeedbackModel.class);
                if(holder instanceof FeedbackWithoutReviewHolder) {
                    ((FeedbackWithoutReviewHolder) holder).bindTo(mModel);
                    return;
                }
                ((FeedbackHolder) holder).bindTo(mModel);
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view;
                RecyclerView.ViewHolder viewHolder;
                switch (i){
                    case HEADER:
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.dish_detail_header, viewGroup, false);
                        viewHolder = new DishDetailHeaderHolder(view);
                        break;
                    case FEEDBACK_WITHOUT_REVIEW:
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.feedback_without_review, viewGroup, false);
                        viewHolder = new FeedbackWithoutReviewHolder(view);
                        break;
                    default:
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.feedback, viewGroup, false);
                        viewHolder = new FeedbackHolder(view);
                        break;

                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                if(position == 0) return HEADER;
                if(this.getCurrentList().get(position-1).getBoolean("hre")) return FEEDBACK;
                return FEEDBACK_WITHOUT_REVIEW;
            }
        };
    }

    private class MyFeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final int HEADER = 0;
        private final int FEEDBACK = 1;

        private Context context;
        ArrayList<String> feedbacksLinks = new ArrayList<>();

        MyFeedbackAdapter(Context context){
            this.context = context;
            feedbacksLinks.add("");
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof DishDetailHeaderHolder){
                ((DishDetailHeaderHolder) holder).bindTo(context, dishLink);
                return;
            }
            ((FeedbackHolderCommon)holder).bindTo(context, feedbacksLinks.get(position));
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            RecyclerView.ViewHolder viewHolder;
            switch (viewType){
                case(HEADER):
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.dish_detail_header, parent, false);
                    viewHolder = new DishDetailHeaderHolder(view);
                    break;
                default:
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.feedback, parent, false);
                    viewHolder = new FeedbackHolderCommon(view);
                    break;
            }
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return HEADER;
            }
            return FEEDBACK;
        }

        @Override
        public int getItemCount() {
            return feedbacksLinks.size();
        }
    }
}
