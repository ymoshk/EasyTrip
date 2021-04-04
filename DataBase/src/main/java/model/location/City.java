package model.location;

import com.google.maps.model.LatLng;
import model.Model;
import model.attraction.Attraction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    List<Attraction> attractionList;

    public City(String name, LatLng latLng, Country countryToUpdate) {
        this.cityName = name;
        this.cityCenter = latLng;
        this.country = countryToUpdate;
        setCreateTime(LocalDateTime.now());
        setUpdateTime(LocalDateTime.now());
    }

    public City() {
        setCreateTime(LocalDateTime.now());
        setUpdateTime(LocalDateTime.now());
    }

    public List<Attraction> getAttractionList() {
        return attractionList;
    }

    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

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

    public void addAirportToAirportList(Airport airportToAdd) {
        if (airportList == null)
            airportList = new ArrayList<>();
        airportList.add(airportToAdd);
    }
}
