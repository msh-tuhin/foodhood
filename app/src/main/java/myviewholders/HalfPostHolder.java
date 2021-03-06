package myviewholders;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.DateTimeExtractor;
import myapp.utils.EntryPoints;
import myapp.utils.PictureBinder;
import myapp.utils.PostBuilder;
import myapp.utils.PostImagesAdapter;
import myapp.utils.SourceMorePeople;

public class HalfPostHolder extends BaseHomeFeedHolder
        implements PostHolderInterface{

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-east2");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public Context mContext;
    public DocumentReference mPostReference;
    public String mPostLink;
    public PostBuilder mPostBuilder;
    public DocumentSnapshot mPostSnapShot;

    public LinearLayout parentLayout;
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
    public TextView goToFull;
    // com_n_like.xml
    public TextView noOfLikesTV;
    public TextView noOfCommentsTV;
    public ImageView like;
    public ImageView comment;


    public HalfPostHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
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
        goToFull = v.findViewById(R.id.go_to_full);
        noOfLikesTV = v.findViewById(R.id.no_likes);
        noOfCommentsTV = v.findViewById(R.id.no_comments);
        like = v.findViewById(R.id.like);
        comment = v.findViewById(R.id.comment);
    }

    public void bindTo(Context context, DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event
        refreshHolder();
        setmContext(context);
        setmPostLink(activity.getString("wh"));
        setmPostReference(activity.getString("wh"));
        bindValuesIndependent();
        setOnClickListenersIndependent();
        setBindValuesOnClickListenersDependentOnPostDownload();
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

    public void setBindValuesOnClickListenersDependentOnPostDownload(){
        mPostReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot post = task.getResult();
                    if(post.exists()){
                        setmPostSnapshot(post);
                        setmPostBuilder(mContext, post);
                        bindValuesDependentOnPostDownload();
                        setOnClickListenersDependentOnPostDownload();
                    }
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
        bindParentLayout();
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
        setParentLayoutOnClickListener();
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
        if(personLink.equals(mAuth.getCurrentUser().getUid())){
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
        postCaption.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(mContext, MorePeole.class);
                if(isLikeFilled){
                    if(!likes.contains(mAuth.getCurrentUser().getUid())){
                        likes.add(mAuth.getCurrentUser().getUid());
                    }
                }else{
                    likes.remove(mAuth.getCurrentUser().getUid());
                }
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
        List<String> comments = (List<String>) mPostSnapShot.get("coms");
        int numOfComments = 0;
        if(comments != null){
            numOfComments = comments.size();
        }
        noOfCommentsTV.setText(Integer.toString(numOfComments));
    }

    @Override
    public void setNoOfCommentOnClickListener() {
        noOfCommentsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullPost.class);
                intent.putExtra("entry_point", EntryPoints.CLICKED_GO_TO_FULL_POST);
                intent.putExtra("postLink", mPostLink);
                mContext.startActivity(intent);
            }
        });
    }

    public void bindParentLayout(){

    }

    public void setParentLayoutOnClickListener(){
//        parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, FullPost.class);
//                intent.putExtra("entry_point", EntryPoints.CLICKED_GO_TO_FULL_POST);
//                intent.putExtra("postLink", mPostLink);
//                mContext.startActivity(intent);
//            }
//        });
    }

    private void addLikeToPost(){
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

    private void removeLikeFromPost(){
        String likedBy = mAuth.getCurrentUser().getUid();
        mPostReference.update("l", FieldValue.arrayRemove(likedBy))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i("UPDATE", "SUCCESSFUL");
                            Log.i("UPDATE", "SUCCESSFUL");
                        }else{
                            Exception e = task.getException();
                            Log.i("UPDATE", e.getMessage());
                        }
                    }
                });
    }

    private void createActivityForLike(){
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

    private void decreaseNumOfLikes(){
        String str = (String) noOfLikesTV.getText();
        int numOfLikes = Integer.valueOf(str);
        noOfLikesTV.setText(Integer.toString(numOfLikes-1));
    }

    private void increaseNumOfLikes(){
        String str = noOfLikesTV.getText().toString();
        int numOfLikes = Integer.valueOf(str);
        noOfLikesTV.setText(Integer.toString(numOfLikes+1));
    }

    public void refreshHolder(){
        Log.i("refreshing", "halfpostholder");
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
