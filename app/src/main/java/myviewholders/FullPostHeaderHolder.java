package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tuhin.myapplication.CreatePostAddDishes;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.EntryPoints;

public class FullPostHeaderHolder extends HalfPostHolder {

//    LinearLayout restaurantFeedbackLayout;
    FrameLayout restaurantFeedbackLayout;
    LinearLayout dishesFeedbackLayout;
    FirebaseFirestore db;
    public FullPostHeaderHolder(View v){
        super(v);
        restaurantFeedbackLayout = v.findViewById(R.id.rest_feedback_layout);
        dishesFeedbackLayout = v.findViewById(R.id.dishes_feedback_layout);
    }


    public void bindTo(final Context context, Task<DocumentSnapshot> taskPost, final String postLink){
        // to bind a newly downloaded post
        // doYourBit(context, FirebaseFirestore.getInstance().collection("posts").document(postLink).get(), postLink);
        // to use the post downloaded in home page
        Log.i("bindTo", this.getClass().toString());
        doYourBit(context, taskPost, postLink);
        db = FirebaseFirestore.getInstance();
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("postLink", postLink);
                intent.putExtra("entry_point", EntryPoints.FULL_POST_PAGE);
                ((FullPost)context).startActivityForResult(intent, ((FullPost)context).REQUEST_COMMENT);
            }
        });
        // adding the feedbacks
        taskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot post = task.getResult();
                    if(post.exists()){
                        Log.i("documentsnapshot", post.toString());
                        final Map<String, String> dishes = (Map<String, String>) post.get("d");
                        final Map<String, String> restaurant = (Map<String, String>) post.get("r");
                        List<String> dishFeedbacksList = (List<String>) post.get("f");
                        String restaurantFeedback = post.getString("rf");
                        Log.i("before", "rest");
                        db.collection("feedbacks").document(restaurantFeedback)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot feedback = task.getResult();
                                    if(feedback.exists()){
                                        float rating = feedback.getDouble("r").floatValue();
                                        final View view = LayoutInflater.from(context).inflate(R.layout.feedback_full_post, null);
                                        ((TextView)view.findViewById(R.id.dish_name)).setText(restaurant.get("n"));
                                        ((TextView)view.findViewById(R.id.review)).setText(feedback.getString("re"));
                                        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setRating(rating);
                                        ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setIsIndicator(true);
                                        view.setVisibility(View.VISIBLE);
                                        restaurantFeedbackLayout.findViewById(R.id.not_found).setVisibility(View.GONE);
                                        restaurantFeedbackLayout.addView(view);

                                    }
                                }
                            }
                        });

                        Log.i("before", "dish");
                        int a=1;
                        for(final String dishFeedback : dishFeedbacksList){
                            db.collection("feedbacks").document(dishFeedback).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot feedback = task.getResult();
                                                if(feedback.exists()){
                                                    float rating = feedback.getDouble("r").floatValue();
                                                    String dishLink = feedback.getString("wh");
                                                    final View view = LayoutInflater.from(context).inflate(R.layout.feedback_full_post, null);
                                                    ((TextView)view.findViewById(R.id.dish_name)).setText(dishes.get(dishLink));
                                                    ((TextView)view.findViewById(R.id.review)).setText(feedback.getString("re"));
                                                    ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setRating(rating);
                                                    ((RatingBar)view.findViewById(R.id.dish_ratingBar)).setIsIndicator(true);
                                                    dishesFeedbackLayout.addView(view);

                                                }
                                            }
                                        }
                                    });

                        }

                    }

                }
            }
        });
    }

}
