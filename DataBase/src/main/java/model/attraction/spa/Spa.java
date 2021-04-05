package model.attraction.spa;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Spa")
public class Spa extends Attraction {

    public Spa(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceRange, City city) {
        super(searchResultObject, placeType, priceRange, city);
    }

    public Spa() {

    }
}
