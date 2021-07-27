package template.tag;

import com.google.gson.internal.LinkedTreeMap;

public class TripTag {
    public int id;
    public String tagName;
    public String imgSource;

    public TripTag(LinkedTreeMap<String, Object> map) {
        this.id = Integer.parseInt(map.get("id").toString());
        this.tagName = map.get("name").toString();
        this.imgSource = map.get("src").toString();
    }
}
