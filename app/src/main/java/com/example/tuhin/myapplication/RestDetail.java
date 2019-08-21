package com.example.tuhin.myapplication;

import androidx.paging.PagedList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import myviewholders.FeedbackHolder;
import myviewholders.FeedbackWithoutReviewHolder;
import myviewholders.RestaurantDetailHeaderHolder;

// receives explicit intent with String extra : restaurantLink
public class RestDetail extends AppCompatActivity {

    RecyclerView rv;
    FirestorePagingAdapter<FeedbackModel, RecyclerView.ViewHolder> adapter;
    String restaurantLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_detail);
        restaurantLink = getIntent().getExtras().getString("restaurantLink");
        rv = findViewById(R.id.restaurant_detail_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        getAdapter();
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void getAdapter(){
        final int HEADER = 0;
        final int FEEDBACK_WITHOUT_REVIEW = 1;
        final int FEEDBACK = 2;

        // needs composite index
        // TODO maybe separate dish and restaurant feedbacks in firebase
        Query bQuery = FirebaseFirestore.getInstance().collection("feedbacks")
                .orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<FeedbackModel> options = new FirestorePagingOptions.Builder<FeedbackModel>()
                .setLifecycleOwner(this)
                .setQuery(bQuery, config, FeedbackModel.class).build();

        adapter = new FirestorePagingAdapter<FeedbackModel, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull FeedbackModel model) {
//                if(position == getItemCount()-1) return;
                if(holder instanceof RestaurantDetailHeaderHolder){
                    ((RestaurantDetailHeaderHolder) holder).bindTo(rv.getContext(), restaurantLink);
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
                                .inflate(R.layout.restaurant_detail_header, viewGroup, false);
                        viewHolder = new RestaurantDetailHeaderHolder(view);
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
}
