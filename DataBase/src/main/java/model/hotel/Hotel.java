package model.hotel;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.Attraction;

public class Hotel extends Attraction {

    public Hotel(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel) {
        super(searchResultObject, placeType, priceLevel);
    }
}
