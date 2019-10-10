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

public class RestFeedCommentHolder extends RestFeedHolder
        implements CommentInterface{

    private Context mContext;
    private String mNameCommentBy;
    private String mLinkCommentBy;
    private String mCommentLink;
    private String mCommentText;
    private String mRestFeedLink;

    private LinearLayout commentLayout;
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
        commentLayout = v.findViewById(R.id.small_comment_layout);
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

    public void setmRestFeedLink(String mRestFeedLink) {
        this.mRestFeedLink = mRestFeedLink;
    }

    public void setmLinkCommentBy(String mLinkCommentBy) {
        this.mLinkCommentBy = mLinkCommentBy;
    }

    private void setPrivateGlobalsIndependent(Context context, DocumentSnapshot activity){
        Map commentBy = (Map) activity.get("w");
        String nameCommentBy = (String) commentBy.get("n");
        String linkCommentBy = (String) commentBy.get("l");
        Map<String, String> commentData = (Map) activity.get("com");
        String commentText = commentData.get("text");
        String commentLink = commentData.get("l");

        setmContext(context);
        setmNameCommentBy(nameCommentBy);
        setmCommentText(commentText);
        setmCommentLink(commentLink);
        setmLinkCommentBy(linkCommentBy);

        String restFeedLink = activity.getString("wh");
        setmRestFeedLink(restFeedLink);
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
        replyToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("reply2comment", "from home RF+comment");
                // needed if reply activity has redundant data for the comment
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("byl", mLinkCommentBy);
                commentMap.put("text", mCommentText);
                commentMap.put("byn", mNameCommentBy);
                commentMap.put("l", mCommentLink);

                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(EntryPoints.R2C_FROM_HOME_RF);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setPostLink(mRestFeedLink);
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
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("comment_detail", "from home RF comment");
                CommentIntentExtra commentIntentExtra = new CommentIntentExtra();
                commentIntentExtra.setEntryPoint(
                        EntryPoints.CLICKED_COMMENT_BODY_FROM_HOME_RF);
                commentIntentExtra.setCommentLink(mCommentLink);
                commentIntentExtra.setPostLink(mRestFeedLink);

                Intent intent = new Intent(mContext, CommentDetail.class);
                intent.putExtra("comment_extra", commentIntentExtra);
                mContext.startActivity(intent);
            }
        });
    }
}
