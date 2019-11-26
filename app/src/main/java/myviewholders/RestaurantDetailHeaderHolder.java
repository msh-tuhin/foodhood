package myviewholders;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.RestDetail;
import com.example.tuhin.myapplication.RestaurantAllDishes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

import myapp.utils.AccountTypes;
import myapp.utils.PictureBinder;
import myapp.utils.SourceMorePeople;

public class RestaurantDetailHeaderHolder extends RecyclerView.ViewHolder{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String currentUserUid;
    private Context mContext;
    private String mRestaurantLink;

    LinearLayout addressLayout;
    LinearLayout phoneLayout;
    LinearLayout emailLayout;
    LinearLayout websiteLayout;
    LinearLayout ratingLayout;
    LinearLayout dishesListLayout;
    TextView restaurantNameTV;
    TextView restaurantAddressTV;
    TextView restaurantPhoneTV;
    TextView restaurantEmailTV;
    TextView restaurantWebsiteTV;
    TextView numFollowedByTV;
    TextView restaurantRatingTV;
    TextView seeAll;
    Button followRestaurant;
    RecyclerView rv;

    public RestaurantDetailHeaderHolder(@NonNull View v) {
        super(v);
        currentUserUid = mAuth.getCurrentUser().getUid();
        addressLayout = v.findViewById(R.id.address_layout);
        phoneLayout = v.findViewById(R.id.phone_layout);
        emailLayout = v.findViewById(R.id.email_layout);
        websiteLayout = v.findViewById(R.id.website_layout);
        ratingLayout = v.findViewById(R.id.rating_layout);
        dishesListLayout = v.findViewById(R.id.dishes_layout);
        restaurantNameTV = v.findViewById(R.id.restaurant_name);
        followRestaurant = v.findViewById(R.id.follow_restaurant);
        restaurantAddressTV = v.findViewById(R.id.restaurant_address);
        restaurantPhoneTV = v.findViewById(R.id.phone);
        restaurantEmailTV = v.findViewById(R.id.email);
        restaurantWebsiteTV = v.findViewById(R.id.web);
        numFollowedByTV = v.findViewById(R.id.num_followed_by);
        restaurantRatingTV = v.findViewById(R.id.rating);
        seeAll = v.findViewById(R.id.see_all);
        rv = v.findViewById(R.id.all_dishes);
    }

