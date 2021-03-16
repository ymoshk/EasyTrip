package model.attraction;

import city.City;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.attraction.Attraction;
import model.attraction.hotel.Hotel;
import model.attraction.restaurant.Restaurant;

public class AttractionsFactory {


    public static Attraction getAttraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city)
    {
        Attraction res = null;

        switch (placeType){

            case LODGING:
                res = new Hotel(searchResultObject, placeType, priceLevel, city);
                break;

            case RESTAURANT:
                res = new Restaurant(searchResultObject, placeType, priceLevel, city);
                break;

        }

        return res;
    }
}
