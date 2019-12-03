package models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SelectedPerson implements Serializable {

    public String name;
    public String id;
    public String imageUrl;

    public void setFromJSONObject(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        this.id = obj.getString("objectID");
        this.imageUrl = obj.getString("image_url");
    }
}
