package application;

import com.google.maps.model.PlaceType;
import connection.DataEngine;
import container.PriceRange;
import model.attraction.Attraction;

import java.util.List;


public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Application is starting...");
        DataEngine data = new DataEngine();

        List<Attraction> attractionList = data.getAttractions(PlaceType.RESTAURANT, "RAMAT GAN", new PriceRange());
    }
}

