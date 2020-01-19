package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import site.sht.bd.foodhood.CommentDetail;
import site.sht.bd.foodhood.MorePeole;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.AccountTypes;
import myapp.utils.CommentIntentExtra;
import myapp.utils.DateTimeExtractor;
import myapp.utils.EntryPoints;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.NotificationTypes;
import myapp.utils.PictureBinder;
import myapp.utils.ResourceIds;
import myapp.utils.SourceMorePeople;

public class FullPostCommentHolder extends RecyclerView.ViewHolder
        implements CommentInterface{

    private Task<DocumentSnapshot> mTaskComment;
    private DocumentSnapshot mCommentSnapshot;
    private Context mContext;
    private String mPostLink;
    private String mCommentLink;
    private String mCommentText;
    private String mLinkCommentBy;
    private String mNameCommentBy;

    private LinearLayout commentLayout;
    private CircleImageView commenterImage;
    private TextView commenterNameTextView, theCommentTextView, repliesLinkTextView;
    private TextView commentTimeTV;
    private ImageView likeComment, replyToComment;
    private TextView numberOfLikesTextView, numberOfRepliesTextView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public FullPostCommentHolder(@NonNull View v) {
        super(v);
        commentLayout = v.findViewById(R.id.small_comment_layout);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTextView = v.findViewById(R.id.commenter_name);
        theCommentTextView = v.findViewById(R.id.the_comment);
        commentTimeTV = v.findViewById(R.id.time);
        repliesLinkTextView = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        numberOfLikesTextView = v.findViewById(R.id.number_of_likes);
        numberOfRepliesTextView = v.findViewById(R.id.number_of_replies);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void bindTo(Context context, String postLink, String commentLink) {
        Log.i("bindTo", this.getClass().toString());
        refreshHolder();
        mContext = context;
        mPostLink = postLink;
        mCommentLink = commentLink;
        mTaskComment = db.collection("comments")
                .document(commentLink).get();

        setElementsDependentOnCommentDownload();
        setElementsIndependent();
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
                        mLinkCommentBy = commentSnapshot.getString("w.l");
                        mNameCommentBy = commentSnapshot.getString("w.n");
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
        commenterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLinkCommentBy.equals(mAuth.getCurrentUser().getUid())) {
                    return;
                }
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", mLinkCommentBy);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNameCommentBy() {
        commenterNameTextView.setText(mNameCommentBy);
    }

    @Override
    public void setNameCommentByOnClickListener() {
        if(mLinkCommentBy.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        commenterNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "commenter name from full post");
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
        theCommentTextView.setText(mCommentSnapshot.getString("te"));
    }

    @Override
    public void setCommentOnClickListener() {

    }

    @Override
    public void bindRepliesLink() {

    }

    @Override
    public void setRepliesLinkOnClickListener() {
        repliesLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from full post comment body click");

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_BODY_FROM_FULL_POST);
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);

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
                Log.i("like", "cliked from full post comment");
                if(likeComment.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext,
                                ResourceIds.LIKE_EMPTY).getConstantState())){
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
        numberOfLikesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "no of likes");
                ArrayList<String> likes = (ArrayList<String>) mCommentSnapshot.get("l");
                boolean isLikeFilled = likeComment.getDrawable().getConstantState().
                        equals(ContextCompat.getDrawable(mContext,
                                R.drawable.baseline_favorite_black_24dp).getConstantState());
                if(isLikeFilled){
                    if(!likes.contains(mAuth.getCurrentUser().getUid())){
                        likes.add(mAuth.getCurrentUser().getUid());
                    }
                }else{
                    likes.remove(mAuth.getCurrentUser().getUid());
                }
                Intent intent = new Intent(mContext, MorePeole.class);
                intent.putExtra("source", SourceMorePeople.LIKERS_COMMENT_REPLY);
                intent.putExtra("commentLOrReplyLink", mCommentLink);
                intent.putStringArrayListExtra("personsList", likes);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindReplyToCommentIcon() {

    }

    @Override
    public void setReplyToCommentIconOnClickListener() {
        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply", "from full post");

                // needed if reply activity has redundant data for the comment
                // commentData might not be populated yet
                // TODO do something about it
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
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_FULL_POST);
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
        numberOfRepliesTextView.setText(Integer.toString(numberOfReplies));
    }

    @Override
    public void setNoOfRepliesToCommentOnClickListener() {

    }

    @Override
    public void setCommentLayoutOnClickListener() {
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from full post comment body click");

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_BODY_FROM_FULL_POST);
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);

                Intent intent = new Intent(mContext, CommentDetail.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
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

    private void refreshHolder(){
        Log.i("refreshing", "fullpostcommentholder");
        commenterImage.setImageResource(R.drawable.ltgray);
        commenterNameTextView.setText("");
        commentTimeTV.setText("");
        theCommentTextView.setText("");
        numberOfLikesTextView.setText("0");
        numberOfRepliesTextView.setText("0");
    }
}
