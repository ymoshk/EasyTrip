package model.attraction;


import com.google.maps.model.*;
import constant.Constants;
import model.City;
import model.Model;
import util.GoogleMapsApiUtils;

import java.net.URL;

public abstract class Attraction extends Model {
    private String formattedAddress;
    private Geometry geometry;
    private String name;
    private URL icon;
    private String placeId;
    private float rating;
    private String[] types;
    private OpeningHours openingHours;
    private String photoUrl;
    private String vicinity;
    private boolean permanentlyClosed;
    private int userRatingsTotal;
    private String businessStatus;
    private PlaceType placeType;
    private PriceLevel priceLevel;
    private City city;

    public Attraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel, City city) {
        this.formattedAddress = searchResultObject.formattedAddress;
        this.geometry = searchResultObject.geometry;
        this.name = searchResultObject.name;
        this.icon = searchResultObject.icon;
        this.placeId = searchResultObject.placeId;
        this.rating = searchResultObject.rating;
        this.types = searchResultObject.types;
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

    public String[] getTypes() {
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
