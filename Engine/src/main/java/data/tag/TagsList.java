package data.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagsList {
    private static final List<TripTag> textTags = new ArrayList<>();
    private static final List<TripTag> imageTags = new ArrayList<>();

    static {
        textTags.add(new TripTag(0, "tag1"));
        textTags.add(new TripTag(1, "tag2"));
        textTags.add(new TripTag(2, "tag3"));

        imageTags.add(new TripTag(3, "imgTag1", "imgSource1"));
        imageTags.add(new TripTag(4, "imgTag2", "imgSource2"));
        imageTags.add(new TripTag(5, "imgTag3", "imgSource3"));
    }

    public static HashMap<String, List<TripTag>> getAll() {
        HashMap<String, List<TripTag>> result = new HashMap<>();
        result.put("textTags", textTags);
        result.put("imgTags", imageTags);

        return result;
    }
}
