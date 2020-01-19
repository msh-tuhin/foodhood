package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;

import myapp.utils.SourceAllDishes;
import myviewholders.AllDishesItemHolder;


// receives explicit intent with StringArraylistExtra : dishesList
//                               intExtra : source
// could also receive restaurantLink, personLink
// may use StringArraylistExtra : dishesList for showing dishes for post
// and restaurantLink, personLink for showing all dishes of restaurant
// and wishlist
// might be decided by checking intExtra : source
public class AllDishes extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView rv;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dishes);

        int source = getIntent().getIntExtra("source", 0);
        ArrayList<String> links = getIntent().getStringArrayListExtra("dishesList");

        // TODO if personLink/restaurantLink is given then fetch
        // TODO wishlist/restaurants_all_dishes

        rv = findViewById(R.id.rv);
        toolbar = findViewById(R.id.toolbar);

        String title = source == SourceAllDishes.WISHLIST ? "Wishlist" : "All Dishes";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        DishesAdapter adapter = new DishesAdapter(links);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // might use a different adapter later
    private class DishesAdapter extends RecyclerView.Adapter<AllDishesItemHolder>{

        ArrayList<String> dishes;
        Task<DocumentSnapshot> taskWithCurrentUserWishlist;

        DishesAdapter(ArrayList<String> dishes){
            this.dishes = dishes;
            taskWithCurrentUserWishlist = db.collection("wishlist")
                    .document(mAuth.getCurrentUser().getUid()).get();
        }

        @Override
        public void onBindViewHolder(@NonNull AllDishesItemHolder dishHolder, int i) {
            dishHolder.bindTo(AllDishes.this, dishes.get(i), taskWithCurrentUserWishlist);
        }
        @Override
        public int getItemCount() {
            return dishes.size();
        }

        @NonNull
        @Override
        public AllDishesItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.all_dishes_item, viewGroup, false);
            return new AllDishesItemHolder(view);
        }
    }
}
