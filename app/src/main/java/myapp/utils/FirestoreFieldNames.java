package myapp.utils;

public class FirestoreFieldNames {
    public static final String ACTIVITIES_TYPE = "t";
    public static final String ACTIVITIES_CREATOR_MAP = "w";
    public static final String ACTIVITIES_CREATOR_NAME = "n";
    public static final String ACTIVITIES_CREATOR_LINK = "l";
    // where the activity happened(post or rest feed link)
    public static final String ACTIVITIES_PARENT_POST_RF = "wh";
    public static final String ACTIVITIES_COMMENT_MAP = "com";
    public static final String ACTIVITIES_COMMENT_LINK = "l";
    public static final String ACTIVITIES_COMMENT_TEXT = "text";
    public static final String ACTIVITIES_COMMENT_BY_LINK = "byl";
    public static final String ACTIVITIES_COMMENT_BY_NAME = "byn";
    public static final String ACTIVITIES_REPLY_MAP = "rep";
    public static final String ACTIVITIES_REPLY_LINK = "l";
    public static final String ACTIVITIES_REPLY_TEXT = "text";

    public static final String COMMENTS_LINK = "comLink";
    public static final String COMMENTS_TYPE = "type";
    public static final String COMMENTS_REPLY_LINK = "replyLink";
    public static final String COMMENTS_REPLY_TO = "replyTo";
    public static final String COMMENTS_TEXT = "te";
    public static final String COMMENTS_LIKES = "l";
    public static final String COMMENTS_REPLIES = "r";
    public static final String COMMENTS_REPLIES_BY = "rb";
    public static final String COMMENTS_CREATOR_MAP = "w";
    public static final String COMMENTS_CREATOR_LINK = "l";
    public static final String COMMENTS_CREATOR_NAME = "n";
    public static final String COMMENTS_CREATOR_TYPE = "t";
    // the post or rest feed link where the comment was given
    public static final String COMMENTS_PARENT_POST_RF = "wh";

    public static final String DISH_VITAL_NAME = "n";
    public static final String DISH_VITAL_NUMBER_OF_RATING = "npr";
    // public static final String DISH_VITAL_NUMBER_OF_WISHERS = "num_wishlist";
    public static final String DISH_VITAL_NUMBER_OF_WISHERS = "nw";
    public static final String DISH_VITAL_PRICE = "p";
    public static final String DISH_VITAL_COVER_PICTURE = "cp";
    public static final String DISH_VITAL_RESTAURANT_MAP = "re";
    public static final String DISH_VITAL_RESTAURANT_LINK = "l";
    public static final String DISH_VITAL_RESTAURANT_NAME = "n";
    public static final String DISH_VITAL_TOTAL_RATING = "tr";
    public static final String DISH_VITAL_CATEGORIES = "c";
    public static final String DISH_VITAL_DESCRIPTION = "d";

    public static final String DISHES_ARRAY = "a";

    public static final String EMAIL_TYPE_IS_FOR_PERSON = "forPerson";

    public static final String FEEDBACKS_RATING = "r";
    public static final String FEEDBACKS_REVIEW = "re";
    public static final String FEEDBACKS_TYPE = "t";
    public static final String FEEDBACKS_TIMESTAMP = "ts";
    public static final String FEEDBACKS_CREATOR = "w";
    public static final String FEEDBACKS_FOR = "wh";
    public static final String FEEDBACKS_HAS_REVIEW = "hre";

    public static final String FEEDBACKS_LIST_ARRAY = "a";

    public static final String FOLLOWERS_ARRAY = "a";

    public static final String FOLLOWINGS_ARRAY = "a";

    public static final String FOLLOWONG_RESTAURANTS_ARRAY = "a";

    public static final String FRIENDS_ACTIVITIES_ACTIVITY_LINK = "l";
    public static final String FRIENDS_ACTIVITIES_TYPE = "t";
    public static final String FRIENDS_ACTIVITIES_TIMESTAMP = "ts";

    public static final String IN_TIMELINE_ARRAY = "a";

    public static final String IN_WALL_ARRAY = "a";

    public static final String LIKED_ONCE_ARRAY = "a";

