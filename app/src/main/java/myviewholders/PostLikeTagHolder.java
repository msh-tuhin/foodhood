package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Map;

public class PostLikeTagHolder extends HalfPostHolder{

    TextView postLikeTagHeader;

    public PostLikeTagHolder(@NonNull View v) {
        super(v);
        postLikeTagHeader = v.findViewById(R.id.post_like_tag_header);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        Map<String, String> postLikedBy = (Map) activity.get("w");
        String nameLikedBy = postLikedBy.get("n");
        postLikeTagHeader.setText(nameLikedBy + " liked this");
    }
}
