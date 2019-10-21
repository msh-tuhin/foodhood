package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
import com.google.android.gms.tasks.Continuation;
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
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myapp.utils.NotificationTypes;
import myapp.utils.ResourceIds;

public class FullRestFeedCommentHolder extends RecyclerView.ViewHolder
        implements CommentInterface{

    private Task<DocumentSnapshot> mTaskComment;
    private DocumentSnapshot mCommentSnapshot;
    private Context mContext;
    private String mRestFeedLink;
    private String mCommentLink;
    private String mCommentText;
    private String mLinkCommentBy;
    private String mNameCommentBy;

    private LinearLayout commentLayout;
    private CircleImageView commenterImage;
    private TextView commenterNameTextView, theCommentTextView, repliesLinkTextView;
    private ImageView likeComment, replyToComment;
    private TextView numberOfLikesTextView, numberOfRepliesTextView;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public FullRestFeedCommentHolder(@NonNull View v) {
        super(v);
        commentLayout = v.findViewById(R.id.small_comment_layout);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTextView = v.findViewById(R.id.commenter_name);
        theCommentTextView = v.findViewById(R.id.the_comment);
        repliesLinkTextView = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        numberOfLikesTextView = v.findViewById(R.id.number_of_likes);
        numberOfRepliesTextView = v.findViewById(R.id.number_of_replies);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    public void bindTo(final Context context,
                       final String restFeedLink,
                       final String commentLink) {
        Log.i("bindTo", this.getClass().toString());

        mContext = context;
        mRestFeedLink = restFeedLink;
        mCommentLink = commentLink;
        mTaskComment = db.collection("comments")
                .document(commentLink).get();

        setElementsDependentOnCommentDownload();
        setElementsDependentOnPersonVitalDownload();
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
                        mLinkCommentBy = commentSnapshot.getString("w");
                        bindValuesDependentOnCommentDownload();
                        setOnClickListenersDependentOnCommentDownload();
                    }
                }
            }
        });
    }

    private void bindValuesDependentOnCommentDownload(){
        bindCommentByAvatar();
        bindCommentTime();
        bindComment();
        bindRepliesLink();
        bindLikeCommentIcon();
        bindNoOfLikeInComment();
        bindReplyToCommentIcon();
        bindNoOfRepliesToComment();
    }

    private void setOnClickListenersDependentOnCommentDownload(){
        setCommentTimeOnClickListener();
        setCommentByAvatarOnClickListener();
        setCommentOnClickListener();
        setRepliesLinkOnClickListener();
        setLikeCommentIconOnClickListener();
        setNoOfLikeInCommentOnClickListener();
        setReplyToCommentIconOnClickListener();
        setNoOfRepliesToCommentOnClickListener();
    }

    private void setElementsDependentOnPersonVitalDownload(){
        // TODO make the "w" field a map with keys "n", "l"
        mTaskComment.continueWithTask(new Continuation<DocumentSnapshot, Task<DocumentSnapshot>>() {
            @Override
            public Task<DocumentSnapshot> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                return getPersonVitalTask(task);
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot personVital) {
                if(personVital.exists()){
                    Log.i("commenter_name", personVital.getString("n"));
                    mNameCommentBy = personVital.getString("n");
                    bindValuesDependentOnPersonVitalDownload();
                    setOnClickListenersDependentOnPersonVitalDownload();
                }
            }
        });
    }

    private Task<DocumentSnapshot> getPersonVitalTask(Task<DocumentSnapshot> task){
        DocumentReference personRef = db
                .collection("person_vital")
                .document("abc");
        if(task.isSuccessful()){
            DocumentSnapshot commentSnapshot = task.getResult();
            if(commentSnapshot.exists()){
                personRef = db
                        .collection("person_vital")
                        .document(commentSnapshot.getString("w"));
            }
        }
        return personRef.get();
    }

    private void bindValuesDependentOnPersonVitalDownload(){
        bindNameCommentBy();
    }

    private void setOnClickListenersDependentOnPersonVitalDownload(){
        setNameCommentByOnClickListener();
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
        commenterNameTextView.setText(mNameCommentBy);
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
                    sendNotificationLikeCommentCloud();
                }else{
                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromComment();
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
        int numberofLikes = likers.size();
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
                Log.i("reply", "from full RF");

                // needed if reply activity has redundant data for the comment
                // commentData might not be populated yet
                // TODO do something about it
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mLinkCommentBy);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mNameCommentBy);
                commentMap.put("l", mCommentLink);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_FULL_RF);
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
        int numberOfReplies = ((List<String>)mCommentSnapshot.get("r")).size();
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
                Log.i("comment_detail", "from full RF");
                Intent intent = new Intent(mContext, CommentDetail.class);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_BODY_FROM_FULL_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
                commentIntentExtra.setCommentLink(mCommentLink);
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
}