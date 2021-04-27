package model.travel;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import model.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Travel")
public class Travel extends Model {
    private double sourceLng;
    private LatLng dest;
    private double destLat;
    private double destLng;
    private TravelMode mode;
    @Column(length = 512)
    private DistanceMatrixElement distanceMatrixElement;
    private LatLng source;
    private double sourceLat;

    public Travel() {

    }

    public Travel(LatLng source, LatLng dest, TravelMode mode, DistanceMatrixElement distanceMatrixElement) {
        this.source = source;
        this.sourceLat = this.source.lat;
        this.sourceLng = this.source.lng;
        this.dest = dest;
        this.destLat = this.dest.lat;
        this.destLng = this.dest.lng;
        this.mode = mode;
        this.distanceMatrixElement = distanceMatrixElement;
    }

    public LatLng getSource() {
        return this.source;
    }

    public LatLng getDest() {
        return this.dest;
    }

    public TravelMode getMode() {
        return this.mode;
    }

    public DistanceMatrixElement getDistanceMatrixElement() {
        return this.distanceMatrixElement;
    }
}
