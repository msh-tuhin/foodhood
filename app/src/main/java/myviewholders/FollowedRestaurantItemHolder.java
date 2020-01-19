package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import site.sht.bd.foodhood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FollowedRestaurantItemHolder extends RecyclerView.ViewHolder {
    ImageView followedRestaurantImage;
    TextView followedRestaurantName, followedRestaurantRating;

    public FollowedRestaurantItemHolder(@NonNull View v) {
        super(v);
        followedRestaurantImage = v.findViewById(R.id.followed_restaurant_image);
        followedRestaurantName = v.findViewById(R.id.followed_restaurant_name);
        followedRestaurantRating = v.findViewById(R.id.followed_restaurant_rating);
    }

    public void bindTo(String restaurantLink){
        FirebaseFirestore.getInstance().collection("rest_vital").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restaurantInfo = task.getResult();
                    if(restaurantInfo.exists()){
                        String restaurantName = restaurantInfo.getString("n");
                        Double rating = restaurantInfo.getDouble("r");
                        followedRestaurantName.setText(restaurantName);
                        followedRestaurantRating.setText(Double.toString(rating));
                    }
                }
            }
        });
    }
}
