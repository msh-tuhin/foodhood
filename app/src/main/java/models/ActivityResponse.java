package models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.Map;

@IgnoreExtraProperties
public class ActivityResponse {

    @PropertyName("t")
    public int type;
    @PropertyName("l")
    public String link;
    @PropertyName("ts")
    public Timestamp timestamp;
    @PropertyName("w")
    public Map<String, String> who;

    public ActivityResponse(){
    }

    public ActivityResponse(int type, String link, Timestamp timestamp){
        this.type = type;
        this.link = link;
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getWho() {
        return who;
    }

    public void setWho(Map<String, String> who) {
        this.who = who;
    }
}
