package site.sht.bd.foodhood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import myapp.utils.SourceMorePeople;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import site.sht.bd.foodhood.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoreOptionsFragment extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private int[] icons = {
            R.drawable.outline_account_circle_black_24dp,
            R.drawable.outline_people_outline_black_24dp,
            // icon for wishlist,
            R.drawable.wishlist_96,
            R.drawable.outline_search_black_24dp,
            //R.drawable.outline_settings_applications_black_24dp,
            R.drawable.logout_96
            // icon for favorite restaurants
    };
    public MoreOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] moreItemsArray = getResources().getStringArray(R.array.more_items);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(moreItemsArray));
        ListView optionsListView = view.findViewById(R.id.options_listView);
        MyArrayAdapter adapter = new MyArrayAdapter(this.requireContext(),
                android.R.layout.simple_list_item_1, arrayList);
        optionsListView.setAdapter(adapter);

        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity parentActivity = MoreOptionsFragment.this.getActivity();
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(parentActivity, EditPersonProfile.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(parentActivity, MorePeole.class);
                        intent.putExtra("source", SourceMorePeople.FOLLOWERS);
                        intent.putExtra("personLink", mAuth.getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(parentActivity, Wishlist.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(parentActivity, AdvancedSearchDish.class);
                        startActivity(intent);
                        break;
                    case 4:
                        mAuth.signOut();
                        break;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.alter_timeline).setVisible(false);
    }

    private class MyArrayAdapter extends ArrayAdapter{

        MyArrayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.more_options_item, parent, false);
            ImageView iconImageView = view.findViewById(R.id.icon);
            TextView itemName = view.findViewById(R.id.item_name);
            iconImageView.setImageDrawable(getResources().getDrawable(icons[position]));
            itemName.setText((String)this.getItem(position));
            return view;
        }
    }
}
