package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myviewholders.RestaurantAllDishesItemHolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;

public class RestaurantAllDishes extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Toolbar toolbar;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_all_dishes);

        ArrayList<String> dishes = getIntent().getStringArrayListExtra("dishesList");

        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.rv);

        toolbar.setTitle("Dishes");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        RestaurantAllDishesAdapter adapter = new RestaurantAllDishesAdapter(dishes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class RestaurantAllDishesAdapter extends RecyclerView.Adapter<RestaurantAllDishesItemHolder>{

        public ArrayList<String> dishes;
        Task<DocumentSnapshot> taskWithCurrentUserWishlist;

        RestaurantAllDishesAdapter(ArrayList<String> dishes){
            this.dishes = dishes;
            taskWithCurrentUserWishlist = db.collection("wishlist")
                    .document(mAuth.getCurrentUser().getUid()).get();
        }

        @NonNull
        @Override
        public RestaurantAllDishesItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rest_dishes_item, parent, false);
            return new RestaurantAllDishesItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RestaurantAllDishesItemHolder holder, int position) {
            holder.bindTo(RestaurantAllDishes.this, dishes.get(position), taskWithCurrentUserWishlist);
        }

        @Override
        public int getItemCount() {
            return dishes.size();
        }
    }

}
