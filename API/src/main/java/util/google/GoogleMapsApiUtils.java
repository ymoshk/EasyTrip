package util.google;

import com.google.maps.*;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.TravelMode;
import container.PriceRange;

public class GoogleMapsApiUtils {
    public static int RADIUS = 50 * 1000;

    public static TextSearchRequest getTextSearchRequest(GeoApiContext context, String attractionName, String cityName,
                                                         LatLng cityCenter, PriceRange priceRange, PlaceType type) {
        if(type.equals(PlaceType.GROCERY_OR_SUPERMARKET)) {
            return PlacesApi.textSearchQuery(context, "market" + "+" + cityName).location(cityCenter).radius(RADIUS);
        }
        // DOCTOR == BEACH
        else if(type.equals(PlaceType.DOCTOR)) {
            return PlacesApi.textSearchQuery(context, "beach" + "+" + cityName).location(cityCenter).radius(RADIUS);
        }
        // ATM == TOP SIGHT
        else if(type.equals(PlaceType.ATM)) {
            return PlacesApi.textSearchQuery(context, "top sights" + "+" + cityName).location(cityCenter).radius(RADIUS);
        }

        return PlacesApi.textSearchQuery(context, attractionName).type(type).location(cityCenter).radius(RADIUS);
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
