package model.attraction.other;

import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.PriceLevel;
import model.attraction.Attraction;
import model.location.City;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Other_Attraction")
public class OtherAttraction extends Attraction {

    public OtherAttraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        super(searchResultObject, placeType, priceLevel, city);
    }

    public OtherAttraction() {

    }
}
