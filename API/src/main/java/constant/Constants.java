package constant;

public class Constants {

    private static final String saharApiKey = "AIzaSyA7J13fNA-XT138vseVyVOFmpQ7fhmiKyQ";

    public String getSaharApiKey() {
        return saharApiKey;
    }

    public String getStandardQuery(String placeToSearchType, String location){
        // ex: "Restaurants in London"
        return placeToSearchType + "in " + location;
    }
}
