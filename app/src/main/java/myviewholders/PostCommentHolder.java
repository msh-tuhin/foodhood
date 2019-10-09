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
import myapp.utils.CommentIntentExtra;
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

    private Context mContext;
    private String mCommentText;
    private String mCommentLink;
    private String mPostLink;
    private String mNameCommentBy;
    private String mLinkCommentBy;

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
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);

        setPrivateGlobalFields(context, activity);
        bindValues();
        setOnClickListeners();
    }

    private void setPrivateGlobalFields(Context context, DocumentSnapshot activity){
        setmContext(context);

        // set mNameCommentBy, mLinkCommentBy
        final Map<String, String> commenter = (Map) activity.get("w");
        String nameOfCommenter = commenter.get("n");
        String linkOfCommenter = commenter.get("l");
        setmNameCommentBy(nameOfCommenter);
        setmLinkCommentBy(linkOfCommenter);

        // set mCommentText, mCommentLink
        final Map<String, String> commentData = (Map) activity.get("com");
        final String commentText = commentData.get("text");
        final String commentLink = commentData.get("l");
        setmCommentText(commentText);
        setmCommentLink(commentLink);

        final String postLink = activity.getString("wh");
        setmPostLink(postLink);
    }

    private void bindValues(){
        bindHeader();
        bindCommentByAvatar();
        bindNameCommentBy();
        bindCommentTime();
        bindComment();
        bindRepliesLink();
        bindLikeCommentIcon();
        bindNoOfLikeInComment();
        bindReplyToCommentIcon();
        bindNoOfRepliesToComment();
    }

    private void setOnClickListeners(){
        setCommentByAvatarOnClickListener();
        setNameCommentByOnClickListener();
        setCommentTimeOnClickListener();
        setCommentOnClickListener();
        setRepliesLinkOnClickListener();
        setLikeCommentIconOnClickListener();
        setNoOfLikeInCommentOnClickListener();
        setReplyToCommentIconOnClickListener();
        setNoOfRepliesToCommentOnClickListener();
        setCommentLayoutOnClickListener();
    }

    private void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    private void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    private void setmCommentLink(String mCommentLink) {
        this.mCommentLink = mCommentLink;
    }

    private void setmPostLink(String mPostLink) {
        this.mPostLink = mPostLink;
    }

    private void setmNameCommentBy(String mNameCommentBy) {
        this.mNameCommentBy = mNameCommentBy;
    }

    private void setmLinkCommentBy(String mLinkCommentBy) {
        this.mLinkCommentBy = mLinkCommentBy;
    }

    public void bindHeader(){
        postCommentHeader.setText(mNameCommentBy + " commented on this");
    }

    @Override
    public void bindCommentByAvatar() {

    }

    @Override
    public void setCommentByAvatarOnClickListener() {

    }

    @Override
    public void bindNameCommentBy() {
        commenterName.setText(mNameCommentBy);
    }

    @Override
    public void setNameCommentByOnClickListener() {

    }

    @Override
    public void bindCommentTime() {

    }

    @Override
    public void setCommentTimeOnClickListener() {

    }

    @Override
    public void bindComment() {
        theComment.setText(mCommentText);
    }

    @Override
    public void setCommentOnClickListener() {

    }

    @Override
    public void bindRepliesLink() {

    }

    @Override
    public void setRepliesLinkOnClickListener() {

    }

    @Override
    public void bindLikeCommentIcon() {

    }

    @Override
    public void setLikeCommentIconOnClickListener() {
        likeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "cliked from home comment");
                if(likeComment.getDrawable().getConstantState().equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY).getConstantState())){
                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToComment();
                    sendNotificationLikeCommentCloud();
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment();
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
    public void bindReplyToCommentIcon() {

    }

    @Override
    public void setReplyToCommentIconOnClickListener() {
        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply2comment", "from home comment");
                // needed if reply activity has redundant data for the comment
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mLinkCommentBy);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mNameCommentBy);
                commentMap.put("l", mCommentLink);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_HOME_POST);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentMap(commentMap);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
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
    public void setCommentLayoutOnClickListener() {
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "go to comment detail");
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_BODY_FROM_HOME_POST);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setPostLink(mPostLink);

                Intent intent = new Intent(mContext, CommentDetail.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }

    private void addLikeToComment(){
        DocumentReference commentRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mCommentLink);
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

    private void removeLikeFromComment(){
        DocumentReference commentRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mCommentLink);
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

    private void sendNotificationLikeCommentCloud(){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", mPostLink);
        notification.put("commentLink", mCommentLink);
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
