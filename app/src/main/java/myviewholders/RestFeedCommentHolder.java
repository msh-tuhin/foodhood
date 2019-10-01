package myviewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

public class RestFeedCommentHolder extends RestFeedHolder {

    TextView commentHeaderTV;
    CircleImageView commenterImage;
    TextView commenterName, theComment;
    TextView noOfLikesOnComment, noOfRepliesToComment;
    ImageView likeComment, replyToComment;

    public RestFeedCommentHolder(@NonNull View v) {
        super(v);
        commentHeaderTV = v.findViewById(R.id.rest_feed_comment_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterName = v.findViewById(R.id.commenter_name);
        theComment = v.findViewById(R.id.the_comment);
        noOfLikesOnComment = v.findViewById(R.id.number_of_likes);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        Map commentBy = (Map) activity.get("w");
        String nameCommentBy = (String) commentBy.get("n");
        commentHeaderTV.setText(nameCommentBy + " commented on this");

        final Map<String, String> commenter = (Map) activity.get("w");
        String nameOfCommenter = commenter.get("n");
        final Map<String, String> commentData = (Map) activity.get("com");
        final String commentText = commentData.get("text");
        final String commentLink = commentData.get("l");
        final String restFeedLink = activity.getString("wh");

        commenterName.setText(nameOfCommenter);
        theComment.setText(commentText);
    }
}
