package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class RestFeedHolder extends BaseHomeFeedHolder{

    public RestFeedHolder(@NonNull View v) {
        super(v);
    }

    @Override
    public void bindTo(final Context context, final DocumentSnapshot activity) {
        // TODO attach a lifecycleobserver to the context and handle lifecycle event
        super.bindTo(context, activity);
        String restFeedLink = activity.getString("wh");
        DocumentReference restFeedRef = FirebaseFirestore.getInstance().collection("rest_feed")
                .document(restFeedLink);
        restFeedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot restFeed = task.getResult();
                    if(restFeed.exists()){
                        String caption = restFeed.getString("c");
//                        TODO delete next line after database(rest_feed) is updated
                        Map<String, String> who = (Map) activity.get("w");
//                        this is the correct one
//                        Map<String, String> who = (Map) restFeed.get("w");
                        String restaurantName = who.get("n");
                        String linkToRestaurant = who.get("l");
                        SpannableString mSpannableString = Helper.getSpannableStringFromRestaurantName
                                (restaurantName, linkToRestaurant);
//                        postHeader.setText(mSpannableString, TextView.BufferType.SPANNABLE);
//                        postHeader.setMovementMethod(LinkMovementMethod.getInstance());
                        postCaption.setText(caption);
                    }
                }
            }
        });
    }
}
