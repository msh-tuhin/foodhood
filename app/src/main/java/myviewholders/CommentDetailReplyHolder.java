package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
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
import myapp.utils.AccountTypes;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myapp.utils.NotificationTypes;
import myapp.utils.ResourceIds;

public class CommentDetailReplyHolder  extends RecyclerView.ViewHolder
        implements ReplyInterface{

    private Context mContext;
    private int mEntryPoint;
    private String mPostLink;
    private String mCommentLink;
    private String mReplyLink;
    Task<DocumentSnapshot> mReplyTask;
    private DocumentSnapshot mReplySnapshot;
    private String mReplyText;
    private String mReplierLink;
    private String mReplierName;
    private boolean mForPerson;

    private CircleImageView imageReplyBy;
    private TextView nameReplyByTV;
    private TextView replyTV;
    private TextView replyingToTV;
    private ImageView likeReply;
    private ImageView replyToReply;
    private TextView numberOfLikesTV;
    private TextView numberOfRepliesTV;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    View mView;

    public CommentDetailReplyHolder(@NonNull View v) {
        super(v);
        mView = v;
        imageReplyBy = v.findViewById(R.id.replier_image);
        nameReplyByTV = v.findViewById(R.id.replier_name);
        replyTV = v.findViewById(R.id.the_reply);
        replyingToTV = v.findViewById(R.id.replying_to);
        likeReply = v.findViewById(R.id.like_reply);
        replyToReply = v.findViewById(R.id.reply_to_reply);
        numberOfLikesTV = v.findViewById(R.id.number_of_likes_in_reply);
        numberOfRepliesTV = v.findViewById(R.id.number_of_replies_to_reply);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void bindTo(Context context,
                       int entryPoint,
                       String postLink,
                       String commentLink,
                       String replyLink) {
        Log.i("bindTo", this.getClass().toString());

        mContext = context;
        mEntryPoint = entryPoint;
        mPostLink = postLink;
        mCommentLink = commentLink;
        mReplyLink = replyLink;
        mReplyTask = db.collection("comments").document(mReplyLink).get();
        setmForPerson();

        setElementsDependentOnReplyDownload();
        setElementsIndepent();
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

    private void setElementsDependentOnReplyDownload(){
        mReplyTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot replySnapshot = task.getResult();
                    if(replySnapshot.exists()){
                        mReplySnapshot = replySnapshot;
                        mReplyText = replySnapshot.getString("te");
                        mReplierLink = replySnapshot.getString("w.l");
                        mReplierName = replySnapshot.getString("w.n");
                        bindValuesDependentOnReplyDownload();
                        setOnClickListenersDependentOnReplyDownload();
                    }
                }
            }
        });
    }

    private void bindValuesDependentOnReplyDownload(){
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

    private void setOnClickListenersDependentOnReplyDownload(){
        setReplyByAvatarOnClickListener();
        setNameReplyByOnClickListener();
        setReplyTimeOnClickListener();
        setReplyingToLinkOnClickListener();
        setReplyOnClickListener();
        setLikeReplyIconOnClickListener();
        setNoOfLikeInReplyOnClickListener();
        setReplyToReplyIconOnClickListener();
        setNoOfRepliesToReplyOnClickListener();
    }

    private void setElementsIndepent(){
        bindValuesIndependent();
        setOnClickListenersIndependent();
    }

    private void bindValuesIndependent(){

    }

    private void setOnClickListenersIndependent(){
        setReplyLayoutOnClickListener();
        setCommentReplyLayoutOnClickListener();
    }

    @Override
    public void bindReplyByAvatar() {

    }

    @Override
    public void setReplyByAvatarOnClickListener() {

    }

    @Override
    public void bindNameReplyBy() {
        nameReplyByTV.setText(mReplierName);
    }

    @Override
    public void setNameReplyByOnClickListener() {
        nameReplyByTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(isCommenterAPerson()){
                    Log.i("clicked", "replier(person) name from CD reply");
                    intent = new Intent(mContext, PersonDetail.class);
                    intent.putExtra("personLink", mReplierLink);
                } else{
                    Log.i("clicked", "replier(restaurant) name from CD reply");
                    intent = new Intent(mContext, RestDetail.class);
                    intent.putExtra("restaurantLink", mReplierLink);
                }
                mContext.startActivity(intent);
            }
        });
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
        replyTV.setText(mReplyText);
    }

    @Override
    public void setReplyOnClickListener() {

    }

    @Override
    public void bindLikeReplyIcon() {
        List<String> likers = (List<String>)mReplySnapshot.get("l");
        String currentUserLink = mAuth.getCurrentUser().getUid();
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
                Log.i("like", "clicked from comment detail reply holder");
                if(likeReply.getDrawable().getConstantState()
                        .equals(ContextCompat.getDrawable(mContext, ResourceIds.LIKE_EMPTY).getConstantState())){
                    likeReply.setImageResource(ResourceIds.LIKE_FULL);
                    addLikeToReply();
                    increaseNumOfLikes();
                    if(mForPerson){
                        sendNotificationLikeReplyCloud("sendLikeReplyNotification");
                    }else{
                        sendNotificationLikeReplyCloud("sendLikeReplyByRFNotification");
                    }
                }else{
                    likeReply.setImageResource(ResourceIds.LIKE_EMPTY);
                    removeLikeFromReply();
                    decreaseNumOfLikes();
                }
            }
        });
    }

    private void addLikeToReply(){
        DocumentReference replyRef = db.collection("comments")
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
        DocumentReference replyRef = db.collection("comments")
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

    @Override
    public void bindNoOfLikeInReply() {
        List<String> likers = (List<String>)mReplySnapshot.get("l");
        int numberofLikes = 0;
        if(likers != null){
            numberofLikes = likers.size();
        }
        numberOfLikesTV.setText(Integer.toString(numberofLikes));
    }

    @Override
    public void setNoOfLikeInReplyOnClickListener() {

    }

    @Override
    public void bindReplyToReplyIcon() {

    }

    @Override
    public void setReplyToReplyIconOnClickListener() {
        replyToReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("reply_to_reply", "from comment detail");
                int replyPosition = ((CommentDetail)mContext).rv.getChildAdapterPosition(mView);
                Log.i("reply_pos", Integer.toString(replyPosition));

                Intent intent = new Intent(mContext, WriteComment.class);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                if(isCommentInRF()){
                    commentIntentExtra.setEntryPoint(EntryPoints.R2R_FROM_CD_RF);
                } else{
                    commentIntentExtra.setEntryPoint(EntryPoints.R2R_FROM_CD_POST);
                }
                commentIntentExtra.setPostLink(mPostLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setReplyLink(mReplyLink);
                commentIntentExtra.setNewReplyPosition(replyPosition);
                intent.putExtra("comment_extra", commentIntentExtra);
                ((CommentDetail)mContext).startActivityForResult(intent,
                        ((CommentDetail)mContext).REQUEST_REPLY_TO_REPLY);
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
        numberOfRepliesTV.setText(Integer.toString(numberOfReplies));
    }

    @Override
    public void setNoOfRepliesToReplyOnClickListener() {

    }

    @Override
    public void setReplyLayoutOnClickListener() {

    }

    @Override
    public void setCommentReplyLayoutOnClickListener() {

    }

    private void sendNotificationLikeReplyCloud(String cloudFunctionName){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        final Map<String, Object> notification = new HashMap<>();
        notification.put("postLink", mPostLink);
        notification.put("commentLink", mCommentLink);
        notification.put("replyLink", mReplyLink);
        notification.put("w", who);
        if(isCommentInRF()){
            notification.put("t", NotificationTypes.NOTIF_LIKE_REPLY_RF);
        } else{
            notification.put("t", NotificationTypes.NOTIF_LIKE_REPLY);
        }

        FirebaseFunctions.getInstance().getHttpsCallable(cloudFunctionName).call(notification)
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
        String str = (String) numberOfLikesTV.getText();
        int numOfLikes = Integer.valueOf(str);
        numberOfLikesTV.setText(Integer.toString(numOfLikes-1));
    }

    private void increaseNumOfLikes(){
        String str = numberOfLikesTV.getText().toString();
        int numOfLikes = Integer.valueOf(str);
        numberOfLikesTV.setText(Integer.toString(numOfLikes+1));
    }

    private boolean isCommenterAPerson(){
        Long commenterType = mReplySnapshot.getLong("w.t");
        if(commenterType != null){
            return commenterType == AccountTypes.PERSON;
        }
        return true;
    }
}
