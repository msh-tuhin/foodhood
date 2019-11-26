package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Map;

public class RestFeedLikeHolder extends RestFeedHolder {

    private String mNameLikedBy;
    TextView restFeedLikeHeader;
    public RestFeedLikeHolder(@NonNull View v) {
        super(v);
        restFeedLikeHeader = v.findViewById(R.id.rest_feed_like_header);
    }

    @Override
    public void bindTo(final Context context, final DocumentSnapshot activity) {
        super.bindTo(context, activity);
        Map restFeedLikedBy = (Map) activity.get("w");
        String nameLikedBy = (String) restFeedLikedBy.get("n");
        mNameLikedBy = nameLikedBy;
    }

    @Override
    public void bindHeader() {
        restFeedLikeHeader.setText(mNameLikedBy + " liked this");
    }

    @Override
    public void refreshHolder() {
        Log.i("refreshing", "restfeedlikeholder");
        super.refreshHolder();
        restFeedLikeHeader.setText("");
    }
}
