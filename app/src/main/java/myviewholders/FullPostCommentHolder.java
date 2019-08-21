package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import models.CommentModel;
import myapp.utils.EntryPoints;
import myapp.utils.ResourceIds;

public class FullPostCommentHolder extends RecyclerView.ViewHolder {

    LinearLayout commentLayout;
    private CircleImageView commenterImage;
    private TextView commenterNameTextView, theCommentTextView, repliesLinkTextView;
    private ImageView likeComment, replyToComment;
    private TextView numberOfLikesTextView, numberOfRepliesTextView;
    FirebaseFirestore db;

    public FullPostCommentHolder(@NonNull View v) {
        super(v);
        commentLayout = v.findViewById(R.id.small_comment_layout);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTextView = v.findViewById(R.id.commenter_name);
        theCommentTextView = v.findViewById(R.id.the_comment);
        repliesLinkTextView = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        numberOfLikesTextView = v.findViewById(R.id.number_of_likes);
        numberOfRepliesTextView = v.findViewById(R.id.number_of_replies);
        db = FirebaseFirestore.getInstance();
    }

    public void bindTo(final Context context, final String postLink, final String commentLink) {
        Log.i("bindTo", this.getClass().toString());

        final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // needed if reply activity has redundant data for the comment
        final CommentData commentData = new CommentData();

        db.collection("comments").document(commentLink).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot commentSnapshot = task.getResult();
                            if(commentSnapshot.exists()){
                                String commenterLink = commentSnapshot.getString("w");
                                commentData.commenterLink = commenterLink;
                                commentData.commentText = commentSnapshot.getString("te");

                                db.collection("person_vital").document(commenterLink).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot personVital) {
                                                if(personVital.exists()){
                                                    commenterNameTextView.setText(personVital.getString("n"));
                                                    commentData.commenterName = personVital.getString("n");
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("error", "failed to fetch person name");
                                    }
                                });
                                theCommentTextView.setText(commentSnapshot.getString("te"));
                                int numberOfReplies = ((List<String>)commentSnapshot.get("r")).size();
                                numberOfRepliesTextView.setText(Integer.toString(numberOfReplies));
                                List<String> likers = (List<String>) commentSnapshot.get("l");
                                if(likers.contains(currentUserLink)){
                                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                                }else{
                                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                                }
                                int numberofLikes = likers.size();
                                numberOfLikesTextView.setText(Integer.toString(numberofLikes));
                            }
                        }
                    }
                });

        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply", "from full post");

                // needed if reply activity has redundant data for the comment
                // commentData might not be populated yet
                // TODO do something about it
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", commentData.commenterLink);
                commentMap.put("text", commentData.commentText);
                commentMap.put("byn", commentData.commenterName);
                commentMap.put("l", commentLink);

                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_COMMENT);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentLink);
                intent.putExtra("commentMap", (HashMap<String, Object>)commentMap);
                context.startActivity(intent);
            }
        });

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "go to comment detail");
                Intent intent = new Intent(context, CommentDetail.class);
                intent.putExtra("entry_point", EntryPoints.FULL_POST_PAGE);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentLink);
                context.startActivity(intent);
            }
        });

        likeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "clicked from full post(comment)");
                DocumentReference commentRef = FirebaseFirestore.getInstance().collection("comments").document(commentLink);
                if(likeComment.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, ResourceIds.LIKE_EMPTY).getConstantState())){
                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                    commentRef.update("l", FieldValue.arrayUnion(currentUserLink))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.i("UPDATE", "LIKE SUCCESSFUL ADDED");
                                    }else{
                                        Exception e = task.getException();
                                        Log.i("UPDATE", e.getMessage());
                                    }
                                }
                            });
                    sendNotificationLikeCommentCloud(commentLink, postLink);
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    commentRef.update("l", FieldValue.arrayRemove(currentUserLink))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.i("UPDATE", "LIKE SUCCESSFULLY REMOVED");
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

    private class CommentData {
        String commenterLink = "";
        String commentText = "";
        String commenterName = "";
        String commentLink = "";
    }

    private void sendNotificationLikeCommentCloud(String commentLink, String postLink){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", postLink);
        notification.put("commentLink", commentLink);
        notification.put("w", who);

        FirebaseFunctions.getInstance().getHttpsCallable("sendLikeCommentNotification").call(notification)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Log.i("func_call", "returned successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("func_call", e.getMessage());
                    }
                });
    }
}
