package model.attraction.topSight;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import container.PriceRange;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TopSight")
public class TopSight extends Attraction {

    public TopSight(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceLevel, City city){
        super(searchResultObject, placeType, priceLevel, city);
    }

    public TopSight() {

    }
}
