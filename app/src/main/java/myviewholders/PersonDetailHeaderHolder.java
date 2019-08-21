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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import myapp.utils.SourceAllDishes;

public class PersonDetailHeaderHolder extends RecyclerView.ViewHolder {
    TextView name, birthdate, hometown, currentLocation, phone, seeAll;
    Button connect;
    RecyclerView rvWishlist, rvFollowedRestaurants;
    public PersonDetailHeaderHolder(@NonNull View v) {
        super(v);
        name = v.findViewById(R.id.name);
        birthdate = v.findViewById(R.id.birthdate);
        hometown = v.findViewById(R.id.hometown);
        currentLocation = v.findViewById(R.id.current_location);
        phone = v.findViewById(R.id.phone);
        seeAll = v.findViewById(R.id.see_all);
        connect = v.findViewById(R.id.connect);
        rvWishlist = v.findViewById(R.id.wishlist);
        rvFollowedRestaurants = v.findViewById(R.id.followed_restaurants);
    }
    public void bindTo(final Context context, String personLink){
        if(personLink.equals("")) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("person_vital").document(personLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personInfo = task.getResult();
                    if(personInfo.exists()){
                        String personName = personInfo.getString("n");
                        Timestamp personBirthdate = personInfo.getTimestamp("b");
                        String personHometown = personInfo.getString("ht");
                        String personCurrentLocation = personInfo.getString("ct");
                        String personPhone = personInfo.getString("p");
                        name.setText(personName);
                        birthdate.setText(personBirthdate.toString());
                        currentLocation.setText(personCurrentLocation);
                        hometown.setText(personHometown);
                        phone.setText(personPhone);
                    }
                }
            }
        });
        db.collection("person_extra").document(personLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personExtraData = task.getResult();
                    if(personExtraData.exists()){
                        // horizontal wishlist
                        // firestore array --> JAVA arraylist
                        final ArrayList<String> wishlist = (ArrayList) personExtraData.get("w");
                        seeAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("CLICKED", "See All Clicked");
                                Intent intent = new Intent(context, AllDishes.class);
                                intent.putStringArrayListExtra("dishesList", wishlist);
                                intent.putExtra("source", SourceAllDishes.WISHLIST);
                                context.startActivity(intent);
                            }
                        });
                        LinearLayoutManager wishlistLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rvWishlist.setLayoutManager(wishlistLayoutManager);
                        rvWishlist.setNestedScrollingEnabled(false);
                        WishlistAdapter adapter = new WishlistAdapter(wishlist);
                        adapter.notifyDataSetChanged();
                        rvWishlist.setAdapter(adapter);

                        // horizontal list of followed restaurants
                        ArrayList<String> followedRestaurants = (ArrayList) personExtraData.get("re");
                        LinearLayoutManager followedRestaurantLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rvFollowedRestaurants.setLayoutManager(followedRestaurantLayoutManager);
                        rvFollowedRestaurants.setNestedScrollingEnabled(false);
                        FollowedRestaurantsAdapter followedRestaurantsAdapter = new FollowedRestaurantsAdapter(followedRestaurants);
                        followedRestaurantsAdapter.notifyDataSetChanged();
                        rvFollowedRestaurants.setAdapter(followedRestaurantsAdapter);
                    }
                }
            }
        });
    }

    private class WishlistAdapter extends RecyclerView.Adapter<WishlistItemHolder>{
        ArrayList<String> wishlist;
        WishlistAdapter(ArrayList<String> wishlist){
            this.wishlist = wishlist;
        }
        @Override
        public void onBindViewHolder(@NonNull WishlistItemHolder wishlistItemHolder, int i) {
            wishlistItemHolder.bindTo(wishlist.get(i));
        }
        @Override
        public int getItemCount() {
            return wishlist.size();
        }

        @NonNull
        @Override
        public WishlistItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.wishlist_item, viewGroup, false);
             return new WishlistItemHolder(view);
        }
    }

    private class FollowedRestaurantsAdapter extends RecyclerView.Adapter<FollowedRestaurantItemHolder>{
        ArrayList<String> followedRestaurants;
        FollowedRestaurantsAdapter(ArrayList<String> followedRestaurants){
            this.followedRestaurants = followedRestaurants;
        }
        @Override
        public void onBindViewHolder(@NonNull FollowedRestaurantItemHolder followedRestaurantItemHolder, int i) {
            followedRestaurantItemHolder.bindTo(followedRestaurants.get(i));
        }
        @Override
        public int getItemCount() {
            return followedRestaurants.size();
        }

        @NonNull
        @Override
        public FollowedRestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.followed_restaurants_item, viewGroup, false);
            return new FollowedRestaurantItemHolder(view);
        }
    }
}
