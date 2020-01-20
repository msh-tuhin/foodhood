package models;

import org.json.JSONException;
import org.json.JSONObject;

import myapp.utils.AlgoliaAttributeNames;

public class SelectedPlace {

    public String name;
    public String id;
    public String imageUrl;

    public void setFromJSONObject(JSONObject obj) throws JSONException {
        this.name = obj.getString(AlgoliaAttributeNames.NAME);
        this.id = obj.getString(AlgoliaAttributeNames.ID);
        this.imageUrl = obj.getString(AlgoliaAttributeNames.IMAGE_URL);
    }

}
