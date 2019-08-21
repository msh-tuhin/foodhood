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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.EntryPoints;
import myapp.utils.ResourceIds;

public class CommentDetailReplyHolder  extends RecyclerView.ViewHolder {

    private CircleImageView commenterImage;
    private TextView commenterNameTextView, theCommentTextView, repLyingTo;
    private ImageView likeComment, replyToComment;
    private TextView numberOfLikesTextView, numberOfRepliesTextView;
    FirebaseFirestore db;
    View mView;

    public CommentDetailReplyHolder(@NonNull View v) {
        super(v);
        mView = v;
        commenterImage = v.findViewById(R.id.replier_image);
        commenterNameTextView = v.findViewById(R.id.replier_name);
        theCommentTextView = v.findViewById(R.id.the_reply);
        repLyingTo = v.findViewById(R.id.replying_to);
        likeComment = v.findViewById(R.id.like_reply);
        replyToComment = v.findViewById(R.id.reply_to_reply);
        numberOfLikesTextView = v.findViewById(R.id.number_of_likes_in_reply);
        numberOfRepliesTextView = v.findViewById(R.id.number_of_replies_to_reply);
        db = FirebaseFirestore.getInstance();
    }

    public void bindTo(final Context context, final String postLink, final String commentLink, final String replyLink) {
        Log.i("bindTo", this.getClass().toString());
        final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("comments").document(replyLink).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot commentSnapshot = task.getResult();
                            if(commentSnapshot.exists()){
                                String commenterLink = commentSnapshot.getString("w");
                                // TODO make the "w" field a map with keys "n", "l"
                                // so that name does not have to be downloaded
                                db.collection("person_vital").document(commenterLink).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot personVital) {
                                                if(personVital.exists()){
                                                    commenterNameTextView.setText(personVital.getString("n"));
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
                                List<String> likers = (List<String>)commentSnapshot.get("l");
                                int numberofLikes = likers.size();
                                numberOfLikesTextView.setText(Integer.toString(numberofLikes));
                                if(likers.contains(currentUserLink)){
                                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                                }else{
                                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                                }
                                numberOfLikesTextView.setText(Integer.toString(numberofLikes));

                            }
                        }
                    }
                });

        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("reply_to_reply", "from comment detail");
                int replyPosition = ((CommentDetail)context).rv.getChildAdapterPosition(mView);
                Log.i("reply_pos", Integer.toString(replyPosition));

                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_REPLY_CD);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentLink);
                intent.putExtra("replyLink", replyLink);
                intent.putExtra("newReplyPosition", replyPosition);
                ((CommentDetail)context).startActivityForResult(intent, ((CommentDetail)context).REQUEST_REPLY_TO_REPLY);
            }
        });

        // user likes a reply
        likeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "clicked from full post(reply)");
                DocumentReference commentRef = FirebaseFirestore.getInstance().collection("comments").document(replyLink);
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
                    sendNotificationLikeReplyCloud(replyLink, commentLink, postLink);
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

    private void sendNotificationLikeReplyCloud(String replyLink, String commentLink, String postLink){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", postLink);
        notification.put("commentLink", commentLink);
        notification.put("replyLink", replyLink);
        notification.put("w", who);

        FirebaseFunctions.getInstance().getHttpsCallable("sendLikeReplyNotification").call(notification)
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
