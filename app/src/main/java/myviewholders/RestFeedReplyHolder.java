package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.R;
import com.example.tuhin.myapplication.WriteComment;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;

public class RestFeedReplyHolder extends RestFeedHolder
        implements CommentInterface, ReplyInterface{

    private Context mContext;
    private String mCommentText;
    private String mNameCommentBy;
    private String mLinkCommentBy;
    private String mReplyText;
    private String mNameReplyBy;
    private String mRestFeedLink;
    private String mCommentLink;
    private String mReplyLink;

    private LinearLayout commentReplyLayout;
    private TextView replyHeaderTV;
    private CircleImageView commenterImage;
    private TextView commenterNameTV;
    private TextView commentTimeTV;
    private TextView commentTV;
    private TextView repliesLinkTV;
    private ImageView likeComment;
    private TextView noOfLikesInComment;
    private ImageView replyToComment;
    private TextView noOfRepliesToComment;

    CircleImageView replierImage;
    TextView replierNameTV;
    TextView replyTime;
    TextView replyingToTV;
    TextView replyTV;
    ImageView likeReply;
    TextView numOfLikesInReply;
    ImageView replyTOReply;
    TextView numOfRepliesToReply;

    public RestFeedReplyHolder(@NonNull View v) {
        super(v);
        commentReplyLayout = v.findViewById(R.id.comment_reply_layout);
        replyHeaderTV = v.findViewById(R.id.rest_feed_reply_header);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTV = v.findViewById(R.id.commenter_name);
        commentTimeTV = v.findViewById(R.id.time);
        commentTV = v.findViewById(R.id.the_comment);
        repliesLinkTV = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        noOfLikesInComment = v.findViewById(R.id.number_of_likes);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        noOfRepliesToComment = v.findViewById(R.id.number_of_replies);

        replierImage = v.findViewById(R.id.replier_image);
        replierNameTV = v.findViewById(R.id.replier_name);
        replyTime = v.findViewById(R.id.time_reply);
        replyingToTV = v.findViewById(R.id.replying_to);
        replyTV = v.findViewById(R.id.the_reply);
        likeReply = v.findViewById(R.id.like_reply);
        numOfLikesInReply = v.findViewById(R.id.number_of_likes_in_reply);
        replyTOReply = v.findViewById(R.id.reply_to_reply);
        numOfRepliesToReply = v.findViewById(R.id.number_of_replies_to_reply);
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

    private void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    private void setmNameCommentBy(String mNameCommentBy) {
        this.mNameCommentBy = mNameCommentBy;
    }

    private void setmLinkCommentBy(String mLinkCommentBy) {
        this.mLinkCommentBy = mLinkCommentBy;
    }

    private void setmReplyText(String mReplyText) {
        this.mReplyText = mReplyText;
    }

    private void setmNameReplyBy(String mNameReplyBy) {
        this.mNameReplyBy = mNameReplyBy;
    }

    private void setmRestFeedLink(String mRestFeedLink) {
        this.mRestFeedLink = mRestFeedLink;
    }

    private void setmCommentLink(String mCommentLink) {
        this.mCommentLink = mCommentLink;
    }

    private void setmReplyLink(String mReplyLink) {
        this.mReplyLink = mReplyLink;
    }

    private void setPrivateGlobalsIndependent(Context context, DocumentSnapshot activity){
        Map replyBy = (Map) activity.get("w");
        String nameReplyBy = (String) replyBy.get("n");

        Map<String, String> commentData = (Map) activity.get("com");
        String commentText = commentData.get("text");
        String commentLink = commentData.get("l");
        String nameCommentBy = commentData.get("byn");
        String linkCommentBy = commentData.get("byl");
        Map<String, String> replyData = (Map) activity.get("rep");
        String replyText = replyData.get("text");
        String replyLink = replyData.get("l");

        setmContext(context);
        setmNameCommentBy(nameCommentBy);
        setmLinkCommentBy(linkCommentBy);
        setmCommentText(commentText);
        setmNameReplyBy(nameReplyBy);
        setmReplyText(replyText);
        setmCommentLink(commentLink);
        setmReplyLink(replyLink);
        setmRestFeedLink(activity.getString("wh"));
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

        bindReplyByAvatar();
        bindNameReplyBy();
        bindReplyTime();
        bindReplyingToLink();
        bindReply();
        bindLikeReplyIcon();
        bindNoOfLikeInReply();
        bindReplyToReplyIcon();
        bindNoOfRepliesToReply();
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

        setReplyByAvatarOnClickListener();
        setNameReplyByOnClickListener();
        setReplyTimeOnClickListener();
        setReplyingToLinkOnClickListener();
        setReplyOnClickListener();
        setLikeReplyIconOnClickListener();
        setNoOfLikeInReplyOnClickListener();
        setReplyToReplyIconOnClickListener();
        setNoOfRepliesToReplyOnClickListener();

        setReplyLayoutOnClickListener();
        setCommentReplyLayoutOnClickListener();
    }

    @Override
    public void bindHeader() {
        replyHeaderTV.setText(mNameReplyBy + " replied");
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
        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply2comment", "from home reply");
                // needed if reply activity has redundant data for the comment
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mLinkCommentBy);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mNameCommentBy);
                commentMap.put("l", mCommentLink);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setCommentMap(commentMap);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
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

    @Override
    public void bindReplyByAvatar() {

    }

    @Override
    public void setReplyByAvatarOnClickListener() {

    }

    @Override
    public void bindNameReplyBy() {
        replierNameTV.setText(mNameReplyBy);
    }

    @Override
    public void setNameReplyByOnClickListener() {

    }

    @Override
    public void bindReplyTime() {

    }

    @Override
    public void setReplyTimeOnClickListener() {

    }

    @Override
    public void bindReplyingToLink() {

    }

    @Override
    public void setReplyingToLinkOnClickListener() {

    }

    @Override
    public void bindReply() {
        replyTV.setText(mReplyText);
    }

    @Override
    public void setReplyOnClickListener() {

    }

    @Override
    public void bindLikeReplyIcon() {

    }

    @Override
    public void setLikeReplyIconOnClickListener() {

    }

    @Override
    public void bindNoOfLikeInReply() {

    }

    @Override
    public void setNoOfLikeInReplyOnClickListener() {

    }

    @Override
    public void bindReplyToReplyIcon() {

    }

    @Override
    public void setReplyToReplyIconOnClickListener() {
        replyTOReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply2reply", "from home rf+reply");

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2R_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setReplyLink(mReplyLink);

                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindNoOfRepliesToReply() {

    }

    @Override
    public void setNoOfRepliesToReplyOnClickListener() {

    }

    @Override
    public void setReplyLayoutOnClickListener() {

    }

    @Override
    public void setCommentReplyLayoutOnClickListener() {
        commentReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home RF reply");
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_REPLY_BODY_FROM_HOME_RF);
                commentIntentExtra.setPostLink(mRestFeedLink);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setReplyLink(mReplyLink);

                Intent intent = new Intent(mContext, CommentDetail.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }
}
