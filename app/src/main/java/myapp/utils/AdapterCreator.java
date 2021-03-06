package myapp.utils;

import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import models.ActivityResponse;

import models.PostModel;
import myviewholders.AlternatePostHolder;
import myviewholders.BaseHomeFeedHolder;
import myviewholders.EditPersonProfileHeaderHolder;
import myviewholders.HalfPostHolder;
import myviewholders.PersonDetailHeaderHolder;
import myviewholders.PostTagHolder;
import myviewholders.RestFeedCommentHolder;
import myviewholders.RestFeedHolder;
import myviewholders.PostLikeTagHolder;
import myviewholders.RestFeedLikeHolder;
import myviewholders.PostCommentHolder;
import myviewholders.PostReplyHolder;
import myviewholders.RestFeedReplyHolder;

import site.sht.bd.foodhood.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdapterCreator {
    public static  FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>
    getHomeFeedAdapter(final LifecycleOwner lifecycleOwner,
                       final Context context,
                       final String personLink){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query bQuery = db.collection("friends_activities")
                .document(currentUserLink)
                .collection("act").orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<ActivityResponse> options = new FirestorePagingOptions.Builder<ActivityResponse>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, ActivityResponse.class).build();

        FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
        adapter = new FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position, @NonNull final ActivityResponse model) {
                ((BaseHomeFeedHolder)holder).refreshHolder();
                Log.i("recyclerview", "onBindViewHolder");
                Log.i("currentlistsize", Integer.toString(this.getCurrentList().size()));
                // because holder is used in inner method
                final RecyclerView.ViewHolder holder1 = holder;
                Log.i("holder1", holder1.getClass().toString());
                Log.i("TUHIN-DEBUG", model.getLink());
                String activityLink = model.getLink();
                // activityLink cannot be used because the position doesn't match
//                DocumentReference docRef = db.collection("activities").document(this.getCurrentList().get(position-1).getString("l"));
//                DocumentReference docRef = db.collection("activities").document(this.getCurrentList().get(position).getString("l"));
                DocumentReference docRef = db.collection("activities").document(activityLink);
                Log.i("download", "start");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("download", "finish");
                        if(task.isSuccessful()){
                            final DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                if(holder1 instanceof PostTagHolder){
                                    ((PostTagHolder) holder1).bindTo(context, documentSnapshot, model);
                                }else{
                                    ((BaseHomeFeedHolder)holder1).bindTo(context, documentSnapshot);
                                }
                            } else{
                                Log.i("activity", "does not exist");
                            }

                        }
                    }
                });

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.i("recyclerview", "onCreateViewHolder");
                RecyclerView.ViewHolder viewHolder;
                View view;
                switch(i){
                    case 0:
                        // a person posts
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.post_half, viewGroup, false);
                        viewHolder = new HalfPostHolder(view);
                        break;
                    case 1:
                        // a person gets tagged in a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_tag_post, viewGroup, false);
                        viewHolder = new PostTagHolder(view);
                        break;
                    case 2:
                        // a restaurant posts
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.rest_feed, viewGroup, false);
                        viewHolder = new RestFeedHolder(view);
                        break;
                    case 3:
                        // a person likes a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_tag_post, viewGroup, false);
                        viewHolder = new PostLikeTagHolder(view);
                        break;
                    case 4:
                        // a person likes a restaurant feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedLikeHolder(view);
                        break;
                    case 5:
                        // a person comments on a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.comment_post, viewGroup, false);
                        viewHolder = new PostCommentHolder(view);
                        break;
                    case 7:
                        // a person replies to a comment on a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.reply_post, viewGroup, false);
                        viewHolder = new PostReplyHolder(view);
                        break;

                    case 6:
                        // a person comments on a rest feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.comment_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedCommentHolder(view);
                        break;
                    case 8:
                        // a person replies to comment on rest feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.reply_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedReplyHolder(view);
                        break;
                    default:
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.person_detail_header, viewGroup, false);
                        viewHolder = new PersonDetailHeaderHolder(view);
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
//                if(position==0) return 100;
                Log.i("recyclerview", "getItemViewType");
                Log.i("position", Integer.toString(position));
                // damn!!! this is clever !
