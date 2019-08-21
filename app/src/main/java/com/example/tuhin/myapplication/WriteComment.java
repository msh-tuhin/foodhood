package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;
import models.CommentModel;
import myapp.utils.CommentTypes;
import myapp.utils.EntryPoints;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/*
    has multiple entry points
    1) home_feed (comment on post)   (EntryPoints.Home_page)  * these names are misleading, got to change*
    2) home_feed (reply on comment)  (EntryPoints.REPLY_TO_COMMENT)
    3) full_post (comment on post)   (EntryPoints.FULL_POST)
    4) full_post (reply on comment)  (EntryPoints.REPLY_TO_COMMENT)
    5) comment_detail (reply on comment) (EntryPoints.REPLY_TO_COMMENT_CD)
    6) comment_detail (reply on reply) (EntryPoints.REPLY_TO_REPLY_CD)
    7) home_feed (reply on reply)   (EntryPoints.REPLY_TO_REPLY_HOME)
 */
public class WriteComment extends AppCompatActivity {

    ImageView cancel;
    Button done;
    EditText commentEditext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        // expected from all entry points
        final String postLink = getIntent().getStringExtra("postLink");
        final int entryPoint = getIntent().getIntExtra("entry_point", EntryPoints.HOME_PAGE);

        cancel = findViewById(R.id.cancel);
        done = findViewById(R.id.done);
        commentEditext = findViewById(R.id.comment_editText);

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