    public void bindTo(final Context context, final String restaurantLink){
        refreshHolder();
        if(restaurantLink==null || restaurantLink.equals("")) return;

        mContext = context;
        mRestaurantLink = restaurantLink;

        db.collection("rest_vital").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restaurantVital = task.getResult();
                    if(restaurantVital.exists()){
                        setCollapsedTitle(context, restaurantVital);
                        bindCoverPhoto(context, restaurantVital);
                        bindName(restaurantVital);
                        bindAddress(restaurantVital);
                        bindPhone(restaurantVital);
                        bindEmail(restaurantVital);
                        bindWebsite(restaurantVital);
                        bindRating(restaurantVital);
                        bindFollowers(restaurantVital);
                        setFollowersOnClickListener(context, restaurantLink);
                    }
                }
            }
        });
        bindDishesListLayout(context, restaurantLink);
        bindFollowButton();
    }

    private void bindCoverPhoto(Context context, DocumentSnapshot dishVitalSnapshot){
        PictureBinder.bindCoverPicture(((RestDetail)context).coverPhoto, dishVitalSnapshot);
    }

    private void bindName(DocumentSnapshot restVitalSnapshot){
        String name = restVitalSnapshot.getString("n");
        if(name==null || name.equals("")) return;
        restaurantNameTV.setText(name);
    }

    private void bindAddress(DocumentSnapshot restVitalSnapshot){
        String address = restVitalSnapshot.getString("a");
        if(address==null || address.equals("")) return;
        restaurantAddressTV.setText(address);
        addressLayout.setVisibility(View.VISIBLE);
    }

    private void bindPhone(DocumentSnapshot restVitalSnapshot){
        String phone = restVitalSnapshot.getString("p");
        if(phone==null || phone.equals("")) return;
        restaurantPhoneTV.setText(phone);
        phoneLayout.setVisibility(View.VISIBLE);
    }

    private void bindEmail(DocumentSnapshot restVitalSnapshot){
        String email = restVitalSnapshot.getString("e");
        if(email==null || email.equals("")) return;
        restaurantEmailTV.setText(email);
        emailLayout.setVisibility(View.VISIBLE);
    }

    private void bindWebsite(DocumentSnapshot restVitalSnapshot){
        String website = restVitalSnapshot.getString("w");
        if(website==null || website.equals("")) return;
        restaurantWebsiteTV.setText(website);
        websiteLayout.setVisibility(View.VISIBLE);
    }

    private void bindRating(DocumentSnapshot restVitalSnapshot){
        Double noOfRatings = restVitalSnapshot.getDouble("npr");
        if(noOfRatings==null){
            restaurantRatingTV.setText("N/A");
            ratingLayout.setVisibility(View.VISIBLE);
            return;
        }
        Double totalRating = restVitalSnapshot.getDouble("tr");
        if(totalRating==null){
            restaurantRatingTV.setText("N/A");
            ratingLayout.setVisibility(View.VISIBLE);
            return;
        }
        Double rating = noOfRatings==0 ? 0:totalRating/noOfRatings;
        if(rating == 0){
            restaurantRatingTV.setText("N/A");
        }else{
            DecimalFormat formatter = new DecimalFormat("#.0");
            restaurantRatingTV.setText(formatter.format(rating));
        }
        ratingLayout.setVisibility(View.VISIBLE);
    }

    private void bindFollowers(DocumentSnapshot restVitalSnapshot){
        Long numFollowers = restVitalSnapshot.getLong("nfb");
        if(numFollowers==null || numFollowers==0L) return;
        String numString = Long.toString(numFollowers);
        String fullText = "Followed by " + numString + " people";
        SpannableStringBuilder spannedText = getSpannedText(fullText, numString);
        numFollowedByTV.setText(spannedText);
        numFollowedByTV.setVisibility(View.VISIBLE);
    }

    private void setFollowersOnClickListener(final Context context, final String restaurantLink){
        numFollowedByTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MorePeole.class);
                intent.putExtra("source", SourceMorePeople.FOLLOWERS);
                intent.putExtra("personLink", restaurantLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindDishesListLayout(final Context context, final String restaurantLink){
        db.collection("dishes").document(restaurantLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot dishesSnap = task.getResult();
                    if(dishesSnap.exists()){
                        final ArrayList<String> dishes = new ArrayList<>();
                        try{
                            ArrayList<String> d = (ArrayList) dishesSnap.get("a");
                            dishes.addAll(d);
                        }catch (NullPointerException e){
                            Log.e("error", e.getMessage());
                        }
                        if(dishes.size() > 0){
                            setSeeAllOnClickListener(context, dishes);
                            bindDishesRV(context, dishes);
                            dishesListLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void setSeeAllOnClickListener(final Context context,
                                          final ArrayList<String> dishes){
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICKED", "See All Clicked");
                // Intent intent = new Intent(context, AllDishes.class);
                Intent intent = new Intent(context, RestaurantAllDishes.class);
                intent.putStringArrayListExtra("dishesList", dishes);
                // intent.putExtra("source", SourceAllDishes.RESTAURANT_ALL_DISHES);
                context.startActivity(intent);
            }
        });
    }


    private void bindDishesRV(Context context, ArrayList<String> dishes){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setNestedScrollingEnabled(false);
        RestaurantDishesAdapter adapter = new RestaurantDishesAdapter(dishes);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);
    }

    private class RestaurantDishesAdapter extends RecyclerView.Adapter<RestaurantDishHolder>{

        ArrayList<String> dishes;

        RestaurantDishesAdapter(ArrayList<String> dishes){
            this.dishes = dishes;
        }

        @Override
        public void onBindViewHolder(@NonNull RestaurantDishHolder dishHolder, int i) {
            dishHolder.bindTo(mContext, dishes.get(i));
        }

        @Override
        public int getItemCount() {
            return dishes.size();
        }

        @NonNull
        @Override
        public RestaurantDishHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.restaurant_dish_item_hrv, viewGroup, false);
            return new RestaurantDishHolder(view);
        }
    }

    private void bindFollowButton(){
        if(getAccountType() == AccountTypes.RESTAURANT){
            return;
        }
        db.collection("followers").document(mRestaurantLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> followers = (ArrayList) documentSnapshot.get("a");
                                if(followers.contains(currentUserUid)){
                                    followRestaurant.setText("UNFOLLOW");
                                }
                            } catch (NullPointerException e){
                                Log.i("Error", e.getMessage());
                            }
                        }
                        followRestaurant.setVisibility(View.VISIBLE);
                        followRestaurant.setOnClickListener(getFollowRestaurantOnClickListener(mRestaurantLink));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        followRestaurant.setVisibility(View.VISIBLE);
                        followRestaurant.setOnClickListener(getFollowRestaurantOnClickListener(mRestaurantLink));
                    }
                });
    }

    private View.OnClickListener getFollowRestaurantOnClickListener(final String restaurantLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference personRestFollowRef = db.collection("following_restaurants")
                        .document(currentUserUid);
                DocumentReference restaurantFollowerRef = db.collection("followers")
                        .document(restaurantLink);
                DocumentReference restRef = db.collection("rest_vital")
                        .document(restaurantLink);
                DocumentReference personRef = db.collection("person_vital")
                        .document(currentUserUid);
                switch (((Button)v).getText().toString()){
                    case "FOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followRestaurant.setText("UNFOLLOW");
                        personRestFollowRef.update("a", FieldValue.arrayUnion(restaurantLink));
                        restaurantFollowerRef.update("a", FieldValue.arrayUnion(currentUserUid));
                        restRef.update("nfb", FieldValue.increment(1));
                        personRef.update("nfr", FieldValue.increment(1));
                        break;
                    case "UNFOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followRestaurant.setText("FOLLOW");
                        personRestFollowRef.update("a", FieldValue.arrayRemove(restaurantLink));
                        restaurantFollowerRef.update("a", FieldValue.arrayRemove(currentUserUid));
                        restRef.update("nfb", FieldValue.increment(-1));
                        personRef.update("nfr", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }

    private void setCollapsedTitle(final Context context, DocumentSnapshot restVitalSnapshot){
        final String name = restVitalSnapshot.getString("n");
        if(name==null) return;
        ((RestDetail)context).appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    ((RestDetail)context).collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if(isShow) {
                    ((RestDetail)context).collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private int getAccountType(){
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sPref = mContext.getSharedPreferences(
                mContext.getString(R.string.account_type),
                Context.MODE_PRIVATE);
        return sPref.getInt(user.getEmail(), AccountTypes.UNSET);
    }

    private SpannableStringBuilder getSpannedText(String fullText, String spannable){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullText);
        int start = fullText.indexOf(spannable);
        int end = start + spannable.length();
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private void refreshHolder(){
        restaurantNameTV.setText("");
    }
}
