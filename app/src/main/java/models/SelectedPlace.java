package models;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectedPlace {

    public String name;
    public String id;

    public void setFromJSONObject(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        this.id = obj.getString("objectID");
    }

}
