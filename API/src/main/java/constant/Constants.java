package constant;

import java.util.Random;

public class Constants {

    private static final String saharApiKey = "AIzaSyA7J13fNA-XT138vseVyVOFmpQ7fhmiKyQ";
    private static final String barApiKey = "AIzaSyAYtKxi1-RER7t9mS_N3a74B94CDdIeaZY";

    public static String getSaharApiKey() {
        return saharApiKey;
    }

    public static String getBarApiKey() { return barApiKey; }

    public String getStandardQuery(String placeToSearchType, String location){
        // ex: "Restaurants in London"
        return placeToSearchType + " in " + location;
    }

//    //TODO -> לראות שאצל כולם יש גישה לכל המפתחות של כולם
    //TODO -> לממש פונקציה שמחזירה כל פעם מפתח אחר
//    public String getRandomApi(){
//        int rand = new Random().nextInt(1);
//        if(rand == 0 ){
//            return saharApiKey;
//        }
//        else{
//            return barApiKey;
//        }
//    }
}
