package myapp.utils;


import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algolia.instantsearch.ui.views.Hits;
import com.example.tuhin.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

/*Search hit binder for main index*/

public class SearchHitBinder {

    Hits hits;
    int position;
    View view;

    public SearchHitBinder(Hits hits, int position, View view){
        this.hits = hits;
        this.position = position;
        this.view = view;
    }

    public void bindName(){
        Log.i("inside", "bindname");
        String name = "";
        try{
            name = hits.get(position).getString(AlgoliaAttributeNames.NAME);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(name);
            getHighlight(hits.get(position), AlgoliaAttributeNames.NAME, builder);
            ((TextView) view.findViewById(R.id.name)).setText(builder);
        }catch (JSONException e){
            Log.i("name", "no value found");
            ((TextView) view.findViewById(R.id.name)).setText("");
        }
    }

    public void bindRating(){
        Double rating;
        try{
            rating = hits.get(position).getDouble(AlgoliaAttributeNames.RATING);
        }catch (JSONException e){
            Log.i("rating", "no value found");
            return;
        }
        TextView ratingTV = view.findViewById(R.id.rating);
        LinearLayout ratingLayout = view.findViewById(R.id.rating_layout);
        if(rating <= 0.0){
            ratingTV.setText("N/A");
        }else{
            DecimalFormat formatter = new DecimalFormat("#.0");
            ratingTV.setText(formatter.format(rating));
        }
        ratingLayout.setVisibility(View.VISIBLE);
    }

    public void bindPrice(){
        Double price;
        try{
            price = hits.get(position).getDouble(AlgoliaAttributeNames.PRICE);
        }catch (JSONException e){
            Log.i("price", "no value found");
            return;
        }
        TextView priceTV = view.findViewById(R.id.price);
        LinearLayout priceLayout = view.findViewById(R.id.price_layout);
        DecimalFormat formatter = new DecimalFormat("#.00");
        priceTV.setText(formatter.format(price));
        priceLayout.setVisibility(View.VISIBLE);
    }

    public void bindAddress(){
        String text = "";
        String currentTown;
        String address;
        String restaurantName = "";
        String restaurantAddress = "";
        TextView addressTV = view.findViewById(R.id.address);
        LinearLayout addressLayout = view.findViewById(R.id.address_layout);

        // for persons
        try{
            currentTown = hits.get(position).getString(AlgoliaAttributeNames.CURRENT_TOWN);
            //SpannableString spannableAddress = new SpannableString(currentTown);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(currentTown);
            getHighlight(hits.get(position), AlgoliaAttributeNames.CURRENT_TOWN, builder);
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0,
                    currentTown.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            addressTV.setText(builder);
            addressLayout.setVisibility(View.VISIBLE);
            return;
        }catch (JSONException e){
            Log.i("current_town", "no value found");
        }

        // for restaurants
        try{
            address = hits.get(position).getString(AlgoliaAttributeNames.ADDRESS);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(address);
            getHighlight(hits.get(position), AlgoliaAttributeNames.ADDRESS, builder);
            addressTV.setText(builder);
            addressLayout.setVisibility(View.VISIBLE);
            return;
        }catch (JSONException e){
            Log.i("address", "no value found");
        }

        // for dishes
        try{
            restaurantName = hits.get(position).getString(AlgoliaAttributeNames.RESTAURANT_NAME);
            restaurantAddress = hits.get(position).getString(AlgoliaAttributeNames.RESTAURANT_ADDRESS);
        }catch (JSONException e){
            Log.i("name_address", "no value found");
        }
        if(restaurantName==null || restaurantName.equals("")) return;
        text = restaurantName;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(restaurantName);
        getHighlight(hits.get(position), AlgoliaAttributeNames.RESTAURANT_NAME, builder);
        int start = text.indexOf(restaurantName);
        int end = start + restaurantName.length();
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(restaurantAddress!=null && !restaurantAddress.equals("")){
            builder.append("\n");
            builder.append(restaurantAddress);
            getHighlight(hits.get(position), AlgoliaAttributeNames.RESTAURANT_ADDRESS, builder);
        }
        addressTV.setText(builder);
        addressLayout.setVisibility(View.VISIBLE);
    }

    public void bindPicture(){
        try{
            String link = hits.get(position).getString(AlgoliaAttributeNames.IMAGE_URL);
            PictureBinder.bindPictureSearchResult((CircleImageView)view.findViewById(R.id.avatar), link);
        }catch (JSONException e){
            Log.i("image_url", "no value found");
        }
    }

    public void bindDistrict(boolean onlyHighlighted){
        try{
            String district = hits.get(position).getString(AlgoliaAttributeNames.DISTRICT);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(district);
            boolean isDistrictHighlighted = getHighlight(hits.get(position),
                    AlgoliaAttributeNames.DISTRICT, builder);
            SpannableStringBuilder newBuilder = new SpannableStringBuilder("District: ");
            newBuilder.append(builder);
            if(!(onlyHighlighted && !isDistrictHighlighted)){
                TextView districtTV = view.findViewById(R.id.district);
                districtTV.setText(newBuilder);
                districtTV.setVisibility(View.VISIBLE);
            }
        }catch (JSONException e){
            Log.i("district", "no value found");
        }
    }

