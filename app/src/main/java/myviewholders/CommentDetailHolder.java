package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.Continuation;
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
import myapp.utils.AccountTypes;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myapp.utils.NotificationTypes;
import myapp.utils.ResourceIds;

public class CommentDetailHolder extends RecyclerView.ViewHolder
        implements CommentInterface{

    private Context mContext;
    private int mEntryPoint;
    private String mCommentLink;
    private DocumentSnapshot mCommentSnapshot;
    private String mCommentText;
    private String mCommenterLink;
    private String mCommenterName;
    private String mPostLink;
    private Task<DocumentSnapshot> mTaskComment;
    private boolean mForPerson;

    private TextView postLinkTextView;
    private CircleImageView commenterImage;
    private TextView commenterNameTextView;
    private TextView theCommentTextView;
    private ImageView likeComment;
    private ImageView replyToComment;
    private TextView numberOfLikesTextView;
    private TextView numberOfRepliesTextView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
    }

    public void bindTo(Context context,
                       int entryPoint,
                       Task<DocumentSnapshot> taskComment,
                       String postLink,
                       String commentLink) {

        Log.i("bindTo", this.getClass().toString());

        mContext = context;
        mEntryPoint = entryPoint;
        mPostLink = postLink;
        mCommentLink = commentLink;
        mTaskComment = taskComment;
        setmForPerson();

        setElementsDependentOnCommentDownload();
        setElementsIndependent();
    }

    private void setmForPerson(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        SharedPreferences sPref = mContext.getSharedPreferences(
                mContext.getString(R.string.account_type),
                Context.MODE_PRIVATE);
        int accountType = sPref.getInt(email, AccountTypes.PERSON);
        mForPerson = accountType == AccountTypes.PERSON;
        if(mForPerson) Log.i("account", "for person");
        else Log.i("account", "for restaurant");
    }

    private void setElementsDependentOnCommentDownload(){
        mTaskComment.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot commentSnapshot = task.getResult();
                    if(commentSnapshot.exists()){
                        mCommentSnapshot = commentSnapshot;
                        mCommentText = commentSnapshot.getString("te");
                        mCommenterLink = commentSnapshot.getString("w.l");
                        mCommenterName = commentSnapshot.getString("w.n");
                        bindValuesDependentOnCommentDownload();
                        setOnClickListenersDependentOnCommentDownload();
                    }
                }
            }
        });
    }

    private void bindValuesDependentOnCommentDownload(){
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

    private void setOnClickListenersDependentOnCommentDownload(){
        setNameCommentByOnClickListener();
        setCommentTimeOnClickListener();
        setCommentByAvatarOnClickListener();
        setCommentOnClickListener();
        setRepliesLinkOnClickListener();
        setLikeCommentIconOnClickListener();
        setNoOfLikeInCommentOnClickListener();
        setReplyToCommentIconOnClickListener();
        setNoOfRepliesToCommentOnClickListener();
    }

    private void setElementsIndependent(){
        bindValuesIndependent();
        setOnClickListenersIndependent();
    }

    private void bindValuesIndependent(){

    }

    private void setOnClickListenersIndependent(){
        setCommentLayoutOnClickListener();
    }

    @Override
    public void bindCommentByAvatar() {

    }

    @Override
    public void setCommentByAvatarOnClickListener() {

    }

    @Override
    public void bindNameCommentBy() {
        commenterNameTextView.setText(mCommenterName);
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
        theCommentTextView.setText(mCommentText);
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
        List<String> likers = (List<String>) mCommentSnapshot.get("l");
        String currentUserLink = mAuth.getCurrentUser().getUid();
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
                Log.i("like", "cliked from comment detail");
                if(likeComment.getDrawable().getConstantState().equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY).getConstantState())){
                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToComment();
                    increaseNumOfLikes();
                    if(mForPerson){
                        sendNotificationLikeCommentCloud("sendLikeCommentNotification");
                    }else{
                        sendNotificationLikeCommentCloud("sendLikeCommentByRFNotification");
                    }

                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment();
                    decreaseNumOfLikes();
                }
            }
        });
    }

    private void addLikeToComment(){
        DocumentReference commentRef = db.collection("comments")
                .document(mCommentLink);
        String currentUserLink = mAuth.getCurrentUser().getUid();
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
        DocumentReference commentRef = db.collection("comments")
                .document(mCommentLink);
        String currentUserLink = mAuth.getCurrentUser().getUid();
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

    @Override
    public void bindNoOfLikeInComment() {
        List<String> likers = (List<String>) mCommentSnapshot.get("l");
        int numberofLikes = 0;
        if(likers != null){
            numberofLikes = likers.size();
        }
        numberOfLikesTextView.setText(Integer.toString(numberofLikes));
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
                Log.i("reply", "from comment detail page");

                // needed if reply activity has redundant data for the comment
                // maybe let all this be handled in a cloud function
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mCommenterLink);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mCommenterName);
                commentMap.put("l", mCommentLink);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                if(isCommentInRF()){
                    commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_CD_RF);
                } else{
                    commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_CD_POST);
                }
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setCommentMap(commentMap);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                ((CommentDetail)mContext).startActivityForResult(intent,
                        ((CommentDetail)mContext).REQUEST_REPLY);
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
        numberOfRepliesTextView.setText(Integer.toString(numberOfReplies));
    }

    @Override
    public void setNoOfRepliesToCommentOnClickListener() {

    }

    @Override
    public void setCommentLayoutOnClickListener() {

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

    private void sendNotificationLikeCommentCloud(String cloudFunctionName){
        String currentUserLink = mAuth.getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", mPostLink);
        notification.put("commentLink", mCommentLink);
        notification.put("w", who);
        if(isCommentInRF()){
            notification.put("t", NotificationTypes.NOTIF_LIKE_COMMENT_RF);
        } else{
            notification.put("t", NotificationTypes.NOTIF_LIKE_COMMENT);
        }

        FirebaseFunctions.getInstance()
                .getHttpsCallable(cloudFunctionName)
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

    private boolean isCommentInRF(){
        return mEntryPoint%2 == 0;
    }

    private void decreaseNumOfLikes(){
        String str = (String) numberOfLikesTextView.getText();
        int numOfLikes = Integer.valueOf(str);
        numberOfLikesTextView.setText(Integer.toString(numOfLikes-1));
    }

    private void increaseNumOfLikes(){
        String str = numberOfLikesTextView.getText().toString();
        int numOfLikes = Integer.valueOf(str);
        numberOfLikesTextView.setText(Integer.toString(numOfLikes+1));
    }
}
