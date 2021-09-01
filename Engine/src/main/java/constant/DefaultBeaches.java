package constant;

import java.util.HashMap;

// This class is optional
// When city added to DB must add manually to map if contains beach
public class DefaultBeaches {
    private static final HashMap<String, Boolean> cityToBeach = new HashMap<>();

    static {
        cityToBeach.put("Tel Aviv-Yafo", true);
        cityToBeach.put("Barcelona", true);
    }

    public static boolean cityHasBeach(String cityName){
        return cityToBeach.containsKey(cityName);
    }
}
