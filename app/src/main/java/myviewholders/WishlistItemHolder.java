package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class WishlistItemHolder extends RecyclerView.ViewHolder {
    ImageView wishlistDishImage;
    TextView wishlistDishName, wishlistDishRating, wishlistDishRestName;
    public WishlistItemHolder(@NonNull View v) {
        super(v);
        wishlistDishImage = v.findViewById(R.id.wishlist_dish_image);
        wishlistDishName = v.findViewById(R.id.wishlist_dish_name);
        wishlistDishRating = v.findViewById(R.id.wishlist_dish_rating);
        wishlistDishRestName = v.findViewById(R.id.wishlist_dish_rest_name);
    }
    public void bindTo(String dishLink){
        FirebaseFirestore.getInstance().collection("dish_vital").document(dishLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishInfo = task.getResult();
                    if(dishInfo.exists()){
                        String dishName = dishInfo.getString("n");
                        Double rating = dishInfo.getDouble("r");
                        Map<String, String> restaurantMap = (Map) dishInfo.get("re");
                        String restaurantName = restaurantMap.get("n");
                        wishlistDishName.setText(dishName);
                        wishlistDishRating.setText(Double.toString(rating));
                        wishlistDishRestName.setText(restaurantName);
                    }
                }
            }
        });
    }
}
