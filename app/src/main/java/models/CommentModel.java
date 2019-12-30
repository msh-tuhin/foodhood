package models;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import myapp.utils.FirestoreFieldNames;

@IgnoreExtraProperties
public class CommentModel {

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLIES)
    private List<String> r;
    @PropertyName(FirestoreFieldNames.COMMENTS_REPLIES_BY)
    private List<String> rb;
    @PropertyName(FirestoreFieldNames.COMMENTS_TEXT)
    private String te;
    @PropertyName(FirestoreFieldNames.COMMENTS_CREATOR_MAP)
    private Map<String, Object> w;
    // postlink for comment, reply, reply to reply
    @PropertyName(FirestoreFieldNames.COMMENTS_PARENT_POST_RF)
    private String wh;
    @PropertyName(FirestoreFieldNames.COMMENTS_LIKES)
    private List<String> l;

    // private Boolean isReply;
    @PropertyName(FirestoreFieldNames.COMMENTS_REPLY_TO)
    private Map<String, Object> replyTo;

    // commentlink for reply, empty for comment
    @PropertyName(FirestoreFieldNames.COMMENTS_LINK)
    private String comLink;
    // replylink for reply to reply, empty for others
    @PropertyName(FirestoreFieldNames.COMMENTS_REPLY_LINK)
    private String replyLink;
    @PropertyName(FirestoreFieldNames.COMMENTS_TYPE)
    private  int type;
    @PropertyName(FirestoreFieldNames.COMMENTS_TIMESTAMP)
    private Timestamp ts;

    public CommentModel(){

    }

    public CommentModel(String comment, String postLink){
        this.te = comment;
        this.wh = postLink;
        this.l = new ArrayList<>();
        this.r = new ArrayList<>();
        this.rb = new ArrayList<>();
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLIES)
    public List<String> getR() {
        return r;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLIES)
    public void setR(List<String> r) {
        this.r = r;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLIES_BY)
    public List<String> getRb() {
        return rb;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLIES_BY)
    public void setRb(List<String> rb) {
        this.rb = rb;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_TEXT)
    public String getTe() {
        return te;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_TEXT)
    public void setTe(String te) {
        this.te = te;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_CREATOR_MAP)
    public Map<String, Object> getW() {
        return w;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_CREATOR_MAP)
    public void setW(Map<String, Object> w) {
        this.w = w;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_PARENT_POST_RF)
    public String getWh() {
        return wh;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_PARENT_POST_RF)
    public void setWh(String wh) {
        this.wh = wh;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_LIKES)
    public List<String> getL() {
        return l;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_LIKES)
    public void setL(List<String> l) {
        this.l = l;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLY_TO)
    public Map<String, Object> getReplyTo() {
        return replyTo;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLY_TO)
    public void setReplyTo(Map<String, Object> replyTo) {
        this.replyTo = replyTo;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_LINK)
    public String getComLink() {
        return comLink;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_LINK)
    public void setComLink(String comLink) {
        this.comLink = comLink;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLY_LINK)
    public String getReplyLink() {
        return replyLink;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_REPLY_LINK)
    public void setReplyLink(String replyLink) {
        this.replyLink = replyLink;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_TYPE)
    public int getType() {
        return type;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_TYPE)
    public void setType(int type) {
        this.type = type;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_TIMESTAMP)
    public Timestamp getTs() {
        return ts;
    }

    @PropertyName(FirestoreFieldNames.COMMENTS_TIMESTAMP)
    public void setTs(Timestamp ts) {
        this.ts = ts;
    }
}
