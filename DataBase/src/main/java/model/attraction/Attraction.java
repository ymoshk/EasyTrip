package model.attraction;


import com.google.maps.model.*;
import constant.Constants;
import model.location.City;
import model.Model;
import util.GoogleMapsApiUtils;

import javax.persistence.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Attraction extends Model {
    protected String formattedAddress;
    protected Geometry geometry;
    protected String name;
    protected URL icon;
    protected String placeId;
    protected float rating;
    @ElementCollection
    protected List<String> types;
    protected OpeningHours openingHours;
    protected String photoUrl;
    protected String vicinity;
    protected boolean permanentlyClosed;
    protected int userRatingsTotal;
    protected String businessStatus;
    protected PlaceType placeType;
    protected PriceLevel priceLevel;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    protected City city;


    public Attraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        this.formattedAddress = searchResultObject.formattedAddress;
        this.geometry = searchResultObject.geometry;
        this.name = searchResultObject.name;
        this.icon = searchResultObject.icon;
        this.placeId = searchResultObject.placeId;
        this.rating = searchResultObject.rating;
        this.types = Arrays.asList(searchResultObject.types);
        this.openingHours = searchResultObject.openingHours;

        if (searchResultObject.photos != null && searchResultObject.photos.length > 0) {
            this.photoUrl = GoogleMapsApiUtils.getPhotoUrl(Constants.getSaharApiKey(), searchResultObject.photos[0].photoReference);
        } else {
            this.photoUrl = null;
        }

        this.vicinity = searchResultObject.vicinity;
        this.permanentlyClosed = searchResultObject.permanentlyClosed;
        this.userRatingsTotal = searchResultObject.userRatingsTotal;
        this.businessStatus = searchResultObject.businessStatus;
        this.placeType = placeType;
        this.priceLevel = priceLevel;

        this.city = city;
    }

    public Attraction() {

    }

    public City getCity() {
        return this.city;
    }

    public PlaceType getPlaceType() {
        return this.placeType;
    }

    public PriceLevel getPriceLevel() {
        return this.priceLevel;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public String getName() {
        return this.name;
    }

    public URL getIcon() {
        return this.icon;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public float getRating() {
        return this.rating;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public OpeningHours getOpeningHours() {
        return this.openingHours;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public String getVicinity() {
        return this.vicinity;
    }

    public boolean isPermanentlyClosed() {
        return this.permanentlyClosed;
    }

    public int getUserRatingsTotal() {
        return this.userRatingsTotal;
    }

    public String getBusinessStatus() {
        return this.businessStatus;
    }


}
