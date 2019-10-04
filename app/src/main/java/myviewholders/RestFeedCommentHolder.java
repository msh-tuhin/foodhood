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

public class RestFeedCommentHolder extends RestFeedHolder
        implements CommentInterface{

    private Context mContext;
    private String mNameCommentBy;
    private String mCommentLink;
    private String mCommentText;

    private TextView commentHeaderTV;
    private CircleImageView commenterImage;
    private TextView commenterNameTV;
    private TextView commentTimeTV;
    private TextView commentTV;
    private TextView repliesLinkTV;
    private ImageView likeComment;
    private TextView noOfLikesInComment;
    private ImageView replyToComment;
    private TextView noOfRepliesToComment;

    public RestFeedCommentHolder(@NonNull View v) {
        super(v);
        commentHeaderTV = v.findViewById(R.id.rest_feed_comment_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTV = v.findViewById(R.id.commenter_name);
        commentTimeTV = v.findViewById(R.id.time);
        commentTV = v.findViewById(R.id.the_comment);
        repliesLinkTV = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        noOfLikesInComment = v.findViewById(R.id.number_of_likes);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);
    }

    @Override
    public void bindTo(Context context, DocumentSnapshot activity) {
        super.bindTo(context, activity);
        setPrivateGlobalsIndependent(context, activity);
        bindValues();
        setOnClickListeners();
    }

    private void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    private void setmNameCommentBy(String mNameCommentBy) {
        this.mNameCommentBy = mNameCommentBy;
    }

    private void setmCommentLink(String mCommentLink) {
        this.mCommentLink = mCommentLink;
    }

    private void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    private void setPrivateGlobalsIndependent(Context context, DocumentSnapshot activity){
        Map commentBy = (Map) activity.get("w");
        String nameCommentBy = (String) commentBy.get("n");
        Map<String, String> commentData = (Map) activity.get("com");
        String commentText = commentData.get("text");
        String commentLink = commentData.get("l");

        setmContext(context);
        setmNameCommentBy(nameCommentBy);
        setmCommentText(commentText);
        setmCommentLink(commentLink);
    }

    private void bindValues(){
        bindHeader();
        bindCommentByAvatar();
        bindNameCommentBy();
        bindCommentTime();
        bindComment();
        bindRepliesLink();
        bindLikeCommentIcon();
        bindNoOfLikeInComment();
        bindReplyToCommentIcon();
        bindNoOfRepliesToComment();
    }

    private void setOnClickListeners(){
        setCommentByAvatarOnClickListener();
        setNameCommentByOnClickListener();
        setCommentTimeOnClickListener();
        setCommentOnClickListener();
        setRepliesLinkOnClickListener();
        setLikeCommentIconOnClickListener();
        setNoOfLikeInCommentOnClickListener();
        setReplyToCommentIconOnClickListener();
        setNoOfRepliesToCommentOnClickListener();
        setCommentLayoutOnClickListener();
    }

    @Override
    public void bindHeader() {
        commentHeaderTV.setText(mNameCommentBy + " commented on this");
    }

    @Override
    public void bindCommentByAvatar() {

    }

    @Override
    public void setCommentByAvatarOnClickListener() {

    }

    @Override
    public void bindNameCommentBy() {
        commenterNameTV.setText(mNameCommentBy);
    }

    @Override
    public void setNameCommentByOnClickListener() {

    }

    @Override
    public void bindCommentTime() {

    }

    @Override
    public void setCommentTimeOnClickListener() {

    }

    @Override
    public void bindComment() {
        commentTV.setText(mCommentText);
    }

    @Override
    public void setCommentOnClickListener() {

    }

    @Override
    public void bindRepliesLink() {

    }

    @Override
    public void setRepliesLinkOnClickListener() {

    }

    @Override
    public void bindLikeCommentIcon() {

    }

    @Override
    public void setLikeCommentIconOnClickListener() {

    }

    @Override
    public void bindNoOfLikeInComment() {

    }

    @Override
    public void setNoOfLikeInCommentOnClickListener() {

    }

    @Override
    public void bindReplyToCommentIcon() {

    }

    @Override
    public void setReplyToCommentIconOnClickListener() {

    }

    @Override
    public void bindNoOfRepliesToComment() {

    }

    @Override
    public void setNoOfRepliesToCommentOnClickListener() {

    }

    @Override
    public void setCommentLayoutOnClickListener() {

    }
}
