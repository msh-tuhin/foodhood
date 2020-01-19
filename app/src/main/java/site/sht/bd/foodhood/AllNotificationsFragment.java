package site.sht.bd.foodhood;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import models.NotificationModel;
import myviewholders.NotificationHolder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import site.sht.bd.foodhood.R;

public class AllNotificationsFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rv;
    FirebaseFirestore db;
    FirestorePagingAdapter<NotificationModel, NotificationHolder> adapter;
    LinearLayoutManager linearLayoutManager;

    public AllNotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("LIFECYCLE-TEST", "oncreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("LIFECYCLE-TEST", "oncreateview");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i("LIFECYCLE-TEST", "onviewcreated");
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rv = view.findViewById(R.id.notifications_rv);

        linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), linearLayoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter = getAdapter(this);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.alter_timeline).setVisible(false);
    }

    @Override
    public void onStart() {
        Log.i("LIFECYCLE-TEST", "onstart");
        super.onStart();
//        adapter.startListening();
    }

    @Override
    public void onStop() {
        Log.i("LIFECYCLE-TEST", "oncstop");
        super.onStop();
//        adapter.stopListening();
    }

    @Override
    public void onResume() {
        Log.i("LIFECYCLE-TEST", "onresume");
        super.onResume();
//        adapter.refresh();
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        Log.i("LIFECYCLE-TEST", "onpause");
        super.onPause();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.i("LIFECYCLE-TEST", "onattach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.i("LIFECYCLE-TEST", "ondetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i("LIFECYCLE-TEST", "ondestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.i("LIFECYCLE-TEST", "ondestroyview");
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("LIFECYCLE-TEST", "onactivitycreated");
        super.onActivityCreated(savedInstanceState);
    }

    private FirestorePagingAdapter<NotificationModel, NotificationHolder> getAdapter(LifecycleOwner lifecycleOwner){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query bQuery = db.collection("notifications")
                .document(currentUserLink)
                .collection("n").orderBy("ts", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(10).build();
        FirestorePagingOptions<NotificationModel> options = new FirestorePagingOptions.Builder<NotificationModel>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(bQuery, config, NotificationModel.class).build();

        FirestorePagingAdapter<NotificationModel, NotificationHolder> adapter =
                new FirestorePagingAdapter<NotificationModel, NotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder notificationHolder, int i, @NonNull NotificationModel notificationModel) {
                Log.i("calling", "onBindViewHolder");
                notificationHolder.bindTo(notificationModel, AllNotificationsFragment.this.getActivity());
            }

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.i("calling", "onCreateViewHolder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.a_notification, parent, false);
                return new NotificationHolder(view);
            }
        };

        return adapter;
    }
}
