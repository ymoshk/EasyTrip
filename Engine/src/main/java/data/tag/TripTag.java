package data.tag;

public class TripTag {
    public int id;
    public String tagName;
    public String imgSource;

    public TripTag(int id, String tagName, String imgSource) {
        this.id = id;
        this.tagName = tagName;
        this.imgSource = imgSource;
    }

    public TripTag(int id, String tagName) {
        this.id = id;
        this.tagName = tagName;
        this.imgSource = null;
    }
}
