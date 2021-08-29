package application;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PriceLevel;
import connection.DataEngine;
import container.PriceRange;
import model.attraction.Attraction;
import model.attraction.AttractionImage;
import model.location.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Application is starting...");
        DataEngine data = DataEngine.getInstance();
        City city = data.getCity("Tel Aviv-Yafo").orElse(null);
        List<Attraction> attractionList = data.getAttractions(PlaceType.RESTAURANT, city.getCityName(), new PriceRange(3), PriceLevel.INEXPENSIVE);


        System.out.println(attractionList.size());


        HashMap<String, Attraction> stringAttractionHashMap = new HashMap<>();
        for(Attraction attraction : attractionList){
            if(!stringAttractionHashMap.containsKey(attraction.getPlaceId())){
                stringAttractionHashMap.put(attraction.getPlaceId(), attraction);
            }
        }

        System.out.println(stringAttractionHashMap.size());

//        stringAttractionHashMap.forEach((s, attraction) -> System.out.println(attraction.getName()));
        stringAttractionHashMap.forEach((s, attraction) -> System.out.println(attraction.getName() + "-" + attraction.getPriceLevel()));

        //AttractionImage image = data.getAttractionImage(attractionList.get(0));


        System.out.println();
    }
}

