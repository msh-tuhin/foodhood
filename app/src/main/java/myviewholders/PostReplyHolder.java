package myviewholders;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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

public class PostReplyHolder extends HalfPostHolder
        implements CommentInterface, ReplyInterface{

    LinearLayout commentLayout;
    CircleImageView commenterImage;
    TextView commenterName;
    TextView commentTimeTV;
    TextView theComment;
    TextView linkToReplies;
    TextView noOfLikesOnComment;
    TextView noOfRepliesToComment;
    ImageView likeComment;
    ImageView replyToComment;

    LinearLayout commentReplyLayout;
    TextView postReplyHeader;
    CircleImageView replierImage;
    TextView replierName;
    TextView replyTime;
    TextView replyingToTV;
    TextView theReply;
    ImageView likeReply;
    ImageView replyTOReply;
    TextView numOfLikesInReply;
    TextView numOfRepliesToReply;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // public Context mContext;
    private String mCommentText;
    private String mCommentLink;
    // public String mPostLink;
    private String mNameCommentBy;
    private String mLinkCommentBy;
    private String mReplyText;
    private String mReplyLink;
    private String mNameReplyBy;
    private String mLinkReplyBy;
    private Task<DocumentSnapshot> mTaskReply;
    private Task<DocumentSnapshot> mTaskComment;
    private DocumentSnapshot mReplySnapshot;
    private DocumentSnapshot mCommentSnapshot;

    public PostReplyHolder(@NonNull View v) {
        super(v);

        commentLayout = v.findViewById(R.id.small_comment_layout);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterName = v.findViewById(R.id.commenter_name);
        commentTimeTV = v.findViewById(R.id.time);
        theComment = v.findViewById(R.id.the_comment);
        linkToReplies = v.findViewById(R.id.replies_link);
        noOfLikesOnComment = v.findViewById(R.id.number_of_likes);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);

        commentReplyLayout = v.findViewById(R.id.comment_reply_layout);
        postReplyHeader = v.findViewById(R.id.post_reply_header);
        replierImage = v.findViewById(R.id.replier_image);
        replierName = v.findViewById(R.id.replier_name);
        replyingToTV = v.findViewById(R.id.replying_to);
        replyTime = v.findViewById(R.id.time_reply);
        theReply = v.findViewById(R.id.the_reply);
        likeReply = v.findViewById(R.id.like_reply);
        replyTOReply = v.findViewById(R.id.reply_to_reply);
        numOfLikesInReply = v.findViewById(R.id.number_of_likes_in_reply);
        numOfRepliesToReply = v.findViewById(R.id.number_of_replies_to_reply);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);

        setpublicGlobalFields(context, activity);
        bindValuesIndependentOfOtherDownloads();
        setOnClickListenersIndependentOfOtherDownloads();
        setElementsDependentOnCommentDownload();
        setElementsDependentOnReplyDownload();
    }

    private void setpublicGlobalFields(Context context, DocumentSnapshot activity){
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

        setmTaskComment();
        setmTaskReply();
    }

    private void bindValuesIndependentOfOtherDownloads(){
        bindHeader();
        bindCommentByAvatar();
        bindNameCommentBy();
        bindComment();

        bindReplyByAvatar();
        bindNameReplyBy();
        bindReply();
    }

    private void setOnClickListenersIndependentOfOtherDownloads(){
        setCommentByAvatarOnClickListener();
        setNameCommentByOnClickListener();
        setCommentOnClickListener();
        setCommentLayoutOnClickListener();

        setReplyByAvatarOnClickListener();
        setNameReplyByOnClickListener();
        setReplyOnClickListener();

        setReplyLayoutOnClickListener();
        setCommentReplyLayoutOnClickListener();
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

    private void setElementsDependentOnReplyDownload(){
        mTaskReply.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot replySnapshot = task.getResult();
                    if(replySnapshot.exists()){
                        mReplySnapshot = replySnapshot;
                        bindValuesDependentOnReplyDownload();
                        setOnClickListenersDependentOnReplyDownload();
                    }
                }
            }
        });
    }

    private void bindValuesDependentOnReplyDownload(){
        bindReplyTime();
        bindReplyingToLink();
        bindLikeReplyIcon();
        bindNoOfLikeInReply();
        bindReplyToReplyIcon();
        bindNoOfRepliesToReply();
    }

    private void setOnClickListenersDependentOnReplyDownload(){
        setReplyTimeOnClickListener();
        setReplyingToLinkOnClickListener();
        setLikeReplyIconOnClickListener();
        setNoOfLikeInReplyOnClickListener();
        setReplyToReplyIconOnClickListener();
        setNoOfRepliesToReplyOnClickListener();
    }

//    public void setmContext(Context mContext) {
//        this.mContext = mContext;
//    }

    private void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    private void setmCommentLink(String mCommentLink) {
        this.mCommentLink = mCommentLink;
    }

