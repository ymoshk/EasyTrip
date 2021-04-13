package model.travel;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import model.Model;

import javax.persistence.Entity;

@Entity()
public class Travel extends Model {
    LatLng source;
    LatLng dest;
    TravelMode mode;
    DistanceMatrixElement distanceMatrixElement;

    public Travel()
    {

    }

    public Travel(LatLng source, LatLng dest, TravelMode mode, DistanceMatrixElement distanceMatrixElement) {
        this.source = source;
        this.dest = dest;
        this.mode = mode;
        this.distanceMatrixElement = distanceMatrixElement;
    }
}
