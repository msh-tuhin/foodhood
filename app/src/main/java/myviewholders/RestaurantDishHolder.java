package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantDishHolder extends RecyclerView.ViewHolder {
    ImageView dishImage;
    TextView dishhName, dishRating, dishPrice;
    private String NO_DOCUMENT = "NO_DOCUMENT";

    public RestaurantDishHolder(@NonNull View v) {
        super(v);
        dishImage = v.findViewById(R.id.wishlist_dish_image);
        dishhName = v.findViewById(R.id.wishlist_dish_name);
        dishRating = v.findViewById(R.id.wishlist_dish_rating);
        dishPrice = v.findViewById(R.id.wishlist_dish_rest_name);
    }

    public void bindTo(String dishLink){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dish_vital").document(dishLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishInfo = task.getResult();
                    if(dishInfo.exists()){
                        String dishName = dishInfo.getString("n");
                        Double rating = dishInfo.getDouble("r");
                        dishhName.setText(dishName);
                        dishRating.setText(Double.toString(rating));
                    }
                }
            }
        });

        db.collection("dish_extra").document(dishLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishInfo = task.getResult();
                    if(dishInfo.exists()){
                        Double price = dishInfo.getDouble("p");
                        dishPrice.setText(Double.toString(price));
                    }else{
                        Log.i(NO_DOCUMENT, "document not found");
                    }
                }
            }
        });
    }
}
