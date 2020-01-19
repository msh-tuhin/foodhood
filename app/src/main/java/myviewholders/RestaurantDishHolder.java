package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.PictureBinder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import site.sht.bd.foodhood.DishDetail;
import site.sht.bd.foodhood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class RestaurantDishHolder extends RecyclerView.ViewHolder {
    private LinearLayout parentLayout;
    private ImageView dishImage;
    private TextView dishNameTV;
    private TextView dishRatingTV;
    private TextView dishPriceTV;
    private String NO_DOCUMENT = "NO_DOCUMENT";

    public RestaurantDishHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        dishImage = v.findViewById(R.id.wishlist_dish_image);
        dishNameTV = v.findViewById(R.id.wishlist_dish_name);
        dishRatingTV = v.findViewById(R.id.wishlist_dish_rating);
        dishPriceTV = v.findViewById(R.id.price);
    }

    public void bindTo(final Context context, final String dishLink){
        refreshHolder();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    private void refreshHolder(){
        Log.i("refreshing", "restaurantdishholder");
        dishImage.setImageResource(R.drawable.ltgray);
        dishNameTV.setText("");
        dishRatingTV.setText("");
        dishPriceTV.setText("");
    }
}
