package myviewholders;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import myapp.utils.SourceAllDishes;

public class RestaurantDetailHeaderHolder extends RecyclerView.ViewHolder{
    TextView restaurantName, restaurantAddress, restaurantPhone, restaurantEmail;
    TextView restaurantWebsite, numFollowedBy, restaurantRating, seeAll;
    Button followRestaurant;
    RecyclerView rv;
    private FirebaseFirestore db;
    private final String currentUserUid;

    public RestaurantDetailHeaderHolder(@NonNull View v) {
        super(v);
        db = FirebaseFirestore.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        restaurantName = v.findViewById(R.id.restaurant_name);
        followRestaurant = v.findViewById(R.id.follow_restaurant);
        restaurantAddress = v.findViewById(R.id.restaurant_address);
        restaurantPhone = v.findViewById(R.id.phone);
        restaurantEmail = v.findViewById(R.id.email);
        restaurantWebsite = v.findViewById(R.id.web);
        numFollowedBy = v.findViewById(R.id.num_followed_by);
        restaurantRating = v.findViewById(R.id.rating);
        seeAll = v.findViewById(R.id.see_all);
        rv = v.findViewById(R.id.all_dishes);

        // TODO set seeAll buttons onClickListener

    }

    public void bindTo(final Context context, final String restaurantLink){
        db.collection("rest_vital").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restaurantVital = task.getResult();
                    if(restaurantVital.exists()){
                        String name = restaurantVital.getString("n");
                        Double rating = restaurantVital.getDouble("r");
                        restaurantName.setText(name);
                        restaurantRating.setText(Double.toString(rating));
//                        ((RestDetail)context).toolbar.setTitle(name);
                        setCollapsedTitle(context, name);
                    }
                }
            }
        });

        db.collection("rest_extra").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restaurantExtra = task.getResult();
                    if(restaurantExtra.exists()){
                        final ArrayList<String> dishes = (ArrayList) restaurantExtra.get("dishes");
                        seeAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("CLICKED", "See All Clicked");
                                Intent intent = new Intent(context, AllDishes.class);
                                intent.putStringArrayListExtra("dishesList", dishes);
                                intent.putExtra("source", SourceAllDishes.RESTAURANT_ALL_DISHES);
                                context.startActivity(intent);
                            }
                        });
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rv.setLayoutManager(linearLayoutManager);
                        rv.setNestedScrollingEnabled(false);
                        RestaurantDishesAdapter adapter = new RestaurantDishesAdapter(dishes);
                        adapter.notifyDataSetChanged();
                        rv.setAdapter(adapter);

                        Map<String, String> addressMap = (Map) restaurantExtra.get("a");
                        String addressLine1 = addressMap.get("l1");
                        String addressLine2 = addressMap.get("l2");
                        String address = addressLine1 + "\n" + addressLine2;
                        String phone = restaurantExtra.getString("p");
                        String web = restaurantExtra.getString("w");
                        String email = restaurantExtra.getString("e");
                        Double followedBy = restaurantExtra.getDouble("nfb");
                        restaurantAddress.setText(address);
                        restaurantPhone.setText(phone);
                        restaurantWebsite.setText(web);
                        restaurantEmail.setText(email);
                        numFollowedBy.setText("Followed by " + Double.toString(followedBy));
                    }
                }
            }
        });

        db.collection("rest_followers").document(restaurantLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> followers = (ArrayList) documentSnapshot.get("a");
                                if(followers.contains(currentUserUid)){
                                    followRestaurant.setText("UNFOLLOW");
                                }
                            } catch (NullPointerException e){
                                Log.i("Error", e.getMessage());
                            }
                        }
                        followRestaurant.setVisibility(View.VISIBLE);
                        followRestaurant.setOnClickListener(getFollowRestaurantOnClickListener(restaurantLink));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        followRestaurant.setVisibility(View.VISIBLE);
                        followRestaurant.setOnClickListener(getFollowRestaurantOnClickListener(restaurantLink));
                    }
                });
    }

    private class RestaurantDishesAdapter extends RecyclerView.Adapter<RestaurantDishHolder>{

        ArrayList<String> dishes;

        RestaurantDishesAdapter(ArrayList<String> dishes){
            this.dishes = dishes;
        }

        @Override
        public void onBindViewHolder(@NonNull RestaurantDishHolder dishHolder, int i) {
            dishHolder.bindTo(dishes.get(i));
        }

        @Override
        public int getItemCount() {
            return dishes.size();
        }

        @NonNull
        @Override
        public RestaurantDishHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.wishlist_item, viewGroup, false);
            return new RestaurantDishHolder(view);
        }
    }

    private View.OnClickListener getFollowRestaurantOnClickListener(final String restaurantLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference personRestFollowRef = db.collection("following_restaurants")
                        .document(currentUserUid);
                DocumentReference restaurantFollowerRef = db.collection("rest_followers")
                        .document(restaurantLink);
                DocumentReference restRef = db.collection("rest_extra")
                        .document(restaurantLink);
                switch (((Button)v).getText().toString()){
                    case "FOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followRestaurant.setText("UNFOLLOW");
                        personRestFollowRef.update("a", FieldValue.arrayUnion(restaurantLink));
                        restaurantFollowerRef.update("a", FieldValue.arrayUnion(currentUserUid));
                        restRef.update("nfb", FieldValue.increment(1));
                        break;
                    case "UNFOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followRestaurant.setText("FOLLOW");
                        personRestFollowRef.update("a", FieldValue.arrayRemove(restaurantLink));
                        restaurantFollowerRef.update("a", FieldValue.arrayRemove(currentUserUid));
                        restRef.update("nfb", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }

    private void setCollapsedTitle(final Context context, final String name){
        ((RestDetail)context).appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    ((RestDetail)context).collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if(isShow) {
                    ((RestDetail)context).collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
