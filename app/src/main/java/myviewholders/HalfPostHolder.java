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
import myapp.utils.EntryPoints;
import myapp.utils.PostBuilder;
import myapp.utils.SourceAllDishes;

public class HalfPostHolder extends BaseHomeFeedHolder
        implements PostHolderInterface{

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    private boolean likeIconFilled = false;

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

    public void bindTo(final Context context, final DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event
        final String postLink = activity.getString("wh");
        final DocumentReference postRef = db.collection("posts").document(postLink);
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot post = task.getResult();
                if(post.exists()){
                    bindValuesAndSetOnClickListeners(context, post);
                }
            }
        });
        setGoToFullOnClickListener(context, postLink);
        setLikeIconOnClickListener(postRef, postLink);
        setCommentIconOnClickListener(context, postLink);
    }

    public void bindValuesAndSetOnClickListeners(final Context context, DocumentSnapshot post) {
        final PostBuilder postBuilder = new PostBuilder(context, post);
        bindNamePostedBy(postBuilder);
        setNamePostedByOnClickListener(context, postBuilder);
        bindRestaurantName(postBuilder);
        setRestaurantNameOnClickListener(context, postBuilder);
        bindDishes(postBuilder);
        setDishesOnClickListener(context, postBuilder);
        bindTaggedPeople(postBuilder);
        setTaggedPeopleOnClickListener(context, postBuilder);
        bindCaption(postBuilder);
        bindLikeIcon(post);
    }

    @Override
    public void bindAvatar() {

    }

    @Override
    public void setAvatarOnClickListener() {

    }

    @Override
    public void bindNamePostedBy(final PostBuilder postBuilder) {
        namePostedBy.setText(postBuilder.getNamePostedBy());
    }

    @Override
    public void setNamePostedByOnClickListener(final Context context,
                                               final PostBuilder postBuilder) {
        namePostedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonDetail.class);
                intent.putExtra("personLink",
                        postBuilder.getLinkToPostedBy());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void bindPostTime() {

    }

    @Override
    public void bindRestaurantName(final PostBuilder postBuilder) {
        restaurantName.setText(postBuilder.getRestaurantName());
    }

    @Override
    public void setRestaurantNameOnClickListener(final Context context,
                                                 final PostBuilder postBuilder) {
        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestDetail.class);
                intent.putExtra("restaurantLink",
                        postBuilder.getRestaurantLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void bindTaggedPeople(final PostBuilder postBuilder) {
        taggedPeople.setText(postBuilder.getPeopleText());
    }

    @Override
    public void setTaggedPeopleOnClickListener(final Context context,
                                               final PostBuilder postBuilder) {
        taggedPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MorePeole.class);
                intent.putStringArrayListExtra("personsList",
                        postBuilder.getSortedTaggedPeopleLinks());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void bindDishes(final PostBuilder postBuilder) {
        dishes.setText(postBuilder.getDishesText());
    }

    @Override
    public void setDishesOnClickListener(final Context context,
                                         final PostBuilder postBuilder) {
        dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllDishes.class);
                intent.putStringArrayListExtra("dishesList",
                        postBuilder.getSortedDishLinks());
                intent.putExtra("source", SourceAllDishes.POST);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void bindCaption(final PostBuilder postBuilder) {
        postCaption.setText(postBuilder.getCaption());
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
    public void bindLikeIcon(DocumentSnapshot post) {
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> likers = (List<String>) post.get("l");
        // find if current user has already liked this post
        if(likers.contains(likedBy)){
            like.setImageResource(R.drawable.baseline_favorite_black_24dp);
            likeIconFilled = true;
        }else{
            like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
            likeIconFilled = false;
        }
    }

    @Override
    public void setGoToFullOnClickListener(final Context context, final String postLink) {
        goToFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullPost.class);
                intent.putExtra("postLink", postLink);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void setLikeIconOnClickListener(final DocumentReference postRef,
                                           final String postLink) {
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
                // like.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, R.drawable.outline_favorite_border_black_24dp).getConstantState())
                if(likeIconFilled){
                    like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
                    likeIconFilled = false;
                    removeLikeFromPost(postRef);
                }else{
                    like.setImageResource(R.drawable.baseline_favorite_black_24dp);
                    likeIconFilled = true;
                    addLikeToPost(postRef);
                    createActivityForLike(postLink);
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
    public void setCommentIconOnClickListener(final Context context,
                                              final String postLink) {
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("postLink", postLink);
                intent.putExtra("entry_point", EntryPoints.HOME_PAGE);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNoOfComment() {

    }

    @Override
    public void setNoOfCommentOnClickListener() {

    }

    void addLikeToPost(DocumentReference postRef){
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postRef.update("l", FieldValue.arrayUnion(likedBy))
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

    void removeLikeFromPost(DocumentReference postRef){
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postRef.update("l", FieldValue.arrayRemove(likedBy))
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

    void createActivityForLike(final String postLink){
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
                                if(postsOnceLiked.contains(postLink)){
                                    Log.i("LIKED_ALREADY", "YES");
                                }else{
                                    Log.i("LIKED_ALREADY", "NO");
                                    postsLikedByUserRef.update("a", FieldValue.arrayUnion(postLink));
                                    ActualActivity likeActivity = new ActualActivity();
                                    likeActivity.setT(3);
                                    Map<String, String> who = new HashMap<>();
                                    who.put("l", likedBy);
                                    likeActivity.setW(who);
                                    likeActivity.setWh(postLink);
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
