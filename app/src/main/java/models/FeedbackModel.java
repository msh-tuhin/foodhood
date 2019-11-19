package models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.Map;

@IgnoreExtraProperties
public class FeedbackModel {

    @PropertyName("hre")
    private boolean hasReview;

    @PropertyName("r")
    private float rating;

    @PropertyName("re")
    private String review;

    @PropertyName("t")
    private int type;

    @PropertyName("w")
    private Map<String, String> who;

    @PropertyName("wh")
    private String where;

    @PropertyName("ts")
    public Timestamp timestamp;

    public FeedbackModel(){

    }

    @PropertyName("hre")
    public boolean getHasReview() {
        return hasReview;
    }

    @PropertyName("hre")
    public void setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
    }

    @PropertyName("r")
    public float getRating() {
        return rating;
    }

    @PropertyName("r")
    public void setRating(float rating) {
        this.rating = rating;
    }

    @PropertyName("re")
    public String getReview() {
        return review;
    }

    @PropertyName("re")
    public void setReview(String review) {
        this.review = review;
    }

    @PropertyName("t")
    public int getType() {
        return type;
    }

    @PropertyName("t")
    public void setType(int type) {
        this.type = type;
    }

    @PropertyName("w")
    public Map<String, String> getWho() {
        return who;
    }

    @PropertyName("w")
    public void setWho(Map<String, String> who) {
        this.who = who;
    }

    @PropertyName("wh")
    public String getWhere() {
        return where;
    }

    @PropertyName("wh")
    public void setWhere(String where) {
        this.where = where;
    }

    @PropertyName("ts")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @PropertyName("ts")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
