package city;

import airport.Airport;
import com.google.maps.model.Geometry;
import model.Model;

import java.util.List;

public class City extends Model {
    String cityName;
    Geometry cityCenter;
    long CountryID;
    double averagePricePerDay;
    List<Airport> airportList;

}
