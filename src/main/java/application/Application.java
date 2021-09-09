package application;

import java.time.LocalTime;

public class Application {

    public static void main(String[] args) throws Exception {
        //        System.out.println("Application is starting...");
        //        DataEngine data = DataEngine.getInstance();
        //
        //
        //        City city = data.getCity("Tel Aviv-Yafo").orElse(null);
        //        List<Attraction> attractionList = data.getAttractions(city.getCityName(), new PriceRange(3),
        //                true);
        //
        //
        //        System.out.println(attractionList.size());
        //
        //
        //        HashMap<String, Attraction> stringAttractionHashMap = new HashMap<>();
        //        for(Attraction attraction : attractionList){
        //            if(!stringAttractionHashMap.containsKey(attraction.getPlaceId())){
        //                stringAttractionHashMap.put(attraction.getPlaceId(), attraction);
        //            }
        //        }
        //
        //        System.out.println(stringAttractionHashMap.size());
        //
        ////        stringAttractionHashMap.forEach((s, attraction) -> System.out.println(attraction.getName()));
        //        stringAttractionHashMap.forEach((s, attraction) -> System.out.println(attraction.getName() + "-" + attraction.getPriceLevel()));
        //
        //        //AttractionImage image = data.getAttractionImage(attractionList.get(0));
        //
        //
        //        System.out.println();
        String time = "15:00";
        String time2 = "15:50";

        LocalTime localTime = LocalTime.parse(time);
        LocalTime localTime2 = LocalTime.parse(time2);

        System.out.println(localTime2.toString());
        //        System.out.println(Duration.between(localTime, localTime2).toMinutes());
    }
}

