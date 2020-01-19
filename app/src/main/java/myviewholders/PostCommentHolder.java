package myviewholders;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import site.sht.bd.foodhood.CommentDetail;
import site.sht.bd.foodhood.PersonDetail;
import site.sht.bd.foodhood.R;
import site.sht.bd.foodhood.WriteComment;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AccountTypes;
import myapp.utils.CommentIntentExtra;
import myapp.utils.DateTimeExtractor;
import myapp.utils.EntryPoints;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.NotificationTypes;
import myapp.utils.PictureBinder;
import myapp.utils.ResourceIds;

public class PostCommentHolder extends HalfPostHolder
        implements CommentInterface{

    LinearLayout commentLayout;
    TextView postCommentHeader;
    CircleImageView commenterImage;
    TextView commenterName;
    TextView commentTimeTV;
    TextView theComment;
    TextView linkToReplies;
    TextView noOfLikesOnComment;
    TextView noOfRepliesToComment;
    ImageView likeComment;
    ImageView replyToComment;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Task<DocumentSnapshot> mTaskComment;
    //private Context mContext;
    private String mCommentText;
    private String mCommentLink;
    //private String mPostLink;
    private String mNameCommentBy;
    private String mLinkCommentBy;
    private DocumentSnapshot mCommentSnapshot;

    public PostCommentHolder(@NonNull View v) {
        super(v);
        commentLayout = v.findViewById(R.id.small_comment_layout);
        postCommentHeader = v.findViewById(R.id.post_comment_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterName = v.findViewById(R.id.commenter_name);
        commentTimeTV = v.findViewById(R.id.time);
        theComment = v.findViewById(R.id.the_comment);
        linkToReplies = v.findViewById(R.id.replies_link);
        noOfLikesOnComment = v.findViewById(R.id.number_of_likes);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);

    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        setpublicGlobalFields(context, activity);
        bindValuesIndependentOfCommentDownload();
        setOnClickListenersIndependentOfCommentDownload();
        setElementsDependentOnCommentDownload();
    }

    private void setpublicGlobalFields(Context context, DocumentSnapshot activity){
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
        setmTaskComment();

        final String postLink = activity.getString("wh");
        setmPostLink(postLink);
    }

    private void bindValuesIndependentOfCommentDownload(){
        bindHeader();
        bindCommentByAvatar();
        bindNameCommentBy();
        bindComment();
    }

    private void setOnClickListenersIndependentOfCommentDownload(){
        setCommentByAvatarOnClickListener();
        setNameCommentByOnClickListener();
        setCommentOnClickListener();
        setCommentLayoutOnClickListener();
    }

    private void setElementsDependentOnCommentDownload(){
        mTaskComment.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot commentSnapshot = task.getResult();
                    if(commentSnapshot.exists()){
                        mCommentSnapshot = commentSnapshot;
                        bindValuesDependentOnCommentDownload();
                        setOnClickListenersDependentOnCommentDownload();
                    }
                }
            }
        });
    }

    private void bindValuesDependentOnCommentDownload(){
        bindCommentTime();
        bindRepliesLink();
        bindLikeCommentIcon();
        bindNoOfLikeInComment();
        bindReplyToCommentIcon();
        bindNoOfRepliesToComment();
    }

    private void setOnClickListenersDependentOnCommentDownload(){
        setCommentTimeOnClickListener();
        setRepliesLinkOnClickListener();
        setLikeCommentIconOnClickListener();
        setNoOfLikeInCommentOnClickListener();
        setReplyToCommentIconOnClickListener();
        setNoOfRepliesToCommentOnClickListener();
    }

//    private void setmContext(Context mContext) {
//        this.mContext = mContext;
//    }

    private void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    private void setmCommentLink(String mCommentLink) {
        this.mCommentLink = mCommentLink;
    }

