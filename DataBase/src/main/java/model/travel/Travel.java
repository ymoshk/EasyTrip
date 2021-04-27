package model.travel;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import model.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Travel")
public class Travel extends Model {
    private LatLng source;
    private double sourceLat;
    private double sourceLng;
    private LatLng dest;
    private double destLat;
    private double destLng;
    private TravelMode mode;
    private DistanceMatrixElement distanceMatrixElement;

    public Travel()
    {

    }

    public Travel(LatLng source, LatLng dest, TravelMode mode, DistanceMatrixElement distanceMatrixElement) {
        this.source = source;
        this.sourceLat = this.source.lat;
        this.sourceLng = this.source.lng;
        this.dest = dest;
        this.sourceLat = this.dest.lat;
        this.sourceLng = this.dest.lng;
        this.mode = mode;
        this.distanceMatrixElement = distanceMatrixElement;
    }
}
