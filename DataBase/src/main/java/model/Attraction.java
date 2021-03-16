package model;


import com.google.maps.model.*;

import java.io.Serializable;
import java.net.URL;

public class Attraction implements Serializable {
    private String formattedAddress;
    private Geometry geometry;
    private String name;
    private URL icon;
    private String placeId;
    private float rating;
    private String[] types;
    private OpeningHours openingHours;
    private Photo[] photos;
    private String vicinity;
    private boolean permanentlyClosed;
    private int userRatingsTotal;
    private String businessStatus;
    private PlaceType placeType;
    private PriceLevel priceLevel;

    public Attraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceLevel priceLevel) {
        this.formattedAddress = searchResultObject.formattedAddress;
        this.geometry = searchResultObject.geometry;
        this.name = searchResultObject.name;
        this.icon = searchResultObject.icon;
        this.placeId = searchResultObject.placeId;
        this.rating = searchResultObject.rating;
        this.types = searchResultObject.types;
        this.openingHours = searchResultObject.openingHours;
        this.photos = searchResultObject.photos;
        this.vicinity = searchResultObject.vicinity;
        this.permanentlyClosed = searchResultObject.permanentlyClosed;
        this.userRatingsTotal = searchResultObject.userRatingsTotal;
        this.businessStatus = searchResultObject.businessStatus;
        this.placeType = placeType;
        this.priceLevel = priceLevel;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public URL getIcon() {
        return icon;
    }

    public String getPlaceId() {
        return placeId;
    }

    public float getRating() {
        return rating;
    }

    public String[] getTypes() {
        return types;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public Photo[] getPhotos() {
        return photos;
    }

    public String getVicinity() {
        return vicinity;
    }

    public boolean isPermanentlyClosed() {
        return permanentlyClosed;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public String getBusinessStatus() {
        PlaceType.
        return businessStatus;
    }



}
