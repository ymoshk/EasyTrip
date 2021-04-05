package model.attraction.aquarium;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Aquarium")
public class Aquarium extends Attraction {

    public Aquarium(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public Aquarium() {

    }
}
