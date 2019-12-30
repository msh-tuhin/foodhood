package models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.Map;

import myapp.utils.FirestoreFieldNames;

@IgnoreExtraProperties
public class NotificationModel {

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_TYPE)
    private int t;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_CREATOR_MAP)
    private Map<String, Object> w;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_TIMESTAMP)
    private Timestamp ts;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_POST_LINK)
    private String postLink;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_POST_OWNER_NAME)
    private String postOwnerName;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_COMMENT_OWNER_NAME)
    private String commentOwnerName;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_REPLY_OWNER_NAME)
    private String replyOwnerName;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK)
    private String commentLink;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_REPLY_LINK)
    private String replyLink;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_OLD_REPLY_LINK)
    private String oldReplyLink;
    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_NEW_REPLY_LINK)
    private String newReplyLink;

    public NotificationModel(){

    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_TYPE)
    public int getT() {
        return t;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_TYPE)
    public void setT(int t) {
        this.t = t;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_CREATOR_MAP)
    public Map<String, Object> getW() {
        return w;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_CREATOR_MAP)
    public void setW(Map<String, Object> w) {
        this.w = w;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_TIMESTAMP)
    public Timestamp getTs() {
        return ts;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_TIMESTAMP)
    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_POST_LINK)
    public String getPostLink() {
        return postLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_POST_LINK)
    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK)
    public String getCommentLink() {
        return commentLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_COMMENT_LINK)
    public void setCommentLink(String commentLink) {
        this.commentLink = commentLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_REPLY_LINK)
    public String getReplyLink() {
        return replyLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_REPLY_LINK)
    public void setReplyLink(String replyLink) {
        this.replyLink = replyLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_OLD_REPLY_LINK)
    public String getOldReplyLink() {
        return oldReplyLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_OLD_REPLY_LINK)
    public void setOldReplyLink(String oldReplyLink) {
        this.oldReplyLink = oldReplyLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_NEW_REPLY_LINK)
    public String getNewReplyLink() {
        return newReplyLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_NEW_REPLY_LINK)
    public void setNewReplyLink(String newReplyLink) {
        this.newReplyLink = newReplyLink;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_POST_OWNER_NAME)
    public String getPostOwnerName() {
        return postOwnerName;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_POST_OWNER_NAME)
    public void setPostOwnerName(String postOwnerName) {
        this.postOwnerName = postOwnerName;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_COMMENT_OWNER_NAME)
    public String getCommentOwnerName() {
        return commentOwnerName;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_COMMENT_OWNER_NAME)
    public void setCommentOwnerName(String commentOwnerName) {
        this.commentOwnerName = commentOwnerName;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_REPLY_OWNER_NAME)
    public String getReplyOwnerName() {
        return replyOwnerName;
    }

    @PropertyName(FirestoreFieldNames.NOTIFICATIONS_REPLY_OWNER_NAME)
    public void setReplyOwnerName(String replyOwnerName) {
        this.replyOwnerName = replyOwnerName;
    }
}
