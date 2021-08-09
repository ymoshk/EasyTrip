package model.attraction.cafe;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Cafe")
public class Cafe extends Attraction {

    public Cafe(PlacesSearchResult placesSearchResult, PlaceType placeType, PriceRange priceRange, City city){
        super(placesSearchResult, placeType, priceRange, city);
    }

    public Cafe() {super();}
}
