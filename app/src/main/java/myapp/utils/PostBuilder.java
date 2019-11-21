package myapp.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import models.PostModel;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.DishDetail;
import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.RestDetail;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class PostBuilder {
    private int noOfTaggedPeople, noOfDishes;
    private String caption;
    private String restaurantName, restaurantLink, nameOfPostedBy, linkToPostedBy;
    private Map<String, String> restaurant, dishes, who, taggedPeople;
    private ArrayList<String> sortedDishLinks, sortedTaggedPeopleLinks;
    private Context context;
    private Timestamp postTime;

    public PostBuilder(Context context, DocumentSnapshot post) {
        postTime = post.getTimestamp("ts");
        caption = post.getString("c");
        Log.i("caption", caption);

        dishes = (Map) post.get("d");
        Log.i("noofdishes",Integer.toString(dishes.keySet().size()));
        restaurant = (Map) post.get("r");
        who = (Map) post.get("w");
        taggedPeople = (Map) post.get("tp");

        noOfTaggedPeople = taggedPeople.keySet().size();
        noOfDishes = dishes.keySet().size();
        restaurantName = restaurant.get("n");
        restaurantLink = restaurant.get("l");
        nameOfPostedBy = who.get("n");
        linkToPostedBy = who.get("l");
        this.context = context;
        sortedDishLinks = getSortedLinks(dishes.keySet());
        sortedTaggedPeopleLinks = getSortedLinks(taggedPeople.keySet());
    }

    public PostBuilder(Context context, PostModel postModel){
        caption = postModel.getCaption();
        Log.i("caption", caption);

        dishes = postModel.getDishes();
        Log.i("noofdishes",Integer.toString(dishes.keySet().size()));
        restaurant = postModel.getRestaurant();
        who = postModel.getWho();
        taggedPeople = postModel.getTaggedPeople();

        noOfTaggedPeople = taggedPeople.keySet().size();
        noOfDishes = dishes.keySet().size();
        restaurantName = restaurant.get("n");
        restaurantLink = restaurant.get("l");
        nameOfPostedBy = who.get("n");
        linkToPostedBy = who.get("l");
        this.context = context;
        sortedDishLinks = getSortedLinks(dishes.keySet());
        sortedTaggedPeopleLinks = getSortedLinks(taggedPeople.keySet());
    }

    public String getNamePostedBy(){
        return nameOfPostedBy;
    }

    public String getLinkToPostedBy(){
        return linkToPostedBy;
    }

    public String getRestaurantName(){
        return restaurantName;
    }

    public String getRestaurantLink(){
        return restaurantLink;
    }

    public String getPeopleText(){
        if(sortedTaggedPeopleLinks.size() == 0) return "";
        String text = taggedPeople.get(sortedTaggedPeopleLinks.get(0));
        if(noOfTaggedPeople > 1){
            text = text + "  +" + Integer.toString(noOfTaggedPeople - 1);
        }
        return text;
    }

    public String getDishesText(){
        String text = dishes.get(sortedDishLinks.get(0));
        if(noOfDishes > 1){
            text = text + "  +" + Integer.toString(noOfDishes - 1);
        }
        return text;
    }

    public ArrayList<String> getSortedDishLinks(){
        return sortedDishLinks;
    }

    public ArrayList<String> getSortedTaggedPeopleLinks(){
        return sortedTaggedPeopleLinks;
    }

    public String getCaption(){
        return caption;
    }

    public Timestamp getPostTime(){
        return postTime;
    }

    private ArrayList<String> getSortedLinks(Set<String> set){
        ArrayList<String> links = new ArrayList<>();
//        links.addAll(set);
        for(String key : set){
            links.add(key);
        }
        Collections.sort(links);
        return links;
    }
}
