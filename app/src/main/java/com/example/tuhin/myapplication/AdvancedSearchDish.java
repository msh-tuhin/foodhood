package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AlgoliaCredentials;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.algolia.search.saas.AbstractQuery;

import org.json.JSONException;

public class AdvancedSearchDish extends AppCompatActivity {

    NumericRefinement minPriceFilter, maxPriceFilter, ratingFilter;
    Double previousMinPrice = 0.0, previousMaxPrice = 0.0, previousRating = 0.0;

    Toolbar toolbar;
    Searcher searcher;
    InstantSearch instantSearch;
    SearchBox searchBox;
    Hits hits;

    Button searchButton;
    EditText minPriceEditText, maxPriceEditText, ratingEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_dish);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Food Frenzy");
        setSupportActionBar(toolbar);

        searchButton = findViewById(R.id.search);
        minPriceEditText = findViewById(R.id.min_price_edittext);
        maxPriceEditText = findViewById(R.id.max_price_edittext);
        ratingEditText = findViewById(R.id.rating_edittext);

        hits = findViewById(R.id.search_hits);
        final String ALGOLIA_INDEX_NAME = "Dishes";
        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
//        searcher.addFacetRefinement("restaurant", restaurantLink);
//        searcher.addNumericRefinement(new NumericRefinement("price", 5, 100));
//        searcher.addNumericRefinement(new NumericRefinement("rating", 4, 4));
        instantSearch = new InstantSearch(this, searcher);
//        instantSearch.search();

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));
                String name = "";
                try{
                    name = hits.get(position).getString("name");
                    ((TextView) view.findViewById(R.id.restaurant_name)).setText(name);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO hide keyboard

                boolean shouldClearHits = true;
                boolean shouldSearch = false;
                double minPrice = 0.0, maxPrice = 0.0, rating = 0.0;
                searcher.clearFacetRefinements();
                searcher.removeNumericRefinement("price");
                searcher.removeNumericRefinement("rating");

                String minPriceString = minPriceEditText.getText().toString();
                if(!minPriceString.isEmpty()){
                    minPrice = Double.valueOf(minPriceString);
                    if(minPrice != 0.0){
                        minPriceFilter = new NumericRefinement("price", 4, minPrice);
                        searcher.addNumericRefinement(minPriceFilter);
                        shouldClearHits = false;
                    } else{
                        minPrice = 0.0;
                    }

                } else {
                    minPrice = 0.0;
                }

                if(!previousMinPrice.equals(minPrice)){
                    previousMinPrice = minPrice;
                    shouldSearch = true;
                }

                String maxPriceString = maxPriceEditText.getText().toString();
                if(!maxPriceString.isEmpty()){
                    maxPrice = Double.valueOf(maxPriceString);
                    if(maxPrice != 0.0){
                        maxPriceFilter = new NumericRefinement("price", 1, maxPrice);
                        searcher.addNumericRefinement(maxPriceFilter);
                        shouldClearHits = false;
                    } else {
                        maxPrice = 0.0;
                    }
                } else{
                    maxPrice = 0.0;
                }
                if(!previousMaxPrice.equals(maxPrice)){
                    previousMaxPrice = maxPrice;
                    shouldSearch = true;
                }

                String ratingString = ratingEditText.getText().toString();
                if(!ratingString.isEmpty()){
                    rating = Double.valueOf(ratingString);
                    if(rating != 0.0){
                        ratingFilter = new NumericRefinement("rating", 4, rating);
                        searcher.addNumericRefinement(ratingFilter);
                        shouldClearHits = false;

                    } else{
                        rating = 0.0;
                    }
                } else{
                    rating = 0.0;
                }
                if(!previousRating.equals(rating)){
                    previousRating = rating;
                    shouldSearch = true;
                }

                if(shouldSearch && !(minPrice==0.0 && maxPrice==0.0 && rating==0.0)) {
                    AbstractQuery.LatLng userLocation = new AbstractQuery.LatLng(40.71, -74.01);
                    searcher.getQuery().setAroundLatLng(userLocation).setAroundRadius(1000000);
                    instantSearch.search();
                }

                if(shouldClearHits){
                    hits.clear();
                    // TODO evaluate if this is needed or not
                    // searcher.reset();
                }
            }
        });

    }
}
