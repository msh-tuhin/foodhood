package models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ActualActivity {

    @PropertyName("t")
    public int t;
    @PropertyName("w")
    public Map<String, String> w;
    @PropertyName("wh")
    public String wh;

    public ActualActivity(){
    }

    public ActualActivity(int type, Map<String, String> who, String where){
        this.t = type;
        this.w = who;
        this.wh = where;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public Map<String, String> getW() {
        return w;
    }

    public void setW(Map<String, String> w) {
        this.w = w;
    }

    public String getWh() {
        return wh;
    }

    public void setWh(String wh) {
        this.wh = wh;
    }



}
