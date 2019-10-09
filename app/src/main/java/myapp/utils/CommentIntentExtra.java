package myapp.utils;

import java.io.Serializable;
import java.util.Map;

public class CommentIntentExtra implements Serializable {
    private int entryPoint;
    private String postLink;
    private String commentLink;
    private String replyLink;
    private String replyToReplyLink;
    private int newReplyPosition;
    private Map<String, Object> commentMap;

    public CommentIntentExtra(){

    }

    public CommentIntentExtra(int entryPoint,
                              String postLink,
                              String commentLink,
                              String replyLink,
                              String replyToReplyLink,
                              int newReplyPosition,
                              Map<String, Object> commentMap) {
        this.entryPoint = entryPoint;
        this.postLink = postLink;
        this.commentLink = commentLink;
        this.replyLink = replyLink;
        this.replyToReplyLink = replyToReplyLink;
        this.newReplyPosition = newReplyPosition;
        this.commentMap = commentMap;
    }

    public int getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(int entryPoint) {
        this.entryPoint = entryPoint;
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

    public String getReplyToReplyLink() {
        return replyToReplyLink;
    }

    public void setReplyToReplyLink(String replyToReplyLink) {
        this.replyToReplyLink = replyToReplyLink;
    }

    public int getNewReplyPosition() {
        return newReplyPosition;
    }

    public void setNewReplyPosition(int newReplyPosition) {
        this.newReplyPosition = newReplyPosition;
    }

    public Map<String, Object> getCommentMap() {
        return commentMap;
    }

    public void setCommentMap(Map<String, Object> commentMap) {
        this.commentMap = commentMap;
    }
}
