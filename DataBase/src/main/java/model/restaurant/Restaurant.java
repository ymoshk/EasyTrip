package model.restaurant;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.Attraction;

public class Restaurant extends Attraction {

    public Restaurant(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel) {
        super(searchResultObject, placeType, priceLevel);
    }
}
