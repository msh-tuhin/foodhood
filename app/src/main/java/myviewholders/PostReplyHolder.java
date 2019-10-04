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

public class PostReplyHolder extends HalfPostHolder
        implements CommentInterface, ReplyInterface{

    LinearLayout commentLayout;
    CircleImageView commenterImage;
    TextView commenterName;
    TextView theComment;
    TextView noOfLikesOnComment;
    TextView noOfRepliesToComment;
    ImageView likeComment;
    ImageView replyToComment;

    LinearLayout commentReplyLayout;
    TextView postReplyHeader;
    CircleImageView replierImage;
    TextView replierName;
    TextView replyTime;
    TextView theReply;
    ImageView likeReply;
    ImageView replyTOReply;
    TextView numOfLikesInReply;
    TextView numOfRepliesToReply;

    private Context mContext;
    private String mCommentText;
    private String mCommentLink;
    private String mPostLink;
    private String mNameCommentBy;
    private String mLinkCommentBy;
    private String mReplyText;
    private String mReplyLink;
    private String mNameReplyBy;
    private String mLinkReplyBy;

    public PostReplyHolder(@NonNull View v) {
        super(v);

        commentLayout = v.findViewById(R.id.small_comment_layout);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterName = v.findViewById(R.id.commenter_name);
        theComment = v.findViewById(R.id.the_comment);
        noOfLikesOnComment = v.findViewById(R.id.number_of_likes);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);

        commentReplyLayout = v.findViewById(R.id.comment_reply_layout);
        postReplyHeader = v.findViewById(R.id.post_reply_header);
        replierImage = v.findViewById(R.id.replier_image);
        replierName = v.findViewById(R.id.replier_name);
        theReply = v.findViewById(R.id.the_reply);
        likeReply = v.findViewById(R.id.like_reply);
        replyTOReply = v.findViewById(R.id.reply_to_reply);
        numOfLikesInReply = v.findViewById(R.id.number_of_likes_in_reply);
        numOfRepliesToReply = v.findViewById(R.id.number_of_replies_to_reply);
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

        // set mNameReplyBy, mLinkReplyBy;
        Map<String, String> replyBy = (Map) activity.get("w");
        setmNameReplyBy(replyBy.get("n"));
        setmLinkReplyBy(replyBy.get("l"));

        // set mCommentText, mCommentLink, mNameCommentBy, mLinkCommentBy
        final Map<String, String> commentData = (Map) activity.get("com");
        setmCommentText(commentData.get("text"));
        setmCommentLink(commentData.get("l"));
        setmLinkCommentBy(commentData.get("byl"));
        setmNameCommentBy(commentData.get("byn"));

        // set mReplyText, mReplyLink
        final Map<String, String> replyData = (Map) activity.get("rep");
        setmReplyText(replyData.get("text"));
        setmReplyLink(replyData.get("l"));

        // set mPostLink
        setmPostLink(activity.getString("wh"));
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

        bindReplyByAvatar();
        bindNameReplyBy();
        bindReplyTime();
        bindReplyingToLink();
        bindReply();
        bindLikeReplyIcon();
        bindNoOfLikeInReply();
        bindReplyToReplyIcon();
        bindNoOfRepliesToReply();
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

        setReplyByAvatarOnClickListener();
        setNameReplyByOnClickListener();
        setReplyTimeOnClickListener();
        setReplyingToLinkOnClickListener();
        setReplyOnClickListener();
        setLikeReplyIconOnClickListener();
        setNoOfLikeInReplyOnClickListener();
        setReplyToReplyIconOnClickListener();
        setNoOfRepliesToReplyOnClickListener();

        setReplyLayoutOnClickListener();
        setCommentReplyLayoutOnClickListener();
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

    private void setmReplyText(String mReplyText) {
        this.mReplyText = mReplyText;
    }

    private void setmReplyLink(String mReplyLink) {
        this.mReplyLink = mReplyLink;
    }

    private void setmNameReplyBy(String mNameReplyBy) {
        this.mNameReplyBy = mNameReplyBy;
    }

    private void setmLinkReplyBy(String mLinkReplyBy) {
        this.mLinkReplyBy = mLinkReplyBy;
    }

    public void bindHeader(){
        postReplyHeader.setText(mNameReplyBy + " replied");
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
                Log.i("like", "cliked from home comment+reply");
                if(likeComment.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY)
                                .getConstantState())){
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

    private void addLikeToComment(){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser()
                .getUid();
        DocumentReference commentRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mCommentLink);
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
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser()
                .getUid();
        DocumentReference commentRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mCommentLink);
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
                Log.i("reply2comment", "from home reply");
                // needed if reply activity has redundant data for the comment
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mLinkCommentBy);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mNameCommentBy);
                commentMap.put("l", mCommentLink);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_COMMENT);
                intent.putExtra("postLink", mPostLink);
                intent.putExtra("commentLink", mCommentLink);
                intent.putExtra("commentMap", (HashMap)commentMap);
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

    }

    @Override
    public void bindReplyByAvatar() {

    }

    @Override
    public void setReplyByAvatarOnClickListener() {

    }

    @Override
    public void bindNameReplyBy() {
        replierName.setText(mNameReplyBy);
    }

    @Override
    public void setNameReplyByOnClickListener() {

    }

    @Override
    public void bindReplyTime() {

    }

    @Override
    public void setReplyTimeOnClickListener() {

    }

    @Override
    public void bindReplyingToLink() {

    }

    @Override
    public void setReplyingToLinkOnClickListener() {

    }

    @Override
    public void bindReply() {
        theReply.setText(mReplyText);
    }

    @Override
    public void setReplyOnClickListener() {

    }

    @Override
    public void bindLikeReplyIcon() {

    }

    @Override
    public void setLikeReplyIconOnClickListener() {
        likeReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("like", "clicked from home reply");
                if(((ImageView)v).getDrawable().getConstantState()
                        .equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY)
                                .getConstantState())){
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToReply();
                    sendNotificationLikeReplyCloud();
                }else{
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromReply();
                }
            }
        });
    }

    private void addLikeToReply(){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference replyRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mReplyLink);
        replyRef.update("l", FieldValue.arrayUnion(currentUserLink))
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

    private void removeLikeFromReply(){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference replyRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mReplyLink);
        replyRef.update("l", FieldValue.arrayRemove(currentUserLink))
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

    private void sendNotificationLikeReplyCloud(){
        String currentUserLink = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", mPostLink);
        notification.put("commentLink", mCommentLink);
        notification.put("replyLink", mReplyLink);
        notification.put("w", who);

        FirebaseFunctions.getInstance().getHttpsCallable("sendLikeReplyNotification")
                .call(notification)
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

    @Override
    public void bindNoOfLikeInReply() {

    }

    @Override
    public void setNoOfLikeInReplyOnClickListener() {

    }

    @Override
    public void bindReplyToReplyIcon() {

    }

    @Override
    public void setReplyToReplyIconOnClickListener() {
        replyTOReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply2reply", "from home reply");
                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("entry_point", EntryPoints.REPLY_TO_REPLY_HOME);
                intent.putExtra("postLink", mPostLink);
                intent.putExtra("commentLink", mCommentLink);
                intent.putExtra("replyLink", mReplyLink);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNoOfRepliesToReply() {

    }

    @Override
    public void setNoOfRepliesToReplyOnClickListener() {

    }

    @Override
    public void setReplyLayoutOnClickListener() {

    }

    public void setCommentReplyLayoutOnClickListener(){
        commentReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home post reply");
                Intent intent = new Intent(mContext, CommentDetail.class);
                // EntryPoints.REPLY_TO_COMMENT works but not accurate
                intent.putExtra("entry_point", EntryPoints.CD_FROM_HOME_COMMENT_REPLY);
                intent.putExtra("postLink", mPostLink);
                intent.putExtra("commentLink", mCommentLink);
                intent.putExtra("replyLink", mReplyLink);
                mContext.startActivity(intent);
            }
        });
    }
}
