package util.google;

import com.google.maps.*;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.TravelMode;
import container.PriceRange;

public class GoogleMapsApiUtils {

    public static TextSearchRequest getTextSearchRequest(GeoApiContext context, String attractionName, String cityName, PriceRange priceRange, PlaceType type) {
        if(type.equals(PlaceType.GROCERY_OR_SUPERMARKET)) {
            return PlacesApi.textSearchQuery(context, "market" + " in " + cityName);
        }
        // DOCTOR == BEACH
        else if(type.equals(PlaceType.DOCTOR)) {
            return PlacesApi.textSearchQuery(context, "beach" + " in " + cityName);
        }

        return PlacesApi.textSearchQuery(context, attractionName + " in " + cityName).type(type);
    }

    public static TextSearchRequest getNextPageTextSearchRequest(GeoApiContext context, String pageToken) {
        return PlacesApi.textSearchQuery(context, "").pageToken(pageToken);

    }

    public static DistanceMatrixApiRequest getDistanceMatrixApiRequest(GeoApiContext context, LatLng source, LatLng dest, TravelMode mode) {
        return DistanceMatrixApi.newRequest(context).mode(mode).origins(source).destinations(dest);
    }

    public static PlaceDetailsRequest getPlaceDetails(GeoApiContext context, String placeID){
        return PlacesApi.placeDetails(context, placeID);
    }
}
