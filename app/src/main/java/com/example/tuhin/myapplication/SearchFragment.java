package com.example.tuhin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AlgoliaCredentials;
import myapp.utils.PictureBinder;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algolia.instantsearch.core.events.QueryTextChangeEvent;
import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;

import org.json.JSONException;

import java.text.DecimalFormat;

public class SearchFragment extends Fragment {

    private final String ALGOLIA_INDEX_NAME = "main";

    InstantSearch instantSearch;
    Searcher searcher;
    SearchBox searchBox;
    Hits hits;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchBox = view.findViewById(R.id.searchBox);
        hits = view.findViewById(R.id.search_hits);

        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID,
                AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);

        instantSearch = new InstantSearch(getActivity(), searcher);
        instantSearch.search();
        instantSearch.setSearchOnEmptyString(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

        // TODO add highlighting
        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                refreshView(view);
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));
                bindName(hits, position, view);
                bindRating(hits, position, view);
                bindPrice(hits, position, view);
                bindAddress(hits, position, view);
                bindPicture(hits, position, view);
                // setParentLayoutOnClickListener(hits, position, view);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                try{
                    String id = hits.get(position).getString("objectID");
                    int type = hits.get(position).getInt("type");
                    Intent intent;
                    switch (type){
                        case 0:
                            intent = new Intent(SearchFragment.this.getActivity(), PersonDetail.class);
                            intent.putExtra("personLink", id);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(SearchFragment.this.getActivity(), DishDetail.class);
                            intent.putExtra("dishLink", id);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(SearchFragment.this.getActivity(), RestDetail.class);
                            intent.putExtra("restaurantLink", id);
                            startActivity(intent);
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void bindName(Hits hits, int position, View view){
        String name = "";
        try{
            name = hits.get(position).getString("name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        ((TextView) view.findViewById(R.id.name)).setText(name);
    }

    private void bindRating(Hits hits, int position, View view){
        Double rating;
        try{
            rating = hits.get(position).getDouble("rating");
        }catch (JSONException e){
            e.printStackTrace();
            return;
        }
        TextView ratingTV = view.findViewById(R.id.rating);
        LinearLayout ratingLayout = view.findViewById(R.id.rating_layout);
        if(rating <= 0.0){
            ratingTV.setText("N/A");
        }else{
            DecimalFormat formatter = new DecimalFormat("#.0");
            ratingTV.setText(formatter.format(rating));
        }
        ratingLayout.setVisibility(View.VISIBLE);
    }

    private void bindPrice(Hits hits, int position, View view){
        Double price;
        try{
            price = hits.get(position).getDouble("price");
        }catch (JSONException e){
            e.printStackTrace();
            return;
        }
        TextView priceTV = view.findViewById(R.id.price);
        LinearLayout priceLayout = view.findViewById(R.id.price_layout);
        DecimalFormat formatter = new DecimalFormat("#.00");
        priceTV.setText(formatter.format(price));
        priceLayout.setVisibility(View.VISIBLE);
    }

    private void bindAddress(Hits hits, int position, View view){
        String text = "";
        String currentTown;
        String address;
        String restaurantName = "";
        String restaurantAddress = "";
        TextView addressTV = view.findViewById(R.id.address);
        LinearLayout addressLayout = view.findViewById(R.id.address_layout);

        // for persons
        try{
            currentTown = hits.get(position).getString("current_town");
            SpannableString spannableAddress = new SpannableString(currentTown);
            spannableAddress.setSpan(new StyleSpan(Typeface.BOLD), 0,
                    currentTown.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            addressTV.setText(spannableAddress);
            addressLayout.setVisibility(View.VISIBLE);
            return;
        }catch (JSONException e){
            e.printStackTrace();
        }

        // for restaurants
        try{
            address = hits.get(position).getString("address");
            addressTV.setText(address);
            addressLayout.setVisibility(View.VISIBLE);
            return;
        }catch (JSONException e){
            e.printStackTrace();
        }

        // for dishes
        try{
            restaurantName = hits.get(position).getString("restaurant_name");
            restaurantAddress = hits.get(position).getString("restaurant_address");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(restaurantName==null || restaurantName.equals("")) return;
        text = restaurantName;
        if(restaurantAddress!=null && !restaurantAddress.equals("")){
            text += "\n" + restaurantAddress;
        }
        SpannableString spannedAddress = new SpannableString(text);
        spannedAddress.setSpan(new StyleSpan(Typeface.BOLD), text.indexOf(restaurantName),
                restaurantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        addressTV.setText(spannedAddress);
        addressLayout.setVisibility(View.VISIBLE);
    }

    private void bindPicture(Hits hits, int position, View view){
        try{
            String link = hits.get(position).getString("image_url");
            PictureBinder.bindPictureSearchResult((CircleImageView)view.findViewById(R.id.avatar), link);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setParentLayoutOnClickListener(final Hits hits, final int position, final View view){
        LinearLayout parentLayout = view.findViewById(R.id.parent_layout);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String id = hits.get(position).getString("objectID");
                    int type = hits.get(position).getInt("type");
                    Intent intent;
                    switch (type){
                        case 0:
                            intent = new Intent(SearchFragment.this.getActivity(), PersonDetail.class);
                            intent.putExtra("personLink", id);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(SearchFragment.this.getActivity(), DishDetail.class);
                            intent.putExtra("dishLink", id);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(SearchFragment.this.getActivity(), RestDetail.class);
                            intent.putExtra("restaurantLink", id);
                            startActivity(intent);
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshView(View view){
        LinearLayout parentLayout = view.findViewById(R.id.parent_layout);
        CircleImageView avatar = view.findViewById(R.id.avatar);
        TextView nameTV = view.findViewById(R.id.name);
        LinearLayout ratingLayout = view.findViewById(R.id.rating_layout);
        TextView ratingTV = view.findViewById(R.id.rating);
        LinearLayout priceLayout = view.findViewById(R.id.price_layout);
        TextView priceTV = view.findViewById(R.id.price);
        LinearLayout addressLayout = view.findViewById(R.id.address_layout);
        TextView addressTV = view.findViewById(R.id.address);

        avatar.setImageResource(R.drawable.ltgray);
        nameTV.setText("");
        ratingTV.setText("");
        priceTV.setText("");
        addressTV.setText("");
        ratingLayout.setVisibility(View.GONE);
        priceLayout.setVisibility(View.GONE);
        addressLayout.setVisibility(View.GONE);
        //parentLayout.setOnClickListener(null);
    }

}
