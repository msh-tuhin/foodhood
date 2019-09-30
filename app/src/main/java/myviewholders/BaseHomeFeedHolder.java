package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;


//  acts as a parent for all viewholders
//  initializes elements declared in post_q.xml and com_n_like.xml
public class BaseHomeFeedHolder extends RecyclerView.ViewHolder {

    // post_q.xml
    public CircleImageView profileImage;
    public TextView namePostedBy, postTime, postCaption;
    public TextView restaurantName, taggedPeople, dishes;
    public ImageView postImages;
    // com_n_like.xml
    public TextView noOfLikes, noOfComments;
    public ImageView like, comment;

    public BaseHomeFeedHolder(@NonNull View v) {
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
    }
    public void bindTo(final Context context, final DocumentSnapshot activity) {
        Log.i("entering", "binder"+this.getClass().toString());
    }

}
