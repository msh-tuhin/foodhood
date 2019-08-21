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
import com.example.tuhin.myapplication.R;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import models.NotificationModel;
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
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " likes your comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_COMMENT:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " replied to your comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_COMMENT_ALSO:
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
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " replied to you";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_REPLY_COMMENT_OWNER:
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " replied on your comment";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
            case NotificationTypes.NOTIF_REPLY_REPLY_ALSO:
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
                who = notificationModel.getW();
                name = (String) who.get("n");
                notificationText = name + " likes your reply";
                spannableStringBuilder = getSpannedNotification(notificationText, name);
                notificationTextView.setText(spannableStringBuilder);
                break;
        }

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type){
                    case NotificationTypes.NOTIF_TAGGED_POST:
                    case NotificationTypes.NOTIF_LIKE_POST:
                    case NotificationTypes.NOTIF_LIKE_POST_TAGGED:
                        Intent intent = new Intent(context, FullPost.class);
                        intent.putExtra("postLink", notificationModel.getPostLink());
                        intent.putExtra("entry_point", EntryPoints.NOTIF_LIKE_POST);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_COMMENT_POST:
                    case NotificationTypes.NOTIF_COMMENT_POST_TAGGED:
                    case NotificationTypes.NOTIF_COMMENT_ALSO:
                    case NotificationTypes.NOTIF_LIKE_COMMENT:
                        // EntryPoints.NOTIF_LIKE_COMMENT == EntryPoints.NOTIF_COMMENT_POST

                        intent = new Intent(context, FullPost.class);
                        intent.putExtra("postLink", notificationModel.getPostLink());
                        intent.putExtra("commentLink", notificationModel.getCommentLink());
                        intent.putExtra("entry_point", EntryPoints.NOTIF_COMMENT_POST);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_REPLY_COMMENT:
                    case NotificationTypes.NOTIF_REPLY_COMMENT_ALSO:
                    case NotificationTypes.NOTIF_LIKE_REPLY:
                        intent = new Intent(context, CommentDetail.class);
                        intent.putExtra("postLink", notificationModel.getPostLink());
                        intent.putExtra("commentLink", notificationModel.getCommentLink());
                        intent.putExtra("replyLink", notificationModel.getReplyLink());
                        intent.putExtra("entry_point", EntryPoints.NOTIF_REPLY_COMMENT);
                        context.startActivity(intent);
                        break;
                    case NotificationTypes.NOTIF_REPLY_REPLY:
                    case NotificationTypes.NOTIF_REPLY_REPLY_COMMENT_OWNER:
                    case NotificationTypes.NOTIF_REPLY_REPLY_ALSO:
                        intent = new Intent(context, CommentDetail.class);
                        intent.putExtra("postLink", notificationModel.getPostLink());
                        intent.putExtra("commentLink", notificationModel.getCommentLink());
                        intent.putExtra("replyLink", notificationModel.getOldReplyLink());
                        intent.putExtra("replyToReplyLink", notificationModel.getNewReplyLink());
                        intent.putExtra("entry_point", EntryPoints.NOTIF_REPLY_REPLY);
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
