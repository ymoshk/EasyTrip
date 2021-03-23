package model;

import com.google.maps.model.LatLng;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "City")
@Cacheable
public class City extends Model {
    String cityName;
    LatLng cityCenter;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id", insertable = true, updatable = true)
    Country country;
    double averagePricePerDay;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
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

//    public Country getCountry() {
//        return country;
//    }
//
//    public void setCountry(Country country) {
//        this.country = country;
//    }

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
