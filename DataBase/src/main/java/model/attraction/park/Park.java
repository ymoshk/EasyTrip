package model.attraction.park;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Park")
public class Park extends Attraction {

    public Park(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public Park() {

    }
}
