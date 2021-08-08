package template;

import java.util.HashMap;

public class TypesDefaultDuration {

    // TODO set a different duration for each type
    private static final HashMap<String, Integer> typesDurationMap;
    private static final int MINUTE = 60;
    private static final int DEFAULT = 60;

    static {
        typesDurationMap = new HashMap<>();
        typesDurationMap.put("Restaurant", MINUTE * 90);
    }

    public static int getMinDurationAsSeconds(String type) {
        int result = DEFAULT;

        if (typesDurationMap.containsKey(type)) {
            result = typesDurationMap.get(type);
        }

        return result;
    }
}
