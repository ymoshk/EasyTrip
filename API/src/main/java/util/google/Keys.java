package util.google;

import java.util.ArrayList;
import java.util.List;

public class Keys {

    private static final String saharApiKey = "";
    private static final String barApiKey = "";
    private static final String yotamApiKey = "";

    private static final List<String> keys = new ArrayList<>();
    private static int currentIndex = 0;

    static {
        keys.add(saharApiKey);
        keys.add(barApiKey);
        keys.add(yotamApiKey);
    }

    public static String getKey() {
        if (!keys.isEmpty()) {
            String result = keys.get(currentIndex);
            currentIndex = (currentIndex + 1) % keys.size();
            return result;
        }

        return "";
    }
}
