package site.sht.bd.foodhood;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import models.NotificationModel;
import myviewholders.NotificationHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import site.sht.bd.foodhood.R;

public class RestAllNotificationsFragment extends Fragment {

    private RecyclerView rv;
    private FirestorePagingAdapter<NotificationModel, NotificationHolder> adapter;
    private LinearLayoutManager linearLayoutManager;

    public RestAllNotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rest_all_notifications, container, false);
    }
}
