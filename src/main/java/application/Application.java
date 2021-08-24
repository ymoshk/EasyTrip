package application;

import com.google.maps.model.PlaceType;
import connection.DataEngine;
import container.PriceRange;
import model.attraction.Attraction;
import model.attraction.AttractionImage;
import model.location.City;

import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Application is starting...");
        DataEngine data = DataEngine.getInstance();
        City city = data.getCity("Tel Aviv-Yafo").orElse(null);
        List<Attraction> attractionList = data.getAttractions(city.getCityName(), new PriceRange(3));

        AttractionImage image = data.getAttractionImage(attractionList.get(0));


        System.out.println();
    }
}

