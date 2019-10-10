package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActualActivity;
import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.CreatePostAddDishes;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myapp.utils.PostBuilder;
import myapp.utils.SourceAllDishes;

public class FullPostHeaderHolder extends RecyclerView.ViewHolder
        implements PostHolderInterface{

    FirebaseFirestore db;
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private Context mContext;
    private DocumentReference mPostReference;
    private String mPostLink;
    private PostBuilder mPostBuilder;
    private DocumentSnapshot mPostSnapShot;
    private Task<DocumentSnapshot> mTaskPost;

    public CircleImageView profileImage;
    public TextView namePostedBy;
    public TextView postTime;
    public TextView postCaption;
    public TextView restaurantName;
    public TextView taggedPeople;
    public TextView dishes;
    public ImageView postImages;
    // com_n_like.xml
    public TextView noOfLikes;
    public TextView noOfComments;
    public ImageView like;
    public ImageView comment;

    FrameLayout restaurantFeedbackLayout;
    LinearLayout dishesFeedbackLayout;

    public FullPostHeaderHolder(View v){
        super(v);
        profileImage = v.findViewById(R.id.profile_image);
        namePostedBy = v.findViewById(R.id.name);
        restaurantName = v.findViewById(R.id.restaurant_name);
        taggedPeople = v.findViewById(R.id.tagged_people);
        dishes = v.findViewById(R.id.dishes);
        postTime = v.findViewById(R.id.post_time);
        postCaption = v.findViewById(R.id.post_caption);
        postImages = v.findViewById(R.id.post_images);
        noOfLikes = v.findViewById(R.id.no_likes);
        noOfComments = v.findViewById(R.id.no_comments);
        like = v.findViewById(R.id.like);
        comment = v.findViewById(R.id.comment);

        restaurantFeedbackLayout = v.findViewById(R.id.rest_feedback_layout);
        dishesFeedbackLayout = v.findViewById(R.id.dishes_feedback_layout);
    }


    public void bindTo(Context context,
                       Task<DocumentSnapshot> taskPost,
                       String postLink){
        Log.i("bindTo", this.getClass().toString());
        db = FirebaseFirestore.getInstance();

        setmContext(context);
        setmTaskPost(taskPost);
        setmPostLink(postLink);
        setmPostReference(postLink);

        setBindValuesOnClickListenersDependentOnPostDownload();
        bindValuesIndependent();
        setOnClickListenersIndependent();

        addTheFeedbacks();
    }

    private void setmContext(Context context){
        mContext = context;
    }

    private void setmPostLink(String postLink){
        mPostLink = postLink;
    }

    private void setmPostReference(String postLink){
        mPostReference = db.collection("posts").document(postLink);
    }

    private void setmPostSnapshot(DocumentSnapshot post){
        mPostSnapShot = post;
    }

    private void setmPostBuilder(Context context, DocumentSnapshot post){
        mPostBuilder = new PostBuilder(context, post);
    }

    private void setmTaskPost(Task<DocumentSnapshot> taskPost) {
        mTaskPost = taskPost;
    }

    private void setBindValuesOnClickListenersDependentOnPostDownload(){
        mTaskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot post = task.getResult();
                if(post.exists()){
                    setmPostSnapshot(post);
                    setmPostBuilder(mContext, post);
                    bindValuesDependentOnPostDownload();
                    setOnClickListenersDependentOnPostDownload();
                }
            }
        });
    }

    private void bindValuesDependentOnPostDownload() {
        bindHeader();
        bindAvatar();
        bindNamePostedBy();
        bindPostTime();
        bindRestaurantName();
        bindTaggedPeople();
        bindDishes();
        bindCaption();
        bindImages();
        bindLikeIcon();
        bindNoOfLike();
        bindNoOfComment();
    }

    private void bindValuesIndependent(){
        bindGoToFull();
        bindCommentIcon();
    }

    private void setOnClickListenersDependentOnPostDownload(){
        setHeaderOnClickListener();
        setAvatarOnClickListener();
        setNamePostedByOnClickListener();
        setPostTimeOnClickListener();
        setRestaurantNameOnClickListener();
        setTaggedPeopleOnClickListener();
        setDishesOnClickListener();
        setCaptionOnClickListener();
        setImagesOnClickListener();
        setNoOfLikeOnClickListener();
        setNoOfCommentOnClickListener();
    }

    private void setOnClickListenersIndependent(){
        setGoToFullOnClickListener();
        setLikeIconOnClickListener();
        setCommentIconOnClickListener();
    }

    @Override
    public void bindHeader() {

    }

    @Override
    public void setHeaderOnClickListener() {

    }

    @Override
    public void bindAvatar() {

    }

    @Override
    public void setAvatarOnClickListener() {

    }

    @Override
    public void bindNamePostedBy() {
        namePostedBy.setText(mPostBuilder.getNamePostedBy());
    }

    @Override
    public void setNamePostedByOnClickListener() {
        namePostedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink",
                        mPostBuilder.getLinkToPostedBy());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindPostTime() {

    }

    @Override
    public void setPostTimeOnClickListener() {

    }

    @Override
    public void bindRestaurantName() {
        restaurantName.setText(mPostBuilder.getRestaurantName());
    }

    @Override
    public void setRestaurantNameOnClickListener() {
        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestDetail.class);
                intent.putExtra("restaurantLink",
                        mPostBuilder.getRestaurantLink());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindTaggedPeople() {
        taggedPeople.setText(mPostBuilder.getPeopleText());
    }

    @Override
    public void setTaggedPeopleOnClickListener() {
        taggedPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MorePeole.class);
                intent.putStringArrayListExtra("personsList",
                        mPostBuilder.getSortedTaggedPeopleLinks());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindDishes() {
        dishes.setText(mPostBuilder.getDishesText());
    }

    @Override
    public void setDishesOnClickListener() {
        dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AllDishes.class);
                intent.putStringArrayListExtra("dishesList",
                        mPostBuilder.getSortedDishLinks());
                intent.putExtra("source", SourceAllDishes.POST);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindCaption() {
        postCaption.setText(mPostBuilder.getCaption());
    }

    @Override
    public void setCaptionOnClickListener() {

    }

    @Override
    public void bindImages() {

    }

    @Override
    public void setImagesOnClickListener() {

    }

    @Override
    public void bindGoToFull() {

    }

    @Override
    public void setGoToFullOnClickListener() {

    }

    @Override
    public void bindLikeIcon() {
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> likers = (List<String>) mPostSnapShot.get("l");
        // find if current user has already liked this post
        if(likers.contains(likedBy)){
            like.setImageResource(R.drawable.baseline_favorite_black_24dp);
        }else{
            like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
        }
    }

    @Override
    public void setLikeIconOnClickListener() {
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LIKE", "CLICKED");
                mFunctions.getHttpsCallable("printMessage").call().addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        if(task.isSuccessful()){
                            String s = (String)task.getResult().getData();
                            Log.i("function-data", s);
                        }
                    }
                });
                boolean isLikeFilled = like.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext,
                                R.drawable.baseline_favorite_black_24dp).getConstantState());
                if(isLikeFilled){
                    like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
                    removeLikeFromPost();
                }else{
                    like.setImageResource(R.drawable.baseline_favorite_black_24dp);
                    addLikeToPost();
                    createActivityForLike();
                }
            }
        });
    }

    @Override
    public void bindNoOfLike() {

    }

    @Override
    public void setNoOfLikeOnClickListener() {

    }

    @Override
    public void bindCommentIcon() {

    }

    @Override
    public void setCommentIconOnClickListener() {
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.COMMENT_ON_FULL_POST);
                commentIntentExtra.setPostLink(mPostLink);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                ((FullPost)mContext).startActivityForResult(intent,
                        ((FullPost)mContext).REQUEST_COMMENT);
            }
        });
    }

    @Override
    public void bindNoOfComment() {

    }

    @Override
    public void setNoOfCommentOnClickListener() {

    }

    private void addLikeToPost(){
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPostReference.update("l", FieldValue.arrayUnion(likedBy))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i("UPDATE", "SUCCESSFUL");
                        }else{
                            Exception e = task.getException();
                            Log.i("UPDATE", e.getMessage());
                        }
                    }
                });
    }

    private void removeLikeFromPost(){
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPostReference.update("l", FieldValue.arrayRemove(likedBy))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i("UPDATE", "SUCCESSFUL");
                        }else{
                            Exception e = task.getException();
                            Log.i("UPDATE", e.getMessage());
                        }
                    }
                });
    }

    private void createActivityForLike(){
        final String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DocumentReference postsLikedByUserRef = db.collection("liked_once").document(likedBy);
        postsLikedByUserRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                List<String> postsOnceLiked = (List<String>) documentSnapshot.get("a");
                                if(postsOnceLiked.contains(mPostLink)){
                                    Log.i("LIKED_ALREADY", "YES");
                                }else{
                                    Log.i("LIKED_ALREADY", "NO");
                                    postsLikedByUserRef.update("a", FieldValue.arrayUnion(mPostLink));
                                    ActualActivity likeActivity = new ActualActivity();
                                    likeActivity.setT(3);
                                    Map<String, String> who = new HashMap<>();
                                    who.put("l", likedBy);
                                    likeActivity.setW(who);
                                    likeActivity.setWh(mPostLink);
                                    db.collection("activities").add(likeActivity)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if(task.isSuccessful()){
                                                        DocumentReference docRef = task.getResult();
                                                        Log.i("new_activity_at", docRef.getId());
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                    }
                });
    }

    private void addTheFeedbacks(){
        mTaskPost.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot post) {
                if(post.exists()){
                    Log.i("documentsnapshot", post.toString());
                    Log.i("before", "rest");
                    addRestaurantFeedback(post);
                    Log.i("before", "dish");
                    addDishFeedbacks(post);
                }
            }
        });
    }

    private void addRestaurantFeedback(DocumentSnapshot post){
        String restaurantFeedback = post.getString("rf");
        final Map<String, String> restaurant = (Map<String, String>) post.get("r");
        db.collection("feedbacks").document(restaurantFeedback)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot feedback = task.getResult();
                    if(feedback.exists()){
                        View view = getViewForRestaurantFeedback(feedback, restaurant.get("n"));
                        restaurantFeedbackLayout.findViewById(R.id.not_found).setVisibility(View.GONE);
                        restaurantFeedbackLayout.addView(view);

                    }
                }
            }
        });
    }

    private View getViewForRestaurantFeedback(DocumentSnapshot restaurantFeedbackSnapshot,
                                              String restaurantName){
        float rating = restaurantFeedbackSnapshot.getDouble("r").floatValue();
        View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_full_post, null);
        String review = restaurantFeedbackSnapshot.getString("re");
        bindFeedbackView(view, restaurantName, rating, review);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    private void addDishFeedbacks(DocumentSnapshot post){
        final Map<String, String> dishes = (Map<String, String>) post.get("d");
        List<String> dishFeedbacksList = (List<String>) post.get("f");
        for(final String dishFeedback : dishFeedbacksList){
            db.collection("feedbacks").document(dishFeedback).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot feedback = task.getResult();
                                if(feedback.exists()){
                                    View view = getViewForDishFeedback(feedback, dishes);
                                    dishesFeedbackLayout.addView(view);
                                }
                            }
                        }
                    });

        }
    }

    private View getViewForDishFeedback(DocumentSnapshot dishFeedbackSnapshot,
                                        Map<String, String> dishes){
        float rating = dishFeedbackSnapshot.getDouble("r").floatValue();
        String dishLink = dishFeedbackSnapshot.getString("wh");
        View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_full_post, null);
        String review = dishFeedbackSnapshot.getString("re");
        bindFeedbackView(view, dishes.get(dishLink), rating, review);
        return view;
    }

    private void bindFeedbackView(View view, String name,
                                  float rating, String review){
        ((TextView)view.findViewById(R.id.dish_name)).setText(name);
        ((TextView)view.findViewById(R.id.review)).setText(review);
        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setRating(rating);
        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setIsIndicator(true);
    }
}
