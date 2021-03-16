package model.attraction.hotel;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.attraction.Attraction;

public class Hotel extends Attraction {

    public Hotel(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel) {
        super(searchResultObject, placeType, priceLevel);
    }
}
