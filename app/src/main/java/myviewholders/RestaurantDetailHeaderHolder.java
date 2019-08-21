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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import myapp.utils.SourceAllDishes;

public class RestaurantDetailHeaderHolder extends RecyclerView.ViewHolder {
    TextView restaurantName, restaurantAddress, restaurantPhone, restaurantEmail;
    TextView restaurantWebsite, numFollowedBy, restaurantRating, seeAll;
    Button followRestaurant;
    RecyclerView rv;

    public RestaurantDetailHeaderHolder(@NonNull View v) {
        super(v);
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

    public void bindTo(final Context context, String restaurantLink){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
}
