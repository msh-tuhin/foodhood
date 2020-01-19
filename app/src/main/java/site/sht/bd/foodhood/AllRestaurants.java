package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myviewholders.AllRestaurantItemHolder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;

public class AllRestaurants extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RestaurantAdapter adapter;
    RecyclerView rv;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_restaurants);

        String personLink = getIntent().getStringExtra("personLink");

        rv = findViewById(R.id.rv);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Followed Restaurants");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new RestaurantAdapter(new ArrayList<String>());
        rv.setAdapter(adapter);
        populateAdapter(personLink);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateAdapter(String personLink){
        if(personLink==null) return;
        db.collection("following_restaurants")
                .document(personLink)
                .get()
                .addOnSuccessListener(AllRestaurants.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> links = (ArrayList)documentSnapshot.get("a");
                                adapter.restaurants.addAll(links);
                                adapter.notifyDataSetChanged();
                            }catch (NullPointerException e){
                                Log.e("error", e.getMessage());
                            }
                        }
                    }
                });
    }

    private class RestaurantAdapter extends RecyclerView.Adapter<AllRestaurantItemHolder>{
        ArrayList<String> restaurants;
        Task<DocumentSnapshot> taskWithCurrentUserFollowingRestaurants;
        RestaurantAdapter(ArrayList<String> restaurants){
            this.restaurants = restaurants;
            taskWithCurrentUserFollowingRestaurants = db.collection("following_restaurants")
                    .document(mAuth.getCurrentUser().getUid()).get();
        }

        @Override
        public void onBindViewHolder(@NonNull AllRestaurantItemHolder restaurantItemHolder, int i) {
            restaurantItemHolder.bindTo(AllRestaurants.this, restaurants.get(i),
                    taskWithCurrentUserFollowingRestaurants);
        }

        @Override
        public int getItemCount() {
            return restaurants.size();
        }

        @NonNull
        @Override
        public AllRestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.all_restaurant_item, viewGroup, false);
            return new AllRestaurantItemHolder(view);
        }
    }
}
