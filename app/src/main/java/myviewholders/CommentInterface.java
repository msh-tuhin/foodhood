package myviewholders;

import android.content.Context;

import java.util.Map;

public interface CommentInterface {
    void bindCommentByAvatar();
    void setCommentByAvatarOnClickListener();
    void bindNameCommentBy(String name);
    void setNameCommentByOnClickListener();
    void bindCommentTime();
    void bindComment(String comment);
    void setRepliesLinkOnClickListener();
    void bindLikeCommentIcon();
    void setLikeCommentIconOnClickListener(Context context, String commentLink, String postLink);
    void bindNoOfLikeInComment();
    void setNoOfLikeInCommentOnClickListener();
    void setReplyToCommentIconOnClickListener(Context context,
                                              Map<String, String> commenter,
                                              String commentText,
                                              String commentLink,
                                              String postLink);
    void bindNoOfRepliesToComment();
    void setNoOfRepliesToCommentOnClickListener();
    void setCommentLayoutOnClickListener(Context context, String commentLink, String postLink);
}
