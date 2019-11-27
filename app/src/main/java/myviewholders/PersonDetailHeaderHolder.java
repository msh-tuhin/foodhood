package myviewholders;

import android.app.Activity;
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

import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.AllRestaurants;
import com.example.tuhin.myapplication.MorePeole;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.PersonDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import myapp.utils.AccountTypes;
import myapp.utils.DateTimeExtractor;
import myapp.utils.PictureBinder;
import myapp.utils.SourceAllDishes;
import myapp.utils.SourceMorePeople;

public class PersonDetailHeaderHolder extends RecyclerView.ViewHolder {
    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context mContext;
    private String mCurrentUserUid;

    private LinearLayout wishlistLayout;
    private LinearLayout currentLocationLayout;
    private LinearLayout hometownLayout;
    private LinearLayout phoneLayout;
    private TextView nameTV;
    private TextView bioTV;
    private TextView birthdateTV;
    private TextView hometownTV;
    private TextView currentLocationTV;
    private TextView phoneTV;
    private TextView numFollowingTV;
    private TextView numFollowerTV;
    private TextView numFollowedRestaurantTV;
    private TextView seeAllTV;
    private Button followButton;
    private RecyclerView rvWishlist;

    public PersonDetailHeaderHolder(@NonNull View v) {
        super(v);
        wishlistLayout = v.findViewById(R.id.wishlist_layout);
        currentLocationLayout = v.findViewById(R.id.current_location_layout);
        hometownLayout = v.findViewById(R.id.hometown_layout);
        phoneLayout = v.findViewById(R.id.phone_layout);
        nameTV = v.findViewById(R.id.name);
        bioTV = v.findViewById(R.id.bio);
        birthdateTV = v.findViewById(R.id.birthdate);
        hometownTV = v.findViewById(R.id.hometown);
        currentLocationTV = v.findViewById(R.id.current_location);
        phoneTV = v.findViewById(R.id.phone);
        numFollowingTV = v.findViewById(R.id.num_following);
        numFollowerTV = v.findViewById(R.id.num_followed_by);
        numFollowedRestaurantTV = v.findViewById(R.id.num_followed_restaurants);
        seeAllTV = v.findViewById(R.id.see_all);
        followButton = v.findViewById(R.id.follow_person);
        rvWishlist = v.findViewById(R.id.wishlist);
    }

