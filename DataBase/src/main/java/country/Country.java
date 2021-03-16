package country;

import city.City;
import com.google.maps.model.Geometry;
import model.Model;

import java.util.List;

public class Country extends Model {
    String countryName;
    Geometry countryCenter;
    double averagePricePerDay;
    List<City> cityList;
}
