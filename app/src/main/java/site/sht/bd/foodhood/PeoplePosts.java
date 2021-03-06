package site.sht.bd.foodhood;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import models.PostModel;
import myviewholders.HalfPostHolderForRestaurant;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import site.sht.bd.foodhood.R;

public class PeoplePosts extends Fragment {

    RecyclerView rv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirestorePagingAdapter<PostModel, HalfPostHolderForRestaurant> adapter;
    LinearLayoutManager linearLayoutManager;

    public PeoplePosts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.person_posts_rv);

        linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = getAdapter(this);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    private FirestorePagingAdapter<PostModel, HalfPostHolderForRestaurant>
    getAdapter(LifecycleOwner lifecycleOwner){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query bQuery = db.collection("posts")
                // needs composite index
                .whereEqualTo("r.l", currentUserLink)
                .orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<PostModel> options = new FirestorePagingOptions.Builder<PostModel>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, PostModel.class).build();

        FirestorePagingAdapter<PostModel, HalfPostHolderForRestaurant> adapter =
                new FirestorePagingAdapter<PostModel, HalfPostHolderForRestaurant>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull HalfPostHolderForRestaurant halfPostHolderForRestaurant,
                                                    int i,
                                                    @NonNull PostModel postModel) {
                        Log.i("calling", "onBindViewHolder");
                        DocumentSnapshot postSnapshot = this.getCurrentList().get(i);
                        halfPostHolderForRestaurant.bindTo(PeoplePosts.this.getActivity(), postSnapshot);
                    }

                    @NonNull
                    @Override
                    public HalfPostHolderForRestaurant onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.i("calling", "onCreateViewHolder");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_half, parent, false);
                        return new HalfPostHolderForRestaurant(view);
                    }
                };

        return adapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
