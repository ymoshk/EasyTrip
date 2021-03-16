package model;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.hotel.Hotel;
import model.restaurant.Restaurant;

public class AttractionsFactory {


    public static Attraction getAttraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel)
    {
        Attraction res = null;

        switch (placeType){

            case LODGING:
                res = new Hotel(searchResultObject, placeType, priceLevel);
                break;

            case RESTAURANT:
                res = new Restaurant(searchResultObject, placeType, priceLevel);
                break;

        }

        return res;
    }
}
