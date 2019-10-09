package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.ActualActivity;
import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.OnCompleteListener;
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

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myapp.utils.PostBuilder;
import myapp.utils.SourceAllDishes;

public class HalfPostHolder extends BaseHomeFeedHolder
        implements PostHolderInterface{

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private Context mContext;
    private DocumentReference mPostReference;
    private String mPostLink;
    private PostBuilder mPostBuilder;
    private DocumentSnapshot mPostSnapShot;

    public CircleImageView profileImage;
    public TextView namePostedBy;
    public TextView postTime;
    public TextView postCaption;
    public TextView restaurantName;
    public TextView taggedPeople;
    public TextView dishes;
    public ImageView postImages;
    public TextView goToFull;
    // com_n_like.xml
    public TextView noOfLikes;
    public TextView noOfComments;
    public ImageView like;
    public ImageView comment;


    public HalfPostHolder(@NonNull View v) {
        super(v);
        profileImage = v.findViewById(R.id.profile_image);
        namePostedBy = v.findViewById(R.id.name);
        restaurantName = v.findViewById(R.id.restaurant_name);
        taggedPeople = v.findViewById(R.id.tagged_people);
        dishes = v.findViewById(R.id.dishes);
        postTime = v.findViewById(R.id.post_time);
        postCaption = v.findViewById(R.id.post_caption);
        postImages = v.findViewById(R.id.post_images);
        goToFull = v.findViewById(R.id.go_to_full);
        noOfLikes = v.findViewById(R.id.no_likes);
        noOfComments = v.findViewById(R.id.no_comments);
        like = v.findViewById(R.id.like);
        comment = v.findViewById(R.id.comment);
    }

    public void bindTo(Context context, DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event
        setmContext(context);
        setmPostLink(activity.getString("wh"));
        setmPostReference(activity.getString("wh"));
        bindValuesIndependent();
        setOnClickListenersIndependent();
        setBindValuesOnClickListenersDependentOnPostDownload();
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

    private void setBindValuesOnClickListenersDependentOnPostDownload(){
        mPostReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        goToFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullPost.class);
                intent.putExtra("entry_point", EntryPoints.CLICKED_GO_TO_FULL_POST);
                intent.putExtra("postLink", mPostLink);
                mContext.startActivity(intent);
            }
        });
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
                commentIntentExtra.setEntryPoint(EntryPoints.COMMENT_ON_HOME_POST);
                commentIntentExtra.setPostLink(mPostLink);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
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
}
