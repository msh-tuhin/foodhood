package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AccountTypes;
import myapp.utils.CommentIntentExtra;
import myapp.utils.DateTimeExtractor;
import myapp.utils.EntryPoints;
import myapp.utils.NotificationTypes;
import myapp.utils.PictureBinder;
import myapp.utils.ResourceIds;

public class RestFeedReplyHolder extends RestFeedHolder
        implements CommentInterface, ReplyInterface{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context mContext;
    private String mCommentText;
    private String mNameCommentBy;
    private String mLinkCommentBy;
    private String mReplyText;
    private String mNameReplyBy;
    private String mLinkReplyBy;
    private String mRestFeedLink;
    private String mCommentLink;
    private String mReplyLink;
    private Task<DocumentSnapshot> mTaskReply;
    private Task<DocumentSnapshot> mTaskComment;
    private DocumentSnapshot mReplySnapshot;
    private DocumentSnapshot mCommentSnapshot;

    private LinearLayout commentReplyLayout;
    private TextView replyHeaderTV;
    private CircleImageView commenterImage;
    private TextView commenterNameTV;
    private TextView commentTimeTV;
    private TextView commentTV;
    private TextView repliesLinkTV;
    private ImageView likeComment;
    private TextView noOfLikesInComment;
    private ImageView replyToComment;
    private TextView noOfRepliesToComment;

    CircleImageView replierImage;
    TextView replierNameTV;
    TextView replyTime;
    TextView replyingToTV;
    TextView replyTV;
    ImageView likeReply;
    TextView numOfLikesInReply;
    ImageView replyTOReply;
    TextView numOfRepliesToReply;

    public RestFeedReplyHolder(@NonNull View v) {
        super(v);
        commentReplyLayout = v.findViewById(R.id.comment_reply_layout);
        replyHeaderTV = v.findViewById(R.id.rest_feed_reply_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTV = v.findViewById(R.id.commenter_name);
        commentTimeTV = v.findViewById(R.id.time);
        commentTV = v.findViewById(R.id.the_comment);
        repliesLinkTV = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        noOfLikesInComment = v.findViewById(R.id.number_of_likes);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);

        replierImage = v.findViewById(R.id.replier_image);
        replierNameTV = v.findViewById(R.id.replier_name);
        replyTime = v.findViewById(R.id.time_reply);
        replyingToTV = v.findViewById(R.id.replying_to);
        replyTV = v.findViewById(R.id.the_reply);
        likeReply = v.findViewById(R.id.like_reply);
        numOfLikesInReply = v.findViewById(R.id.number_of_likes_in_reply);
        replyTOReply = v.findViewById(R.id.reply_to_reply);
        numOfRepliesToReply = v.findViewById(R.id.number_of_replies_to_reply);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);

        setPrivateGlobalsIndependent(context, activity);
        bindValuesIndependent();
        setOnClickListenersIndependent();
        setElementsDependentOnCommentDownload();
        setElementsDependentOnReplyDownload();
    }

    private void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    private void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
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

    private void setmNameReplyBy(String mNameReplyBy) {
        this.mNameReplyBy = mNameReplyBy;
    }

    private void setmLinkReplyBy(String mLinkReplyBy) {
        this.mLinkReplyBy = mLinkReplyBy;
    }

    private void setmRestFeedLink(String mRestFeedLink) {
        this.mRestFeedLink = mRestFeedLink;
    }

    private void setmCommentLink(String mCommentLink) {
        this.mCommentLink = mCommentLink;
    }

    private void setmReplyLink(String mReplyLink) {
        this.mReplyLink = mReplyLink;
    }

    private void setmTaskComment(){
        mTaskComment = db.collection("comments").document(mCommentLink).get();
    }

    private void setmTaskReply(){
        mTaskReply = db.collection("comments").document(mReplyLink).get();
    }

    private void setPrivateGlobalsIndependent(Context context, DocumentSnapshot activity){
        Map replyBy = (Map) activity.get("w");
        String nameReplyBy = (String) replyBy.get("n");
        String linkReplyBy = (String) replyBy.get("l");

        Map<String, String> commentData = (Map) activity.get("com");
        String commentText = commentData.get("text");
        String commentLink = commentData.get("l");
        String nameCommentBy = commentData.get("byn");
        String linkCommentBy = commentData.get("byl");
        Map<String, String> replyData = (Map) activity.get("rep");
        String replyText = replyData.get("text");
        String replyLink = replyData.get("l");

        setmContext(context);
        setmNameCommentBy(nameCommentBy);
        setmLinkCommentBy(linkCommentBy);
        setmCommentText(commentText);
        setmNameReplyBy(nameReplyBy);
        setmLinkReplyBy(linkReplyBy);
        setmReplyText(replyText);
        setmCommentLink(commentLink);
        setmReplyLink(replyLink);
        setmRestFeedLink(activity.getString("wh"));
        setmTaskComment();
        setmTaskReply();
    }

    private void bindValuesIndependent(){
        bindHeader();
        bindCommentByAvatar();
        bindNameCommentBy();
        bindComment();

        bindReplyByAvatar();
        bindNameReplyBy();
        bindReply();
    }

    private void setOnClickListenersIndependent(){
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

    @Override
    public void bindHeader() {
        replyHeaderTV.setText(mNameReplyBy + " replied");
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
        commenterNameTV.setText(mNameCommentBy);
    }

    @Override
    public void setNameCommentByOnClickListener() {
        if(mLinkCommentBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        commenterNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(isCommenterAPerson()){
                    Log.i("clicked", "commenter(person) name from home rf+comment+reply");
                    intent = new Intent(mContext, PersonDetail.class);
                    intent.putExtra("personLink", mLinkCommentBy);
                }else{
                    Log.i("clicked", "commenter(restaurant) name from home rf+comment+reply");
                    intent = new Intent(mContext, RestDetail.class);
                    intent.putExtra("restaurantLink", mLinkCommentBy);
                }

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
        commentTV.setText(mCommentText);
    }

    @Override
    public void setCommentOnClickListener() {

    }

    @Override
    public void bindRepliesLink() {

    }

    @Override
    public void setRepliesLinkOnClickListener() {
        repliesLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home RF reply");
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_BODY_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setReplyLink(mReplyLink);

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
                Log.i("like", "clicked from home rf comment+reply");
                if(likeComment.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY)
                                .getConstantState())){
                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToComment();
                    increaseNumOfLikes(noOfLikesInComment);
                    sendNotificationLikeCommentCloud();
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment();
                    decreaseNumOfLikes(noOfLikesInComment);
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
        notification.put("postLink", mRestFeedLink);
        notification.put("commentLink", mCommentLink);
        notification.put("w", who);
        notification.put("t", NotificationTypes.NOTIF_LIKE_COMMENT_RF);

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
        List<String> likers = (List<String>) mCommentSnapshot.get("l");
        int numberofLikes = 0;
        if(likers != null){
            numberofLikes = likers.size();
        }
        noOfLikesInComment.setText(Integer.toString(numberofLikes));
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
                if(isCommenterAPerson()){
                    replyingTo.put("t", AccountTypes.PERSON);
                }else{
                    replyingTo.put("t", AccountTypes.RESTAURANT);
                }

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setCommentMap(commentMap);

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
        replierNameTV.setText(mNameReplyBy);
    }

    @Override
    public void setNameReplyByOnClickListener() {
        if(mLinkReplyBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        replierNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "replier name from home rf+comment+reply");
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
        Map<String, Object> replyTo = (Map<String, Object>) mReplySnapshot.get("replyTo");
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
//                Map<String, Object> replyTo = (Map<String, Object>) mReplySnapshot.get("replyTo");
//                if(replyTo != null){
//                    String link = (String) replyTo.get("l");
//                    Long type = (Long) replyTo.get("t");
//                    Intent intent;
//                    if(type == AccountTypes.PERSON){
//                        Log.i("clicked", "replying to(person) from home rf+reply");
//                        intent = new Intent(mContext, PersonDetail.class);
//                        intent.putExtra("personLink", link);
//                    }else{
//                        Log.i("clicked", "replying to(restaurant) from home rf+reply");
//                        intent = new Intent(mContext, RestDetail.class);
//                        intent.putExtra("restaurantLink", link);
//                    }
////            mContext.startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    public void bindReply() {
        replyTV.setText(mReplyText);
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
                Log.i("like", "clicked from home rf comment+reply");
                if(likeReply.getDrawable().getConstantState()
                        .equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY).getConstantState())){
                    likeReply.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToReply();
                    increaseNumOfLikes(numOfLikesInReply);
                    sendNotificationLikeReplyCloud();
                }else{
                    likeReply.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromReply();
                    decreaseNumOfLikes(numOfLikesInReply);
                }
            }
        });
    }

    private void addLikeToReply(){
        DocumentReference replyRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mReplyLink);
        String currentUserLink = mAuth.getCurrentUser().getUid();
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
        DocumentReference replyRef = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(mReplyLink);
        String currentUserLink = mAuth.getCurrentUser().getUid();
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
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", mRestFeedLink);
        notification.put("commentLink", mCommentLink);
        notification.put("replyLink", mReplyLink);
        notification.put("w", who);
        notification.put("t", NotificationTypes.NOTIF_LIKE_REPLY_RF);

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
                Log.i("reply2reply", "from home rf+reply");

                Map<String, Object> replyingTo = new HashMap<>();
                replyingTo.put("n", mNameReplyBy);
                replyingTo.put("l", mLinkReplyBy);
                replyingTo.put("t", AccountTypes.PERSON);
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2R_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
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

    @Override
    public void setCommentReplyLayoutOnClickListener() {
        commentReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home RF reply");
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_REPLY_BODY_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
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

    private boolean isCommenterAPerson(){
        Long commenterType = mCommentSnapshot.getLong("w.t");
        if(commenterType != null){
            return commenterType == AccountTypes.PERSON;
        }
        return true;
    }

    @Override
    public void refreshHolder() {
        Log.i("refreshing", "restfeedreplyholder");
        super.refreshHolder();
        replyHeaderTV.setText("");
        commenterImage.setImageResource(R.drawable.ltgray);
        commenterNameTV.setText("");
        commentTimeTV.setText("");
        commentTV.setText("");
        noOfLikesInComment.setText("");
        noOfRepliesToComment.setText("");

        replierImage.setImageResource(R.drawable.ltgray);
        replierNameTV.setText("");
        replyTime.setText("");
        replyingToTV.setText("");
        replyTV.setText("");
        numOfLikesInReply.setText("");
        numOfRepliesToReply.setText("");
    }
}
