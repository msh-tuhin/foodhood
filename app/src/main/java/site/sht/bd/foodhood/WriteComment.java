package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import models.CommentModel;
import myapp.utils.AccountTypes;
import myapp.utils.CommentIntentExtra;
import myapp.utils.CommentTypes;
import myapp.utils.EntryPoints;
import myapp.utils.FirestoreFieldNames;
import myapp.utils.NotificationTypes;
import myapp.utils.OrphanUtilityMethods;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import site.sht.bd.foodhood.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/*
    has multiple entry points
    1) home_feed (comment on post)
    2) home_feed (reply on comment)
    3) full_post (comment on post)
    4) full_post (reply on comment)
    5) comment_detail (reply on comment)
    6) comment_detail (reply on reply)
    7) home_feed (reply on reply)
 */
public class WriteComment extends AppCompatActivity {

    private boolean mForPerson;
    private String mPostLink;
    private int mEntryPoint;
    private CommentIntentExtra mCommentIntentExtra;

    ConstraintLayout commentLayout;
    ImageView cancel;
    Button done;
    EditText commentEditext;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        setmForPerson();
        mCommentIntentExtra = (CommentIntentExtra)getIntent()
                .getSerializableExtra("comment_extra");
        // expected from all entry points
        mPostLink =mCommentIntentExtra.getPostLink();
        mEntryPoint =mCommentIntentExtra.getEntryPoint();

