package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import myviewholders.SelfWishlistItemHolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView wishlistRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        toolbar = findViewById(R.id.toolbar);
        wishlistRV = findViewById(R.id.wishlist_rv);

        toolbar.setTitle("Wishlist");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final WishlistAdapter adapter = new WishlistAdapter();

        FirebaseFirestore.getInstance().collection("person_extra")
                .document(currentUserUid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            adapter.wishlist.addAll((List<String>)documentSnapshot.get("wishlist"));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        wishlistRV.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(wishlistRV.getContext(), layoutManager.getOrientation());
        wishlistRV.addItemDecoration(dividerItemDecoration);
        wishlistRV.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class WishlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public ArrayList<String> wishlist;

        WishlistAdapter(){
            wishlist = new ArrayList<>();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.self_wishlist_item, parent, false);
            return new SelfWishlistItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            // if i set the onClickListener on removeImageView here
            // then i don't have to send the reference to this adapter
            // to SelfWishlistHolder
            ((SelfWishlistItemHolder)holder).bindTo(this, wishlist.get(position));
        }

        @Override
        public int getItemCount() {
            return wishlist.size();
        }

        public void removeItem(String dishLink){
            int index = wishlist.indexOf(dishLink);
            wishlist.remove(dishLink);
            notifyItemRemoved(index);
        }
    }

}
