package model.location;

import com.google.maps.model.LatLng;
import model.Model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Airport")
public class Airport extends Model implements Serializable{
    String airportName;
    @ManyToOne
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    City city;
    String IATA;
    @Column(unique = true)
    String threeLetterCode;
    LatLng airportLocation;

    public Airport(String airportName, City city, String IATA, String threeLetterCode, LatLng airportLocation) {
        this.airportName = airportName;
        this.city = city;
        this.IATA = IATA;
        this.threeLetterCode = threeLetterCode;
        this.airportLocation = airportLocation;
        setCreateTime(LocalDateTime.now());
        setUpdateTime(LocalDateTime.now());
    }

    public Airport(){
        setCreateTime(LocalDateTime.now());
        setUpdateTime(LocalDateTime.now());
    }

    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    public void setThreeLetterCode(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getIATA() {
        return IATA;
    }

    public void setIATA(String IATA) {
        this.IATA = IATA;
    }

    public LatLng getAirportLocation() {
        return airportLocation;
    }

    public void setAirportLocation(LatLng airportLocation) {
        this.airportLocation = airportLocation;
    }
}
