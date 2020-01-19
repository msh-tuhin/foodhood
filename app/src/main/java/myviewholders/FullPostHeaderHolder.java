package myviewholders;

import android.app.Activity;
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

import models.ActualActivity;

import site.sht.bd.foodhood.FullPost;
import site.sht.bd.foodhood.MorePeole;
import site.sht.bd.foodhood.PersonDetail;
import site.sht.bd.foodhood.R;
import site.sht.bd.foodhood.RestDetail;
import site.sht.bd.foodhood.RestaurantAllDishes;
import site.sht.bd.foodhood.WriteComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.DateTimeExtractor;
import myapp.utils.EntryPoints;
import myapp.utils.PictureBinder;
import myapp.utils.PostBuilder;
import myapp.utils.PostImagesAdapter;
import myapp.utils.SourceMorePeople;

public class FullPostHeaderHolder extends RecyclerView.ViewHolder
        implements PostHolderInterface{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public Context mContext;
    public DocumentReference mPostReference;
    public String mPostLink;
    public PostBuilder mPostBuilder;
    public DocumentSnapshot mPostSnapShot;
    public Task<DocumentSnapshot> mTaskPost;

    public LinearLayout personLayout;
    public CircleImageView profileImage;
    public TextView namePostedBy;
    public TextView postTime;
    public TextView postCaption;
    public TextView restaurantName;
    public TextView taggedPeople;
    public TextView dishes;
    public TextView viewPagerCurrentPositionTV;
    public ViewPager postImagesViewPager;
    // com_n_like.xml
    public TextView noOfLikesTV;
    public TextView noOfCommentsTV;
    public ImageView like;
    public ImageView comment;

    FrameLayout restaurantFeedbackLayout;
    LinearLayout dishesFeedbackLayout;

    public FullPostHeaderHolder(View v){
        super(v);
        personLayout = v.findViewById(R.id.person_layout);
        profileImage = v.findViewById(R.id.profile_image);
        namePostedBy = v.findViewById(R.id.name);
        restaurantName = v.findViewById(R.id.restaurant_name);
        taggedPeople = v.findViewById(R.id.tagged_people);
        dishes = v.findViewById(R.id.dishes);
        postTime = v.findViewById(R.id.post_time);
        postCaption = v.findViewById(R.id.post_caption);
        viewPagerCurrentPositionTV = v.findViewById(R.id.image_position);
        postImagesViewPager = v.findViewById(R.id.post_images_viewPager);
        noOfLikesTV = v.findViewById(R.id.no_likes);
        noOfCommentsTV = v.findViewById(R.id.no_comments);
        like = v.findViewById(R.id.like);
        comment = v.findViewById(R.id.comment);

        restaurantFeedbackLayout = v.findViewById(R.id.rest_feedback_layout);
        dishesFeedbackLayout = v.findViewById(R.id.dishes_feedback_layout);
    }


    public void bindTo(Context context,
                       Task<DocumentSnapshot> taskPost,
                       String postLink){
        Log.i("bindTo", this.getClass().toString());

        refreshHolder();

        setmContext(context);
        setmTaskPost(taskPost);
        setmPostLink(postLink);
        setmPostReference(postLink);

        setBindValuesOnClickListenersDependentOnPostDownload();
        bindValuesIndependent();
        setOnClickListenersIndependent();

        addTheFeedbacks();
    }

    public void setmContext(Context context){
        mContext = context;
    }

    public void setmPostLink(String postLink){
        mPostLink = postLink;
    }

    public void setmPostReference(String postLink){
        mPostReference = db.collection("posts").document(postLink);
    }

    public void setmPostSnapshot(DocumentSnapshot post){
        mPostSnapShot = post;
    }

    public void setmPostBuilder(Context context, DocumentSnapshot post){
        mPostBuilder = new PostBuilder(context, post);
    }

    public void setmTaskPost(Task<DocumentSnapshot> taskPost) {
        mTaskPost = taskPost;
    }

    public void setBindValuesOnClickListenersDependentOnPostDownload(){
        mTaskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("Post", "downloaded");
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

    public void bindValuesDependentOnPostDownload() {
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

    public void bindValuesIndependent(){
        bindGoToFull();
        bindCommentIcon();
    }

    public void setOnClickListenersDependentOnPostDownload(){
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

    public void setOnClickListenersIndependent(){
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
        // Map<String, String> person = (Map<String, String>)mPostSnapShot.get("w");
        // if(person==null) return;
        // String personLink = person.get("l");
        String personLink = mPostBuilder.getLinkToPostedBy();
        if(personLink==null || personLink.equals("")) return;
        db.collection("person_vital")
                .document(personLink)
                .get()
                .addOnSuccessListener((Activity)mContext, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                        PictureBinder.bindProfilePicture(profileImage, personVitalSnapshot);
                    }
                });
    }

    @Override
    public void setAvatarOnClickListener() {
        final String personLink = mPostBuilder.getLinkToPostedBy();
        if(personLink==null || personLink.equals("")) return;
        if(personLink.equals(mAuth.getCurrentUser().getUid())) {
            return;
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", personLink);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNamePostedBy() {
        namePostedBy.setText(mPostBuilder.getNamePostedBy());
    }

    @Override
    public void setNamePostedByOnClickListener() {
        if(mPostBuilder.getLinkToPostedBy().equals(mAuth.getCurrentUser().getUid())){
            return;
        }
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
        Timestamp ts = mPostBuilder.getPostTime();
        if(ts==null) return;
        String dateOrTimeString = DateTimeExtractor.getDateOrTimeString(ts);
        postTime.setText(dateOrTimeString);
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
        String tpText = mPostBuilder.getPeopleText();
        if(tpText==null || tpText.equals("")){
            return;
        }
        Log.i("tagged_people", tpText);
        taggedPeople.setText(tpText);
        personLayout.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(mContext, RestaurantAllDishes.class);
                intent.putStringArrayListExtra("dishesList",
                        mPostBuilder.getSortedDishLinks());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindCaption() {
        String caption = mPostBuilder.getCaption();
        if(caption==null || caption.equals("")) return;
        postCaption.setText(caption);
        postCaption.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCaptionOnClickListener() {

    }

    @Override
    public void bindImages() {
        ArrayList<String> imageUris = (ArrayList) mPostSnapShot.get("i");
        if(imageUris==null || imageUris.size() == 0) return;
        postImagesViewPager.setVisibility(View.VISIBLE);
        postImagesViewPager.setOffscreenPageLimit(2);
        final PostImagesAdapter adapter = new PostImagesAdapter(mContext, imageUris);
        postImagesViewPager.setAdapter(adapter);
        viewPagerCurrentPositionTV.setText("1" + "/" + Integer.toString(adapter.imageUris.size()));
        viewPagerCurrentPositionTV.setVisibility(View.VISIBLE);
        postImagesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerCurrentPositionTV.setText(Integer.toString(position+1)+"/" +
                        Integer.toString(adapter.imageUris.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        String likedBy = mAuth.getCurrentUser().getUid();
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
                    decreaseNumOfLikes();
                }else{
                    like.setImageResource(R.drawable.baseline_favorite_black_24dp);
                    addLikeToPost();
                    increaseNumOfLikes();
                    createActivityForLike();
                }
            }
        });
    }

    @Override
    public void bindNoOfLike() {
        List<String> likes = (List<String>) mPostSnapShot.get("l");
        int numOfLikes = 0;
        if(likes != null){
            numOfLikes = likes.size();
        }
        noOfLikesTV.setText(Integer.toString(numOfLikes));
    }

    @Override
    public void setNoOfLikeOnClickListener() {
        noOfLikesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "no of likes");
                ArrayList<String> likes = (ArrayList<String>) mPostSnapShot.get("l");
                boolean isLikeFilled = like.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext,
                                R.drawable.baseline_favorite_black_24dp).getConstantState());
                if(isLikeFilled){
                    if(!likes.contains(mAuth.getCurrentUser().getUid())){
                        likes.add(mAuth.getCurrentUser().getUid());
                    }
                }else{
                    likes.remove(mAuth.getCurrentUser().getUid());
                }
                Intent intent = new Intent(mContext, MorePeole.class);
                intent.putExtra("source", SourceMorePeople.LIKERS_POST);
                intent.putExtra("postLink", mPostLink);
                intent.putStringArrayListExtra("personsList", likes);
                mContext.startActivity(intent);
            }
        });
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
        List<String> comments = (List<String>) mPostSnapShot.get("coms");
        int numOfComments = 0;
        if(comments != null){
            numOfComments = comments.size();
        }
        noOfCommentsTV.setText(Integer.toString(numOfComments));
    }

    @Override
    public void setNoOfCommentOnClickListener() {

    }

    public void addLikeToPost(){
        String likedBy = mAuth.getCurrentUser().getUid();
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

    public void removeLikeFromPost(){
        String likedBy = mAuth.getCurrentUser().getUid();
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

    public void createActivityForLike(){
        final String likedBy = mAuth.getCurrentUser().getUid();
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

    public void addTheFeedbacks(){
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

    public void addRestaurantFeedback(DocumentSnapshot post){
        String restaurantFeedback = post.getString("rf");
        if(restaurantFeedback==null || restaurantFeedback.equals("")) return;
        final Map<String, String> restaurant = (Map<String, String>) post.get("r");
        db.collection("feedbacks").document(restaurantFeedback)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot feedback = task.getResult();
                    if(feedback.exists()){
                        View view = getViewForRestaurantFeedback(feedback, restaurant);
                        restaurantFeedbackLayout.findViewById(R.id.not_found).setVisibility(View.GONE);
                        restaurantFeedbackLayout.addView(view);

                    }
                }
            }
        });
    }

    public View getViewForRestaurantFeedback(DocumentSnapshot restaurantFeedbackSnapshot,
                                             Map<String, String> restaurant){
        // maybe restaurantFeedbackSnapshot is never null
        String restLink = restaurant==null ? null : restaurant.get("l");
        String restName = restaurant==null ? null : restaurant.get("n");
        Double ratingDouble = restaurantFeedbackSnapshot.getDouble("r");
        float rating = ratingDouble==null ? 0.0f:ratingDouble.floatValue();
        View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_full_post, null);
        String review = restaurantFeedbackSnapshot.getString("re");
        bindRestaurantFeedbackImage((ImageView)view.findViewById(R.id.dish_avatar), restLink);
        bindFeedbackNameRatingReview(view, restName, rating, review);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    public void addDishFeedbacks(DocumentSnapshot post){
        final Map<String, String> dishes = (Map<String, String>) post.get("d");
        List<String> dishFeedbacksList = (List<String>) post.get("f");
        addEmptyViews(dishFeedbacksList);
        int i = -1;
        for(final String dishFeedback : dishFeedbacksList){
            i++;
            final int j = i;
            db.collection("feedbacks").document(dishFeedback).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot feedback = task.getResult();
                                if(feedback.exists()){
                                    Log.i("feedback", "adding");
                                    View view = dishesFeedbackLayout.getChildAt(j);
                                    bindDishFeedbackView(view, feedback, dishes);
                                }
                            }
                        }
                    });

        }
    }

    public void addEmptyViews(List<String> dishFeedbacksList){
        for(int i=0; i<dishFeedbacksList.size(); i++){
            Log.i("child #: ", Integer.toString(i));
            View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_full_post, null);
            if(dishesFeedbackLayout.getChildAt(i) != null){
                dishesFeedbackLayout.removeViewAt(i);
            }
            dishesFeedbackLayout.addView(view, i);
        }
    }

    public void bindDishFeedbackView(View view, DocumentSnapshot dishFeedbackSnapshot,
                                       Map<String, String> dishes) {
        Double ratingDouble = dishFeedbackSnapshot.getDouble("r");
        float rating = ratingDouble==null ? 0.0f:ratingDouble.floatValue();
        String dishLink = dishFeedbackSnapshot.getString("wh");
        String review = dishFeedbackSnapshot.getString("re");
        bindDishFeedbackImage((ImageView)view.findViewById(R.id.dish_avatar), dishLink);
        bindFeedbackNameRatingReview(view, dishes.get(dishLink), rating, review);
    }

    private void bindFeedbackNameRatingReview(View view, String name,
                                              float rating, String review){
        if(name!=null){
            ((TextView)view.findViewById(R.id.dish_name)).setText(name);
        }else{
            ((TextView)view.findViewById(R.id.dish_name)).setText("");
        }
        if(review!=null && !review.equals("")){
            ((TextView)view.findViewById(R.id.review)).setText(review);
            // view.findViewById(R.id.review).setVisibility(View.VISIBLE);
            // view.findViewById(R.id.review_header).setVisibility(View.VISIBLE);
        }else{
            ((TextView)view.findViewById(R.id.review)).setText("N/A");
        }
        // rating is never null
        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setRating(rating);
        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setIsIndicator(true);
    }

    private void bindDishFeedbackImage(final ImageView view, String dishLink){
        if(dishLink==null || dishLink.equals("")) return;
        db.collection("dish_vital").document(dishLink)
                .get()
                .addOnSuccessListener((Activity) mContext, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            PictureBinder.bindCoverPicture(view, documentSnapshot);
                        }
                    }
                });
    }

    private void bindRestaurantFeedbackImage(final ImageView view, String restaurantLink){
        if(restaurantLink==null || restaurantLink.equals("")) return;
        db.collection("rest_vital").document(restaurantLink)
                .get()
                .addOnSuccessListener((Activity) mContext, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            PictureBinder.bindCoverPicture(view, documentSnapshot);
                        }
                    }
                });
    }

    public void decreaseNumOfLikes(){
        String str = (String) noOfLikesTV.getText();
        int numOfLikes = Integer.valueOf(str);
        noOfLikesTV.setText(Integer.toString(numOfLikes-1));
    }

    public void increaseNumOfLikes(){
        String str = noOfLikesTV.getText().toString();
        int numOfLikes = Integer.valueOf(str);
        noOfLikesTV.setText(Integer.toString(numOfLikes+1));
    }

    private void refreshHolder(){
        profileImage.setImageResource(R.drawable.ltgray);
        namePostedBy.setText("");
        postTime.setText("");
        postCaption.setText("");
        postCaption.setVisibility(View.GONE);
        restaurantName.setText("");
        taggedPeople.setText("");
        personLayout.setVisibility(View.GONE);
        dishes.setText("");
        viewPagerCurrentPositionTV.setVisibility(View.GONE);
        postImagesViewPager.setAdapter(null);
        postImagesViewPager.setVisibility(View.GONE);
        noOfLikesTV.setText("0");
        noOfCommentsTV.setText("0");
    }
}
