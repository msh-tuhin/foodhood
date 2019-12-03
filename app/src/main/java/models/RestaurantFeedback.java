package models;

import java.io.Serializable;

public class RestaurantFeedback implements Serializable {

    public String link;
    public String name;
    public float rating;
    public String review;
    public String imageUrl;

    public RestaurantFeedback(String link, String name, float rating, String review) {
        this.link = link;
        this.name = name;
        this.rating = rating;
        this.review = review;
    }
}
