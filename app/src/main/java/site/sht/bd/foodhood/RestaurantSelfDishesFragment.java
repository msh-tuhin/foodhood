package site.sht.bd.foodhood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import myapp.utils.EditDishFormSource;
import myviewholders.SelfDishesItemHolder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;

public class RestaurantSelfDishesFragment extends Fragment {

    RecyclerView rv;
    FloatingActionButton fab;
    SelfDishesAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ListenerRegistration registration;

    public int editedPosition = -1;

    public RestaurantSelfDishesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_self_dishes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.dishes_rv);
        fab = view.findViewById(R.id.fab);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new SelfDishesAdapter(RestaurantSelfDishesFragment.this.getActivity(),
                RestaurantSelfDishesFragment.this,
                new ArrayList<String>());
        rv.setAdapter(adapter);
        initializeAdapter();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FAB", "clicked");
                Intent intent = new Intent(getActivity(), EditDishForm.class);
                intent.putExtra("source", EditDishFormSource.NEW_DISH);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeAdapter();
                Toast.makeText(RestaurantSelfDishesFragment.this.getActivity(),
                        "Refreshing", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // initializeAdapter();
        // registerListener();
        if(editedPosition > -1){
            Log.i("item", "being edited");
            adapter.notifyItemChanged(editedPosition);
            editedPosition = -1;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // registration.remove();
    }

    private void initializeAdapter(){
        FirebaseFirestore.getInstance().collection("dishes")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(RestaurantSelfDishesFragment.this.getActivity(),
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    ArrayList<String> dishes = (ArrayList) documentSnapshot.get("a");
                                    if(dishes != null){
                                        adapter.dishes.clear();
                                        adapter.dishes.addAll(dishes);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
    }

    private void registerListener(){
        registration = FirebaseFirestore.getInstance().collection("dishes")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        Log.i("event-listener", "called");
                        if(e!=null){
                            Log.e("error", e.getMessage());
                            return;
                        }
                        if(documentSnapshot!=null && documentSnapshot.exists()){
                            ArrayList<String> dishes = (ArrayList) documentSnapshot.get("a");
                            if(dishes != null){
                                adapter.dishes.clear();
                                adapter.dishes.addAll(dishes);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private class SelfDishesAdapter extends RecyclerView.Adapter<SelfDishesItemHolder>{

        Context context;
        Fragment fragment;
        ArrayList<String> dishes;

        SelfDishesAdapter(Context context, Fragment fragment, ArrayList<String> dishes){
            this.context = context;
            this.fragment = fragment;
            this.dishes = dishes;
        }

        @Override
        public void onBindViewHolder(@NonNull SelfDishesItemHolder dishHolder, int i) {
            dishHolder.bindTo(context, fragment, dishes.get(i), i);
        }

        @Override
        public int getItemCount() {
            return dishes.size();
        }

        @NonNull
        @Override
        public SelfDishesItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.self_dishes_item, viewGroup, false);
            return new SelfDishesItemHolder(view);
        }
    }
}