    public void bindTo(final Context context, final String personLink){
        refreshHolder();
        mCurrentUserUid = mAuth.getCurrentUser().getUid();
        mContext = context;
        if(personLink==null || personLink.equals("")) return;

        db.collection("person_vital").document(personLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personVitalSnapshot = task.getResult();
                    if(personVitalSnapshot.exists()){
                        bindCoverPhoto(context, personVitalSnapshot);
                        bindProfilePicture(context, personVitalSnapshot);
                        setCollapsedTitle(personVitalSnapshot);
                        bindName(personVitalSnapshot);
                        bindBio(personVitalSnapshot);
                        bindPhone(personVitalSnapshot);
                        bindBirthdate(personVitalSnapshot);
                        bindCurrentTown(personVitalSnapshot);
                        bindHomeTown(personVitalSnapshot);
                        bindFollowings(personVitalSnapshot);
                        setFollowingsOnClickListener(context, personLink);
                        bindFollowers(personVitalSnapshot);
                        setFollowersOnClickListener(context, personLink);
                        bindFollowingRestaurants(personVitalSnapshot);
                        setFollowingRestaurantsOnClickListener(context, personLink);
                    }
                }
            }
        });

        bindWishlistLayout(context, personLink);
        bindFollowButton(context, personLink);
    }

    private void bindProfilePicture(Context context, DocumentSnapshot personVitalSnapshot){
        PictureBinder.bindProfilePicture(((PersonDetail)context).profilePicture, personVitalSnapshot);
    }

    private void bindCoverPhoto(Context context, DocumentSnapshot personVitalSnapshot){
        PictureBinder.bindCoverPicture(((PersonDetail)context).coverPhoto, personVitalSnapshot);
    }

    private void bindName(DocumentSnapshot personVitalSnapshot){
        String personName = personVitalSnapshot.getString("n");
        if(personName != null){
            nameTV.setText(personName);
        }
    }

    private void bindBio(DocumentSnapshot personVitalSnapshot){
        String bio = personVitalSnapshot.getString("bio");
        if(bio != null && !bio.equals("")){
            bioTV.setText(bio);
            bioTV.setVisibility(View.VISIBLE);
        }
    }

    private void bindPhone(DocumentSnapshot personVitalSnapshot){
        String personPhone = personVitalSnapshot.getString("p");
        if(personPhone != null && !personPhone.equals("")){
            phoneTV.setText(personPhone);
            phoneLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindBirthdate(DocumentSnapshot personVitalSnapshot){
        Timestamp personBirthdateTS = personVitalSnapshot.getTimestamp("b");
        String birthdate = DateTimeExtractor.getDateString(personBirthdateTS);
        if(birthdate != null && !birthdate.equals("")){
            String fullText = "Born on: " + birthdate;
            SpannableStringBuilder ssb = getSpannedText(fullText, "Born on: ");
            birthdateTV.setText(ssb);
            birthdateTV.setVisibility(View.VISIBLE);
        }
    }

    private void bindCurrentTown(DocumentSnapshot personVitalSnapshot){
        String personCurrentTown = personVitalSnapshot.getString("ct");
        if(personCurrentTown != null && !personCurrentTown.equals("")){
            currentLocationTV.setText(personCurrentTown);
            currentLocationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindHomeTown(DocumentSnapshot personVitalSnapshot){
        String personHomeTown = personVitalSnapshot.getString("ht");
        if(personHomeTown != null && !personHomeTown.equals("")){
            hometownTV.setText(personHomeTown);
            hometownLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindFollowings(DocumentSnapshot personVitalSnapshot){
        Long numFollwing = personVitalSnapshot.getLong("nf");
        if(numFollwing==null || numFollwing<=0L) return;
        String numString = Long.toString(numFollwing);
        String fullText = "Follows " + numString + " people";
        SpannableStringBuilder spannedText = getSpannedText(fullText, numString);
        numFollowingTV.setText(spannedText);
        numFollowingTV.setVisibility(View.VISIBLE);
    }

    private void setFollowingsOnClickListener(final Context context, final String personLink){
        numFollowingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MorePeole.class);
                intent.putExtra("source", SourceMorePeople.FOLLOWINGS);
                intent.putExtra("personLink", personLink);
                context.startActivity(intent);
            }
        });
    }

    private void setFollowersOnClickListener(final Context context, final String personLink){
        numFollowerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MorePeole.class);
                intent.putExtra("source", SourceMorePeople.FOLLOWERS);
                intent.putExtra("personLink", personLink);
                context.startActivity(intent);
            }
        });
    }

    private void setFollowingRestaurantsOnClickListener(final Context context, final String personLink){
        numFollowedRestaurantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllRestaurants.class);
                intent.putExtra("personLink", personLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindFollowers(DocumentSnapshot personVitalSnapshot){
        Long numFollower = personVitalSnapshot.getLong("nfb");
        if(numFollower==null || numFollower<=0L) return;
        String numString = Long.toString(numFollower);
        String fullText = "Followed by " + numString + " people";
        SpannableStringBuilder spannedText = getSpannedText(fullText, numString);
        numFollowerTV.setText(spannedText);
        numFollowerTV.setVisibility(View.VISIBLE);
    }

    private void bindFollowingRestaurants(DocumentSnapshot personVitalSnapshot){
        Long numFollwedRestaurant = personVitalSnapshot.getLong("nfr");
        if(numFollwedRestaurant==null || numFollwedRestaurant<=0L) return;
        String numString = Long.toString(numFollwedRestaurant);
        String fullText = "Follows " + numString + " restaurants";
        SpannableStringBuilder spannedText = getSpannedText(fullText, numString);
        numFollowedRestaurantTV.setText(spannedText);
        numFollowedRestaurantTV.setVisibility(View.VISIBLE);
    }

    private void bindFollowButton(final Context context, final String personLink){
        if(getAccountType() == AccountTypes.RESTAURANT){
            return;
        }
        if(personLink.equals(mCurrentUserUid)) return;
        db.collection("followings")
                .document(mCurrentUserUid)
                .get()
                .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> followings = (ArrayList) documentSnapshot.get("a");
                                if(followings.contains(personLink)){
                                    followButton.setText("UNFOLLOW");
                                }
                            } catch (NullPointerException e){
                                Log.i("Error", e.getMessage());
                            }
                        }
                        followButton.setVisibility(View.VISIBLE);
                        followButton.setOnClickListener(getFollowPersonOnClickListener(personLink));
                    }
                })
                .addOnFailureListener((Activity)context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        followButton.setVisibility(View.VISIBLE);
                        followButton.setOnClickListener(getFollowPersonOnClickListener(personLink));
                    }
                });
    }

    private View.OnClickListener getFollowPersonOnClickListener(final String personLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference followingRef = db.collection("followings")
                        .document(mCurrentUserUid);
                DocumentReference followerRef = db.collection("followers")
                        .document(personLink);
                DocumentReference followerVitalRef = db.collection("person_vital")
                        .document(mCurrentUserUid);
                DocumentReference followedVitalRef = db.collection("person_vital")
                        .document(personLink);
                switch (((Button)v).getText().toString()){
                    case "FOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followButton.setText("UNFOLLOW");
                        followingRef.update("a", FieldValue.arrayUnion(personLink));
                        followerRef.update("a", FieldValue.arrayUnion(mCurrentUserUid));
                        followerVitalRef.update("nf", FieldValue.increment(1));
                        followedVitalRef.update("nfb", FieldValue.increment(1));
                        break;
                    case "UNFOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followButton.setText("FOLLOW");
                        followingRef.update("a", FieldValue.arrayRemove(personLink));
                        followerRef.update("a", FieldValue.arrayRemove(mCurrentUserUid));
                        followerVitalRef.update("nf", FieldValue.increment(-1));
                        followedVitalRef.update("nfb", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }

    private void bindWishlistLayout(final Context context, final String personLink){
        db.collection("wishlist").document(personLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot wishlistSnapshot = task.getResult();
                    if(wishlistSnapshot.exists()){
                        final ArrayList<String> wishlist = new ArrayList<>();
                        try{
                            // firestore array --> JAVA arraylist
                            ArrayList<String> w = (ArrayList) wishlistSnapshot.get("a");
                            wishlist.addAll(w);
                        }catch (NullPointerException e){
                            Log.e("error", e.getMessage());
                        }
                        if(wishlist.size() > 0){
                            setSeeAllOnClickListener(context, wishlist);
                            bindWishlistRV(context, wishlist);
                            wishlistLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void setSeeAllOnClickListener(final Context context,
                                          final ArrayList<String> wishlist){
        seeAllTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICKED", "See All Clicked");
                Intent intent = new Intent(context, AllDishes.class);
                intent.putStringArrayListExtra("dishesList", wishlist);
                intent.putExtra("source", SourceAllDishes.WISHLIST);
                context.startActivity(intent);
            }
        });
    }

    private void bindWishlistRV(Context context, ArrayList<String> wishlist){
        LinearLayoutManager wishlistLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvWishlist.setLayoutManager(wishlistLayoutManager);
        rvWishlist.setNestedScrollingEnabled(false);
        WishlistAdapter adapter = new WishlistAdapter(wishlist);
        rvWishlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private SpannableStringBuilder getSpannedText(String fullText, String spannable){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullText);
        int start = fullText.indexOf(spannable);
        int end = start + spannable.length();
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private void setCollapsedTitle(DocumentSnapshot personVitalSnapshot){
        final String name = personVitalSnapshot.getString("n");
        if(name == null) return;
        ((PersonDetail)mContext).appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    ((PersonDetail)mContext).collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if(isShow) {
                    ((PersonDetail)mContext).collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private class WishlistAdapter extends RecyclerView.Adapter<WishlistItemHolder>{
        ArrayList<String> wishlist;
        WishlistAdapter(ArrayList<String> wishlist){
            this.wishlist = wishlist;
        }
        @Override
        public void onBindViewHolder(@NonNull WishlistItemHolder wishlistItemHolder, int i) {
            wishlistItemHolder.bindTo(mContext, wishlist.get(i));
        }
        @Override
        public int getItemCount() {
            return wishlist.size();
        }

        @NonNull
        @Override
        public WishlistItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.wishlist_item, viewGroup, false);
             return new WishlistItemHolder(view);
        }
    }

    private class FollowedRestaurantsAdapter extends RecyclerView.Adapter<FollowedRestaurantItemHolder>{
        ArrayList<String> followedRestaurants;
        FollowedRestaurantsAdapter(ArrayList<String> followedRestaurants){
            this.followedRestaurants = followedRestaurants;
        }
        @Override
        public void onBindViewHolder(@NonNull FollowedRestaurantItemHolder followedRestaurantItemHolder, int i) {
            followedRestaurantItemHolder.bindTo(followedRestaurants.get(i));
        }
        @Override
        public int getItemCount() {
            return followedRestaurants.size();
        }

        @NonNull
        @Override
        public FollowedRestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.followed_restaurants_item, viewGroup, false);
            return new FollowedRestaurantItemHolder(view);
        }
    }

    private int getAccountType(){
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sPref = mContext.getSharedPreferences(
                mContext.getString(R.string.account_type),
                Context.MODE_PRIVATE);
        return sPref.getInt(user.getEmail(), AccountTypes.UNSET);
    }

    private void refreshHolder(){
        nameTV.setText("");
    }
}
