package com.example.tuhin.myapplication;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

@IgnoreExtraProperties
public class FeedbackModel {

    @PropertyName("hr")
    public boolean hasRating;
    @PropertyName("hre")
    public boolean hasReview;
    @PropertyName("ts")
    public Timestamp timestamp;
    @PropertyName("r")
    public Double rating;
    @PropertyName("re")
    public String review;
    @PropertyName("t")
    public int type;
    @PropertyName("w")
    public String who;
    @PropertyName("wh")
    public String where;

    public FeedbackModel() {
    }

    public boolean isHasRating() {
        return hasRating;
    }

    public boolean isHasReview() {
        return hasReview;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public double getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public int getType() {
        return type;
    }

    public String getWho() {
        return who;
    }

    public String getWhere() {
        return where;
    }
}
