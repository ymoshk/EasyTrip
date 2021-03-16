package model;

import com.google.maps.model.LatLng;

import java.util.List;

public class City extends Model {
    String cityName;
    LatLng cityCenter;
    Country country;
    double averagePricePerDay;
    List<Airport> airportList;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public LatLng getCityCenter() {
        return cityCenter;
    }

    public void setCityCenter(LatLng cityCenter) {
        this.cityCenter = cityCenter;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public double getAveragePricePerDay() {
        return averagePricePerDay;
    }

    public void setAveragePricePerDay(double averagePricePerDay) {
        this.averagePricePerDay = averagePricePerDay;
    }

    public List<Airport> getAirportList() {
        return airportList;
    }

    public void setAirportList(List<Airport> airportList) {
        this.airportList = airportList;
    }
}
