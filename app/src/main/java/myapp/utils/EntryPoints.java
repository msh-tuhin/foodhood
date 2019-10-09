package myapp.utils;

public class EntryPoints {

    /* number 1-18 are reserved for post and rest feed
       odd numbers are for post
       even numbers are for rest feed
     */

    public static final int CLICKED_GO_TO_FULL_POST = 1;
    public static final int COMMENT_ON_HOME_POST = 3;
    public static final int COMMENT_ON_FULL_POST = 5;
    public static final int CLICKED_COMMENT_BODY_FROM_HOME_POST = 7;
    public static final int CLICKED_COMMENT_REPLY_BODY_FROM_HOME_POST = 9;
    public static final int CLICKED_COMMENT_BODY_FROM_FULL_POST = 11;
    public static final int R2C_FROM_HOME_POST = 13;
    public static final int R2R_FROM_HOME_POST = 15;
    public static final int R2C_FROM_FULL_POST = 17;

    public static final int R2C_FROM_CD = 31;
    public static final int R2R_FROM_CD = 32;

    public static final int HOME_PAGE_RF = 100;
    public static final int CLICKED_GO_TO_FULL_RF = 2;
    public static final int COMMENT_ON_HOME_RF = 4;
    public static final int COMMENT_ON_FULL_RF = 6;
    public static final int CLICKED_COMMENT_BODY_FROM_HOME_RF = 8;
    public static final int CLICKED_COMMENT_REPLY_BODY_FROM_HOME_RF = 10;
    public static final int CLICKED_COMMENT_BODY_FROM_FULL_RF = 12;
    public static final int R2C_FROM_HOME_RF = 14;
    public static final int R2R_FROM_HOME_RF = 16;
    public static final int R2C_FROM_FULL_RF = 18;

    public static final int NOTIF_LIKE_POST = 51;
    public static final int NOTIF_TAGGED_POST = NOTIF_LIKE_POST;
    public static final int NOTIF_COMMENT_POST = 52;
    public static final int NOTIF_LIKE_COMMENT = NOTIF_COMMENT_POST;
    public static final int NOTIF_REPLY_COMMENT = 53;
    public static final int NOTIF_REPLY_REPLY = 54;

}
