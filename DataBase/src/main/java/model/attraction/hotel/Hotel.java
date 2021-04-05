package model.attraction.hotel;


import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import container.PriceRange;
import model.location.City;
import model.attraction.Attraction;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Hotel")
public class Hotel extends Attraction {

    public Hotel(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public Hotel() {
        super();
    }
}
