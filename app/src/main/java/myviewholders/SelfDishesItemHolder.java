package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.DishDetail;
import com.example.tuhin.myapplication.EditDishForm;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestaurantSelfDishesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.EditDishFormSource;
import myapp.utils.PictureBinder;

public class SelfDishesItemHolder extends RecyclerView.ViewHolder {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private LinearLayout parentLayout;
    private CircleImageView avatar;
    private TextView dishNameTV;
    private TextView dishDescriptionTV;
    private TextView dishRatingTV;
    private TextView dishPriceTV;
    private ImageView removeImageView;
    private ImageView editImageView;
    private TextView categoryTV;
    public SelfDishesItemHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        avatar = v.findViewById(R.id.avatar);
        dishNameTV = v.findViewById(R.id.name);
        dishDescriptionTV = v.findViewById(R.id.description);
        dishRatingTV = v.findViewById(R.id.rating);
        dishPriceTV = v.findViewById(R.id.price);
        removeImageView = v.findViewById(R.id.remove);
        editImageView = v.findViewById(R.id.edit);
        categoryTV = v.findViewById(R.id.category);
    }

    public void bindTo(final Context context, final Fragment fragment, final String dishLink, final int position){
        refreshHolder();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dish_vital").document(dishLink)
                .get()
                .addOnCompleteListener((Activity)context, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot dishInfo = task.getResult();
                            if(dishInfo.exists()){
                                bindDishImage(dishInfo);
                                bindDishName(dishInfo);
                                setDishNameOnClickListener(context, dishLink);
                                bindDescription(dishInfo);
                                bindRating(dishInfo);
                                bindPrice(dishInfo);
                                bindCategory(dishInfo);
                                setParentLayoutOnClickListener(context, dishLink);
                            }
                        }
                    }
                });

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RestaurantSelfDishesFragment)fragment).editedPosition = position;
                Intent intent = new Intent(context, EditDishForm.class);
                intent.putExtra("source", EditDishFormSource.EDIT_DISH);
                intent.putExtra("dishLink", dishLink);
                context.startActivity(intent);
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
        PictureBinder.bindCoverPicture(avatar, dishVitalSnapshot);
    }

    private void bindDishName(DocumentSnapshot dishVitalSnapshot){
        String name = dishVitalSnapshot.getString("n");
        if(name==null || name.equals(""))return;
        dishNameTV.setText(name);
        dishNameTV.setVisibility(View.VISIBLE);
    }

    private void bindDescription(DocumentSnapshot dishVitalSnapshot){
        String description = dishVitalSnapshot.getString("d");
        if(description==null || description.equals(""))return;
        dishDescriptionTV.setText(description);
        dishDescriptionTV.setVisibility(View.VISIBLE);
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

    private void bindCategory(DocumentSnapshot dishVitalSnapshot){
        String categoryText = "Category: ";
        ArrayList<String> categories = (ArrayList<String>) dishVitalSnapshot.get("c");
        if(categories!=null && categories.size()>0){
            categoryText += categories.get(0);
            categoryTV.setText(categoryText);
            categoryTV.setVisibility(View.VISIBLE);
        }
    }

    private void refreshHolder(){
        avatar.setImageResource(R.drawable.ltgray);
        dishNameTV.setText("");
        dishRatingTV.setText("");
        dishPriceTV.setText("");
        dishDescriptionTV.setText("");
        dishDescriptionTV.setVisibility(View.GONE);
    }
}
