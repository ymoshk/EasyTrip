package application;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PriceLevel;
import connection.DataEngine;
import model.attraction.Attraction;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application is starting...");
        DataEngine data = new DataEngine();

        List<Attraction> attractionList = data.getAttractions(PlaceType.RESTAURANT,"Shoham", PriceLevel.EXPENSIVE);
    }
}

