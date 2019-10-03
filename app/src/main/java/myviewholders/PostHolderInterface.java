package myviewholders;

public interface PostHolderInterface {
    void bindHeader();
    void bindAvatar();
    void setAvatarOnClickListener();
    void bindNamePostedBy();
    void setNamePostedByOnClickListener();
    void bindPostTime();
    void setPostTimeOnClickListener();
    void bindRestaurantName();
    void setRestaurantNameOnClickListener();
    void bindTaggedPeople();
    void setTaggedPeopleOnClickListener();
    void bindDishes();
    void setDishesOnClickListener();
    void bindCaption();
    void setCaptionOnClickListener();
    void bindImages();
    void setImagesOnClickListener();
    void bindGoToFull();
    void setGoToFullOnClickListener();
    void bindLikeIcon();
    void setLikeIconOnClickListener();
    void bindNoOfLike();
    void setNoOfLikeOnClickListener();
    void bindCommentIcon();
    void setCommentIconOnClickListener();
    void bindNoOfComment();
    void setNoOfCommentOnClickListener();
}