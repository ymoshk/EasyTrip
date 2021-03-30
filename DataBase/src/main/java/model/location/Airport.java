package model.location;

import com.google.maps.model.LatLng;
import model.Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Airport")
public class Airport extends Model implements Serializable{
    @Column(unique = true)
    String airportName;
    //TODO - check this
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", insertable = true, updatable = true)
    City city;
    String IATA;
    String threeLetterCode;
    LatLng airportLocation;

    public Airport(String airportName, City city, String IATA, String threeLetterCode, LatLng airportLocation) {
        this.airportName = airportName;
        this.city = city;
        this.IATA = IATA;
        this.threeLetterCode = threeLetterCode;
        this.airportLocation = airportLocation;
    }

    public Airport(){}

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
