package distanceCalculator;

import com.google.maps.model.LatLng;

public class DistanceCalculator {
    private static final int EARTH_RADIUS = 6371; // Radius of the earth in km


    //    https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
    public static double calculateDistance(LatLng source, LatLng destination){
        //case we initial an empty route, and there's no last attraction
        if(source == null){
            return 0;
        }

        double latDelta = deg2rad(destination.lat - source.lat);
        double longDelta = deg2rad(destination.lng - source.lng);

        double a = Math.sin(latDelta/2) * Math.sin(latDelta/2) +
                Math.cos(deg2rad(destination.lat)) * Math.cos(deg2rad(source.lat)) *
                        Math.sin(longDelta/2) * Math.sin(longDelta/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }

    private static double deg2rad(double degree) {
        return degree * (Math.PI/180);
    }

}
