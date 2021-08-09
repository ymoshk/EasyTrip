package model.attraction.bar;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Bar")
public class Bar extends Attraction {

    public Bar(PlacesSearchResult placesSearchResult, PlaceType placeType, PriceRange priceRange, City city){
        super(placesSearchResult, placeType, priceRange, city);
    }

    public Bar() {super ();}
}
