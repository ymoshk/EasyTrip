package model.attraction;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.location.City;
import model.attraction.hotel.Hotel;

public class AttractionsFactory {


    public static Attraction getAttraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        Attraction res = null;

        switch (placeType) {

            case LODGING:
                res = new Hotel(searchResultObject, placeType, priceLevel, city);
                break;

            case RESTAURANT:
//                res = new Restaurant(searchResultObject, placeType, priceLevel, city);
//                break;

        }

        return res;
    }
}
