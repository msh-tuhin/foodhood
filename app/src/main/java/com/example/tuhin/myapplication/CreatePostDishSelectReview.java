package com.example.tuhin.myapplication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import de.hdodenhof.circleimageview.CircleImageView;
import models.DishFeedback;
import models.SelectedDish;
import myapp.utils.AlgoliaAttributeNames;
import myapp.utils.AlgoliaCredentials;
import myapp.utils.AlgoliaIndexNames;
import myapp.utils.PictureBinder;
import myapp.utils.SearchHitBinder;


// receives explicit intent with extra string("restaurantLink")
public class CreatePostDishSelectReview extends AppCompatActivity {

    SelectedDish selectedDish = new SelectedDish();
    Searcher searcher;
    InstantSearch instantSearch;

    Toolbar toolbar;
    Hits hits;
    CoordinatorLayout selectDishLayout;
    CircleImageView selectedDishAvatar;
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
        selectedDishAvatar = findViewById(R.id.selected_dish_avatar);
        selectedDishName = findViewById(R.id.selected_dish_name);
        dishRatingBar = findViewById(R.id.dish_ratingBar);
        reviewEditText = findViewById(R.id.review);
        done = findViewById(R.id.done);
        cancel = findViewById(R.id.cancel);

        toolbar.setTitle("Select A Dish");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

        String restaurantLink = getIntent().getStringExtra("restaurantLink");
//        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY,
//                AlgoliaIndexNames.INDEX_MAIN);
//        searcher.addFacetRefinement(AlgoliaAttributeNames.DISH_PARENT_RESTAURANT_LINK, restaurantLink);
//        searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 1));
//        instantSearch = new InstantSearch(this, searcher);
//        instantSearch.search();
        initiateSearch();

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                SearchHitBinder.refreshView(view);
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));

                SearchHitBinder searchHitBinder = new SearchHitBinder(hits, position, view);
                searchHitBinder.bind(true, true, true, true,
                        false, false, false);
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
                }

                selectDishLayout.setVisibility(View.GONE);
                PictureBinder.bindPictureSearchResult(selectedDishAvatar, selectedDish.imageUrl);
                if(selectedDish.name!=null){
                    selectedDishName.setText(selectedDish.name);
                }
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
                dishFeedback.imageUrl = selectedDish.imageUrl;
                intent.putExtra("dishFeedback", dishFeedback);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if(searcher!=null){
            searcher.destroy();
        }
        super.onDestroy();
    }

    private void initiateSearch(){
        final String restaurantLink = getIntent().getStringExtra("restaurantLink");
        FirebaseFirestore.getInstance().collection("acr")
                .document("a").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            searcher = Searcher.create(documentSnapshot.getString("id"),
                                    documentSnapshot.getString("k"),
                                    AlgoliaIndexNames.INDEX_MAIN);
                            searcher.addFacetRefinement(AlgoliaAttributeNames.DISH_PARENT_RESTAURANT_LINK, restaurantLink);
                            searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 1));
                            instantSearch = new InstantSearch(CreatePostDishSelectReview.this, searcher);
                            instantSearch.search();
                        }
                    }
                });
    }
}
