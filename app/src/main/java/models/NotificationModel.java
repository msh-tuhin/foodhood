package models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public class NotificationModel {

    private int t;
    private Map<String, Object> w;
    private Timestamp ts;
    private String postLink;
    private String postOwnerName;
    private String commentOwnerName;
    private String replyOwnerName;
    private String commentLink;
    private String replyLink;
    private String oldReplyLink;
    private String newReplyLink;

    public NotificationModel(){

    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public Map<String, Object> getW() {
        return w;
    }

    public void setW(Map<String, Object> w) {
        this.w = w;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getCommentLink() {
        return commentLink;
    }

    public void setCommentLink(String commentLink) {
        this.commentLink = commentLink;
    }

    public String getReplyLink() {
        return replyLink;
    }

    public void setReplyLink(String replyLink) {
        this.replyLink = replyLink;
    }

    public String getOldReplyLink() {
        return oldReplyLink;
    }

    public void setOldReplyLink(String oldReplyLink) {
        this.oldReplyLink = oldReplyLink;
    }

    public String getNewReplyLink() {
        return newReplyLink;
    }

    public void setNewReplyLink(String newReplyLink) {
        this.newReplyLink = newReplyLink;
    }

    public String getPostOwnerName() {
        return postOwnerName;
    }

    public void setPostOwnerName(String postOwnerName) {
        this.postOwnerName = postOwnerName;
    }

    public String getCommentOwnerName() {
        return commentOwnerName;
    }

    public void setCommentOwnerName(String commentOwnerName) {
        this.commentOwnerName = commentOwnerName;
    }

    public String getReplyOwnerName() {
        return replyOwnerName;
    }

    public void setReplyOwnerName(String replyOwnerName) {
        this.replyOwnerName = replyOwnerName;
    }
}
