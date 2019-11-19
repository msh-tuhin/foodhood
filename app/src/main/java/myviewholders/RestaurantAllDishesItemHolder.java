package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.DishDetail;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.PictureBinder;

public class RestaurantAllDishesItemHolder extends RecyclerView.ViewHolder {

    private LinearLayout parentLayout;
    private CircleImageView dishAvatar;
    private TextView dishNameTV, dishRatingTV, dishPriceTV;
    private Button addToWishlistButton;
    private ImageButton addToWishlistIB;
    private FirebaseFirestore db;
    private String currentUserUid;
    private  boolean isInWishlist = false;

    public RestaurantAllDishesItemHolder(@NonNull View v) {
        super(v);
        db = FirebaseFirestore.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        parentLayout = v.findViewById(R.id.parent_layout);
        dishAvatar = v.findViewById(R.id.dish_avatar);
        dishNameTV = v.findViewById(R.id.dish_name);
        dishRatingTV = v.findViewById(R.id.dish_rating);
        dishPriceTV = v.findViewById(R.id.dish_price);
        addToWishlistButton = v.findViewById(R.id.add_to_wishlist);
        addToWishlistIB = v.findViewById(R.id.add_to_wishlist_IB);
    }

    public void bindTo(final Context context, final String dishLink){
        addToWishlistButton.setVisibility(View.GONE);
        // set name and rating
        FirebaseFirestore.getInstance().collection("dish_vital")
                .document(dishLink).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            setParentLayoutOnClickListener(context, dishLink);
                            bindAvatar(documentSnapshot);
                            setAvatarOnClickListener(context, dishLink);
                            bindName(documentSnapshot);
                            setNameOnClickListener(context, dishLink);
                            bindRating(documentSnapshot);
                            bindPrice(documentSnapshot);
                        }
                    }
                });

        db.collection("wishers").document(dishLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot wishersSnap) {
                        if (wishersSnap.exists()) {
                            try{
                                ArrayList<String> wishersList = (ArrayList<String>) wishersSnap.get("a");
                                if(wishersList.contains(currentUserUid)){
                                    addToWishlistIB.setImageResource(R.drawable.outline_done_black_24dp);
                                    isInWishlist = true;
                                }
                            }catch (NullPointerException e){
                                Log.e("error", e.getMessage());
                            }
                        }
                        addToWishlistIB.setVisibility(View.VISIBLE);
                        addToWishlistIB.setOnClickListener(getAddToWishlistIBOnClickListener(dishLink));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        addToWishlistIB.setVisibility(View.VISIBLE);
                        addToWishlistIB.setOnClickListener(getAddToWishlistIBOnClickListener(dishLink));
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

    private void bindAvatar(DocumentSnapshot dishVitalSnapshot){
        PictureBinder.bindCoverPicture(dishAvatar, dishVitalSnapshot);
    }

    private void setAvatarOnClickListener(final Context context, final String dishLink){
        dishAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishDetail.class);
                intent.putExtra("dishLink", dishLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot dishVitalSnapshot){
        String dishName = dishVitalSnapshot.getString("n");
        if(dishName==null || dishName.equals("")) return;
        dishNameTV.setText(dishName);
    }

    private void setNameOnClickListener(final Context context, final String dishLink){
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
        Double dishRating = dishVitalSnapshot.getDouble("r");
        if(dishRating==null) return;
        dishRatingTV.setText(Double.toString(dishRating));
    }

    private void bindPrice(DocumentSnapshot dishVitalSnapshot){
        Double price = dishVitalSnapshot.getDouble("p");
        if(price==null) return;
        dishPriceTV.setText(Double.toString(price)+" BDT");
    }

    private View.OnClickListener getAddToWishlistOnClickListener(final String dishLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference wishlistRef = db.collection("wishlist")
                        .document(currentUserUid);
                DocumentReference inWishlistRef = db.collection("wishers")
                        .document(dishLink);
                DocumentReference dishVitalRef = db.collection("dish_vital")
                        .document(dishLink);
                switch (((Button)v).getText().toString()){
                    case "ADD TO WISHLIST":
                        // TODO: this should be done if and only if the updates are successful
                        addToWishlistButton.setText("ADDED TO WISHLIST");
                        wishlistRef.update("a", FieldValue.arrayUnion(dishLink));
                        inWishlistRef.update("a", FieldValue.arrayUnion(currentUserUid));
                        dishVitalRef.update("num_wishlist", FieldValue.increment(1));
                        break;
                    case "ADDED TO WISHLIST":
                        // TODO: this should be done if and only if the updates are successful
                        addToWishlistButton.setText("ADD TO WISHLIST");
                        wishlistRef.update("wishlist", FieldValue.arrayRemove(dishLink));
                        inWishlistRef.update("a", FieldValue.arrayRemove(currentUserUid));
                        dishVitalRef.update("num_wishlist", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }

    private View.OnClickListener getAddToWishlistIBOnClickListener(final String dishLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference wishlistRef = db.collection("wishlist")
                        .document(currentUserUid);
                DocumentReference inWishlistRef = db.collection("wishers")
                        .document(dishLink);
                DocumentReference dishVitalRef = db.collection("dish_vital")
                        .document(dishLink);
                if(isInWishlist){
                    // TODO: this should be done if and only if the updates are successful
                    isInWishlist = false;
                    addToWishlistIB.setImageResource(R.drawable.outline_add_black_24dp);
                    wishlistRef.update("a", FieldValue.arrayRemove(dishLink));
                    inWishlistRef.update("a", FieldValue.arrayRemove(currentUserUid));
                    dishVitalRef.update("num_wishlist", FieldValue.increment(-1));
                } else{
                    // TODO: this should be done if and only if the updates are successful
                    isInWishlist = true;
                    addToWishlistIB.setImageResource(R.drawable.outline_done_black_24dp);
                    wishlistRef.update("a", FieldValue.arrayUnion(dishLink));
                    inWishlistRef.update("a", FieldValue.arrayUnion(currentUserUid));
                    dishVitalRef.update("num_wishlist", FieldValue.increment(1));
                }
            }
        };
    }
}
