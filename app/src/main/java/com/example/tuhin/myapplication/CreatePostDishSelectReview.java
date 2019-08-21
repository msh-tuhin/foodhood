package com.example.tuhin.myapplication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;

import org.json.JSONException;

import models.DishFeedback;
import models.SelectedDish;
import myapp.utils.AlgoliaCredentials;


// receives explicit intent with extra string("restaurantLink")
public class CreatePostDishSelectReview extends AppCompatActivity {

    private final String ALGOLIA_INDEX_NAME = "Dishes";


    SelectedDish selectedDish = new SelectedDish();
    Searcher searcher;
    InstantSearch instantSearch;

    Toolbar toolbar;
    Hits hits;
    CoordinatorLayout selectDishLayout;
    ConstraintLayout dishReviewLayout;
    TextView selectedDishName;
    RatingBar dishRatingBar;
    EditText reviewEditText;
    Button done;
    ImageButton cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_dish_select_review);

        toolbar = findViewById(R.id.toolbar);
        hits = findViewById(R.id.search_hits);
        selectDishLayout = findViewById(R.id.coordinatorLayout);
        dishReviewLayout = findViewById(R.id.dish_review);
        selectedDishName = findViewById(R.id.selected_dish_name);
        dishRatingBar = findViewById(R.id.dish_ratingBar);
        reviewEditText = findViewById(R.id.review);
        done = findViewById(R.id.done);
        cancel = findViewById(R.id.cancel);

        toolbar.setTitle("Select A Dish");
        setSupportActionBar(toolbar);

        String restaurantLink = getIntent().getStringExtra("restaurantLink");
        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        searcher.addFacetRefinement("restaurant", restaurantLink);
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

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                try{
                    // throws JSONException
                    selectedDish.setFromJSONObject(hits.get(position));
                }catch (JSONException e){
                    e.printStackTrace();
                    selectedDish.name = null;
                    selectedDish.id = null;
                    return;
                }

                selectDishLayout.setVisibility(View.GONE);
                selectedDishName.setText(selectedDish.name);
                dishReviewLayout.setVisibility(View.VISIBLE);
            }
        });

        dishRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating < 1){
                    ratingBar.setRating(1);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dishRatingBar.setRating(0);
                reviewEditText.setText("");
                dishReviewLayout.setVisibility(View.GONE);
                selectDishLayout.setVisibility(View.VISIBLE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = dishRatingBar.getRating();
                String review = reviewEditText.getText().toString();
                Intent intent = new Intent();
                // pack all the data in one object
                DishFeedback dishFeedback = new DishFeedback(selectedDish.id, selectedDish.name, rating, review);
                intent.putExtra("dishFeedback", dishFeedback);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }
}
