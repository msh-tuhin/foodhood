package myviewholders;

import android.content.Context;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import myapp.utils.PostBuilder;

public interface PostHolderInterface {
    void bindAvatar();
    void setAvatarOnClickListener();
    void bindNamePostedBy(PostBuilder postBuilder);
    void setNamePostedByOnClickListener(Context context, PostBuilder postBuilder);
    void bindPostTime();
    // void setPostTimeOnClickListener();
    void bindCaption(PostBuilder postBuilder);
    void setCaptionOnClickListener();
    void bindImages();
    void setImagesOnClickListener();
    void bindLikeIcon(DocumentSnapshot post);
    void setLikeIconOnClickListener(DocumentReference postRef, String postLink);
    void bindNoOfLike();
    void setNoOfLikeOnClickListener();
    // void bindCommentIcon();
    void setCommentIconOnClickListener(Context context, String postLink);
    void bindNoOfComment();
    void setNoOfCommentOnClickListener();
    void bindRestaurantName(PostBuilder postBuilder);
    void setRestaurantNameOnClickListener(Context context, PostBuilder postBuilder);
    void bindTaggedPeople(PostBuilder postBuilder);
    void setTaggedPeopleOnClickListener(Context context, PostBuilder postBuilder);
    void bindDishes(PostBuilder postBuilder);
    void setDishesOnClickListener(Context context, PostBuilder postBuilder);
    // void bindGoToFull();
    void setGoToFullOnClickListener(Context context, String postLink);
}