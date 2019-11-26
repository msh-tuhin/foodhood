package myviewholders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import androidx.annotation.NonNull;

public class PostTagHolder extends HalfPostHolder {

    TextView postTagHeader;
    private String mNameTagged;

    public PostTagHolder(@NonNull View v) {
        super(v);
        postTagHeader = v.findViewById(R.id.post_like_tag_header);
    }

    public void bindTo(Context context, DocumentSnapshot activity, ActivityResponse friendsActivity) {
        super.bindTo(context, activity);
        Map<String, String> taggedPerson = (Map) friendsActivity.getWho();
        String taggedPersonName = taggedPerson.get("n");
        mNameTagged = taggedPersonName;
    }

    @Override
    public void bindHeader() {
        postTagHeader.setText(mNameTagged + " was tagged");
    }

    @Override
    public void refreshHolder() {
        Log.i("refreshing", "posttagholder");
        super.refreshHolder();
        postTagHeader.setText("");
    }
}