//    public void setmPostLink(String mPostLink) {
//        this.mPostLink = mPostLink;
//    }

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

    private void setmTaskComment(){
        mTaskComment = db.collection("comments").document(mCommentLink).get();
    }

    private void setmTaskReply(){
        mTaskReply = db.collection("comments").document(mReplyLink).get();
    }

    public void bindHeader(){
        postReplyHeader.setText(mNameReplyBy + " replied");
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
                Log.i("clicked", "commenter name from home post+comment+reply");
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
                Log.i("comment_detail", "from home post reply");
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
                Log.i("like", "cliked from home comment+reply");
                if(likeComment.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY)
                                .getConstantState())){
                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToComment();
                    increaseNumOfLikes(noOfLikesOnComment);
                    sendNotificationLikeCommentCloud();
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment();
                    decreaseNumOfLikes(noOfLikesOnComment);
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
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, mPostLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, mCommentLink);
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_LIKE_COMMENT);

        FirebaseFunctions.getInstance("asia-east2").getHttpsCallable("sendLikeCommentNotification").call(notification)
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
                Log.i("reply2comment", "from home reply");
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
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);
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

    }

    @Override
    public void bindReplyByAvatar() {
        db.collection("person_vital")
                .document(mLinkReplyBy)
                .get()
                .addOnSuccessListener((Activity)mContext, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                        PictureBinder.bindProfilePicture(replierImage, personVitalSnapshot);
                    }
                });
    }

    @Override
    public void setReplyByAvatarOnClickListener() {
        if(mLinkReplyBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        replierImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", mLinkReplyBy);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNameReplyBy() {
        replierName.setText(mNameReplyBy);
    }

    @Override
    public void setNameReplyByOnClickListener() {
        if(mLinkReplyBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        replierName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "replier name from home post+comment+reply");
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", mLinkReplyBy);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindReplyTime() {
        Timestamp ts = mReplySnapshot.getTimestamp("ts");
        if(ts==null) return;
        String dateOrTimeString = DateTimeExtractor.getDateOrTimeString(ts);
        replyTime.setText(dateOrTimeString);
    }

    @Override
    public void setReplyTimeOnClickListener() {

    }

    @Override
    public void bindReplyingToLink() {
        Map<String, Object> replyTo = (Map<String, Object>) mReplySnapshot.get(FirestoreFieldNames.COMMENTS_REPLY_TO);
        if(replyTo != null){
            String name = (String) replyTo.get("n");
            String text = "Replying To " + name;
            SpannableStringBuilder spannedText = getSpannedText(text, name);
            replyingToTV.setText(spannedText);
        }
    }

    @Override
    public void setReplyingToLinkOnClickListener() {
//        replyingToTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, Object> replyTo = (Map<String, Object>) mReplySnapshot.get(FirestoreFieldNames.COMMENTS_REPLY_TO);
//                if(replyTo != null){
//                    String link = (String) replyTo.get("l");
//                    Intent intent;
//                    Log.i("clicked", "replying to from home post+reply");
//                    intent = new Intent(mContext, PersonDetail.class);
//                    intent.putExtra("personLink", link);
//                    // mContext.startActivity(intent);
//                }
//            }
//        });
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
        String currentUserLink = mAuth.getCurrentUser().getUid();
        List<String> likers = (List<String>) mReplySnapshot.get("l");
        if(likers.contains(currentUserLink)){
            likeReply.setImageResource(ResourceIds.LIKE_FULL);
        }else{
            likeReply.setImageResource(ResourceIds.LIKE_EMPTY);
        }
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
                    increaseNumOfLikes(numOfLikesInReply);
                    sendNotificationLikeReplyCloud();
                }else{
                    ((ImageView)v).setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromReply();
                    decreaseNumOfLikes(numOfLikesInReply);
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
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, mPostLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, mCommentLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_REPLY_LINK, mReplyLink);
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_LIKE_REPLY);

        FirebaseFunctions.getInstance("asia-east2").getHttpsCallable("sendLikeReplyNotification")
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
        List<String> likers = (List<String>) mReplySnapshot.get("l");
        int numberofLikes = 0;
        if(likers != null){
            numberofLikes = likers.size();
        }
        numOfLikesInReply.setText(Integer.toString(numberofLikes));
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
                Log.i("reply2reply", "from home post+reply");

                Map<String, Object> replyingTo = new HashMap<>();
                replyingTo.put("n", mNameReplyBy);
                replyingTo.put("l", mLinkReplyBy);
                replyingTo.put("t", AccountTypes.PERSON);
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2R_FROM_HOME_POST);
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setReplyLink(mReplyLink);
                commentIntentExtra.setReplyingTo(replyingTo);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNoOfRepliesToReply() {
        List<String> replies = (List<String>) mReplySnapshot.get("r");
        int numberOfReplies = 0;
        if(replies != null){
            numberOfReplies = replies.size();
        }
        numOfRepliesToReply.setText(Integer.toString(numberOfReplies));
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
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_REPLY_BODY_FROM_HOME_POST);
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setReplyLink(mReplyLink);

                Intent intent = new Intent(mContext, CommentDetail.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }

    private void decreaseNumOfLikes(TextView v){
        String str = (String) v.getText();
        int numOfLikes = Integer.valueOf(str);
        v.setText(Integer.toString(numOfLikes-1));
    }

    private void increaseNumOfLikes(TextView v){
        String str = v.getText().toString();
        int numOfLikes = Integer.valueOf(str);
        v.setText(Integer.toString(numOfLikes+1));
    }

    private SpannableStringBuilder getSpannedText(String text, String name){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        int start = text.indexOf(name);
        int end = start + name.length();
        // spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public void refreshHolder() {
        Log.i("refreshing", "postreplyholder");
        super.refreshHolder();
        postReplyHeader.setText("");
        commenterImage.setImageResource(R.drawable.ltgray);
        commenterName.setText("");
        commentTimeTV.setText("");
        theComment.setText("");
        noOfLikesOnComment.setText("");
        noOfRepliesToComment.setText("");

        replierImage.setImageResource(R.drawable.ltgray);
        replierName.setText("");
        replyTime.setText("");
        replyingToTV.setText("");
        theReply.setText("");
        numOfLikesInReply.setText("");
        numOfRepliesToReply.setText("");
    }
}
