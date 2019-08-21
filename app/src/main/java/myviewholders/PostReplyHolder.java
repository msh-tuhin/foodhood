package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
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
import java.util.Map;

import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.EntryPoints;
import myapp.utils.ResourceIds;

public class PostReplyHolder extends HalfPostHolder{

    LinearLayout commentReplyLayout;
    TextView postReplyHeader;
    CircleImageView commenterImage, replierImage;
    TextView commenterName, theComment, replierName, theReply;
    ImageView likeComment, likeReply, replyToComment, replyTOReply;
    TextView numOfLikesInComment, numOfLikesInReply, numOfRepliesToComment, numOfRepliesToReply;
    public PostReplyHolder(@NonNull View v) {
        super(v);
        commentReplyLayout = v.findViewById(R.id.comment_reply_layout);
        postReplyHeader = v.findViewById(R.id.post_reply_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        replierImage = v.findViewById(R.id.replier_image);
        commenterName = v.findViewById(R.id.commenter_name);
        theComment = v.findViewById(R.id.the_comment);
        replierName = v.findViewById(R.id.replier_name);
        theReply = v.findViewById(R.id.the_reply);
        likeComment = v.findViewById(R.id.like_comment);
        likeReply = v.findViewById(R.id.like_reply);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        replyTOReply = v.findViewById(R.id.reply_to_reply);
        numOfLikesInComment = v.findViewById(R.id.number_of_likes);
        numOfLikesInReply = v.findViewById(R.id.number_of_likes_in_reply);
        numOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        numOfRepliesToReply = v.findViewById(R.id.number_of_replies_to_reply);
    }

    @Override
    public void bindTo(final Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        Map<String, String> replyBy = (Map) activity.get("w");
        String nameReplyBy = replyBy.get("n");
        final Map<String, String> commentData = (Map) activity.get("com");
        final String commentText = commentData.get("text");
        final String nameCommentBy = commentData.get("byn");
        final Map<String, String> replyData = (Map) activity.get("rep");
        String replyText = replyData.get("text");
        final String postLink = activity.getString("wh");
        postReplyHeader.setText(nameReplyBy + " replied");
        commenterName.setText(nameCommentBy);
        theComment.setText(commentText);
        replierName.setText(nameReplyBy);
        theReply.setText(replyText);

        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply_to_comment", "from home post reply");

                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", commentData.get("byl"));
                commentMap.put("text", commentText);
                commentMap.put("byn", nameCommentBy);
                commentMap.put("l", commentData.get("l"));

                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_COMMENT);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentData.get("l"));
                intent.putExtra("commentMap", (HashMap)commentMap);
                context.startActivity(intent);
            }
        });

        replyTOReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply_to_reply", "from home post reply");
                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_REPLY_HOME);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentData.get("l"));
                intent.putExtra("replyLink", replyData.get("l"));
                context.startActivity(intent);
            }
        });

        commentReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home post reply");
                Intent intent = new Intent(context, CommentDetail.class);
                // EntryPoints.REPLY_TO_COMMENT works but not accurate
                intent.putExtra("entry_point", EntryPoints.CD_FROM_HOME_COMMENT_REPLY);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentData.get("l"));
                intent.putExtra("replyLink", replyData.get("l"));
                context.startActivity(intent);
            }
        });

        // TODO
        // maybe add some abstractions over setting these listeners
        // this is copied and pasted in multiple places
        likeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "clicked from home comment");
                String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String commentLink = commentData.get("l");
                DocumentReference commentRef = FirebaseFirestore.getInstance().collection("comments").document(commentLink);
                if(((ImageView)v).getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, ResourceIds.LIKE_EMPTY).getConstantState())){
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_FULL);
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
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_EMPTY);
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

        likeReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "clicked from home reply");
                String commentLink = commentData.get("l");
                String replyLink = replyData.get("l");
                String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference commentRef = FirebaseFirestore.getInstance().collection("comments").document(replyLink);
                if(((ImageView)v).getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, ResourceIds.LIKE_EMPTY).getConstantState())){
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_FULL);
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
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_EMPTY);
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
