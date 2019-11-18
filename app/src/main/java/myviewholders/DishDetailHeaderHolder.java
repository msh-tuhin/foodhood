package myviewholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AccountTypes;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class DishDetailHeaderHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private String mDishLink;

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

    public void bindTo(final Context context, final String dishLink){
        if(dishLink==null || dishLink.equals("")) return;

        mContext = context;
        mDishLink = dishLink;
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
                        Double dishPrice = dishVital.getDouble("p");
                        String priceText = Double.toString(dishPrice) + " BDT";
                        price.setText(priceText);
                        String dishDescription = dishVital.getString("d");
                        description.setText(dishDescription);
                        Map<String, String> restaurant = (Map) dishVital.get("re");
                        String restaurantLink = restaurant.get("l");
                        String restaurantName = restaurant.get("n");
                        setRestaurantNameAddress(restaurantName, restaurantLink);
                    }
                }
            }
        });

        db.collection("wishers").document(dishLink)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot wishersSnap = task.getResult();
                            if(wishersSnap.exists()){
                                try{
                                    ArrayList<String> wishersList = (ArrayList<String>) wishersSnap.get("a");
                                    if(wishersList.contains(personLink)){
                                        addToWishlist.setText("ADDED TO WISHLIST");
                                    }
                                }catch (NullPointerException e){
                                    Log.e("error", e.getMessage());
                                }
                            }
                        }
                        setAddToWishlistButton();
                    }
                });
    }

    private void setAddToWishlistButton(){
        if(getAccountType() == AccountTypes.RESTAURANT){
            return;
        }
        addToWishlist.setVisibility(View.VISIBLE);
        addToWishlist.setOnClickListener(getAddToWishlistOnClickListener(mDishLink));
    }

    private void setRestaurantNameAddress(final String restaurantName, String restaurantLink){
        db.collection("rest_vital").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restVital = task.getResult();
                    if(restVital.exists()){
                        Map<String, String> restAddress = (Map) restVital.get("a");
                        String nameAddress = restaurantName + "\n" + restAddress;
                        SpannableString spannableNameAddress = new SpannableString(nameAddress);
                        spannableNameAddress.setSpan(new StyleSpan(Typeface.BOLD), 0, restaurantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        restNameAddress.setText(spannableNameAddress, TextView.BufferType.SPANNABLE);
                    }
                }
            }
        });
    }

    private View.OnClickListener getAddToWishlistOnClickListener(final String dishLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference wishlistRef = db.collection("wishlist")
                        .document(personLink);
                DocumentReference inWishlistRef = db.collection("wishers")
                        .document(dishLink);
                DocumentReference dishVitalRef = db.collection("dish_vital")
                        .document(dishLink);
                switch (((Button)v).getText().toString()){
                    case "ADD TO WISHLIST":
                        // TODO: this should be done if and only if the updates are successful
                        addToWishlist.setText("ADDED TO WISHLIST");
                        wishlistRef.update("a", FieldValue.arrayUnion(dishLink));
                        inWishlistRef.update("a", FieldValue.arrayUnion(personLink));
                        dishVitalRef.update("num_wishlist", FieldValue.increment(1));
                        break;
                    case "ADDED TO WISHLIST":
                        // TODO: this should be done if and only if the updates are successful
                        addToWishlist.setText("ADD TO WISHLIST");
                        wishlistRef.update("a", FieldValue.arrayRemove(dishLink));
                        inWishlistRef.update("a", FieldValue.arrayRemove(personLink));
                        dishVitalRef.update("num_wishlist", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }

    private int getAccountType(){
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sPref = mContext.getSharedPreferences(
                mContext.getString(R.string.account_type),
                Context.MODE_PRIVATE);
        return sPref.getInt(user.getEmail(), AccountTypes.UNSET);
    }
}
