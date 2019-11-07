package myviewholders;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuhin.myapplication.AllDishes;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.PersonDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import myapp.utils.SourceAllDishes;

public class PersonDetailHeaderHolder extends RecyclerView.ViewHolder {
    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;

    private TextView nameTV;
    private TextView bioTV;
    private TextView birthdateTV;
    private TextView hometownTV;
    private TextView currentLocationTV;
    private TextView phoneTV;
    private TextView seeAllTV;
    private Button followButton;
    private RecyclerView rvWishlist;

    public PersonDetailHeaderHolder(@NonNull View v) {
        super(v);
        nameTV = v.findViewById(R.id.name);
        bioTV = v.findViewById(R.id.bio);
        birthdateTV = v.findViewById(R.id.birthdate);
        hometownTV = v.findViewById(R.id.hometown);
        currentLocationTV = v.findViewById(R.id.current_location);
        phoneTV = v.findViewById(R.id.phone);
        seeAllTV = v.findViewById(R.id.see_all);
        followButton = v.findViewById(R.id.follow_person);
        rvWishlist = v.findViewById(R.id.wishlist);
    }

    public void bindTo(final Context context, String personLink){
        mContext = context;
        if(personLink==null || personLink.equals("")) return;

        db.collection("person_vital").document(personLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personVitalSnapshot = task.getResult();
                    if(personVitalSnapshot.exists()){
                        setCollapsedTitle(personVitalSnapshot);
                        bindName(personVitalSnapshot);
                        bindBio(personVitalSnapshot);
                        bindPhone(personVitalSnapshot);
                        bindBirthdate(personVitalSnapshot);
                        bindCurrentTown(personVitalSnapshot);
                        bindHomeTown(personVitalSnapshot);
                    }
                }
            }
        });

        db.collection("person_extra").document(personLink)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personExtraData = task.getResult();
                    if(personExtraData.exists()){
                        // horizontal wishlist
                        // firestore array --> JAVA arraylist
                        final ArrayList<String> wishlist = (ArrayList) personExtraData.get("w");
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
                        LinearLayoutManager wishlistLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        rvWishlist.setLayoutManager(wishlistLayoutManager);
                        rvWishlist.setNestedScrollingEnabled(false);
                        WishlistAdapter adapter = new WishlistAdapter(wishlist);
                        rvWishlist.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void bindName(DocumentSnapshot personVitalSnapshot){
        String personName = personVitalSnapshot.getString("n");
        if(personName != null){
            nameTV.setText(personName);
        }
    }

    private void bindBio(DocumentSnapshot personVitalSnapshot){
        String bio = personVitalSnapshot.getString("bio");
        if(bio != null){
            bioTV.setText(bio);
        }
    }

    private void bindPhone(DocumentSnapshot personVitalSnapshot){
        String personPhone = personVitalSnapshot.getString("p");
        if(personPhone != null){
            phoneTV.setText(personPhone);
        }
    }

    private void bindBirthdate(DocumentSnapshot personVitalSnapshot){
        Timestamp personBirthdateTS = personVitalSnapshot.getTimestamp("b");
        String birthdate = getBirthDateString(personBirthdateTS);
        if(birthdate != null){
            String fullText = "Born on: " + birthdate;
            SpannableStringBuilder ssb = getSpannedText(fullText, "Born on: ");
            birthdateTV.setText(ssb);
        }
    }

    private void bindCurrentTown(DocumentSnapshot personVitalSnapshot){
        String personCurrentTown = personVitalSnapshot.getString("ct");
        if(personCurrentTown != null){
            currentLocationTV.setText(personCurrentTown);
        }
    }

    private void bindHomeTown(DocumentSnapshot personVitalSnapshot){
        String personHomeTown = personVitalSnapshot.getString("ht");
        if(personHomeTown != null){
            hometownTV.setText(personHomeTown);
        }
    }

    private String getBirthDateString(Timestamp birthDate){
        if(birthDate == null) return null;
        Date dateObj = birthDate.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        String yearString = Integer.toString(year);
        String monthString = months[month];
        String dateString = Integer.toString(date);
        return dateString + " " + monthString + ", " + yearString;
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
            wishlistItemHolder.bindTo(wishlist.get(i));
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
}
