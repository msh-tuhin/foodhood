package models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class PostModel {

    @PropertyName("c")
    private String caption;

    @PropertyName("cb")
    private List<String> commentsBy;

    @PropertyName("coms")
    private List<String> comments;

    @PropertyName("d")
    private Map<String, String> dishes;

    @PropertyName("f")
    private List<String> feedbacks;

    @PropertyName("l")
    private List<String> likes;

    @PropertyName("r")
    private Map<String, String> restaurant;

    @PropertyName("rf")
    private String restaurantFeedback;

    @PropertyName("tp")
    private Map<String, String> taggedPeople;

    @PropertyName("w")
    private Map<String, String> who;

    @PropertyName("i")
    private List<String> images;

    public PostModel(){

    }

    @PropertyName("c")
    public String getCaption() {
        return caption;
    }

    @PropertyName("c")
    public void setCaption(String caption) {
        this.caption = caption;
    }

    @PropertyName("cb")
    public List<String> getCommentsBy() {
        return commentsBy;
    }

    @PropertyName("cb")
    public void setCommentsBy(List<String> commentsBy) {
        this.commentsBy = commentsBy;
    }

    @PropertyName("coms")
    public List<String> getComments() {
        return comments;
    }

    @PropertyName("coms")
    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @PropertyName("d")
    public Map<String, String> getDishes() {
        return dishes;
    }

    @PropertyName("d")
    public void setDishes(Map<String, String> dishes) {
        this.dishes = dishes;
    }

    @PropertyName("f")
    public List<String> getFeedbacks() {
        return feedbacks;
    }

    @PropertyName("f")
    public void setFeedbacks(List<String> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @PropertyName("l")
    public List<String> getLikes() {
        return likes;
    }

    @PropertyName("l")
    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    @PropertyName("r")
    public Map<String, String> getRestaurant() {
        return restaurant;
    }

    @PropertyName("r")
    public void setRestaurant(Map<String, String> restaurant) {
        this.restaurant = restaurant;
    }

    @PropertyName("rf")
    public String getRestaurantFeedback() {
        return restaurantFeedback;
    }

    @PropertyName("rf")
    public void setRestaurantFeedback(String restaurantFeedback) {
        this.restaurantFeedback = restaurantFeedback;
    }

    @PropertyName("tp")
    public Map<String, String> getTaggedPeople() {
        return taggedPeople;
    }

    @PropertyName("tp")
    public void setTaggedPeople(Map<String, String> taggedPeople) {
        this.taggedPeople = taggedPeople;
    }

    @PropertyName("w")
    public Map<String, String> getWho() {
        return who;
    }

    @PropertyName("w")
    public void setWho(Map<String, String> who) {
        this.who = who;
    }

    @PropertyName("i")
    public List<String> getImages() {
        return images;
    }

    @PropertyName("i")
    public void setImages(List<String> images) {
        this.images = images;
    }
}
