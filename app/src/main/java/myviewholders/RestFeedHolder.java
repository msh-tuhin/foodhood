package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActualActivity;
import com.example.tuhin.myapplication.FullRestFeed;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firestore.v1.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestFeedHolder extends BaseHomeFeedHolder
        implements RestFeedInterface{

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private Context mContext;
    private String mRestFeedLink;
    private DocumentSnapshot mRestFeedSnapshot;
    private String mRestaurantLink;
    private String mRestaurantName;
    private String mCaption;

    ConstraintLayout restFeedLayout;
    CircleImageView avatar;
    TextView restaurantNameTV;
    TextView postTimeTV;
    TextView captionTV;
    ImageView postImage;
    ImageView like;
    TextView noOfLikesTV;
    ImageView comment;
    TextView noOfCommentsTV;

    public RestFeedHolder(@NonNull View v) {
        super(v);
        restFeedLayout = v.findViewById(R.id.rest_feed_layout);
        avatar = v.findViewById(R.id.avatar);
        restaurantNameTV = v.findViewById(R.id.restaurant_name);
        postTimeTV = v.findViewById(R.id.post_time);
        captionTV = v.findViewById(R.id.caption);
        postImage = v.findViewById(R.id.post_images);
        like = v.findViewById(R.id.like);
        noOfLikesTV = v.findViewById(R.id.no_likes);
        comment = v.findViewById(R.id.comment);
        noOfCommentsTV = v.findViewById(R.id.no_comments);
    }

    public void bindTo(final Context context, final DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event

        setmContext(context);
        setmRestFeedLink(activity.getString("wh"));

        setBindValuesOnClickListenersDependentOnPostDownload();
        bindValuesIndependent();
        setOnClickListenersIndependent();
    }

    private void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    private void setmRestFeedLink(String mRestFeedLink){
        this.mRestFeedLink = mRestFeedLink;
    }

    private void setmRestFeedSnapshot(DocumentSnapshot mRestFeedSnapshot) {
        this.mRestFeedSnapshot = mRestFeedSnapshot;
    }

    private void setmRestaurantLink(String mRestaurantLink) {
        this.mRestaurantLink = mRestaurantLink;
    }

    private void setmRestaurantName(String mRestaurantName) {
        this.mRestaurantName = mRestaurantName;
    }

    private void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    private void setBindValuesOnClickListenersDependentOnPostDownload(){
        DocumentReference restFeedRef = FirebaseFirestore.getInstance()
                .collection("rest_feed")
                .document(mRestFeedLink);
        restFeedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restFeed = task.getResult();
                    if(restFeed.exists()){
                        Map<String, String> restaurant = (Map) restFeed.get("w");
                        String caption = restFeed.getString("c");
                        setmRestFeedSnapshot(restFeed);
                        setmRestaurantLink(restaurant.get("l"));
                        setmRestaurantName(restaurant.get("n"));
                        setmCaption(caption);
                        bindValuesDependentOnRestFeedDownload();
                        setOnClickListenersDependentOnPostDownload();
                    }
                }
            }
        });
    }

    private void bindValuesDependentOnRestFeedDownload(){
        bindHeader();
        bindAvatar();
        bindRestaurantName();
        bindPostTime();
        bindCaption();
        bindPostImages();
        bindLikeIcon();
        bindNoOfLike();
        bindCommentIcon();
        bindNoOfComment();
    }

    private void bindValuesIndependent(){
        bindRestFeedLayout();
    }

    private void setOnClickListenersDependentOnPostDownload(){
        setHeaderOnClickListener();
        setAvatarOnClickListener();
        setRestaurantNameOnClickListener();
        setPostTimeOnClickListener();
        setCaptionOnClickListener();
        setPostImagesOnClickListener();
        setNoOfLikeOnClickListener();
        setNoOfCommentOnClickListener();
    }

    private void setOnClickListenersIndependent(){
        setLikeIconOnClickListener();
        setCommentIconOnClickListener();
        setRestFeedLayoutOnClickListener();
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
    public void bindRestaurantName() {
        restaurantNameTV.setText(mRestaurantName);
    }

    @Override
    public void setRestaurantNameOnClickListener() {
        restaurantNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestDetail.class);
                intent.putExtra("restaurantLink", mRestaurantLink);
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
    public void bindCaption() {
        captionTV.setText(mCaption);
    }

    @Override
    public void setCaptionOnClickListener() {

    }

    @Override
    public void bindPostImages() {

    }

    @Override
    public void setPostImagesOnClickListener() {

    }

    @Override
    public void bindLikeIcon() {

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

    private void addLikeToPost(){
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference restFeedReference = FirebaseFirestore.getInstance()
                .collection("rest_feed")
                .document(mRestFeedLink);
        restFeedReference.update("l", FieldValue.arrayUnion(likedBy))
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
        DocumentReference restFeedReference = FirebaseFirestore.getInstance()
                .collection("rest_feed")
                .document(mRestFeedLink);
        restFeedReference.update("l", FieldValue.arrayRemove(likedBy))
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
                                if(postsOnceLiked.contains(mRestFeedLink)){
                                    Log.i("LIKED_ALREADY", "YES");
                                }else{
                                    Log.i("LIKED_ALREADY", "NO");
                                    postsLikedByUserRef.update("a", FieldValue.arrayUnion(mRestFeedLink));
                                    ActualActivity likeActivity = new ActualActivity();
                                    likeActivity.setT(4);
                                    Map<String, String> who = new HashMap<>();
                                    who.put("l", likedBy);
                                    likeActivity.setW(who);
                                    likeActivity.setWh(mRestFeedLink);
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
                Log.i("comment", "from home page rest feed");
            }
        });
    }

    @Override
    public void bindNoOfComment() {

    }

    @Override
    public void setNoOfCommentOnClickListener() {

    }

    @Override
    public void bindRestFeedLayout() {

    }

    @Override
    public void setRestFeedLayoutOnClickListener() {
        restFeedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("layout", "clicked");
                Intent intent = new Intent(mContext, FullRestFeed.class);
                intent.putExtra("restFeedLink", mRestFeedLink);
                mContext.startActivity(intent);
            }
        });
    }
}
