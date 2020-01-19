package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.PictureBinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import site.sht.bd.foodhood.DishDetail;
import site.sht.bd.foodhood.R;
import site.sht.bd.foodhood.RestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Map;

public class WishlistItemHolder extends RecyclerView.ViewHolder {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LinearLayout parentLayout;
    private ImageView dishImage;
    private TextView dishNameTV;
    private TextView dishRatingTV;
    private TextView dishPriceTV;
    private TextView restaurantNameTV;
    private TextView restaurantAddressTV;

    public WishlistItemHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        dishImage = v.findViewById(R.id.wishlist_dish_image);
        dishNameTV = v.findViewById(R.id.wishlist_dish_name);
        dishRatingTV = v.findViewById(R.id.wishlist_dish_rating);
        dishPriceTV = v.findViewById(R.id.price);
        restaurantNameTV = v.findViewById(R.id.restaurant_name);
        restaurantAddressTV = v.findViewById(R.id.restaurant_address);
    }

    public void bindTo(final Context context, final String dishLink){
        refreshHolder();
        db.collection("dish_vital").document(dishLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishInfo = task.getResult();
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
        PictureBinder.bindCoverPicture(dishImage, dishVitalSnapshot);
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
        restaurantNameTV.setText(restaurantName);
    }

    private void setRestaurantNameOnclickListener(final Context context, DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map<String, String>) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        final String restaurantLink = restaurant.get("l");
        if(restaurantLink==null || restaurantLink.equals("")) return;
        restaurantNameTV.setOnClickListener(new View.OnClickListener() {
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
                                restaurantAddressTV.setText(address);
                                restaurantAddressTV.setVisibility(View.VISIBLE);
                            }catch (NullPointerException e){
                                Log.e("error", e.getMessage());
                            }
                        }
                    }
                });
    }

    private void refreshHolder(){
        dishImage.setImageResource(R.drawable.ltgray);
        dishNameTV.setText("");
        dishRatingTV.setText("");
        dishPriceTV.setText("");
        restaurantNameTV.setText("");
        restaurantAddressTV.setText("");
    }
}