        commentLayout = findViewById(R.id.comment_layout);
        cancel = findViewById(R.id.cancel);
        done = findViewById(R.id.done);
        commentEditext = findViewById(R.id.comment_editText);
        progressBar = findViewById(R.id.progressBar);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentText = commentEditext.getText().toString().trim();
                if(TextUtils.isEmpty(commentText)){
                    commentEditext.setError("Comment is Empty");
                }else{
                    cancel.setFocusable(false);
                    cancel.setClickable(false);
                    done.setFocusable(false);
                    done.setClickable(false);
                    commentLayout.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    commentOrReply(commentText);
               }
            }
        });

    }

    private void setmForPerson(){
        int accountType = OrphanUtilityMethods.getAccountType(this);
        mForPerson = accountType == AccountTypes.PERSON;
        if(mForPerson) Log.i("account", "for person");
        else Log.i("account", "for restaurant");
    }

    private void commentOrReply(final String commentText){
        CommentModel commentModel = getNewCommentModel(commentText,
                mPostLink, mEntryPoint);
        Task<DocumentReference> addedCommentRef = addCommentToDB(commentModel);
        addedCommentRef.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.i("new_comment", documentReference.getId());
                String newCommentLink = documentReference.getId();
                useNewlyAddedComment(commentText, newCommentLink);
            }
        });
    }

    private Task<DocumentReference> addCommentToDB(CommentModel commentModel){
        return FirebaseFirestore.getInstance()
                .collection("comments")
                .add(commentModel);
    }

    private void useNewlyAddedComment(String commentText, String newCommentLink){
        switch (mEntryPoint){
            case EntryPoints.COMMENT_ON_HOME_POST:
                commentOnPostFromHome(commentText, newCommentLink);
                break;
            case EntryPoints.COMMENT_ON_HOME_RF:
                commentOnRFFromHome(commentText, newCommentLink);
                break;
            case EntryPoints.COMMENT_ON_FULL_POST:
                commentOnPostFromFullPost(commentText, newCommentLink);
                break;
            case EntryPoints.COMMENT_ON_FULL_RF:
                commentOnRFFromFullRF(commentText, newCommentLink);
                break;
            case EntryPoints.R2C_FROM_HOME_POST:
            case EntryPoints.R2C_FROM_FULL_POST:
                replyToCommentFromPost(commentText, newCommentLink);
                break;
            case EntryPoints.R2C_FROM_HOME_RF:
            case EntryPoints.R2C_FROM_FULL_RF:
                replyToCommentFromRF(commentText, newCommentLink);
                break;
            case EntryPoints.R2R_FROM_HOME_POST:
                replyToReplyFromHomePost(newCommentLink);
                break;
            case EntryPoints.R2R_FROM_HOME_RF:
                replyToReplyFromHomeRF(newCommentLink);
                break;
            case EntryPoints.R2C_FROM_CD_POST:
                replyToCommentFromCDPost(commentText, newCommentLink);
                break;
            case EntryPoints.R2C_FROM_CD_RF:
                replyToCommentFromCDRF(commentText, newCommentLink);
                break;
            case EntryPoints.R2R_FROM_CD_POST:
                replyToReplyFromCDPost(newCommentLink);
                break;
            case EntryPoints.R2R_FROM_CD_RF:
                replyToReplyFromCDRF(newCommentLink);
                break;
        }
    }

    private void commentOnPostFromHome(String commentText, String newCommentLink){
        addToPost(mPostLink, newCommentLink);
        addNewCommentActivity(mPostLink, newCommentLink, commentText);
        mCommentIntentExtra.setCommentLink(newCommentLink);
        Intent intent = new Intent(WriteComment.this, FullPost.class);
        intent.putExtra("comment_extra",mCommentIntentExtra);
        startActivity(intent);
        finish();
    }

    private void commentOnRFFromHome(String commentText, String newCommentLink){
        if(mForPerson){
            addToRestFeed(mPostLink, newCommentLink);
            addNewCommentToRFActivity(mPostLink, newCommentLink, commentText);
        }else{
            addToRestFeedOnlyCommentLink(mPostLink, newCommentLink);
            sendCommentToRFByRFNotification(mPostLink, newCommentLink);
        }
        mCommentIntentExtra.setCommentLink(newCommentLink);
        Intent intent = new Intent(WriteComment.this, FullRestFeed.class);
        intent.putExtra("comment_extra",mCommentIntentExtra);
        startActivity(intent);
        finish();
    }

    private void commentOnPostFromFullPost(String commentText, String newCommentLink){
        addToPost(mPostLink, newCommentLink);
        addNewCommentActivity(mPostLink, newCommentLink, commentText);
        Intent intent = new Intent();
        intent.putExtra("commentLink",newCommentLink);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void commentOnRFFromFullRF(String commentText, String newCommentLink){
        if(mForPerson){
            addToRestFeed(mPostLink, newCommentLink);
            addNewCommentToRFActivity(mPostLink, newCommentLink, commentText);
        }else{
            addToRestFeedOnlyCommentLink(mPostLink, newCommentLink);
            sendCommentToRFByRFNotification(mPostLink, newCommentLink);
        }
        Intent intent = new Intent();
        intent.putExtra("commentLink",newCommentLink);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void replyToCommentFromPost(String commentText, String newCommentLink){
        String commentLink =mCommentIntentExtra.getCommentLink();
        addToComment(commentLink, newCommentLink);
        Map<String, Object> commentMap =mCommentIntentExtra.getCommentMap();
        addNewReplyActivity(mPostLink, newCommentLink, commentText, commentMap);
        mCommentIntentExtra.setReplyLink(newCommentLink);
        Intent intent = new Intent(WriteComment.this, CommentDetail.class);
        intent.putExtra("comment_extra", mCommentIntentExtra);
        startActivity(intent);
        finish();
    }

    private void replyToCommentFromRF(String commentText, String newCommentLink){
        String commentLink =mCommentIntentExtra.getCommentLink();
        if(mForPerson){
            addToComment(commentLink, newCommentLink);
            Map<String, Object> commentMap =mCommentIntentExtra.getCommentMap();
            addNewReplyToRFActivity(mPostLink, newCommentLink, commentText, commentMap);
        }else{
            addToCommentOnlyReplyLink(commentLink, newCommentLink);
            sendReplyToCommentByRFNotification(mPostLink, commentLink, newCommentLink);
        }
        mCommentIntentExtra.setReplyLink(newCommentLink);
        Intent intent = new Intent(WriteComment.this, CommentDetail.class);
        intent.putExtra("comment_extra", mCommentIntentExtra);
        startActivity(intent);
        finish();
    }

    private void replyToCommentFromCDPost(String commentText, String newCommentLink){
        String commentLink = mCommentIntentExtra.getCommentLink();
        addToComment(commentLink, newCommentLink);
        Map<String, Object> commentMap = mCommentIntentExtra.getCommentMap();
        addNewReplyActivity(mPostLink, newCommentLink, commentText, commentMap);
        Intent intent = new Intent();
        intent.putExtra("replyLink", newCommentLink);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void replyToCommentFromCDRF(String commentText, String newCommentLink){
        String commentLink = mCommentIntentExtra.getCommentLink();
        if(mForPerson){
            addToComment(commentLink, newCommentLink);
            Map<String, Object> commentMap =mCommentIntentExtra.getCommentMap();
            addNewReplyToRFActivity(mPostLink, newCommentLink, commentText, commentMap);
        }else{
            addToCommentOnlyReplyLink(commentLink, newCommentLink);
            sendReplyToCommentByRFNotification(mPostLink, commentLink, newCommentLink);
        }
        Intent intent = new Intent();
        intent.putExtra("replyLink", newCommentLink);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void replyToReplyFromHomePost(String newCommentLink){
        String commentLink = mCommentIntentExtra.getCommentLink();
        String replyLink = mCommentIntentExtra.getReplyLink();
        addToComment(commentLink, newCommentLink);
        addToReply(replyLink, newCommentLink);
        sendReplyToReplyNotification(mPostLink, commentLink,
                replyLink, newCommentLink, "sendReplyToReplyNotification");
        mCommentIntentExtra.setReplyToReplyLink(newCommentLink);
        Intent intent = new Intent(WriteComment.this, CommentDetail.class);
        intent.putExtra("comment_extra", mCommentIntentExtra);
        startActivity(intent);
        finish();
    }

    private void replyToReplyFromHomeRF(String newCommentLink){
        String commentLink = mCommentIntentExtra.getCommentLink();
        String replyLink = mCommentIntentExtra.getReplyLink();
        addToComment(commentLink, newCommentLink);
        addToReply(replyLink, newCommentLink);
        sendReplyToReplyNotification(mPostLink, commentLink,
                replyLink, newCommentLink, "sendReplyToReplyNotificationRF");
        mCommentIntentExtra.setReplyToReplyLink(newCommentLink);
        Intent intent = new Intent(WriteComment.this, CommentDetail.class);
        intent.putExtra("comment_extra", mCommentIntentExtra);
        startActivity(intent);
        finish();
    }

    private void replyToReplyFromCDPost(String newCommentLink){
        String commentLink = mCommentIntentExtra.getCommentLink();
        String replyLink = mCommentIntentExtra.getReplyLink();
        addToComment(commentLink, newCommentLink);
        addToReply(replyLink, newCommentLink);
        sendReplyToReplyNotification(mPostLink, commentLink,
                replyLink, newCommentLink, "sendReplyToReplyNotification");
        int newReplyPosition = mCommentIntentExtra.getNewReplyPosition();
        Intent intent = new Intent();
        intent.putExtra("replyToReplyLink", newCommentLink);
        intent.putExtra("newReplyPosition", newReplyPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void replyToReplyFromCDRF(String newCommentLink){
        String commentLink = mCommentIntentExtra.getCommentLink();
        String replyLink = mCommentIntentExtra.getReplyLink();
        if(mForPerson){
            addToComment(commentLink, newCommentLink);
            addToReply(replyLink, newCommentLink);
            sendReplyToReplyNotification(mPostLink, commentLink,
                    replyLink, newCommentLink, "sendReplyToReplyNotificationRF");
        }else{
            addToCommentOnlyReplyLink(commentLink, newCommentLink);
            addToReplyOnlyReplyLink(replyLink, newCommentLink);
            sendReplyToReplyNotification(mPostLink, commentLink,
                    replyLink, newCommentLink, "sendReplyToReplyByRFNotification");
        }
        int newReplyPosition = mCommentIntentExtra.getNewReplyPosition();
        Intent intent = new Intent();
        intent.putExtra("replyToReplyLink", newCommentLink);
        intent.putExtra("newReplyPosition", newReplyPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    private CommentModel getNewCommentModel(String comment,
                                            String postLink,
                                            int entryPoint){
        // return an appropriate CommentModel(comment/ reply to comment/ reply to reply)
        // based on the entryPoint
        CommentModel commentModel = new CommentModel(comment, postLink);
        commentModel.setTs(new Timestamp(new Date()));
        Map<String, Object> who = new HashMap<>();
        who.put("l", FirebaseAuth.getInstance().getCurrentUser().getUid());
        who.put("n", OrphanUtilityMethods.getCurrentUserName(this));
        if(mForPerson)  who.put("t", AccountTypes.PERSON);
        else who.put("t", AccountTypes.RESTAURANT);
        commentModel.setW(who);
        switch(entryPoint){
            case EntryPoints.R2C_FROM_HOME_POST:
            case EntryPoints.R2C_FROM_HOME_RF:
            case EntryPoints.R2C_FROM_FULL_POST:
            case EntryPoints.R2C_FROM_FULL_RF:
            case EntryPoints.R2C_FROM_CD_POST:
            case EntryPoints.R2C_FROM_CD_RF:
                commentModel.setType(CommentTypes.REPLY);
                commentModel.setComLink(mCommentIntentExtra.getCommentLink());
                commentModel.setReplyLink("");
                commentModel.setReplyTo(mCommentIntentExtra.getReplyingTo());
                break;
            case EntryPoints.R2R_FROM_CD_POST:
            case EntryPoints.R2R_FROM_CD_RF:
            case EntryPoints.R2R_FROM_HOME_POST:
            case EntryPoints.R2R_FROM_HOME_RF:
                commentModel.setType(CommentTypes.REPLY_TO_REPLY);
                commentModel.setComLink(mCommentIntentExtra.getCommentLink());
                commentModel.setReplyLink(mCommentIntentExtra.getReplyLink());
                commentModel.setReplyTo(mCommentIntentExtra.getReplyingTo());
                break;
            default:
                // means its a comment
                commentModel.setType(CommentTypes.COMMENT);
                commentModel.setComLink("");
                commentModel.setReplyLink("");
        }

        return commentModel;
    }

    private void addToPost(String postLink, String commentLink){
        Log.i("postlink", postLink);
        Log.i("commentlink", commentLink);

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("posts").document(postLink)
                .update("coms", FieldValue.arrayUnion(commentLink),
                        "cb", FieldValue.arrayUnion(currentUserLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addToComment(String commentLink, String replyLink){
        Log.i("commentlink", commentLink);
        Log.i("replylink", replyLink);

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("comments").document(commentLink)
                .update("r", FieldValue.arrayUnion(replyLink),
                        "rb", FieldValue.arrayUnion(currentUserLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addToCommentOnlyReplyLink(String commentLink, String replyLink){
        Log.i("commentlink", commentLink);
        Log.i("replylink", replyLink);

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("comments").document(commentLink)
                .update("r", FieldValue.arrayUnion(replyLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addToReply(String replyLink, String replyToReplyLink){
        Log.i("replylink", replyLink);
        Log.i("replytoreplylink", replyToReplyLink);

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("comments").document(replyLink)
                .update("r", FieldValue.arrayUnion(replyToReplyLink),
                        "rb", FieldValue.arrayUnion(currentUserLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addToReplyOnlyReplyLink(String replyLink, String replyToReplyLink){
        Log.i("replylink", replyLink);
        Log.i("replytoreplylink", replyToReplyLink);

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("comments").document(replyLink)
                .update("r", FieldValue.arrayUnion(replyToReplyLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addToRestFeed(String postLink, String commentLink){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("rest_feed").document(postLink)
                .update("coms", FieldValue.arrayUnion(commentLink),
                        "cb", FieldValue.arrayUnion(currentUserLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addToRestFeedOnlyCommentLink(String postLink, String commentLink){
        FirebaseFirestore.getInstance().collection("rest_feed").document(postLink)
                .update("coms", FieldValue.arrayUnion(commentLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addNewCommentActivity(String postLink,
                                       String commentLink,
                                       String commentText){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> newActivity = new HashMap<>();
        Map<String, Object> who = new HashMap<>();
        Map<String, Object> commentMap = new HashMap<>();

        who.put("l", currentUserLink);

        commentMap.put("l", commentLink);
        commentMap.put("text", commentText);

        newActivity.put("t", 5);
        newActivity.put("w", who);
        newActivity.put("wh", postLink);
        newActivity.put("com", commentMap);

        FirebaseFirestore.getInstance().collection("activities").add(newActivity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                    }
                });
    }

    private void addNewCommentToRFActivity(String postLink,
                                           String commentLink,
                                           String commentText){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> newActivity = new HashMap<>();
        Map<String, Object> who = new HashMap<>();
        Map<String, Object> commentMap = new HashMap<>();

        who.put("l", currentUserLink);

        commentMap.put("l", commentLink);
        commentMap.put("text", commentText);

        newActivity.put("t", 6);
        newActivity.put("w", who);
        newActivity.put("wh", postLink);
        newActivity.put("com", commentMap);

        FirebaseFirestore.getInstance().collection("activities").add(newActivity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                    }
                });
    }

    private void addNewReplyActivity(String postLink,
                                     String replyLink,
                                     String replyText,
                                     Map<String, Object> commentMap){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> newActivity = new HashMap<>();
        Map<String, Object> who = new HashMap<>();
        Map<String, Object> replytMap = new HashMap<>();

        who.put("l", currentUserLink);

        replytMap.put("l", replyLink);
        replytMap.put("text", replyText);

        newActivity.put("t", 7);
        newActivity.put("w", who);
        newActivity.put("wh", postLink);
        newActivity.put("com", commentMap);
        newActivity.put("rep", replytMap);

        FirebaseFirestore.getInstance().collection("activities").add(newActivity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                    }
                });
    }

    private void addNewReplyToRFActivity(String postLink,
                                         String replyLink,
                                         String replyText,
                                         Map<String, Object> commentMap){
        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> newActivity = new HashMap<>();
        Map<String, Object> who = new HashMap<>();
        Map<String, Object> replytMap = new HashMap<>();

        who.put("l", currentUserLink);

        replytMap.put("l", replyLink);
        replytMap.put("text", replyText);

        newActivity.put("t", 8);
        newActivity.put("w", who);
        newActivity.put("wh", postLink);
        newActivity.put("com", commentMap);
        newActivity.put("rep", replytMap);

        FirebaseFirestore.getInstance().collection("activities").add(newActivity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("new_activity", documentReference.getId());
                    }
                });
    }

    private void sendReplyToReplyNotification(String postLink,
                                              String commentLink,
                                              String parentReplyLink,
                                              String newReplyLink,
                                              String cloudFunctionName){

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        Map<String, Object> notification = new HashMap<>();
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, postLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, commentLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_OLD_REPLY_LINK, parentReplyLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_NEW_REPLY_LINK, newReplyLink);

        FirebaseFunctions.getInstance("asia-east2").getHttpsCallable(cloudFunctionName)
                .call(notification).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.i("func_call", "returned successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("func_call", e.getMessage());
            }
        });
    }

    private void sendCommentToRFByRFNotification(String postLink,
                                                 String commentLink){

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        Map<String, Object> notification = new HashMap<>();
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, postLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, commentLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_COMMENT_ALSO_RF);

        FirebaseFunctions.getInstance("asia-east2").getHttpsCallable("sendCommentByRFNotification")
                .call(notification).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.i("func_call", "returned successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("func_call", e.getMessage());
            }
        });
    }

    private void sendReplyToCommentByRFNotification(String postLink,
                                                    String commentLink,
                                                    String replyLink){

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        Map<String, Object> notification = new HashMap<>();
        notification.put(FirestoreFieldNames.ACTIVITIES_CREATOR_MAP, who);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_POST_LINK, postLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK, commentLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_REPLY_LINK, replyLink);
        notification.put(FirestoreFieldNames.NOTIFICATIONS_TYPE, NotificationTypes.NOTIF_COMMENT_ALSO_RF);

        FirebaseFunctions.getInstance("asia-east2").getHttpsCallable("sendReplyToCommentByRFNotification")
                .call(notification).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.i("func_call", "returned successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("func_call", e.getMessage());
            }
        });
    }
}
