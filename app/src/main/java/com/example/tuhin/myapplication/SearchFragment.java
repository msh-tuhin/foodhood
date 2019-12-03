package com.example.tuhin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algolia.instantsearch.core.events.QueryTextChangeEvent;
import com.algolia.instantsearch.core.helpers.Highlighter;
import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.databinding.BindingHelper;
import com.algolia.instantsearch.ui.databinding.RenderingHelper;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
        // instantSearch.setSearchOnEmptyString(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

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
                bindDistrict(hits, position, view);
                bindCategory(hits, position, view);
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
                    Log.i("object_id", "no value found");
                }
            }
        });

    }

    private void bindName(Hits hits, int position, View view){
        Log.i("inside", "bindname");
        String name = "";
        try{
            name = hits.get(position).getString("name");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(name);
            getHighlight(hits.get(position), "name", builder);
            ((TextView) view.findViewById(R.id.name)).setText(builder);
        }catch (JSONException e){
            Log.i("name", "no value found");
            ((TextView) view.findViewById(R.id.name)).setText("");
        }
    }

    private void bindRating(Hits hits, int position, View view){
        Double rating;
        try{
            rating = hits.get(position).getDouble("rating");
        }catch (JSONException e){
            Log.i("rating", "no value found");
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
            Log.i("price", "no value found");
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
            //SpannableString spannableAddress = new SpannableString(currentTown);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(currentTown);
            getHighlight(hits.get(position), "current_town", builder);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0,
                    currentTown.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            addressTV.setText(builder);
            addressLayout.setVisibility(View.VISIBLE);
            return;
        }catch (JSONException e){
            Log.i("current_town", "no value found");
        }

        // for restaurants
        try{
            address = hits.get(position).getString("address");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(address);
            getHighlight(hits.get(position), "address", builder);
            addressTV.setText(builder);
            addressLayout.setVisibility(View.VISIBLE);
            return;
        }catch (JSONException e){
            Log.i("address", "no value found");
        }

        // for dishes
        try{
            restaurantName = hits.get(position).getString("restaurant_name");
            restaurantAddress = hits.get(position).getString("restaurant_address");
        }catch (JSONException e){
            Log.i("name_address", "no value found");;
        }
        if(restaurantName==null || restaurantName.equals("")) return;
        text = restaurantName;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(restaurantName);
        getHighlight(hits.get(position), "restaurant_name", builder);
        int start = text.indexOf(restaurantName);
        int end = start + restaurantName.length();
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(restaurantAddress!=null && !restaurantAddress.equals("")){
            builder.append("\n");
            builder.append(restaurantAddress);
            getHighlight(hits.get(position), "restaurant_address", builder);
        }
        addressTV.setText(builder);
        addressLayout.setVisibility(View.VISIBLE);
    }

    private void bindPicture(Hits hits, int position, View view){
        try{
            String link = hits.get(position).getString("image_url");
            PictureBinder.bindPictureSearchResult((CircleImageView)view.findViewById(R.id.avatar), link);
        }catch (JSONException e){
            Log.i("image_url", "no value found");
        }
    }

    private void bindDistrict(Hits hits, int position, View view){
        try{
            String district = hits.get(position).getString("district");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(district);
            boolean isDistrictHighlighted = getHighlight(hits.get(position), "district", builder);
            SpannableStringBuilder newBuilder = new SpannableStringBuilder("District: ");
            newBuilder.append(builder);
            if(isDistrictHighlighted){
                TextView districtTV = view.findViewById(R.id.district);
                districtTV.setText(newBuilder);
                districtTV.setVisibility(View.VISIBLE);
            }
        }catch (JSONException e){
            Log.i("district", "no value found");
        }
    }

    private void bindCategory(Hits hits, int position, View view){
        try{
            JSONArray categories = hits.get(position).getJSONArray("category");
            String categoryText = "";

            // comma is not added after the last element
            for(int i=0; i <= categories.length()-2; i++){
                categoryText += categories.getString(i);
                categoryText += ", ";
            }
            categoryText += categories.getString(categories.length()-1);

            SpannableStringBuilder builder = new SpannableStringBuilder(categoryText);
            boolean isCategoryHighlighted = getHighlightedArrayField(hits.get(position),
                    "category", builder);
            if(isCategoryHighlighted){
                Log.i("denug", "inside");
                SpannableStringBuilder newBuilder = new SpannableStringBuilder();
                newBuilder.append("Category: ");
                newBuilder.append(builder);
                TextView categoryTV = view.findViewById(R.id.category);
                categoryTV.setText(newBuilder);
                categoryTV.setVisibility(View.VISIBLE);
            }
        }catch (JSONException e){
            Log.i("category", "no value found");
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
                    Log.i("object_id", "no value found");
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
        TextView districtTV = view.findViewById(R.id.district);
        TextView categoryTV = view.findViewById(R.id.category);

        avatar.setImageResource(R.drawable.ltgray);
        nameTV.setText("");
        ratingTV.setText("");
        priceTV.setText("");
        addressTV.setText("");
        districtTV.setText("");
        districtTV.setVisibility(View.GONE);
        categoryTV.setText("");
        categoryTV.setVisibility(View.GONE);
        ratingLayout.setVisibility(View.GONE);
        priceLayout.setVisibility(View.GONE);
        addressLayout.setVisibility(View.GONE);
        //parentLayout.setOnClickListener(null);
    }

    private boolean getHighlight(JSONObject hit, String attributeName,
                                         SpannableStringBuilder builder){
        boolean isHighlighted = false;
        try{
            JSONObject highlightResult = hit.getJSONObject("_highlightResult");
            JSONObject highlightedAttribute = highlightResult.getJSONObject(attributeName);
            JSONArray matchedWords = highlightedAttribute.getJSONArray("matchedWords");

            for(int i=0; i<matchedWords.length(); i++){
                int start = builder.toString().toLowerCase().indexOf(matchedWords.getString(i));
                if(start>=0){
                    int end = start + matchedWords.getString(i).length();
                    builder.setSpan(new ForegroundColorSpan(Color.BLUE),
                            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // return true;
                    isHighlighted = true;
                }
            }

        }catch (JSONException e){
            Log.e("_highlightResult", "no value found");
        }
        return isHighlighted;
    }

    private boolean getHighlightedArrayField(JSONObject hit, String attributeName, SpannableStringBuilder builder){
        boolean isCategoryHighlighted = false;
        try{
            JSONObject highlightResult = hit.getJSONObject("_highlightResult");
            Log.i("hmm", "ase");
            JSONArray highlightedAttributes = highlightResult.getJSONArray(attributeName);
            Log.i("hmm", "nai");
            for(int i=0; i<highlightedAttributes.length();i++){
                JSONObject jsonObject = highlightedAttributes.getJSONObject(i);
                if (jsonObject.getString("matchLevel").equals("none")) {
                    continue;
                }
                JSONArray matchedWords = jsonObject.getJSONArray("matchedWords");
                for(int j=0; j<matchedWords.length(); j++){
                    int start = builder.toString().toLowerCase().indexOf(matchedWords.getString(j));
                    if(start>=0){
                        int end = start + matchedWords.getString(j).length();
                        builder.setSpan(new ForegroundColorSpan(Color.BLUE),
                                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        isCategoryHighlighted = true;
                    }
                }
            }
        }catch (JSONException e){
            Log.e("_highlightResult", "no value found");
        }
        return isCategoryHighlighted;
    }
}