    public static final String NOTIFICATIONS_TYPE = "t";
    public static final String NOTIFICATIONS_TIMESTAMP = "ts";
    public static final String NOTIFICATIONS_CREATOR_MAP = "w";
    public static final String NOTIFICATIONS_CREATOR_LINK = "l";
    public static final String NOTIFICATIONS_CREATOR_NAME = "n";
    public static final String NOTIFICATIONS_POST_LINK = "postLink";
    public static final String NOTIFICATIONS_COMMENT_LINK = "commentLink";
    public static final String NOTIFICATIONS_REPLY_LINK = "replyLink";
    public static final String NOTIFICATIONS_OLD_REPLY_LINK = "oldReplyLink";
    public static final String NOTIFICATIONS_NEW_REPLY_LINK = "newReplyLink";

    public static final String OWN_ACTIVITIES_ACTIVITY_LINK = "l";
    public static final String OWN_ACTIVITIES_TYPE = "t";
    public static final String OWN_ACTIVITIES_TIMESTAMP = "ts";

    public static final String PERSON_VITAL_NAME = "n";
    public static final String PERSON_VITAL_CURRENT_TOWN = "ct";
    public static final String PERSON_VITAL_HOME_TOWN = "ht";
    public static final String PERSON_VITAL_BIO = "bio";
    public static final String PERSON_VITAL_NUMBER_FOLLOWED_BY = "nfb";
    public static final String PERSON_VITAL_NUMBER_FOLLOWING_RESTAURANTS = "nfr";
    public static final String PERSON_VITAL_NUMBER_FOLLOWINGS = "nf";
    public static final String PERSON_VITAL_PHONE = "phone";
    public static final String PERSON_VITAL_COVER_PICTURE = "cp";
    public static final String PERSON_VITAL_PROFILE_PICTURE = "pp";
    public static final String PERSON_VITAL_BIRTHDATE = "b";

    public static final String POSTS_CAPTION = "c";
    public static final String POSTS_COMMENTS = "coms";
    public static final String POSTS_COMMENTS_BY = "cb";
    public static final String POSTS_DISHES = "d";
    public static final String POSTS_DISHES_FEEDBACKS = "f";
    public static final String POSTS_IMAGES = "i";
    public static final String POSTS_LIKES = "l";
    public static final String POSTS_RESTAURANT_MAP = "r";
    public static final String POSTS_RESTAURANT_LINK = "l";
    public static final String POSTS_RESTAURANT_NAME = "n";
    public static final String POSTS_RESTAURANT_FEEDBACK = "rf";
    public static final String POSTS_TAGGED_PEOPLE = "tp";
    public static final String POSTS_CREATOR_MAP = "w";
    public static final String POSTS_CREATOR_LINK = "l";
    public static final String POSTS_CREATOR_NAME = "n";
    public static final String POSTS_TIMESTAMP = "ts";

    public static final String PROFILE_CREATED_ARRAY = "a";

    public static final String REST_FEED_CAPTION = "c";
    public static final String REST_FEED_COMMENTS = "coms";
    public static final String REST_FEED_COMMENTS_BY = "cb";
    public static final String REST_FEED_IMAGES = "i";
    public static final String REST_FEED_LIKES = "l";
    public static final String REST_FEED_CREATOR_MAP = "w";
    public static final String REST_FEED_CREATOR_LINK = "l";
    public static final String REST_FEED_CREATOR_NAME = "n";
    public static final String REST_FEED_TIMESTAMP = "ts";

    public static final String REST_VITAL_NAME = "n";
    public static final String REST_VITAL_ADDRESS = "a";
    public static final String REST_VITAL_EMAIL = "e";
    public static final String REST_VITAL_PHONE = "p";
    public static final String REST_VITAL_WEBSITE = "w";
    public static final String REST_VITAL_COVER_PICTURE = "cp";
    public static final String REST_VITAL_NUMBER_FOLLOWED_BY = "nfb";
    public static final String REST_VITAL_NUMBER_OF_RATINGS = "npr";
    public static final String REST_VITAL_TOTAL_RATING = "tr";
    public static final String REST_VITAL_TOWN = "t";
    public static final String REST_VITAL_LOCTION_MAP = "loc";
    public static final String REST_VITAL_LATITUDE = "lat";
    public static final String REST_VITAL_LONGITUDE = "lng";

    public static final String WISHERS_ARRAY = "a";

    public static final String WISHLIST_ARRAY = "a";
}
