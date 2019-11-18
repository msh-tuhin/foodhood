package myviewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.Wishlist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelfWishlistItemHolder extends RecyclerView.ViewHolder {

    private CircleImageView avatar;
    private TextView dishNameTV, restNameTV, restAddressTV, dishRatingTV, dishPriceTV;
//    private ImageView removeImageView;
    private ImageButton removeImageView;

    public SelfWishlistItemHolder(@NonNull View v) {
        super(v);
        avatar = v.findViewById(R.id.dish_avatar);
        dishNameTV = v.findViewById(R.id.dish_name);
        restNameTV = v.findViewById(R.id.rest_name);
        restAddressTV = v.findViewById(R.id.rest_address);
        dishRatingTV = v.findViewById(R.id.dish_rating);
        dishPriceTV = v.findViewById(R.id.dish_price);
        removeImageView = v.findViewById(R.id.remove_dish);
    }

    public void bindTo(final Wishlist.WishlistAdapter adapter, final String dishLink){
        FirebaseFirestore.getInstance().collection("dish_vital")
                .document(dishLink).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String dishName = documentSnapshot.getString("n");
                            dishNameTV.setText(dishName);
                            Double dishRating = documentSnapshot.getDouble("r");
                            dishRatingTV.setText(Double.toString(dishRating));
                            Map<String, String> restNameLink = (Map) documentSnapshot.get("re");
                            restNameTV.setText(restNameLink.get("n"));
                        }
                    }
                });

        // TODO: binding not complete
        // dish_price, rest_address, dish_avatar

        removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeItem(dishLink);
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore.getInstance().collection("person_extra")
                        .document(currentUserUid)
                        .update("wishlist", FieldValue.arrayRemove(dishLink));
            }
        });
    }

    private void setRestAddress(String restLink){
        FirebaseFirestore.getInstance().collection("rest_vital")
                .document();
    }

}
