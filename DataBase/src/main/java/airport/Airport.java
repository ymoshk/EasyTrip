package airport;

import city.City;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import model.Model;

public class Airport extends Model {
    String airportName;
    City city;
    String IATA;
    String threeLetterCode;
    LatLng airportLocation;

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
