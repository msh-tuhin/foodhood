package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AccountTypes;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.OrphanUtilityMethods;
import myapp.utils.PictureBinder;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import site.sht.bd.foodhood.DishDetail;
import site.sht.bd.foodhood.ImageFull;
import site.sht.bd.foodhood.R;
import site.sht.bd.foodhood.RestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
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
                        setCoverPictureOnClickListener(context, dishVital);
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

    private void setCoverPictureOnClickListener(final Context context, final DocumentSnapshot documentSnapshot){
        String link = documentSnapshot.getString("cp");
        if(link == null || link.equals("")) return;
        (((DishDetail)context).coverPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imageUris = new ArrayList<>();
                imageUris.add(documentSnapshot.getString("cp"));
                Intent intent = new Intent(context, ImageFull.class);
                intent.putExtra("position", 0);
                intent.putStringArrayListExtra("imageUris", imageUris);
                mContext.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot dishVitalSnapshot){
        String dishName = dishVitalSnapshot.getString("n");
        if(dishName==null || dishName.equals("")) return;
        nameTV.setText(dishName);
    }

    private void bindRestaurantNameAddress(DocumentSnapshot dishVitalSnapshot){
        Map<String, String> restaurant = (Map) dishVitalSnapshot.get("re");
        if(restaurant==null) return;
        String restaurantName = restaurant.get("n");
        if(restaurantName==null) return;
        SpannableString spannableName = new SpannableString(restaurantName);
        spannableName.setSpan(new StyleSpan(Typeface.BOLD), 0,
                restaurantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        restNameAddressTV.setText(restaurantName);
        restNameAddressLayout.setVisibility(View.VISIBLE);
        String restaurantLink = restaurant.get("l");
        if(restaurantLink==null) return;
        setRestaurantNameAddressOnClickListener(restaurantLink);
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
        Double rating = noOfRatings<=0 ? 0:totalRating/noOfRatings;
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
        if(OrphanUtilityMethods.getAccountType(mContext) == AccountTypes.RESTAURANT){
            return;
        }
        addToWishlist.setVisibility(View.VISIBLE);
        addToWishlist.setOnClickListener(getAddToWishlistOnClickListener(mDishLink));
    }

    private void setRestaurantNameAddress(final String restaurantName, final String restaurantLink){
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
                // already visible; this is redundant
                restNameAddressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setRestaurantNameAddressOnClickListener(final String restaurantLink){
        restNameAddressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestDetail.class);
                intent.putExtra("restaurantLink", restaurantLink);
                mContext.startActivity(intent);
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
                        dishVitalRef.update(FirestoreFieldNames.DISH_VITAL_NUMBER_OF_WISHERS, FieldValue.increment(1));
                        break;
                    case "ADDED TO WISHLIST":
                        // TODO: this should be done if and only if the updates are successful
                        addToWishlist.setText("ADD TO WISHLIST");
                        wishlistRef.update("a", FieldValue.arrayRemove(dishLink));
                        inWishlistRef.update("a", FieldValue.arrayRemove(personLink));
                        dishVitalRef.update(FirestoreFieldNames.DISH_VITAL_NUMBER_OF_WISHERS, FieldValue.increment(-1));
                        break;
                }
            }
        };
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
