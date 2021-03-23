package model.attraction.museum;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Museum")
public class Museum extends Attraction {

    public Museum(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public Museum() {

    }
}
