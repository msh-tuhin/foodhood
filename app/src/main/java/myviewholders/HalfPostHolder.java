package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.ActualActivity;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
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

import myapp.utils.EntryPoints;
import myapp.utils.PostBuilder;

public class HalfPostHolder extends BaseHomeFeedHolder {

    TextView goToFull;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    public HalfPostHolder(@NonNull View v) {
        super(v);
        goToFull = v.findViewById(R.id.go_to_full);
    }

    @Override
    public void bindTo(final Context context, final DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event
        super.bindTo(context, activity);
        final String postLink = activity.getString("wh");
        final DocumentReference postRef = db.collection("posts").document(postLink);
        doYourBit(context, postRef.get(), postLink);

        goToFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullPost.class);
                intent.putExtra("postLink", postLink);
                context.startActivity(intent);
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("postLink", postLink);
                intent.putExtra("entry_point", EntryPoints.HOME_PAGE);
                context.startActivity(intent);
            }
        });
    };

    protected void doYourBit(final Context context, Task<DocumentSnapshot> taskPost, final String postLink){
        final String likedBy = fAuth.getCurrentUser().getUid();
        final DocumentReference postRef = db.collection("posts").document(postLink);
        taskPost.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot post = task.getResult();
                    if(post.exists()){
//                      Map<String, String> builtPost = Helper.buildPost(post);
//                      halfPostHolder.getPostHeader().setText(builtPost.get("postHeader"));
//                      halfPostHolder.getPostCaption().setText(builtPost.get("caption"));
                        PostBuilder postBuilder = new PostBuilder(context, post);
                        SpannableStringBuilder mSpannedString = postBuilder.getPostHeader();
                        postHeader.setText(mSpannedString, TextView.BufferType.SPANNABLE);
                        postHeader.setMovementMethod(LinkMovementMethod.getInstance());
                        postCaption.setText(postBuilder.getCaption());
                        List<String> likers = (List<String>) post.get("l");

                        // find if current user has already liked this post
                        if(likers.contains(likedBy)){
                            like.setImageResource(R.drawable.baseline_favorite_black_24dp);
                        }else{
                            like.setImageResource(R.drawable.outline_favorite_border_black_24dp);
                        }

                    }
                }
            }
        });

        // TODO
        // copied, pasted and edited in multiple places
        // maybe add some abstraction
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
                    postRef.update("l", FieldValue.arrayUnion(likedBy))
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
                                            if(postsOnceLiked.contains(postLink)){
                                                Log.i("LIKED_ALREADY", "YES");
                                            }else{
                                                Log.i("LIKED_ALREADY", "NO");
                                                documentReference.update("a", FieldValue.arrayUnion(postLink));
                                                ActualActivity likeActivity = new ActualActivity();
                                                likeActivity.setT(3);
                                                Map<String, String> who = new HashMap<>();
                                                who.put("l", likedBy);
                                                likeActivity.setW(who);
                                                likeActivity.setWh(postLink);
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
                    postRef.update("l", FieldValue.arrayRemove(likedBy))
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
    }
}
