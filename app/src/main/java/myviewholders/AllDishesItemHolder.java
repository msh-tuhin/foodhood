package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AccountTypes;
import myapp.utils.OrphanUtilityMethods;
import myapp.utils.PictureBinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Map;

public class AllDishesItemHolder extends RecyclerView.ViewHolder {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUserUid;
    private  boolean isInWishlist = false;

    private LinearLayout parentLayout;
    private CircleImageView dishAvatar;
    private TextView dishNameTV;
    private TextView dishRatingTV;
    private TextView dishPriceTV;
    private TextView restaurantNameTV;
    private ImageButton addToWishlistIB;

    public AllDishesItemHolder(@NonNull View v) {
        super(v);
        currentUserUid = mAuth.getCurrentUser().getUid();
        parentLayout = v.findViewById(R.id.parent_layout);
        dishAvatar = v.findViewById(R.id.dish_avatar);
        dishNameTV = v.findViewById(R.id.dish_name);
        dishRatingTV = v.findViewById(R.id.dish_rating);
        dishPriceTV = v.findViewById(R.id.dish_price);
        restaurantNameTV = v.findViewById(R.id.restaurant_name);
        addToWishlistIB = v.findViewById(R.id.add_to_wishlist_IB);
    }

    public void bindTo(final Context context,
                       final String dishLink,
                       Task<DocumentSnapshot> taskWithCurrentUserWishlist){
        refreshHolder();
        db.collection("dish_vital")
                .document(dishLink).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            setParentLayoutOnClickListener(context, dishLink);
                            bindAvatar(documentSnapshot);
                            setAvatarOnClickListener(context, dishLink);
                            bindName(documentSnapshot);
                            setNameOnClickListener(context, dishLink);
                            bindRating(documentSnapshot);
                            bindPrice(documentSnapshot);
                            bindRestaurantName(documentSnapshot);
                            setRestaurantNameOnclickListener(context, documentSnapshot);
                        }
                    }
                });
        bindAddToWishlistIB(context, dishLink, taskWithCurrentUserWishlist);
    }

    private void setParentLayoutOnClickListener(final Context context, final String dishLink){
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishDetail.class);
                intent.putExtra("dishLink", dishLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindAvatar(DocumentSnapshot dishVitalSnapshot){
        PictureBinder.bindCoverPicture(dishAvatar, dishVitalSnapshot);
    }

    private void setAvatarOnClickListener(final Context context, final String dishLink){
        dishAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishDetail.class);
                intent.putExtra("dishLink", dishLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot dishVitalSnapshot){
        String dishName = dishVitalSnapshot.getString("n");
        if(dishName==null || dishName.equals("")) return;
        dishNameTV.setText(dishName);
    }

    private void setNameOnClickListener(final Context context, final String dishLink){
        dishNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishDetail.class);
                intent.putExtra("dishLink", dishLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindRating(DocumentSnapshot dishVitalSnapshot){
        Double noOfRatings = dishVitalSnapshot.getDouble("npr");
        if(noOfRatings==null) return;
        Double totalRating = dishVitalSnapshot.getDouble("tr");
        if(totalRating==null) return;
        Double rating = noOfRatings==0 ? 0:totalRating/noOfRatings;
        if(rating == 0){
            dishRatingTV.setText("N/A");
        }else{
            DecimalFormat formatter = new DecimalFormat("#.0");
            dishRatingTV.setText(formatter.format(rating));
        }
    }

    private void bindPrice(DocumentSnapshot dishVitalSnapshot){
        Double price = dishVitalSnapshot.getDouble("p");
        if(price==null) return;
        dishPriceTV.setText(Double.toString(price)+" BDT");
    }

    private void bindRestaurantName(DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map<String, String>) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        String restaurantName = restaurant.get("n");
        if(restaurantName==null || restaurantName.equals("")) return;
        restaurantNameTV.setText(restaurantName);
    }

    private void setRestaurantNameOnclickListener(final Context context, DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map<String, String>) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        final String restaurantLink = restaurant.get("l");
        if(restaurantLink==null || restaurantLink.equals("")) return;
        if(restaurantLink.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        restaurantNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestDetail.class);
                intent.putExtra("restaurantLink", restaurantLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindAddToWishlistIB(final Context context,
                                     final String dishLink,
                                     Task<DocumentSnapshot> taskWithCurrentUserWishlist){
        if(OrphanUtilityMethods.getAccountType(context) == AccountTypes.RESTAURANT){
            return;
        }
        taskWithCurrentUserWishlist
                .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot wishListSnap) {
                        if (wishListSnap.exists()) {
                            try{
                                ArrayList<String> wishList = (ArrayList<String>) wishListSnap.get("a");
                                if(wishList.contains(dishLink)){
                                    addToWishlistIB.setImageResource(R.drawable.outline_done_black_24dp);
                                    isInWishlist = true;
                                }
                            }catch (NullPointerException e){
                                Log.e("error", e.getMessage());
                            }
                        }
                        addToWishlistIB.setVisibility(View.VISIBLE);
                        addToWishlistIB.setOnClickListener(getAddToWishlistIBOnClickListener(dishLink));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        addToWishlistIB.setVisibility(View.VISIBLE);
                        addToWishlistIB.setOnClickListener(getAddToWishlistIBOnClickListener(dishLink));
                    }
                });
    }

    private View.OnClickListener getAddToWishlistIBOnClickListener(final String dishLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference wishlistRef = db.collection("wishlist")
                        .document(currentUserUid);
                DocumentReference inWishlistRef = db.collection("wishers")
                        .document(dishLink);
                DocumentReference dishVitalRef = db.collection("dish_vital")
                        .document(dishLink);
                if(isInWishlist){
                    // TODO: this should be done if and only if the updates are successful
                    isInWishlist = false;
                    addToWishlistIB.setImageResource(R.drawable.outline_add_black_24dp);
                    wishlistRef.update("a", FieldValue.arrayRemove(dishLink));
                    inWishlistRef.update("a", FieldValue.arrayRemove(currentUserUid));
                    dishVitalRef.update("num_wishlist", FieldValue.increment(-1));
                } else{
                    // TODO: this should be done if and only if the updates are successful
                    isInWishlist = true;
                    addToWishlistIB.setImageResource(R.drawable.outline_done_black_24dp);
                    wishlistRef.update("a", FieldValue.arrayUnion(dishLink));
                    inWishlistRef.update("a", FieldValue.arrayUnion(currentUserUid));
                    dishVitalRef.update("num_wishlist", FieldValue.increment(1));
                }
            }
        };
    }

    private void refreshHolder(){
        Log.i("refreshing", "alldishesitemholder");
        dishAvatar.setImageResource(R.drawable.ltgray);
        dishNameTV.setText("");
        dishRatingTV.setText("");
        dishPriceTV.setText("");
        restaurantNameTV.setText("");
        addToWishlistIB.setVisibility(View.GONE);
    }
}
