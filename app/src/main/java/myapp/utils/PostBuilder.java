package myapp.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class PostBuilder {

    int start, end;
    int noOfTaggedPeople, noOfDishes;
    String caption;
    String restaurantName, restaurantLink, nameOfPostedBy, linkToPostedBy;
    Map<String, String> restaurant, dishes, who, taggedPeople;
    ArrayList<String> sortedDishLinks, sortedTaggedPeopleLinks;
    Fragment fragment;
    Context context;

    public PostBuilder(Context context, DocumentSnapshot post) {
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

    public SpannableStringBuilder getPostHeader(){
        // TODO mSpannableString can be a class level variable
        SpannableStringBuilder mSpannableString = new SpannableStringBuilder("");
//        ArrayList<String> sortedDishLinks = getSortedLinks(dishes.keySet());
//        ArrayList<String> sortedTaggedPeopleLinks = getSortedLinks(dishes.keySet());
        updateSpannableString(mSpannableString, nameOfPostedBy, linkToPostedBy, DestinationType.PERSONDETAIL);
        mSpannableString.append(" enjoying ");
        String firstDish = dishes.get(sortedDishLinks.get(0));
        updateSpannableString(mSpannableString, firstDish, sortedDishLinks.get(0), DestinationType.DISHDETAIL);
        if(noOfDishes > 1) {
            mSpannableString.append(" and ");
            if(noOfDishes > 2){
                String otherDishesString = Integer.toString(noOfDishes-1) + " others";
                updateSpannableString(mSpannableString, otherDishesString, "", DestinationType.MOREDISHES);
            } else{
                String secondDish = dishes.get(sortedDishLinks.get(1));
                updateSpannableString(mSpannableString, secondDish, sortedDishLinks.get(1), DestinationType.DISHDETAIL);
            }
        }
        mSpannableString.append(" at ");
        updateSpannableString(mSpannableString, restaurantName, restaurantLink, DestinationType.RESTAURANTDETAIL);
        mSpannableString.append(" with ");
        String firstPerson = taggedPeople.get(sortedTaggedPeopleLinks.get(0));
        updateSpannableString(mSpannableString, firstPerson, sortedTaggedPeopleLinks.get(0), DestinationType.PERSONDETAIL);
        if(noOfTaggedPeople > 1) {
            mSpannableString.append(" and ");
            if(noOfTaggedPeople > 2){
                String otherPeopleString = Integer.toString(noOfTaggedPeople-1) + " others";
                updateSpannableString(mSpannableString, otherPeopleString, "", DestinationType.MOREPERSONS);
            } else{
                String secondPerson = taggedPeople.get(sortedTaggedPeopleLinks.get(1));
                updateSpannableString(mSpannableString, secondPerson, sortedTaggedPeopleLinks.get(1), DestinationType.PERSONDETAIL);
            }
        }

        return mSpannableString;
    }

    public String getCaption(){
        return caption;
    }

    private void updateSpannableString(SpannableStringBuilder mSpannableString, String name, String link, int destinationType){
        // if mSpannableString is made a class level variable
        // then it will not be passed here
        start = mSpannableString.length();
        mSpannableString.append(name);
        end = mSpannableString.length();
        mSpannableString.setSpan(new MyClickableSpan(context, link, destinationType), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    private class MyClickableSpan extends ClickableSpan{
        String link;
        int destinationType;
        private Context context;
        MyClickableSpan(Context context,String link, int destinationType){
            this.link = link;
            this.destinationType = destinationType;
            this.context = context;
        }
        @Override
        public void onClick(@NonNull View widget) {
            Log.i("CLICKED", "Go To " + link);
            Log.i("destination", "Type " + Integer.toString(destinationType));
            Intent intent;

            switch (destinationType){
                case DestinationType.PERSONDETAIL:
                    intent = new Intent(context, PersonDetail.class);
                    intent.putExtra("personLink", link);
                    context.startActivity(intent);
                    break;
                case DestinationType.DISHDETAIL:
                    intent = new Intent(context, DishDetail.class);
                    intent.putExtra("dishLink", link);
                    context.startActivity(intent);
                    break;
                case DestinationType.RESTAURANTDETAIL:
                    intent = new Intent(context, RestDetail.class);
                    intent.putExtra("restaurantLink", link);
                    context.startActivity(intent);
                    break;
                case DestinationType.MOREPERSONS:
                    intent = new Intent(context, MorePeole.class);
                    intent.putStringArrayListExtra("personsList", sortedTaggedPeopleLinks);
                    context.startActivity(intent);
                    break;
                case DestinationType.MOREDISHES:
                    intent = new Intent(context, AllDishes.class);
                    intent.putStringArrayListExtra("dishesList", sortedDishLinks);
                    intent.putExtra("source", SourceAllDishes.POST);
                    context.startActivity(intent);
                    break;
            }

        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
