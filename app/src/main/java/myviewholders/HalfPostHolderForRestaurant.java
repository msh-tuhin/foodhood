package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.FullPostForRestaurant;
import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.List;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;
import models.PostModel;
import myapp.utils.EntryPoints;
import myapp.utils.PostBuilder;
import myapp.utils.SourceAllDishes;

public class HalfPostHolderForRestaurant extends BaseHomeFeedHolder
        implements PostHolderInterface{

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private Context mContext;
    private PostModel mPostModel;
    private DocumentReference mPostReference;
    private String mPostLink;
    private PostBuilder mPostBuilder;
    //private DocumentSnapshot mPostSnapShot;

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
    public TextView noOfLikesTV;
    public TextView noOfCommentsTV;
    public ImageView like;
    public ImageView comment;

    public HalfPostHolderForRestaurant(@NonNull View v) {
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
        noOfLikesTV = v.findViewById(R.id.no_likes);
        noOfCommentsTV = v.findViewById(R.id.no_comments);
        like = v.findViewById(R.id.like);
        comment = v.findViewById(R.id.comment);
    }

    public void bindTo(PostModel postModel, Context context, String postLink){
        mPostModel = postModel;
        mContext = context;
        mPostLink = postLink;
        mPostBuilder = new PostBuilder(context, postModel);

        bindValues();
        setOnClickListeners();
    }

    private void bindValues(){
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
        bindGoToFull();
        bindCommentIcon();
    }

    private void setOnClickListeners(){
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
                Intent intent = new Intent(mContext, FullPostForRestaurant.class);
                intent.putExtra("entry_point", EntryPoints.CLICKED_GO_TO_FULL_POST);
                intent.putExtra("postLink", mPostLink);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindLikeIcon() {
        String likedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> likers = mPostModel.getLikes();
        // find if current user has already liked this post
        if(likers.contains(likedBy)){
            like.setImageResource(R.drawable.baseline_favorite_black_24dp);
        }else{
            like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
        }
    }

    @Override
    public void setLikeIconOnClickListener() {

    }

    @Override
    public void bindNoOfLike() {
        List<String> likes = mPostModel.getLikes();
        int numOfLikes = 0;
        if(likes != null){
            numOfLikes = likes.size();
        }
        noOfLikesTV.setText(Integer.toString(numOfLikes));
    }

    @Override
    public void setNoOfLikeOnClickListener() {

    }

    @Override
    public void bindCommentIcon() {

    }

    @Override
    public void setCommentIconOnClickListener() {

    }

    @Override
    public void bindNoOfComment() {
        List<String> comments = mPostModel.getComments();
        int numOfComments = 0;
        if(comments != null){
            numOfComments = comments.size();
        }
        noOfCommentsTV.setText(Integer.toString(numOfComments));
    }

    @Override
    public void setNoOfCommentOnClickListener() {

    }
}
