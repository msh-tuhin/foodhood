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

public class PostCommentHolder extends HalfPostHolder
        implements CommentInterface{

    LinearLayout commentLayout;
    TextView postCommentHeader;
    CircleImageView commenterImage;
    TextView commenterName;
    TextView theComment;
    TextView noOfLikesOnComment;
    TextView noOfRepliesToComment;
    ImageView likeComment;
    ImageView replyToComment;

    public PostCommentHolder(@NonNull View v) {
        super(v);
        commentLayout = v.findViewById(R.id.small_comment_layout);
        postCommentHeader = v.findViewById(R.id.post_comment_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterName = v.findViewById(R.id.commenter_name);
        theComment = v.findViewById(R.id.the_comment);
        noOfLikesOnComment = v.findViewById(R.id.number_of_likes);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);

    }

    @Override
    public void bindTo(final Context context, final DocumentSnapshot activity) {
        super.bindTo(context, activity);

        final Map<String, String> commenter = (Map) activity.get("w");
        String nameOfCommenter = commenter.get("n");
        final Map<String, String> commentData = (Map) activity.get("com");
        final String commentText = commentData.get("text");
        final String commentLink = commentData.get("l");
        final String postLink = activity.getString("wh");

        postCommentHeader.setText(nameOfCommenter + " commented on this" );
        bindNameCommentBy(nameOfCommenter);
        bindComment(commentText);
        setCommentLayoutOnClickListener(context, postLink, commentLink);
        setLikeCommentIconOnClickListener(context, commentLink, postLink);
        setReplyToCommentIconOnClickListener(context, commenter, commentText,
                commentLink, postLink);
    }

    @Override
    public void bindCommentByAvatar() {

    }

    @Override
    public void setCommentByAvatarOnClickListener() {

    }

    @Override
    public void bindNameCommentBy(String name) {
        commenterName.setText(name);
    }

    @Override
    public void setNameCommentByOnClickListener() {

    }

    @Override
    public void bindCommentTime() {

    }

    @Override
    public void bindComment(String comment) {
        theComment.setText(comment);
    }

    @Override
    public void setRepliesLinkOnClickListener() {

    }

    @Override
    public void bindLikeCommentIcon() {

    }

    @Override
    public void setLikeCommentIconOnClickListener(final Context context,
                                                  final String commentLink,
                                                  final String postLink) {
        likeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "cliked from home comment");
                String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference commentRef = FirebaseFirestore.getInstance().collection("comments").document(commentLink);
                if(likeComment.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, ResourceIds.LIKE_EMPTY).getConstantState())){
                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToComment(commentRef);
                    sendNotificationLikeCommentCloud(commentLink, postLink);
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment(commentRef);
                }
            }
        });
    }

    @Override
    public void bindNoOfLikeInComment() {

    }

    @Override
    public void setNoOfLikeInCommentOnClickListener() {

    }

    @Override
    public void setReplyToCommentIconOnClickListener(final Context context,
                                                     final Map<String, String> commenter,
                                                     final String commentText,
                                                     final String commentLink,
                                                     final String postLink) {
        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // needed if reply activity has redundant data for the comment
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", commenter.get("l"));
                commentMap.put("text", commentText);
                commentMap.put("byn", commenter.get("n"));
                commentMap.put("l", commentLink);

                Intent intent = new Intent(context, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_COMMENT);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentLink);
                intent.putExtra("commentMap", (HashMap)commentMap);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNoOfRepliesToComment() {

    }

    @Override
    public void setNoOfRepliesToCommentOnClickListener() {

    }

    @Override
    public void setCommentLayoutOnClickListener(final Context context,
                                                final String postLink,
                                                final String commentLink) {
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "go to comment detail");
                Intent intent = new Intent(context, CommentDetail.class);
                intent.putExtra("entry_point", EntryPoints.HOME_PAGE);
                intent.putExtra("postLink", postLink);
                intent.putExtra("commentLink", commentLink);
                context.startActivity(intent);
            }
        });
    }

    void addLikeToComment(DocumentReference commentRef){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser()
                .getUid();
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
    }

    void removeLikeFromComment(DocumentReference commentRef){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser()
                .getUid();
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
