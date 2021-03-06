package model.location;

import com.google.maps.model.LatLng;
import model.Model;
import model.attraction.Attraction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "City")
@Cacheable
public class City extends Model {
    @Column(nullable = false)
    String cityName;

    public int getCityNumbeoID() {
        return cityNumbeoID;
    }

    public void setCityNumbeoID(int cityNumbeoID) {
        this.cityNumbeoID = cityNumbeoID;
    }

    int cityNumbeoID;
    LatLng cityCenter;
    double lat;
    double lng;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "country_id", insertable = true, updatable = true, nullable = false)
    Country country;
    double averagePricePerDay;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    List<Airport> airportList;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    List<Attraction> attractionList = new ArrayList<>();

    public City(String name, LatLng latLng, Country countryToUpdate) {
        this.cityName = name;
        this.cityCenter = latLng;
        this.lat = this.cityCenter.lat;
        this.lng = this.cityCenter.lng;
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

    public void removeAttraction(Attraction attractionToRemove) {
        this.attractionList.remove(attractionToRemove);
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
