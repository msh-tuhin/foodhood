package myapp.utils;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Helper {

    public Helper() {
    }

    public static Map<String, String> buildPost(@NonNull DocumentSnapshot post){

        String caption = post.getString("c");
        Log.i("caption", caption);

        Map<String, String> dishes = (Map) post.get("d");
        Log.i("noofdishes",Integer.toString(dishes.keySet().size()));
        Map<String, String> restaurant = (Map) post.get("r");
        Map<String, String> who = (Map) post.get("w");
        Map<String, String> taggedPeople = (Map) post.get("tp");

        int noOfTaggedPeople = taggedPeople.keySet().size();
        int noOfDishes = dishes.keySet().size();
        String restaurantName = restaurant.get("n");
        String nameOfPostedBy = who.get("n");

        String postHeader = nameOfPostedBy + " enjoying " + Integer.toString(noOfDishes)
                + " dishes with " + Integer.toString(noOfTaggedPeople)
                + " people at " + restaurantName;

        Map<String, String> builtPost = new HashMap<>();
        builtPost.put("caption", caption);
        builtPost.put("postHeader", postHeader);
        return builtPost;
    }

    public static SpannableString getSpannableStringFromRestaurantName(@NonNull String restaurantName, String linkToRestaurant){
        int length = restaurantName.length();
        SpannableString mSpannableString = new SpannableString(restaurantName);
        mSpannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableString.setSpan(new MyClickableSpan(linkToRestaurant), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return mSpannableString;

    }

    private static class MyClickableSpan extends ClickableSpan{
        String link;
        MyClickableSpan(String link){
            this.link = link;
        }
        @Override
        public void onClick(@NonNull View widget) {
            Log.i("CLICKED", "Go To " + link);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

}