package myapp.utils;

public class NotificationTypes {

    // notifications sent when something is done on a post
    public static final int NOTIF_TAGGED_POST = 0;
    public static final int NOTIF_LIKE_POST = 1;
    public static final int NOTIF_LIKE_POST_TAGGED = 2;
    public static final int NOTIF_COMMENT_POST = 3;
    public static final int NOTIF_COMMENT_POST_TAGGED = 4;
    public static final int NOTIF_COMMENT_ALSO = 5;
    public static final int NOTIF_LIKE_COMMENT = 6;
    public static final int NOTIF_REPLY_COMMENT = 7;
    public static final int NOTIF_REPLY_COMMENT_ALSO = 8;
    public static final int NOTIF_REPLY_REPLY = 9;
    public static final int NOTIF_REPLY_REPLY_COMMENT_OWNER = 10;
    public static final int NOTIF_REPLY_REPLY_ALSO = 11;
    public static final int NOTIF_LIKE_REPLY = 12;

    // notifications sent when something is done on a rest feed
    public static final int NOTIF_LIKE_RF = 1001;
    public static final int NOTIF_COMMENT_RF = 1003;
    public static final int NOTIF_COMMENT_ALSO_RF = 1005;
    public static final int NOTIF_LIKE_COMMENT_RF = 1006;
    public static final int NOTIF_REPLY_COMMENT_RF = 1007;
    public static final int NOTIF_REPLY_COMMENT_ALSO_RF = 1008;
    public static final int NOTIF_REPLY_REPLY_RF = 1009;
    public static final int NOTIF_REPLY_REPLY_COMMENT_OWNER_RF = 1010;
    public static final int NOTIF_REPLY_REPLY_ALSO_RF = 1011;
    public static final int NOTIF_LIKE_REPLY_RF = 1012;
    // new type; not thought through
    public static final int NOTIF_REPLY_COMMENT_RF_OWNER = 1013;
    public static final int NOTIF_REPLY_REPLY_RF_OWNER = 1014;

    public static final int NOTIF_FOLLOW = 1099;
}