//                DocumentSnapshot documentSnapshot = this.getCurrentList().get(position-1);
                DocumentSnapshot documentSnapshot = this.getCurrentList().get(position);
                Long type = (Long) documentSnapshot.get("t");
                Log.i("type", Long.toString(type));
                return type.intValue();
            }

            @Override
            public int getItemCount() {
                int c = super.getItemCount();
                Log.i("count", Integer.toString(c));
                return c;
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state){
                    case LOADING_INITIAL:
                        Log.i("loadingstate", "loading initial");
                        break;
                    case LOADING_MORE:
                        Log.i("loadingstate", "loading more");
                        break;
                    case LOADED:
                        Log.i("loadingstate", "loading done");
                        break;
                    case FINISHED:
                        Log.i("loadingstate", "loading finished");
                        break;
                    case ERROR:
                        Log.i("loadingstate", "error occured");
                }
            }
        };

        return adapter;

    }

    public static  FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>
    getRestHomeFeedAdapter(final LifecycleOwner lifecycleOwner,
                           final Context context){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.i("current_user", currentUserLink);
        Query bQuery = db.collection("own_activities")
                .document(currentUserLink)
                .collection("act").orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<ActivityResponse> options = new FirestorePagingOptions.Builder<ActivityResponse>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, ActivityResponse.class).build();

        FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
        adapter = new FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                            final int position,
                                            @NonNull final ActivityResponse model) {
                ((BaseHomeFeedHolder)holder).refreshHolder();
                Log.i("recyclerview", "onBindViewHolder");
                Log.i("currentlistsize", Integer.toString(this.getCurrentList().size()));
                Log.i("holder", holder.getClass().toString());
                Log.i("activity_link", model.getLink());
                String activityLink = model.getLink();
                DocumentReference docRef = db.collection("activities").document(activityLink);
                Log.i("download", "start");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("download", "finish");
                        if(task.isSuccessful()){
                            final DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                ((BaseHomeFeedHolder)holder).bindTo(context, documentSnapshot);
                            } else{
                                Log.i("activity", "does not exist");
                            }
                        }
                    }
                });

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.i("recyclerview", "onCreateViewHolder");
                RecyclerView.ViewHolder viewHolder;
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.rest_feed, viewGroup, false);
                viewHolder = new RestFeedHolder(view);
                return viewHolder;
            }

            @Override
            public int getItemCount() {
                int c = super.getItemCount();
                Log.i("count", Integer.toString(c));
                return c;
            }
        };

        return adapter;

    }

    public static  FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>
    getPersonDetailAdapter(final LifecycleOwner lifecycleOwner,
                           final Context context,
                           final String personLink){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query bQuery = db.collection("own_activities")
                .document(personLink)
                .collection("act").orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<ActivityResponse> options = new FirestorePagingOptions.Builder<ActivityResponse>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, ActivityResponse.class).build();

        FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
        adapter = new FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position, @NonNull final ActivityResponse model) {
                Log.i("recyclerview", "onBindViewHolder");
                Log.i("currentlistsize", Integer.toString(this.getCurrentList().size()));
                if(holder instanceof PersonDetailHeaderHolder) {
                    ((PersonDetailHeaderHolder) holder).bindTo(context, personLink);
                    return;
                }
                // because holder is used in inner method
                ((BaseHomeFeedHolder)holder).refreshHolder();
                final RecyclerView.ViewHolder holder1 = holder;
                Log.i("holder1", holder1.getClass().toString());
                Log.i("TUHIN-DEBUG", model.getLink());
                String activityLink = model.getLink();
                // activityLink cannot be used because the position doesn't match
//                DocumentReference docRef = db.collection("activities").document(this.getCurrentList().get(position-1).getString("l"));
                DocumentReference docRef = db.collection("activities").document(this.getCurrentList().get(position).getString("l"));
                Log.i("download", "start");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("download", "finish");
                        if(task.isSuccessful()){
                            final DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                if(holder1 instanceof PostTagHolder){
                                    ((PostTagHolder) holder1).bindTo(context, documentSnapshot, model);
                                }else{
                                    ((BaseHomeFeedHolder)holder1).bindTo(context, documentSnapshot);
                                }
                            }

                        }
                    }
                });

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.i("recyclerview", "onCreateViewHolder");
                RecyclerView.ViewHolder viewHolder;
                View view;
                switch(i){
                    case 0:
                        // a person posts
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.post_half, viewGroup, false);
                        viewHolder = new HalfPostHolder(view);
                        break;
                    case 1:
                        // a person gets tagged in a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_tag_post, viewGroup, false);
                        viewHolder = new PostTagHolder(view);
                        break;
                    case 2:
                        // a restaurant posts
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.rest_feed, viewGroup, false);
                        viewHolder = new RestFeedHolder(view);
                        break;
                    case 3:
                        // a person likes a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_tag_post, viewGroup, false);
                        viewHolder = new PostLikeTagHolder(view);
                        break;
                    case 4:
                        // a person likes a restaurant feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedLikeHolder(view);
                        break;
                    case 5:
                        // a person comments on a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.comment_post, viewGroup, false);
                        viewHolder = new PostCommentHolder(view);
                        break;
                    case 7:
                        // a person replies to a comment on a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.reply_post, viewGroup, false);
                        viewHolder = new PostReplyHolder(view);
                        break;

                    case 6:
                        // a person comments on a rest feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.comment_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedCommentHolder(view);
                        break;
                    case 8:
                        // a person replies to comment on rest feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.reply_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedReplyHolder(view);
                        break;
                    default:
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.person_detail_header, viewGroup, false);
                        viewHolder = new PersonDetailHeaderHolder(view);
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                if(position==0) return 100;
                Log.i("recyclerview", "getItemViewType");
                Log.i("position", Integer.toString(position));
                // damn!!! this is clever !
