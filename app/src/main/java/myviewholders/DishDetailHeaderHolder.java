package myviewholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AccountTypes;
import myapp.utils.PictureBinder;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.DishDetail;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class DishDetailHeaderHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private String mDishLink;

    private LinearLayout restNameAddressLayout;
    private LinearLayout ratingLayout;
    private LinearLayout priceLayout;
    private LinearLayout descriptionLayout;
    private TextView nameTV;
    private TextView restNameAddressTV;
    private TextView ratingTV;
    private TextView priceTV;
    private TextView descriptionTV;
    private Button addToWishlist;
    private final FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String personLink;

    public DishDetailHeaderHolder(@NonNull View v) {
        super(v);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        restNameAddressLayout = v.findViewById(R.id.rest_name_address_layout);
        ratingLayout = v.findViewById(R.id.rating_layout);
        priceLayout = v.findViewById(R.id.price_layout);
        descriptionLayout = v.findViewById(R.id.description_layout);
        nameTV = v.findViewById(R.id.dish_name);
        addToWishlist = v.findViewById(R.id.add_to_wishlist_button);
        restNameAddressTV = v.findViewById(R.id.rest_name_address);
        ratingTV = v.findViewById(R.id.rating);
        priceTV = v.findViewById(R.id.price);
        descriptionTV = v.findViewById(R.id.dish_description);
    }

    public void bindTo(final Context context, final String dishLink){
        refreshHolder();
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
                        bindCoverPhoto(context, dishVital);
                        setCollapsedTitle(context, dishVital);
                        bindName(dishVital);
                        bindRestaurantNameAddress(dishVital);
                        bindRating(dishVital);
                        bindPrice(dishVital);
                        bindDescription(dishVital);
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

    private void bindCoverPhoto(Context context, DocumentSnapshot dishVitalSnapshot){
        PictureBinder.bindCoverPicture(((DishDetail)context).coverPhoto, dishVitalSnapshot);
    }

    private void bindName(DocumentSnapshot dishVitalSnapshot){
        String dishName = dishVitalSnapshot.getString("n");
        if(dishName==null || dishName.equals("")) return;
        nameTV.setText(dishName);
    }

    private void bindRestaurantNameAddress(DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        String restaurantLink = restaurant.get("l");
        if(restaurantLink==null) return;
        String restaurantName = restaurant.get("n");
        if(restaurantName==null) return;
        setRestaurantNameAddress(restaurantName, restaurantLink);
    }

    private void bindRating(DocumentSnapshot dishVitalSnapshot){
        Double noOfRatings = dishVitalSnapshot.getDouble("npr");
        if(noOfRatings==null){
            ratingTV.setText("N/A");
            ratingLayout.setVisibility(View.VISIBLE);
            return;
        }
        Double totalRating = dishVitalSnapshot.getDouble("tr");
        if(totalRating==null){
            ratingTV.setText("N/A");
            ratingLayout.setVisibility(View.VISIBLE);
            return;
        }
        Double rating = noOfRatings==0 ? 0:totalRating/noOfRatings;
        if(rating == 0){
            ratingTV.setText("N/A");
        }else{
            DecimalFormat formatter = new DecimalFormat("#.0");
            ratingTV.setText(formatter.format(rating));
        }
        ratingLayout.setVisibility(View.VISIBLE);
    }

    private void bindPrice(DocumentSnapshot dishVitalSnapshot){
        Double price = dishVitalSnapshot.getDouble("p");
        if(price==null) return;
        priceTV.setText(Double.toString(price)+" BDT");
        priceLayout.setVisibility(View.VISIBLE);
    }

    private void bindDescription(DocumentSnapshot dishVitalSnapshot){
        String description = dishVitalSnapshot.getString("d");
        if(description==null || description.equals("")) return;
        descriptionTV.setText(description);
        descriptionLayout.setVisibility(View.VISIBLE);
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
                String nameAddress = restaurantName;
                if(task.isSuccessful()){
                    DocumentSnapshot restVital = task.getResult();
                    if(restVital.exists()){
                        try{
                            String restAddress = restVital.getString("a");
                            if(restAddress!=null){
                                nameAddress = restaurantName + "\n" + restAddress;
                            }
                        }catch (Exception e){
                            Log.e("error", e.getMessage());
                        }
                    }
                }
                SpannableString spannableNameAddress = new SpannableString(nameAddress);
                spannableNameAddress.setSpan(new StyleSpan(Typeface.BOLD), 0, restaurantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                restNameAddressTV.setText(spannableNameAddress, TextView.BufferType.SPANNABLE);
                restNameAddressLayout.setVisibility(View.VISIBLE);
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

    private void setCollapsedTitle(final Context context, DocumentSnapshot dishVital){
        final String name = dishVital.getString("n");
        if(name==null) return;
        ((DishDetail)context).appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    ((DishDetail)context).collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if(isShow) {
                    ((DishDetail)context).collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void refreshHolder(){
        nameTV.setText("");
    }
}
