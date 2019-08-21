package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import models.CommentModel;
import myapp.utils.EntryPoints;
import myapp.utils.ResourceIds;

public class CommentDetailHolder extends RecyclerView.ViewHolder {

    private TextView postLinkTextView;
    private CircleImageView commenterImage;
    private TextView commenterNameTextView, theCommentTextView;
    private ImageView likeComment, replyToComment;
    private TextView numberOfLikesTextView, numberOfRepliesTextView;
    FirebaseFirestore db;

    public CommentDetailHolder(@NonNull View v) {
        super(v);
        postLinkTextView = v.findViewById(R.id.post_link);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTextView = v.findViewById(R.id.commenter_name);
        theCommentTextView = v.findViewById(R.id.the_comment);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        numberOfLikesTextView = v.findViewById(R.id.number_of_likes);
        numberOfRepliesTextView = v.findViewById(R.id.number_of_replies);
        db = FirebaseFirestore.getInstance();
    }

    public void bindTo(final Context context, final Task<DocumentSnapshot> taskComment, final String postLink, final String commentLink) {
        Log.i("bindTo", this.getClass().toString());

        final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // needed if reply activity has redundant data for the comment
        final CommentData commentData = new CommentData();

        // this uses the comment downloaded before
          taskComment.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot commentSnapshot = task.getResult();
                            if(commentSnapshot.exists()){
                                commentData.commentText = commentSnapshot.getString("te");
                                commentData.commenterLink = commentSnapshot.getString("w");
                                String commenterLink = commentSnapshot.getString("w");
                                // TODO make the "w" field a map with keys "n", "l"
                                // so that name does not have to be downloaded
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
                Log.i("reply", "from comment detail page");

                // needed if reply activity has redundant data for the comment
                // commentData might not be populated yet
                // TODO do something about it
                // maybe let all this be handled in a cloud function
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", commentData.commenterLink);
                commentMap.put("text", commentData.commentText);
                commentMap.put("byn", commentData.commenterName);
                commentMap.put("l", commentLink);

                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentLink);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_COMMENT_CD);
                intent.putExtra("commentMap", (HashMap)commentMap);
                ((CommentDetail)context).startActivityForResult(intent, ((CommentDetail)context).REQUEST_REPLY);
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
//                    sendNotificationLikeComment(commentLink, postLink, commentData.commenterLink);
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

    private void sendNotificationLikeComment(String commentLink, String postLink, final String commenterLink){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(commenterLink.equals(currentUserLink)){
            // like on own comment, just ignore
            return;
        }
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("t", 6);
        notification.put("postLink", postLink);
        notification.put("commentLink", commentLink);
        notification.put("ts", new Timestamp(new Date()));

        FirebaseFirestore.getInstance().collection("person_vital").document(currentUserLink).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot personVital = task.getResult();
                            if(personVital.exists()){
                                String personName = personVital.getString("n");
                                who.put("n", personName);
                                notification.put("w", who);
                                FirebaseFirestore.getInstance().collection("notifications").document(commenterLink)
                                        .collection("n").add(notification);
                            }
                        }
                    }
                });
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