//                DocumentSnapshot documentSnapshot = this.getCurrentList().get(position-1);
                DocumentSnapshot documentSnapshot = this.getCurrentList().get(position);
                Long type = (Long) documentSnapshot.get("t");
                Log.i("type", Long.toString(type));
                return type.intValue();
            }

            @Override
            public int getItemCount() {
                int c = super.getItemCount();
                Log.i("count", Integer.toString(c));
                return c;
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state){
                    case LOADING_INITIAL:
                        Log.i("loadingstate", "loading initial");
                        break;
                    case LOADING_MORE:
                        Log.i("loadingstate", "loading more");
                        break;
                    case LOADED:
                        Log.i("loadingstate", "loading done");
                        break;
                    case FINISHED:
                        Log.i("loadingstate", "loading finished");
                        break;
                    case ERROR:
                        Log.i("loadingstate", "error occured");
                }
            }
        };

        return adapter;

    }

    public static  FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>
    getEditPersonProfileAdapter(final LifecycleOwner lifecycleOwner,
                           final Context context,
                           final String personLink){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query bQuery = db.collection("own_activities")
                .document(personLink)
                .collection("act")
                .orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<ActivityResponse> options = new FirestorePagingOptions.Builder<ActivityResponse>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, ActivityResponse.class).build();

        FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder> adapter;
        adapter = new FirestorePagingAdapter<ActivityResponse, RecyclerView.ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position, @NonNull final ActivityResponse model) {
                Log.i("recyclerview", "onBindViewHolder");
                Log.i("currentlistsize", Integer.toString(this.getCurrentList().size()));
                if(holder instanceof EditPersonProfileHeaderHolder) {
                    ((EditPersonProfileHeaderHolder) holder).bindTo(context, personLink);
                    return;
                }

                ((BaseHomeFeedHolder)holder).refreshHolder();
                // because holder is used in inner method
                final RecyclerView.ViewHolder holder1 = holder;
                Log.i("holder1", holder1.getClass().toString());
                Log.i("TUHIN-DEBUG", model.getLink());
                String activityLink = model.getLink();
                // activityLink cannot be used because the position doesn't match
//                DocumentReference docRef = db.collection("activities").document(this.getCurrentList().get(position-1).getString("l"));
                DocumentReference docRef = db.collection("activities").document(this.getCurrentList().get(position).getString("l"));
                Log.i("download", "start");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("download", "finish");
                        if(task.isSuccessful()){
                            final DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                if(holder1 instanceof PostTagHolder){
                                    ((PostTagHolder) holder1).bindTo(context, documentSnapshot, model);
                                }else{
                                    ((BaseHomeFeedHolder)holder1).bindTo(context, documentSnapshot);
                                }
                            }

                        }
                    }
                });

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.i("recyclerview", "onCreateViewHolder");
                RecyclerView.ViewHolder viewHolder;
                View view;
                switch(i){
                    case 0:
                        // a person posts
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.post_half, viewGroup, false);
                        viewHolder = new HalfPostHolder(view);
                        break;
                    case 1:
                        // a person gets tagged in a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_tag_post, viewGroup, false);
                        viewHolder = new PostTagHolder(view);
                        break;
                    case 2:
                        // a restaurant posts
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.rest_feed, viewGroup, false);
                        viewHolder = new RestFeedHolder(view);
                        break;
                    case 3:
                        // a person likes a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_tag_post, viewGroup, false);
                        viewHolder = new PostLikeTagHolder(view);
                        break;
                    case 4:
                        // a person likes a restaurant feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.like_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedLikeHolder(view);
                        break;
                    case 5:
                        // a person comments on a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.comment_post, viewGroup, false);
                        viewHolder = new PostCommentHolder(view);
                        break;
                    case 7:
                        // a person replies to a comment on a post
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.reply_post, viewGroup, false);
                        viewHolder = new PostReplyHolder(view);
                        break;

                    case 6:
                        // a person comments on a rest feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.comment_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedCommentHolder(view);
                        break;
                    case 8:
                        // a person replies to comment on rest feed
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.reply_rest_feed, viewGroup, false);
                        viewHolder = new RestFeedReplyHolder(view);
                        break;
                    default:
                        view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.edit_person_profile_header, viewGroup, false);
                        viewHolder = new EditPersonProfileHeaderHolder(view);
                        break;
                }
                return viewHolder;
            }

            @Override
            public int getItemViewType(int position) {
                if(position==0) return 100;
                Log.i("recyclerview", "getItemViewType");
                Log.i("position", Integer.toString(position));
                // damn!!! this is clever !
//                DocumentSnapshot documentSnapshot = this.getCurrentList().get(position-1);
                DocumentSnapshot documentSnapshot = this.getCurrentList().get(position);
                Long type = (Long) documentSnapshot.get("t");
                Log.i("type", Long.toString(type));
                return type.intValue();
            }

            @Override
            public int getItemCount() {
                int c = super.getItemCount();
                Log.i("count", Integer.toString(c));
                return c;
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state){
                    case LOADING_INITIAL:
                        Log.i("loadingstate", "loading initial");
                        break;
                    case LOADING_MORE:
                        Log.i("loadingstate", "loading more");
                        break;
                    case LOADED:
                        Log.i("loadingstate", "loading done");
                        break;
                    case FINISHED:
                        Log.i("loadingstate", "loading finished");
                        break;
                    case ERROR:
                        Log.i("loadingstate", "error occured");
                }
            }
        };
        return adapter;
    }

    public static  FirestorePagingAdapter<PostModel, AlternatePostHolder>
    getHomeFeedAlternativeAdapter(final LifecycleOwner lifecycleOwner,
                       final Context context,
                       final String district){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query bQuery = db.collection("posts")
                // needs composite index
                .whereEqualTo(FirestoreFieldNames.POSTS_DISTRICT, district)
                .orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<PostModel> options = new FirestorePagingOptions.Builder<PostModel>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, PostModel.class).build();

        FirestorePagingAdapter<PostModel, AlternatePostHolder> adapter;
        adapter = new FirestorePagingAdapter<PostModel, AlternatePostHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final AlternatePostHolder holder, final int position, @NonNull final PostModel model) {
                DocumentSnapshot postSnapshot = this.getCurrentList().get(position);
                holder.bindTo(context, postSnapshot);
            }

            @NonNull
            @Override
            public AlternatePostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                AlternatePostHolder viewHolder;
                View view;
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.post_half, viewGroup, false);
                viewHolder = new AlternatePostHolder(view);
                return viewHolder;
            }

            @Override
            public int getItemCount() {
                int c = super.getItemCount();
                Log.i("count", Integer.toString(c));
                return c;
            }

        };

        return adapter;

    }
}
