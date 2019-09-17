package myviewholders;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class DishDetailHeaderHolder extends RecyclerView.ViewHolder {

    private TextView name, restNameAddress, rating, price, description;
    private Button addToWishlist;
    private final FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String personLink;

    public DishDetailHeaderHolder(@NonNull View v) {
        super(v);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        name = v.findViewById(R.id.dish_name);
        addToWishlist = v.findViewById(R.id.add_to_wishlist_button);
        restNameAddress = v.findViewById(R.id.rest_name_address);
        rating = v.findViewById(R.id.rating);
        price = v.findViewById(R.id.price);
        description = v.findViewById(R.id.dish_description);
    }

    public void bindTo(final String dishLink){
        personLink = mAuth.getCurrentUser().getUid();
        db.collection("dish_vital").document(dishLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishVital = task.getResult();
                    if(dishVital.exists()){
                        String dishName = dishVital.getString("n");
                        Double dishRating = dishVital.getDouble("r");
                        name.setText(dishName);
                        rating.setText(Double.toString(dishRating));
                        Map<String, String> restaurant = (Map) dishVital.get("re");
                        String restaurantLink = restaurant.get("l");
                        String restaurantName = restaurant.get("n");
                        setRestaurantNameAddress(restaurantName, restaurantLink);
                    }
                }
            }
        });

        db.collection("dish_extra").document(dishLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishExtra = task.getResult();
                    if(dishExtra.exists()){
                        Double dishPrice = dishExtra.getDouble("p");
                        String priceText = Double.toString(dishPrice) + " BDT";
                        price.setText(priceText);
                        String dishDescription = dishExtra.getString("d");
                        description.setText(dishDescription);
                        try{
                            ArrayList<String> inWishlistOf = (ArrayList<String>) dishExtra.get("in_wishlist_of");
                            if(inWishlistOf.contains(personLink)){
                                addToWishlist.setText("ADDED TO WISHLIST");
                            }
                        } catch (NullPointerException e){
                            Log.i("in_wishlist_of", e.getMessage());
                        }
                        addToWishlist.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference personRef = db.collection("person_extra")
                        .document(personLink);
                DocumentReference dishRef = db.collection("dish_extra")
                        .document(dishLink);
                switch (((Button)v).getText().toString()){
                    case "ADD TO WISHLIST":
                        // TODO: this should be done if the updates are successful
                        addToWishlist.setText("ADDED TO WISHLIST");
                        personRef.update("wishlist", FieldValue.arrayUnion(dishLink));

                        dishRef.update("in_wishlist_of", FieldValue.arrayUnion(personLink),
                                        "num_wishlist", FieldValue.increment(1));
                        break;
                    case "ADDED TO WISHLIST":
                        // TODO: this should be done if the updates are successful
                        addToWishlist.setText("ADD TO WISHLIST");
                        personRef.update("wishlist", FieldValue.arrayRemove(dishLink));
                        dishRef.update("in_wishlist_of", FieldValue.arrayRemove(personLink),
                                        "num_wishlist", FieldValue.increment(-1));
                        break;
                }
            }
        });

    }

    private void setRestaurantNameAddress(final String restaurantName, String restaurantLink){
        db.collection("rest_extra").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restExtra = task.getResult();
                    if(restExtra.exists()){
                        Map<String, String> restAddress = (Map) restExtra.get("a");
                        String line1 = restAddress.get("l1");
                        String line2 = restAddress.get("l2");
                        String nameAddress = restaurantName + "\n" + line1 + "\n" + line2;
                        SpannableString spannableNameAddress = new SpannableString(nameAddress);
                        spannableNameAddress.setSpan(new StyleSpan(Typeface.BOLD), 0, restaurantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        restNameAddress.setText(spannableNameAddress, TextView.BufferType.SPANNABLE);
                    }
                }
            }
        });
    }
}
