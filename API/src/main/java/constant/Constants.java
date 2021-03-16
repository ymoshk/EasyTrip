package constant;

public class Constants {

    private static final String saharApiKey = "AIzaSyA7J13fNA-XT138vseVyVOFmpQ7fhmiKyQ";
    private static final String barApiKey = "AIzaSyAYtKxi1-RER7t9mS_N3a74B94CDdIeaZY";

    public String getSaharApiKey() {
        return saharApiKey;
    }

    public static String getBarApiKey() { return barApiKey; }

    public String getStandardQuery(String placeToSearchType, String location){
        // ex: "Restaurants in London"
        return placeToSearchType + "in " + location;
    }
}
