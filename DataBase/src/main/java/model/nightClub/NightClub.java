package model.nightClub;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "NightClub")
public class NightClub extends Attraction {

    public NightClub(PlacesSearchResult placesSearchResult, PlaceType placeType, PriceRange priceRange, City city){
        super(placesSearchResult, placeType, priceRange, city);
    }

    public NightClub() {super();}
}
