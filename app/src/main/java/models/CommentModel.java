package models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class CommentModel {

    private List<String> r;
    private List<String> rb;
    private String te;
    private Map<String, Object> w;
    // postlink for comment, reply, reply to reply
    private String wh;
    private List<String> l;

    // private Boolean isReply;
    private String replyTo;

    // commentlink for reply, empty for comment
    private String comLink;
    // replylink for reply to reply, empty for others
    private String replyLink;
    private  int type;

    public CommentModel(){

    }

    public CommentModel(String comment, String postLink){
        this.te = comment;
        this.wh = postLink;
        this.l = new ArrayList<>();
        this.r = new ArrayList<>();
        this.rb = new ArrayList<>();
    }

    public List<String> getR() {
        return r;
    }

    public void setR(List<String> r) {
        this.r = r;
    }

    public List<String> getRb() {
        return rb;
    }

    public void setRb(List<String> rb) {
        this.rb = rb;
    }

    public String getTe() {
        return te;
    }

    public void setTe(String te) {
        this.te = te;
    }

    public Map<String, Object> getW() {
        return w;
    }

    public void setW(Map<String, Object> w) {
        this.w = w;
    }

    public String getWh() {
        return wh;
    }

    public void setWh(String wh) {
        this.wh = wh;
    }

    public List<String> getL() {
        return l;
    }

    public void setL(List<String> l) {
        this.l = l;
    }

//    might need later
    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getComLink() {
        return comLink;
    }

    public void setComLink(String comLink) {
        this.comLink = comLink;
    }

    public String getReplyLink() {
        return replyLink;
    }

    public void setReplyLink(String replyLink) {
        this.replyLink = replyLink;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