//    private void setmPostLink(String mPostLink) {
//        this.mPostLink = mPostLink;
//    }

    private void setmNameCommentBy(String mNameCommentBy) {
        this.mNameCommentBy = mNameCommentBy;
    }

    private void setmLinkCommentBy(String mLinkCommentBy) {
        this.mLinkCommentBy = mLinkCommentBy;
    }

    private void setmTaskComment(){
        mTaskComment = db.collection("comments").document(mCommentLink).get();
    }

    public void bindHeader(){
        postCommentHeader.setText(mNameCommentBy + " commented on this");
    }

    @Override
    public void bindCommentByAvatar() {
        db.collection("person_vital")
                .document(mLinkCommentBy)
                .get()
                .addOnSuccessListener((Activity)mContext, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                        PictureBinder.bindProfilePicture(commenterImage, personVitalSnapshot);
                    }
                });
    }

    @Override
    public void setCommentByAvatarOnClickListener() {
        if(mLinkCommentBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        commenterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", mLinkCommentBy);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNameCommentBy() {
        commenterName.setText(mNameCommentBy);
    }

    @Override
    public void setNameCommentByOnClickListener() {
        if(mLinkCommentBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        commenterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "commenter name from home post+comment");
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", mLinkCommentBy);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindCommentTime() {
        Timestamp ts = mCommentSnapshot.getTimestamp("ts");
        if(ts==null) return;
        String dateOrTimeString = DateTimeExtractor.getDateOrTimeString(ts);
        commentTimeTV.setText(dateOrTimeString);
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
        linkToReplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home post comment");
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

    @Override
    public void bindLikeCommentIcon() {
        String currentUserLink = mAuth.getCurrentUser().getUid();
        List<String> likers = (List<String>) mCommentSnapshot.get("l");
        if(likers.contains(currentUserLink)){
            likeComment.setImageResource(ResourceIds.LIKE_FULL);
        }else{
            likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
        }
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
                    increaseNumOfLikes();
                    sendNotificationLikeCommentCloud();
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment();
                    decreaseNumOfLikes();
                }
            }
        });
    }

    @Override
    public void bindNoOfLikeInComment() {
        List<String> likers = (List<String>) mCommentSnapshot.get("l");
        int numberofLikes = 0;
        if(likers != null){
            numberofLikes = likers.size();
        }
        noOfLikesOnComment.setText(Integer.toString(numberofLikes));
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
                Log.i("reply2comment", "from home post+comment");
                // needed if reply activity has redundant data for the comment
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mLinkCommentBy);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mNameCommentBy);
                commentMap.put("l", mCommentLink);

                Map<String, Object> replyingTo = new HashMap<>();
                replyingTo.put("n", mNameCommentBy);
                replyingTo.put("l", mLinkCommentBy);
                replyingTo.put("t", AccountTypes.PERSON);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_HOME_POST);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentMap(commentMap);
                commentIntentExtra.setReplyingTo(replyingTo);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNoOfRepliesToComment() {
        List<String> replies = (List<String>) mCommentSnapshot.get("r");
        int numberOfReplies = 0;
        if(replies != null){
            numberOfReplies = replies.size();
        }
        noOfRepliesToComment.setText(Integer.toString(numberOfReplies));
    }

    @Override
    public void setNoOfRepliesToCommentOnClickListener() {

    }

    @Override
    public void setCommentLayoutOnClickListener() {
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home post comment");
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
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, mPostLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, mCommentLink);
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_LIKE_COMMENT);

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

    private void decreaseNumOfLikes(){
        String str = (String) noOfLikesOnComment.getText();
        int numOfLikes = Integer.valueOf(str);
        noOfLikesOnComment.setText(Integer.toString(numOfLikes-1));
    }

    private void increaseNumOfLikes(){
        String str = noOfLikesOnComment.getText().toString();
        int numOfLikes = Integer.valueOf(str);
        noOfLikesOnComment.setText(Integer.toString(numOfLikes+1));
    }

    @Override
    public void refreshHolder() {
        Log.i("refreshing", "postcommentholder");
        super.refreshHolder();
        postCommentHeader.setText("");
        commenterImage.setImageResource(R.drawable.ltgray);
        commenterName.setText("");
        commentTimeTV.setText("");
        theComment.setText("");
        noOfLikesOnComment.setText("");
        noOfRepliesToComment.setText("");
    }
}
