package model.attraction;


import com.google.maps.model.*;
import com.google.maps.model.PlaceDetails.*;
import container.PriceRange;
import model.Model;
import model.location.City;
import javax.persistence.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Entity()
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Attraction extends Model {

    private String formattedAddress;
    @Column(length = 1024)
    private Geometry geometry;
    private double lat;
    private double lng;
    @Column(nullable = false)
    private String name;
    private URL icon;
    @Column(nullable = false)
    private String placeId;
    @Column(nullable = false, unique = true)
    private String attractionKey;
    private float rating;
    @ElementCollection
    private List<String> types;
    @Column(length = 1024)
    private OpeningHours openingHours;
    private String photoReference;
    private String vicinity;
    private boolean permanentlyClosed;
    private int userRatingsTotal;
    private String businessStatus;
    @Column(nullable = false)
    private PlaceType placeType;
    private PriceRange priceRange;
    @ManyToOne
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    private City city;
    private PriceLevel priceLevel;
    private String localNumber;
    private String internationalNumber;
    private String website;

    public Attraction(PlacesSearchResult searchResultObject, PlaceType placeType, PriceRange priceRange, City city) {
        this.formattedAddress = searchResultObject.formattedAddress;
        this.geometry = searchResultObject.geometry;
        this.lat = this.geometry.location.lat;
        this.lng = this.geometry.location.lng;
        this.name = searchResultObject.name;
        this.icon = searchResultObject.icon;
        this.placeId = searchResultObject.placeId;
        this.attractionKey = placeType.toString() + " " + searchResultObject.placeId;
        this.rating = searchResultObject.rating;
        this.types = Arrays.asList(searchResultObject.types);
        this.openingHours = searchResultObject.openingHours;

        if (searchResultObject.photos != null && searchResultObject.photos.length > 0) {
            this.photoReference = searchResultObject.photos[0].photoReference;
        } else {
            this.photoReference = null;
        }

        this.vicinity = searchResultObject.vicinity;
        this.permanentlyClosed = searchResultObject.permanentlyClosed;
        this.userRatingsTotal = searchResultObject.userRatingsTotal;
        this.businessStatus = searchResultObject.businessStatus;
        this.placeType = placeType;
        this.priceRange = priceRange;

        this.city = city;
    }

    public Attraction() {

    }

    public double getLat() {
        return lat;
    }

    //TODO - delete setters
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public PlaceType getPlaceType() {
        return this.placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public PriceRange getPriceRange() {
        return this.priceRange;
    }

    public void setPriceRange(PriceRange priceLevel) {
        this.priceRange = priceLevel;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public PriceLevel getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(PriceLevel priceLevel) {
        this.priceLevel = priceLevel;
    }

    public String getLocalNumber() {
        return localNumber;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }

    public String getInternationalNumber() {
        return internationalNumber;
    }

    public void setInternationalNumber(String internationalNumber) {
        this.internationalNumber = internationalNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getIcon() {
        return this.icon;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public float getRating() {
        return this.rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public OpeningHours getOpeningHours() {
        return this.openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getVicinity() {
        return this.vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public boolean isPermanentlyClosed() {
        return this.permanentlyClosed;
    }

    public void setPermanentlyClosed(boolean permanentlyClosed) {
        this.permanentlyClosed = permanentlyClosed;
    }

    public int getUserRatingsTotal() {
        return this.userRatingsTotal;
    }

    public void setUserRatingsTotal(int userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public String getBusinessStatus() {
        return this.businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }
}
