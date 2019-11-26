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

public class PostLikeTagHolder extends HalfPostHolder{

    TextView postLikeTagHeader;
    private String mNameLikedBy;

    public PostLikeTagHolder(@NonNull View v) {
        super(v);
        postLikeTagHeader = v.findViewById(R.id.post_like_tag_header);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        Map<String, String> postLikedBy = (Map) activity.get("w");
        String nameLikedBy = postLikedBy.get("n");
        mNameLikedBy = nameLikedBy;
    }

    @Override
    public void bindHeader() {
        postLikeTagHeader.setText(mNameLikedBy + " liked this");
    }

    @Override
    public void refreshHolder() {
        Log.i("refreshing", "postliketagholder");
        super.refreshHolder();
        postLikeTagHeader.setText("");
    }
}
