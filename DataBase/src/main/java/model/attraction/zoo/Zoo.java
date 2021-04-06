package model.attraction.zoo;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Zoo")
public class Zoo extends Attraction {

    public Zoo(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public Zoo() {

    }
}
