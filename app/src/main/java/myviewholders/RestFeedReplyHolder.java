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

public class RestFeedReplyHolder extends RestFeedHolder {

    TextView replyHeaderTV;
    CircleImageView commenterImage, replierImage;
    TextView commenterName, theComment, replierName, theReply;
    ImageView likeComment, likeReply, replyToComment, replyTOReply;
    TextView numOfLikesInComment, numOfLikesInReply, numOfRepliesToComment, numOfRepliesToReply;

    public RestFeedReplyHolder(@NonNull View v) {
        super(v);
        replyHeaderTV = v.findViewById(R.id.rest_feed_reply_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        replierImage = v.findViewById(R.id.replier_image);
        commenterName = v.findViewById(R.id.commenter_name);
        theComment = v.findViewById(R.id.the_comment);
        replierName = v.findViewById(R.id.replier_name);
        theReply = v.findViewById(R.id.the_reply);
        likeComment = v.findViewById(R.id.like_comment);
        likeReply = v.findViewById(R.id.like_reply);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        replyTOReply = v.findViewById(R.id.reply_to_reply);
        numOfLikesInComment = v.findViewById(R.id.number_of_likes);
        numOfLikesInReply = v.findViewById(R.id.number_of_likes_in_reply);
        numOfRepliesToComment = v.findViewById(R.id.number_of_replies);
        numOfRepliesToReply = v.findViewById(R.id.number_of_replies_to_reply);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        Map replyBy = (Map) activity.get("w");
        String nameReplyBy = (String) replyBy.get("n");
        replyHeaderTV.setText(nameReplyBy + " replied");

        final Map<String, String> commentData = (Map) activity.get("com");
        final String commentText = commentData.get("text");
        final String nameCommentBy = commentData.get("byn");
        final Map<String, String> replyData = (Map) activity.get("rep");
        String replyText = replyData.get("text");
        final String restFeedLink = activity.getString("wh");

        commenterName.setText(nameCommentBy);
        theComment.setText(commentText);
        replierName.setText(nameReplyBy);
        theReply.setText(replyText);
    }
}
