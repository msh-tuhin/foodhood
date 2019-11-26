package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.DishDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.example.tuhin.myapplication.Wishlist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.PictureBinder;

public class SelfWishlistItemHolder extends RecyclerView.ViewHolder {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout parentLayout;
    private CircleImageView avatar;
    private TextView dishNameTV;
    private TextView restNameTV;
    private TextView restAddressTV;
    private TextView dishRatingTV;
    private TextView dishPriceTV;
    private ImageButton removeImageView;

    public SelfWishlistItemHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        avatar = v.findViewById(R.id.dish_avatar);
        dishNameTV = v.findViewById(R.id.dish_name);
        restNameTV = v.findViewById(R.id.rest_name);
        restAddressTV = v.findViewById(R.id.rest_address);
        dishRatingTV = v.findViewById(R.id.dish_rating);
        dishPriceTV = v.findViewById(R.id.dish_price);
        removeImageView = v.findViewById(R.id.remove_dish);
    }

    public void bindTo(final Context context, final String dishLink){
        refreshHolder();
        db.collection("dish_vital")
                .document(dishLink).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dishInfo) {
                        if(dishInfo.exists()){
                            bindDishImage(dishInfo);
                            bindDishName(dishInfo);
                            setDishNameOnClickListener(context, dishLink);
                            bindRating(dishInfo);
                            bindPrice(dishInfo);
                            bindRestaurantName(dishInfo);
                            setRestaurantNameOnclickListener(context, dishInfo);
                            // bindRestaurantAddress(context, dishInfo);
                            setParentLayoutOnClickListener(context, dishLink);
                        }
                    }
                });
        setRemoveImageViewOnClickListener(context, dishLink);
    }

    private void setRemoveImageViewOnClickListener(final Context context,
                                                   final String dishLink){
        removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Wishlist)context).adapter.removeItem(dishLink);
                String currentUserUid = mAuth.getCurrentUser().getUid();
                DocumentReference wishlistRef = db.collection("wishlist")
                        .document(currentUserUid);
                DocumentReference inWishlistRef = db.collection("wishers")
                        .document(dishLink);
                DocumentReference dishVitalRef = db.collection("dish_vital")
                        .document(dishLink);
                wishlistRef.update("a", FieldValue.arrayRemove(dishLink));
                inWishlistRef.update("a", FieldValue.arrayRemove(currentUserUid));
                dishVitalRef.update("num_wishlist", FieldValue.increment(-1));
            }
        });
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

    private void bindDishImage(DocumentSnapshot dishVitalSnapshot){
        PictureBinder.bindCoverPicture(avatar, dishVitalSnapshot);
    }

    private void bindDishName(DocumentSnapshot dishVitalSnapshot){
        String name = dishVitalSnapshot.getString("n");
        if(name==null || name.equals(""))return;
        dishNameTV.setText(name);
    }

    private void setDishNameOnClickListener(final Context context, final String dishLink){
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
        if(noOfRatings==null){
            dishRatingTV.setText("N/A");
            return;
        }
        Double totalRating = dishVitalSnapshot.getDouble("tr");
        if(totalRating==null){
            dishRatingTV.setText("N/A");
            return;
        }
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
        restNameTV.setText(restaurantName);
    }

    private void setRestaurantNameOnclickListener(final Context context, DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map<String, String>) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        final String restaurantLink = restaurant.get("l");
        if(restaurantLink==null || restaurantLink.equals("")) return;
        restNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestDetail.class);
                intent.putExtra("restaurantLink", restaurantLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindRestaurantAddress(Context context, DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map<String, String>) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        String restaurantLink = restaurant.get("l");
        if(restaurantLink==null || restaurantLink.equals("")) return;
        db.collection("rest_vital")
                .document(restaurantLink)
                .get()
                .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot restVitalSnapshot) {
                        if(restVitalSnapshot.exists()){
                            try{
                                String address = restVitalSnapshot.getString("a");
                                restAddressTV.setText(address);
                                restAddressTV.setVisibility(View.VISIBLE);
                            }catch (NullPointerException e){
                                Log.e("error", e.getMessage());
                            }
                        }
                    }
                });
    }

    private void refreshHolder(){
        avatar.setImageResource(R.drawable.ltgray);
        dishNameTV.setText("");
        restNameTV.setText("");
        restAddressTV.setText("");
        dishRatingTV.setText("");
        dishPriceTV.setText("");
    }

}
