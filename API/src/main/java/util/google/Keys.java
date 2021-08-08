package util.google;

import java.util.ArrayList;
import java.util.List;

public class Keys {

    private static final String saharApiKey = "AIzaSyA7J13fNA-XT138vseVyVOFmpQ7fhmiKyQ";
    private static final String barApiKey = "AIzaSyAYtKxi1-RER7t9mS_N3a74B94CDdIeaZY";
    private static final List<String> keys = new ArrayList<>();
    private static final int currentIndex = 0;

    static {
        keys.add(saharApiKey);
        keys.add(barApiKey);
    }

    public static String getKey(){
        //TODO - replace between the comment and the next line.
        return saharApiKey;

        //        if (keys.isEmpty()) {
        //            throw new Exception("You must define at least one google API key.");
        //        }
        //
        //        String result = keys.get(currentIndex);
        //        currentIndex = (currentIndex + 1) % keys.size();
        //        return result;
    }
}