    public void bindCategory(boolean onlyHighlighted){
        try{
            JSONArray categories = hits.get(position).getJSONArray(AlgoliaAttributeNames.CATEGORY);
            String categoryText = "";

            // comma is not added after the last element
            for(int i=0; i <= categories.length()-2; i++){
                categoryText += categories.getString(i);
                categoryText += ", ";
            }
            categoryText += categories.getString(categories.length()-1);

            SpannableStringBuilder builder = new SpannableStringBuilder(categoryText);
            boolean isCategoryHighlighted = getHighlightedArrayField(hits.get(position),
                    AlgoliaAttributeNames.CATEGORY, builder);
            if(!(onlyHighlighted && !isCategoryHighlighted)){
                SpannableStringBuilder newBuilder = new SpannableStringBuilder();
                newBuilder.append("Category: ");
                newBuilder.append(builder);
                TextView categoryTV = view.findViewById(R.id.category);
                categoryTV.setText(newBuilder);
                categoryTV.setVisibility(View.VISIBLE);
            }
        }catch (JSONException e){
            Log.i("category", "no value found");
        }
    }

    public void bind(boolean bindPicture,
                     boolean bindName,
                     boolean bindRating,
                     boolean bindPrice,
                     boolean bindAddress,
                     boolean bindDistrictOnlyHighlighted,
                     boolean bindCategoryOnlyHighlighted){
        if(bindPicture) bindPicture();
        if(bindName) bindName();
        if(bindRating) bindRating();
        if(bindPrice) bindPrice();
        if(bindAddress) bindAddress();
        if(bindDistrictOnlyHighlighted) bindDistrict(true);
        if(bindCategoryOnlyHighlighted) bindCategory(true);
    }

    public void bind(boolean bindPicture,
                     boolean bindName,
                     boolean bindRating,
                     boolean bindPrice,
                     boolean bindAddress,
                     boolean bindDistrictOnlyHighlighted,
                     boolean bindDistrict,
                     boolean bindCategoryOnlyHighlighted,
                     boolean bindCategory){
        if(bindPicture) bindPicture();
        if(bindName) bindName();
        if(bindRating) bindRating();
        if(bindPrice) bindPrice();
        if(bindAddress) bindAddress();
        if(bindDistrictOnlyHighlighted) bindDistrict(true);
        if(bindDistrict) bindDistrict(false);
        if(bindCategoryOnlyHighlighted) bindCategory(true);
        if(bindCategory) bindCategory(false);
    }

    public static void refreshView(View view){
        LinearLayout parentLayout = view.findViewById(R.id.parent_layout);
        CircleImageView avatar = view.findViewById(R.id.avatar);
        TextView nameTV = view.findViewById(R.id.name);
        LinearLayout ratingLayout = view.findViewById(R.id.rating_layout);
        TextView ratingTV = view.findViewById(R.id.rating);
        LinearLayout priceLayout = view.findViewById(R.id.price_layout);
        TextView priceTV = view.findViewById(R.id.price);
        LinearLayout addressLayout = view.findViewById(R.id.address_layout);
        TextView addressTV = view.findViewById(R.id.address);
        TextView districtTV = view.findViewById(R.id.district);
        TextView categoryTV = view.findViewById(R.id.category);

        avatar.setImageResource(R.drawable.ltgray);
        nameTV.setText("");
        ratingTV.setText("");
        priceTV.setText("");
        addressTV.setText("");
        districtTV.setText("");
        districtTV.setVisibility(View.GONE);
        categoryTV.setText("");
        categoryTV.setVisibility(View.GONE);
        ratingLayout.setVisibility(View.GONE);
        priceLayout.setVisibility(View.GONE);
        addressLayout.setVisibility(View.GONE);
        //parentLayout.setOnClickListener(null);
    }

    private boolean getHighlight(JSONObject hit, String attributeName,
                                 SpannableStringBuilder builder){
        boolean isHighlighted = false;
        try{
            JSONObject highlightResult = hit.getJSONObject("_highlightResult");
            JSONObject highlightedAttribute = highlightResult.getJSONObject(attributeName);
            JSONArray matchedWords = highlightedAttribute.getJSONArray("matchedWords");

            for(int i=0; i<matchedWords.length(); i++){
                int start = builder.toString().toLowerCase().indexOf(matchedWords.getString(i));
                if(start>=0){
                    int end = start + matchedWords.getString(i).length();
                    builder.setSpan(new ForegroundColorSpan(Color.BLUE),
                            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // return true;
                    isHighlighted = true;
                }
            }

        }catch (JSONException e){
            Log.e("_highlightResult", "no value found");
        }
        return isHighlighted;
    }

    private boolean getHighlightedArrayField(JSONObject hit, String attributeName, SpannableStringBuilder builder){
        boolean isCategoryHighlighted = false;
        try{
            JSONObject highlightResult = hit.getJSONObject("_highlightResult");
            JSONArray highlightedAttributes = highlightResult.getJSONArray(attributeName);
            for(int i=0; i<highlightedAttributes.length();i++){
                JSONObject jsonObject = highlightedAttributes.getJSONObject(i);
                if (jsonObject.getString("matchLevel").equals("none")) {
                    continue;
                }
                JSONArray matchedWords = jsonObject.getJSONArray("matchedWords");
                for(int j=0; j<matchedWords.length(); j++){
                    int start = builder.toString().toLowerCase().indexOf(matchedWords.getString(j));
                    if(start>=0){
                        int end = start + matchedWords.getString(j).length();
                        builder.setSpan(new ForegroundColorSpan(Color.BLUE),
                                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        isCategoryHighlighted = true;
                    }
                }
            }
        }catch (JSONException e){
            Log.e("_highlightResult", "no value found");
        }
        return isCategoryHighlighted;
    }
}
