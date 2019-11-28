package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.DishDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AccountTypes;
import myapp.utils.OrphanUtilityMethods;
import myapp.utils.PictureBinder;

public class AllRestaurantItemHolder extends RecyclerView.ViewHolder {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mCurrentUserUid;
    private Context mContext;

    LinearLayout parentLayout;
    CircleImageView avatar;
    TextView nameTV;
    TextView ratingTV;
    TextView addressTV;
    Button followButton;
    public AllRestaurantItemHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        avatar = v.findViewById(R.id.restaurant_avatar);
        nameTV = v.findViewById(R.id.restaurant_name);
        ratingTV = v.findViewById(R.id.restaurant_rating);
        addressTV = v.findViewById(R.id.restaurant_address);
        followButton = v.findViewById(R.id.follow);
    }

    public void bindTo(final Context context,
                       final String restaurantLink,
                       Task<DocumentSnapshot> taskWithCurrentUserFollowingRestaurants){
        refreshHolder();
        mContext = context;
        mCurrentUserUid = mAuth.getCurrentUser().getUid();
        db.collection("rest_vital").document(restaurantLink)
                .get()
                .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot restVitalSnapshot) {
                        if(restVitalSnapshot.exists()){
                            setParentLayoutOnClickListener(context, restaurantLink);
                            bindAvatar(restVitalSnapshot);
                            setAvatarOnClickListener(context, restaurantLink);
                            bindName(restVitalSnapshot);
                            setNameOnClickListener(context, restaurantLink);
                            bindRating(restVitalSnapshot);
                            bindAddress(restVitalSnapshot);
                        }
                    }
                });
        bindFollowButton(context, restaurantLink, taskWithCurrentUserFollowingRestaurants);
    }

    private void setParentLayoutOnClickListener(final Context context, final String restaurantLink){
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestDetail.class);
                intent.putExtra("restaurantLink", restaurantLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindAvatar(DocumentSnapshot restVitalSnapshot){
        PictureBinder.bindCoverPicture(avatar, restVitalSnapshot);
    }

    private void setAvatarOnClickListener(final Context context, final String restaurantLink){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestDetail.class);
                intent.putExtra("restaurantLink", restaurantLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot restVitalSnapshot){
        String name = restVitalSnapshot.getString("n");
        if(name==null || name.equals("")) return;
        nameTV.setText(name);
    }

    private void setNameOnClickListener(final Context context, final String restaurantLink){
        nameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestDetail.class);
                intent.putExtra("restaurantLink", restaurantLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindRating(DocumentSnapshot restVitalSnapshot){
        Double noOfRatings = restVitalSnapshot.getDouble("npr");
        if(noOfRatings==null) return;
        Double totalRating = restVitalSnapshot.getDouble("tr");
        if(totalRating==null) return;
        Double rating = noOfRatings==0 ? 0:totalRating/noOfRatings;
        if(rating == 0){
            ratingTV.setText("N/A");
        }else{
            DecimalFormat formatter = new DecimalFormat("#.0");
            ratingTV.setText(formatter.format(rating));
        }
    }

    private void bindAddress(DocumentSnapshot restVitalSnapshot){
        String address = restVitalSnapshot.getString("a");
        if(address==null || address.equals(""))return;
        addressTV.setText(address);
    }

    private void bindFollowButton(final Context context,
                                  final String restaurantLink,
                                  Task<DocumentSnapshot> taskWithCurrentUserFollowingRestaurants){
        if(getAccountType(context) == AccountTypes.RESTAURANT){
            return;
        }
        taskWithCurrentUserFollowingRestaurants
                .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> followingRestaurants = (ArrayList) documentSnapshot.get("a");
                                if(followingRestaurants.contains(restaurantLink)){
                                    followButton.setText("UNFOLLOW");
                                }
                            } catch (NullPointerException e){
                                Log.i("Error", e.getMessage());
                            }
                        }
                        followButton.setVisibility(View.VISIBLE);
                        followButton.setOnClickListener(getFollowRestaurantOnClickListener(restaurantLink));
                    }
                })
                .addOnFailureListener((Activity)context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        followButton.setVisibility(View.VISIBLE);
                        followButton.setOnClickListener(getFollowRestaurantOnClickListener(restaurantLink));
                    }
                });
    }

    private View.OnClickListener getFollowRestaurantOnClickListener(final String restaurantLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference personRestFollowRef = db.collection("following_restaurants")
                        .document(mCurrentUserUid);
                DocumentReference restaurantFollowerRef = db.collection("followers")
                        .document(restaurantLink);
                DocumentReference restRef = db.collection("rest_vital")
                        .document(restaurantLink);
                DocumentReference personRef = db.collection("person_vital")
                        .document(mCurrentUserUid);
                switch (((Button)v).getText().toString()){
                    case "FOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followButton.setText("UNFOLLOW");
                        OrphanUtilityMethods.sendFollowingNotification(mContext, restaurantLink, false);
                        personRestFollowRef.update("a", FieldValue.arrayUnion(restaurantLink));
                        restaurantFollowerRef.update("a", FieldValue.arrayUnion(mCurrentUserUid));
                        restRef.update("nfb", FieldValue.increment(1));
                        personRef.update("nfr", FieldValue.increment(1));
                        break;
                    case "UNFOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followButton.setText("FOLLOW");
                        personRestFollowRef.update("a", FieldValue.arrayRemove(restaurantLink));
                        restaurantFollowerRef.update("a", FieldValue.arrayRemove(mCurrentUserUid));
                        restRef.update("nfb", FieldValue.increment(-1));
                        personRef.update("nfr", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }

    private int getAccountType(Context context){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sPref = context.getSharedPreferences(
                context.getString(R.string.account_type),
                Context.MODE_PRIVATE);
        return sPref.getInt(user.getEmail(), AccountTypes.UNSET);
    }

    private void refreshHolder(){
        Log.i("refreshing", "allrestaurantitemholder");
        avatar.setImageResource(R.drawable.ltgray);
        nameTV.setText("");
        ratingTV.setText("");
        addressTV.setText("");
        followButton.setVisibility(View.GONE);
    }
}
