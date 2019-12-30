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
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
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
import myapp.utils.OrphanUtilityMethods;
import myapp.utils.PictureBinder;
import myapp.utils.ResourceIds;
import myapp.utils.SourceMorePeople;

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
    private TextView replyTimeTV;
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
        replyTimeTV = v.findViewById(R.id.time_reply);
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
        refreshHolder();
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
        int accountType = OrphanUtilityMethods.getAccountType(mContext);
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
        if(isReplierAPerson()){
            db.collection("person_vital")
                    .document(mReplierLink)
                    .get()
                    .addOnSuccessListener((Activity)mContext, new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                            PictureBinder.bindProfilePicture(imageReplyBy, personVitalSnapshot);
                        }
                    });
        }else{
            db.collection("rest_vital")
                    .document(mReplierLink)
                    .get()
                    .addOnSuccessListener((Activity)mContext, new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot restVitalSnapshot) {
                            PictureBinder.bindCoverPicture(imageReplyBy, restVitalSnapshot);
                        }
                    });
        }

    }

    @Override
    public void setReplyByAvatarOnClickListener() {
        if(mReplierLink.equals(mAuth.getCurrentUser().getUid())) {
            return;
        }
        imageReplyBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(isReplierAPerson()){
                    Log.i("clicked", "replier(person) avatar from CD reply");
                    intent = new Intent(mContext, PersonDetail.class);
                    intent.putExtra("personLink", mReplierLink);
                } else{
                    Log.i("clicked", "replier(restaurant) avatar from CD reply");
                    intent = new Intent(mContext, RestDetail.class);
                    intent.putExtra("restaurantLink", mReplierLink);
                }
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNameReplyBy() {
        nameReplyByTV.setText(mReplierName);
    }

    @Override
    public void setNameReplyByOnClickListener() {
        if(mReplierLink.equals(mAuth.getCurrentUser().getUid())){
            return;
        }
        nameReplyByTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(isReplierAPerson()){
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
        Timestamp ts = mReplySnapshot.getTimestamp("ts");
        if(ts==null) return;
        String dateOrTimeString = DateTimeExtractor.getDateOrTimeString(ts);
        replyTimeTV.setText(dateOrTimeString);
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
        replyingToTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> replyTo = (Map<String, Object>) mReplySnapshot.get("replyTo");
                if(replyTo != null){
                    String link = (String) replyTo.get("l");
                    Long type = (Long) replyTo.get("t");
                    Intent intent;
                    if(type == AccountTypes.PERSON){
                        Log.i("clicked", "replying to(person) from CD");
                        intent = new Intent(mContext, PersonDetail.class);
                        intent.putExtra("personLink", link);
                    }else{
                        Log.i("clicked", "replying to(restaurant) from CD");
                        intent = new Intent(mContext, RestDetail.class);
                        intent.putExtra("restaurantLink", link);
                    }
//            mContext.startActivity(intent);
                }
            }
        });
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
        numberOfLikesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "no of likes");
                ArrayList<String> likes = (ArrayList<String>) mReplySnapshot.get("l");
                boolean isLikeFilled = likeReply.getDrawable().getConstantState().
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
                intent.putExtra("commentLOrReplyLink", mReplyLink);
                intent.putStringArrayListExtra("personsList", likes);
                mContext.startActivity(intent);
            }
        });
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

                Map<String, Object> replyingTo = new HashMap<>();
                replyingTo.put("n", mReplierName);
                replyingTo.put("l", mReplierLink);
                if(isReplierAPerson()){
                    replyingTo.put("t", AccountTypes.PERSON);
                }else{
                    replyingTo.put("t", AccountTypes.RESTAURANT);
                }

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
                commentIntentExtra.setReplyingTo(replyingTo);

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
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, mPostLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, mCommentLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_REPLY_LINK, mReplyLink);
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        if(isCommentInRF()){
            notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_LIKE_REPLY_RF);
        } else{
            notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_LIKE_REPLY);
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

    private boolean isReplierAPerson(){
        Long commenterType = mReplySnapshot.getLong("w.t");
        if(commenterType != null){
            return commenterType == AccountTypes.PERSON;
        }
        return true;
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

    private void refreshHolder(){
        Log.i("refreshing", "commentdetailreplyholder");
        imageReplyBy.setImageResource(R.drawable.ltgray);
        nameReplyByTV.setText("");
        replyTimeTV.setText("");
        replyTV.setText("");
        replyingToTV.setText("");
        numberOfLikesTV.setText("0");
        numberOfRepliesTV.setText("0");
    }
}
