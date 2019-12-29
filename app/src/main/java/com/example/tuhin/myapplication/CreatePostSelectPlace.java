package com.example.tuhin.myapplication;

import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.algolia.instantsearch.core.events.QueryTextChangeEvent;
import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.RestaurantFeedback;
import models.SelectedPlace;
import myapp.utils.AlgoliaAttributeNames;
import myapp.utils.AlgoliaCredentials;
import myapp.utils.AlgoliaIndexNames;
import myapp.utils.PictureBinder;
import myapp.utils.SearchHitBinder;

// receives explicit intent with bundle extra
// the keys =>
//     "caption": str
//     "imageSringUris": ArrayList<String>
public class CreatePostSelectPlace extends AppCompatActivity {

    Bundle post;
    Searcher searcher;
    InstantSearch instantSearch;
    SelectedPlace selectedPlace = new SelectedPlace();

    Toolbar toolbar;
    Hits hits;
    ConstraintLayout selectedRestaurantLayout;
    CircleImageView selectedRestaurantAvatar;
    TextView selectedRestaurantName;
    ImageButton deleteButton;
    LinearLayout searchLayout;
    SearchBox searchBox;
    Button next;
    RatingBar ratingBar;
    EditText reviewEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_select_place);

        post = getIntent().getExtras();

        // checking if data received correctly
        String caption = post.getString("caption");
        Log.i("Bundle-Caption", caption);
        ArrayList<String> imageStringUris = post.getStringArrayList("imageSringUris");
        for(String stringUri : imageStringUris){
            Log.i("imageUri", stringUri);
        }

        toolbar = findViewById(R.id.toolbar);
        hits = findViewById(R.id.search_hits);
        selectedRestaurantAvatar = findViewById(R.id.selected_restaurant_avatar);
        selectedRestaurantLayout = findViewById(R.id.selected_restaurant);
        selectedRestaurantName = findViewById(R.id.selected_restaurant_name);
        deleteButton = findViewById(R.id.delete_button);
        searchLayout = findViewById(R.id.searchLayout);
        searchBox = findViewById(R.id.searchBox);
        next = findViewById(R.id.next);
        ratingBar = findViewById(R.id.restaurant_rating);
        reviewEditText = findViewById(R.id.restaurant_review);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

//        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY,
//                AlgoliaIndexNames.INDEX_MAIN);
//        searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 2));
//        instantSearch = new InstantSearch(this, searcher);
//        instantSearch.setSearchOnEmptyString(false);
        // instantSearch.search();
        initiateSearch();
        // because data binding doesn't work
        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                SearchHitBinder.refreshView(view);
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));
                SearchHitBinder searchHitBinder = new SearchHitBinder(hits, position, view);
                searchHitBinder.bind(true, true, true, false,
                        true, false, false, false);
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
                    selectedPlace.setFromJSONObject(hits.get(position));
                }catch (JSONException e){
                    e.printStackTrace();
                }

                searchLayout.setVisibility(View.GONE);
                PictureBinder.bindPictureSearchResult(selectedRestaurantAvatar, selectedPlace.imageUrl);
                if(selectedPlace.name != null){
                    selectedRestaurantName.setText(selectedPlace.name);
                }
                selectedRestaurantLayout.setVisibility(View.VISIBLE);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating < 1){
                    ratingBar.setRating(1);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to clear whatever is already typed
                searchBox.setQuery("", true);
                selectedRestaurantLayout.setVisibility(View.GONE);
                ratingBar.setRating(0);
                reviewEditText.setText("");
                searchLayout.setVisibility(View.VISIBLE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ID", selectedPlace.id);
                Intent intent = new Intent(CreatePostSelectPlace.this, CreatePostAddDishes.class);
                String review = reviewEditText.getText().toString();
                float rating = ratingBar.getRating();
                RestaurantFeedback restaurantFeedback = new RestaurantFeedback(selectedPlace.id, selectedPlace.name, rating, review);
                restaurantFeedback.imageUrl = selectedPlace.imageUrl;
                post.putSerializable("restaurantFeedback", restaurantFeedback);
//                post.putString("restaurantLink", selectedPlace.id);
                intent.putExtras(post);
                startActivity(intent);
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(QueryTextChangeEvent event){
        if(event.query.isEmpty()){
            // TODO
            Log.i("changed", "empty");
        }else{
            // TODO
            Log.i("changed", "not empty");
        }
    }

    private void initiateSearch(){
        FirebaseFirestore.getInstance().collection("acr")
                .document("a").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            searcher = Searcher.create(documentSnapshot.getString("id"),
                                    documentSnapshot.getString("k"),
                                    AlgoliaIndexNames.INDEX_MAIN);
                            searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 2));
                            instantSearch = new InstantSearch(CreatePostSelectPlace.this, searcher);
                            instantSearch.setSearchOnEmptyString(false);
                            // instantSearch.search();
                        }
                    }
                });
    }
}
