package com.example.tuhin.myapplication;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import models.DishFeedback;
import models.RestaurantFeedback;


// receives explicit intent with bundle extra
// the keys =>
//     "caption": str
//     "imageSringUris": ArrayList<String>
//     "restaurantFeedback":RestaurantFeedback
public class CreatePostAddDishes extends AppCompatActivity {

    private final int REQUEST_DISH_FEEDBACK = 1;

    Bundle post;
    ArrayList<String> addedDishes = new ArrayList<>();
    ArrayList<DishFeedback> dishFeedbacks = new ArrayList<>();

    Toolbar toolbar;
    ImageButton addDishes;
    LinearLayout dishesWithFeedback;
    TextView noDish;
    ScrollView scrollView;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_add_dishes);

        post = getIntent().getExtras();

        // checking if data received correctly
        RestaurantFeedback restaurantFeedback = (RestaurantFeedback) post.getSerializable("restaurantFeedback");
        final String restaurantLink = restaurantFeedback.link;
        Log.i("RESTAURANT", restaurantLink);

        toolbar = findViewById(R.id.toolbar);
        addDishes = findViewById(R.id.add_dishes);
        dishesWithFeedback = findViewById(R.id.dishes_with_feedback);
        noDish = findViewById(R.id.no_dish);
        scrollView = findViewById(R.id.scrollView);
        next = findViewById(R.id.next);

        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        addDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePostAddDishes.this, CreatePostDishSelectReview.class);
                intent.putExtra("restaurantLink", restaurantLink);
                startActivityForResult(intent, REQUEST_DISH_FEEDBACK);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePostAddDishes.this, CreatePostAddPeople.class);
                post.putSerializable("dishFeedbacks", dishFeedbacks);
                intent.putExtras(post);
//                intent.putExtra("dishes", dishFeedbacks);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_DISH_FEEDBACK){
                if(data != null){
                    // all the data is packed in this
                    final DishFeedback dishFeedback = (DishFeedback) data.getSerializableExtra("dishFeedback");
                    Log.i("rating", Float.toString(dishFeedback.rating));
                    Log.i("review",dishFeedback.review);
                    Log.i("name", dishFeedback.name);
                    Log.i("link", dishFeedback.link);

                    if(addedDishes.contains(dishFeedback.link)){
                        String toastMsg = "Feedback for " + dishFeedback.name + " already exixts";
                        Toast.makeText(CreatePostAddDishes.this, toastMsg, Toast.LENGTH_SHORT).show();
                    }else{
                        addedDishes.add(dishFeedback.link);
                        dishFeedbacks.add(dishFeedback);
                        final View view = LayoutInflater.from(CreatePostAddDishes.this).inflate(R.layout.dish_with_feedback, null);
                        ((TextView)view.findViewById(R.id.dish_name)).setText(dishFeedback.name);
                        ((TextView)view.findViewById(R.id.review)).setText(dishFeedback.review);
                        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setRating(dishFeedback.rating);
                        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setIsIndicator(true);
                        ((ImageButton)view.findViewById(R.id.delete_dish)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dishesWithFeedback.removeView(view);
                                addedDishes.remove(dishFeedback.link);
                                dishFeedbacks.remove(dishFeedback);
                                if(addedDishes.isEmpty()){
                                    noDish.setVisibility(View.VISIBLE);
                                    scrollView.setVisibility(View.GONE);
                                }
                            }
                        });
                        dishesWithFeedback.addView(view, dishesWithFeedback.getChildCount()-1);
                        noDish.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
