package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AlgoliaCredentials;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;

import org.json.JSONException;

public class AdvancedSearchDish extends AppCompatActivity {

    Toolbar toolbar;
    Searcher searcher;
    InstantSearch instantSearch;
    SearchBox searchBox;
    Hits hits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search_dish);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hits = findViewById(R.id.search_hits);
        final String ALGOLIA_INDEX_NAME = "Dishes";
        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
//        searcher.addFacetRefinement("restaurant", restaurantLink);
        searcher.addNumericRefinement(new NumericRefinement("price", 5, 100));
        searcher.addNumericRefinement(new NumericRefinement("rating", 4, 4));
        instantSearch = new InstantSearch(this, searcher);
        instantSearch.search();

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));
                String name = "";
                try{
                    name = hits.get(position).getString("name");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                ((TextView) view.findViewById(R.id.restaurant_name)).setText(name);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });

    }
}
