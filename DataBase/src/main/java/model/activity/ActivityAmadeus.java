package model.activity;

// TODO: put latitude & longitude in a struct, price & currencyCode

import model.Model;
import model.location.City;

import javax.persistence.*;

@Entity
public class ActivityAmadeus extends Model {

    private String name;
    private String shortDescription;
    private double lat;
    private double lng;
    private double rating;
    private String bookingLink;
    private double price;
    private String currencyCode;
    private String pictureLink;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    private City city;

    public ActivityAmadeus() {
    }

    public ActivityAmadeus(String name, String shortDescription, double lat, double lng, double rating,
                           String bookingLink, double price, String currencyCode, String pictureLink, City city) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.bookingLink = bookingLink;
        this.price = price;
        this.currencyCode = currencyCode;
        this.pictureLink = pictureLink;
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setLat(double latitude) {
        this.lat = latitude;
    }

    public void setLng(double longitude) {
        this.lng = longitude;
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

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
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
