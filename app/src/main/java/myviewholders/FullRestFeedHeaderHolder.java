package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.tuhin.myapplication.RestDetail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import androidx.annotation.NonNull;

public class FullRestFeedHeaderHolder extends RestFeedHolder {
    public FullRestFeedHeaderHolder(@NonNull View v) {
        super(v);
    }

    public void bindTo(final Context context, Task<DocumentSnapshot> taskPost, final String restFeedLink){
        Log.i("bindTo", this.getClass().toString());
        taskPost.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot restFeed) {
                if(restFeed.exists()){
                    String caption = restFeed.getString("c");
//                        this is the correct one
//                    Map<String, String> who = (Map) restFeed.get("w");
//                    String restaurantName = who.get("n");
//                    final String linkToRestaurant = who.get("l");

//                    restaurantNameTV.setText(restaurantName);
                    captionTV.setText(caption);

                    restaurantNameTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(context, RestDetail.class);
//                            intent.putExtra("restaurantLink", linkToRestaurant);
//                            context.startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
