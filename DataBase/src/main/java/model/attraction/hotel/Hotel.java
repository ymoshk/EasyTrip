package model.attraction.hotel;


import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.location.City;
import model.attraction.Attraction;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Hotel")
public class Hotel extends Attraction {

    public Hotel(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public Hotel() {
        super();
    }

    public Hotel(String name, City city) {
//        this.name = name;
        this.city = city;
    }
}
