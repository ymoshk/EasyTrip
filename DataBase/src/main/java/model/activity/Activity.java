package model.activity;

// TODO: put latitude & longitude in a struct, price & currencyCode

import javax.persistence.*;

@Entity
@Table(name="Activity")
public class Activity {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //@Column(name="ACTIVITY_NAME", length=50, nullable=false)
    private String name;
    private String shortDescription;
    private double latitude;
    private double longitude;
    private double rating;
    private String bookingLink;
    private double price;
    private String currencyCode;
    private String pictureLink;

    public Activity() {
    }

    public Activity(Long id, String name, String shortDescription, double latitude, double longitude,
                    double rating, String bookingLink, double price, String currencyCode, String pictureLink) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.bookingLink = bookingLink;
        this.price = price;
        this.currencyCode = currencyCode;
        this.pictureLink = pictureLink;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setBookingLink(String bookingLink) {
        this.bookingLink = bookingLink;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRating() {
        return rating;
    }

    public String getBookingLink() {
        return bookingLink;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    //optional - private double minimumDuration;  have to be calculated (maybe based on tags: hike --> 1/2 day)
    //optional - private String[] typeArr;  add tags based on name & description
}
