package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActualActivity;
import com.example.tuhin.myapplication.FullRestFeed;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestFeedHolder extends BaseHomeFeedHolder {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    public ConstraintLayout restFeedLayout;
    CircleImageView avatar;
    TextView restaurantNameTV, postTimeTV, captionTV;
    ImageView postImage;
    // inherited members from BaseHomeFeedHolder:
    // TextView: noOfLikes, noOfComments
    // ImageView: like, comment

    public RestFeedHolder(@NonNull View v) {
        super(v);
        restFeedLayout = v.findViewById(R.id.rest_feed_layout);
        avatar = v.findViewById(R.id.avatar);
        restaurantNameTV = v.findViewById(R.id.restaurant_name);
        postTimeTV = v.findViewById(R.id.post_time);
        captionTV = v.findViewById(R.id.caption);
        postImage = v.findViewById(R.id.post_images);
    }

    public void bindTo(final Context context, final DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event
        super.bindTo(context, activity);
        final String restFeedLink = activity.getString("wh");
        final DocumentReference restFeedRef = FirebaseFirestore.getInstance().collection("rest_feed")
                .document(restFeedLink);
        restFeedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restFeed = task.getResult();
                    if(restFeed.exists()){
                        String caption = restFeed.getString("c");
//                        TODO delete next line after database(rest_feed) is updated
                        Map<String, String> who = (Map) activity.get("w");
//                        this is the correct one
//                        Map<String, String> who = (Map) restFeed.get("w");
                        String restaurantName = who.get("n");
                        final String linkToRestaurant = who.get("l");

                        restaurantNameTV.setText(restaurantName);
                        captionTV.setText(caption);

                        restaurantNameTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, RestDetail.class);
                                intent.putExtra("restaurantLink", linkToRestaurant);
                                context.startActivity(intent);
                            }
                        });
                    }
                }
            }
        });

        final String likedBy = fAuth.getCurrentUser().getUid();
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LIKE", "CLICKED");
                mFunctions.getHttpsCallable("printMessage").call().addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        if(task.isSuccessful()){
                            String s = (String)task.getResult().getData();
                            Log.i("function-data", s);
                        }
                    }
                });
                if(like.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, R.drawable.outline_favorite_border_black_24dp).getConstantState())){
                    like.setImageResource(R.drawable.baseline_favorite_black_24dp);
                    restFeedRef.update("l", FieldValue.arrayUnion(likedBy))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.i("UPDATE", "SUCCESSFUL");
                                    }else{
                                        Exception e = task.getException();
                                        Log.i("UPDATE", e.getMessage());
                                    }
                                }
                            });
                    final DocumentReference documentReference = db.collection("liked_once").document(likedBy);
                    documentReference.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()){
                                            List<String> postsOnceLiked = (List<String>) documentSnapshot.get("a");
                                            if(postsOnceLiked.contains(restFeedLink)){
                                                Log.i("LIKED_ALREADY", "YES");
                                            }else{
                                                Log.i("LIKED_ALREADY", "NO");
                                                documentReference.update("a", FieldValue.arrayUnion(restFeedLink));
                                                ActualActivity likeActivity = new ActualActivity();
                                                likeActivity.setT(4);
                                                Map<String, String> who = new HashMap<>();
                                                who.put("l", likedBy);
                                                likeActivity.setW(who);
                                                likeActivity.setWh(restFeedLink);
                                                db.collection("activities").add(likeActivity)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if(task.isSuccessful()){
                                                                    DocumentReference docRef = task.getResult();
                                                                    Log.i("new_activity_at", docRef.getId());
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    }

                                }
                            });
                }else{
                    like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
                    restFeedRef.update("l", FieldValue.arrayRemove(likedBy))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.i("UPDATE", "SUCCESSFUL");
                                    }else{
                                        Exception e = task.getException();
                                        Log.i("UPDATE", e.getMessage());
                                    }
                                }
                            });
                }
            }
        });

        restFeedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("layout", "clicked");
                Intent intent = new Intent(context, FullRestFeed.class);
                intent.putExtra("restFeedLink", restFeedLink);
                context.startActivity(intent);
            }
        });
    }
}
