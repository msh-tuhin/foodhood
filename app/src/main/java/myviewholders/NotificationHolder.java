package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.CommentDetail;
import com.example.tuhin.myapplication.FullPost;
import com.example.tuhin.myapplication.FullRestFeed;
import com.example.tuhin.myapplication.R;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import models.NotificationModel;
import myapp.utils.CommentIntentExtra;
import myapp.utils.EntryPoints;
import myapp.utils.NotificationTypes;

public class NotificationHolder extends RecyclerView.ViewHolder {

    private TextView notificationTextView;
    private LinearLayout notificationLayout;

    public NotificationHolder(@NonNull View itemView) {
        super(itemView);
        notificationTextView = itemView.findViewById(R.id.notification_textview);
        notificationLayout = itemView.findViewById(R.id.notification_layout);
    }

    public void bindTo(final NotificationModel notificationModel, final Context context){

        final int type = notificationModel.getT();

        switch (type){
            case NotificationTypes.NOTIF_TAGGED_POST:
                Map<String, Object> who = notificationModel.getW();
                String name = (String) who.get("n");
                String notificationText = "You were tagged in " + name + "'s post";
                SpannableStringBuilder spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_LIKE_POST:
            case NotificationTypes.NOTIF_LIKE_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " likes your post";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_LIKE_POST_TAGGED:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " likes a post you are tagged in";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_COMMENT_POST:
            case NotificationTypes.NOTIF_COMMENT_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " commented on your post";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_COMMENT_POST_TAGGED:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " commented on a post you are tagged in";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_COMMENT_ALSO:
            case NotificationTypes.NOTIF_COMMENT_ALSO_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                String ownerName = notificationModel.getPostOwnerName();
                notificationText = name + " also commented on " + ownerName + "\'s post";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                int ownerNameStart = notificationText.indexOf(ownerName);
                int ownerNameEnd = ownerNameStart + ownerName.length();
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), ownerNameStart, ownerNameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_LIKE_COMMENT:
            case NotificationTypes.NOTIF_LIKE_COMMENT_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " likes your comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_COMMENT:
            case NotificationTypes.NOTIF_REPLY_COMMENT_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " replied to your comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_COMMENT_ALSO:
            case NotificationTypes.NOTIF_REPLY_COMMENT_ALSO_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                ownerName = notificationModel.getCommentOwnerName();
                notificationText = name + " also replied to " + ownerName + "\'s comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                ownerNameStart = notificationText.indexOf(ownerName);
                ownerNameEnd = ownerNameStart + ownerName.length();
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), ownerNameStart, ownerNameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_REPLY:
            case NotificationTypes.NOTIF_REPLY_REPLY_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " replied to you";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_REPLY_COMMENT_OWNER:
            case NotificationTypes.NOTIF_REPLY_REPLY_COMMENT_OWNER_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " replied on your comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_REPLY_ALSO:
            case NotificationTypes.NOTIF_REPLY_REPLY_ALSO_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                ownerName = notificationModel.getCommentOwnerName();
                notificationText = name + " alos replied on " + ownerName + "\'s comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                ownerNameStart = notificationText.indexOf(ownerName);
                ownerNameEnd = ownerNameStart + ownerName.length();
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), ownerNameStart, ownerNameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_LIKE_REPLY:
            case NotificationTypes.NOTIF_LIKE_REPLY_RF:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " likes your reply";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_COMMENT_RF_OWNER:
                who = notificationModel.getW();
                name = (String) who.get("n");
                ownerName = notificationModel.getCommentOwnerName();
                notificationText = name + " replied to " + ownerName + "\'s comment in your post";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                ownerNameStart = notificationText.indexOf(ownerName);
                ownerNameEnd = ownerNameStart + ownerName.length();
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), ownerNameStart, ownerNameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_REPLY_RF_OWNER:
                who = notificationModel.getW();
                name = (String) who.get("n");
                ownerName = notificationModel.getReplyOwnerName();
                notificationText = name + " replied to " + ownerName + " in your post";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                ownerNameStart = notificationText.indexOf(ownerName);
                ownerNameEnd = ownerNameStart + ownerName.length();
                spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), ownerNameStart, ownerNameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                notificationTextView.setText(spannableStringBuilder);
                break;
        }

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentIntentExtra commentIntentExtra;
                Intent intent;
                String intentExtraName = "comment_extra";
                switch (type){
                    case NotificationTypes.NOTIF_TAGGED_POST:
                    case NotificationTypes.NOTIF_LIKE_POST:
                    case NotificationTypes.NOTIF_LIKE_POST_TAGGED:
                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_LIKE_POST);

                        intent = new Intent(context, FullPost.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_COMMENT_POST:
                    case NotificationTypes.NOTIF_COMMENT_POST_TAGGED:
                    case NotificationTypes.NOTIF_COMMENT_ALSO:
                    case NotificationTypes.NOTIF_LIKE_COMMENT:
                        // EntryPoints.NOTIF_LIKE_COMMENT == EntryPoints.NOTIF_COMMENT_POST

                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_COMMENT_POST);
                        commentIntentExtra.setCommentLink(notificationModel.getCommentLink());
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());

                        intent = new Intent(context, FullPost.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_REPLY_COMMENT:
                    case NotificationTypes.NOTIF_REPLY_COMMENT_ALSO:
                    case NotificationTypes.NOTIF_LIKE_REPLY:
                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_REPLY_COMMENT);
                        commentIntentExtra.setCommentLink(notificationModel.getCommentLink());
                        commentIntentExtra.setReplyLink(notificationModel.getReplyLink());
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());

                        intent = new Intent(context, CommentDetail.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_REPLY_REPLY:
                    case NotificationTypes.NOTIF_REPLY_REPLY_COMMENT_OWNER:
                    case NotificationTypes.NOTIF_REPLY_REPLY_ALSO:
                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_REPLY_REPLY);
                        commentIntentExtra.setCommentLink(notificationModel.getCommentLink());
                        commentIntentExtra.setReplyLink(notificationModel.getOldReplyLink());
                        commentIntentExtra.setReplyToReplyLink(notificationModel.getNewReplyLink());
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());

                        intent = new Intent(context, CommentDetail.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;

                    // for rest feeds
                    case NotificationTypes.NOTIF_LIKE_RF:
                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_LIKE_RF);

                        intent = new Intent(context, FullRestFeed.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_COMMENT_RF:
                    case NotificationTypes.NOTIF_COMMENT_ALSO_RF:
                    case NotificationTypes.NOTIF_LIKE_COMMENT_RF:
                        // EntryPoints.NOTIF_LIKE_COMMENT_RF == EntryPoints.NOTIF_COMMENT_RF

                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_COMMENT_RF);
                        commentIntentExtra.setCommentLink(notificationModel.getCommentLink());
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());

                        intent = new Intent(context, FullRestFeed.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_REPLY_COMMENT_RF:
                    case NotificationTypes.NOTIF_REPLY_COMMENT_ALSO_RF:
                    case NotificationTypes.NOTIF_LIKE_REPLY_RF:
                    case NotificationTypes.NOTIF_REPLY_COMMENT_RF_OWNER:
                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_REPLY_COMMENT_RF);
                        commentIntentExtra.setCommentLink(notificationModel.getCommentLink());
                        commentIntentExtra.setReplyLink(notificationModel.getReplyLink());
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());

                        intent = new Intent(context, CommentDetail.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_REPLY_REPLY_RF:
                    case NotificationTypes.NOTIF_REPLY_REPLY_COMMENT_OWNER_RF:
                    case NotificationTypes.NOTIF_REPLY_REPLY_ALSO_RF:
                    case NotificationTypes.NOTIF_REPLY_REPLY_RF_OWNER:
                        commentIntentExtra = new CommentIntentExtra();
                        commentIntentExtra.setEntryPoint(EntryPoints.NOTIF_REPLY_REPLY_RF);
                        commentIntentExtra.setCommentLink(notificationModel.getCommentLink());
                        commentIntentExtra.setReplyLink(notificationModel.getOldReplyLink());
                        commentIntentExtra.setReplyToReplyLink(notificationModel.getNewReplyLink());
                        commentIntentExtra.setPostLink(notificationModel.getPostLink());

                        intent = new Intent(context, CommentDetail.class);
                        intent.putExtra(intentExtraName, commentIntentExtra);
                        context.startActivity(intent);
                        break;
                }
            }
        });
    }

    private SpannableStringBuilder getSpannedNotification(String notificationText, String name){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(notificationText);
        int start = notificationText.indexOf(name);
        int end = start + name.length();
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}
