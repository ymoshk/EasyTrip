package airport;

import com.google.maps.model.Geometry;
import model.Model;

public class Airport extends Model {
    String airportName;
    long cityID;
    String IATA;
    Geometry airportLocation;
}