                    CommentModel commentModel = getNewCommentModel(commentText, postLink, entryPoint);
                    // TODO supply Activity to onSuccessListener
                    // TODO decide if supply Activity to onSuccessListener or not
                    FirebaseFirestore.getInstance().collection("comments").add(commentModel)
                            .addOnSuccessListener(WriteComment.this, new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("new_comment", documentReference.getId());

                                    switch (entryPoint){
                                        case EntryPoints.HOME_PAGE:
                                            // user comments
                                            addToPost(postLink, documentReference.getId());
                                            addNewCommentActivity(postLink, documentReference.getId(), commentText);
                                            Intent intent = new Intent(WriteComment.this, FullPost.class);
                                            intent.putExtra("postLink", postLink);
                                            intent.putExtra("entry_point", EntryPoints.WRITE_COMMENT_PAGE);
                                            intent.putExtra("commentLink", documentReference.getId());
                                            startActivity(intent);
                                            break;
                                        case EntryPoints.FULL_POST_PAGE:
                                            // user comments
                                            addToPost(postLink, documentReference.getId());
                                            addNewCommentActivity(postLink, documentReference.getId(), commentText);
                                            Intent intentFullPost = new Intent();
                                            intentFullPost.putExtra("commentLink",documentReference.getId());
                                            setResult(RESULT_OK, intentFullPost);
                                            finish();
                                            break;
                                        case EntryPoints.REPLY_TO_COMMENT:
                                            addToComment(getIntent().getStringExtra("commentLink"), documentReference.getId());
                                            Map<String, Object> commentMap = (HashMap)getIntent().getSerializableExtra("commentMap");
                                            addNewReplyActivity(postLink, documentReference.getId(), commentText, commentMap);
                                            Intent intentCommentReply = new Intent(WriteComment.this, CommentDetail.class);
                                            intentCommentReply.putExtra("commentLink", getIntent().getStringExtra("commentLink"));
                                            intentCommentReply.putExtra("postLink", postLink);
                                            intentCommentReply.putExtra("replyLink", documentReference.getId());
                                            intentCommentReply.putExtra("entry_point", EntryPoints.REPLY_TO_COMMENT);
                                            startActivity(intentCommentReply);
                                            break;
                                        case EntryPoints.REPLY_TO_COMMENT_CD:
                                            addToComment(getIntent().getStringExtra("commentLink"), documentReference.getId());
                                            Map<String, Object> commentMap2 = (HashMap)getIntent().getSerializableExtra("commentMap");
                                            addNewReplyActivity(postLink, documentReference.getId(), commentText, commentMap2);
                                            Intent intentCommentDetail = new Intent();
                                            intentCommentDetail.putExtra("replyLink",documentReference.getId());
                                            setResult(RESULT_OK, intentCommentDetail);
                                            finish();
                                            break;
                                        case EntryPoints.REPLY_TO_REPLY_CD:
                                            addToComment(getIntent().getStringExtra("commentLink"), documentReference.getId());
                                            addToReply(getIntent().getStringExtra("replyLink"), documentReference.getId());
                                            sendReplyToReplyNotification(postLink, getIntent().getStringExtra("commentLink"),
                                                    getIntent().getStringExtra("replyLink"), documentReference.getId());
                                            int newReplyPosition = getIntent().getIntExtra("newReplyPosition", 1);
                                            Intent intentReplyToReply = new Intent();
                                            intentReplyToReply.putExtra("replyLink",documentReference.getId());
                                            intentReplyToReply.putExtra("newReplyPosition", newReplyPosition);
                                            setResult(RESULT_OK, intentReplyToReply);
                                            finish();
                                            break;
                                        case EntryPoints.REPLY_TO_REPLY_HOME:
                                            addToComment(getIntent().getStringExtra("commentLink"), documentReference.getId());
                                            addToReply(getIntent().getStringExtra("replyLink"), documentReference.getId());
                                            sendReplyToReplyNotification(postLink, getIntent().getStringExtra("commentLink"),
                                                    getIntent().getStringExtra("replyLink"), documentReference.getId());
                                            Intent intentReplyToReplyHome = new Intent(WriteComment.this, CommentDetail.class);
                                            intentReplyToReplyHome.putExtra("commentLink", getIntent().getStringExtra("commentLink"));
                                            intentReplyToReplyHome.putExtra("postLink", postLink);
                                            intentReplyToReplyHome.putExtra("replyLink", getIntent().getStringExtra("replyLink"));
                                            intentReplyToReplyHome.putExtra("entry_point", EntryPoints.REPLY_TO_REPLY_HOME);
                                            intentReplyToReplyHome.putExtra("replyToReplyLink", documentReference.getId());
                                            startActivity(intentReplyToReplyHome);
                                            break;
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
               }
            }
        });

    }

    private CommentModel getNewCommentModel(String comment, String postLink, int entryPoint){
        // return an appropriate CommentModel(comment/ reply to comment/ reply to reply)
        // based on the entryPoint
        CommentModel commentModel = new CommentModel();
        commentModel.setTe(comment);
        commentModel.setL(new ArrayList<String>());
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        commentModel.setW(currentUser);
        commentModel.setWh(postLink);
        commentModel.setR(new ArrayList<String>());
        commentModel.setRb(new ArrayList<String>());
        // commentModel.setIsReply((entryPoint==EntryPoints.REPLY_TO_COMMENT) || (entryPoint==EntryPoints.REPLY_TO_COMMENT_CD));
//        if((entryPoint == EntryPoints.REPLY_TO_COMMENT) || (entryPoint == EntryPoints.REPLY_TO_COMMENT_CD) ){
//            commentModel.setComLink(getIntent().getStringExtra("commentLink"));
//        }else{
//            commentModel.setComLink("");
//        }
        switch(entryPoint){
            case EntryPoints.REPLY_TO_COMMENT:
            case EntryPoints.REPLY_TO_COMMENT_CD:
                commentModel.setType(CommentTypes.REPLY);
                commentModel.setComLink(getIntent().getStringExtra("commentLink"));
                commentModel.setReplyLink("");
                break;
            case EntryPoints.REPLY_TO_REPLY_CD:
                commentModel.setType(CommentTypes.REPLY_TO_REPLY);
                commentModel.setComLink(getIntent().getStringExtra("commentLink"));
                commentModel.setReplyLink(getIntent().getStringExtra("replyLink"));
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
                .update("coms", FieldValue.arrayUnion(commentLink), "cb", FieldValue.arrayUnion(currentUserLink))
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
                .update("r", FieldValue.arrayUnion(replyLink), "rb", FieldValue.arrayUnion(currentUserLink))
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
                .update("r", FieldValue.arrayUnion(replyToReplyLink), "rb", FieldValue.arrayUnion(currentUserLink))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                });
    }

    private void addNewCommentActivity(String postLink, String commentLink, String commentText){
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

    private void addNewReplyActivity(String postLink, String replyLink, String replyText, Map<String, Object> commentMap){
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

    private void sendReplyToReplyNotification(String postLink, String commentLink, String parentReplyLink, String newReplyLink){

        String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> who = new HashMap<>();
        who.put("l", currentUserLink);

        Map<String, Object> notification = new HashMap<>();
        notification.put("w", who);
        notification.put("postLink", postLink);
        notification.put("commentLink", commentLink);
        notification.put("oldReplyLink", parentReplyLink);
        notification.put("newReplyLink", newReplyLink);

        FirebaseFunctions.getInstance().getHttpsCallable("sendReplyToReplyNotification")
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
