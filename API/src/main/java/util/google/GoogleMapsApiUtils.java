package util.google;

import com.google.maps.*;
import com.google.maps.model.*;
import container.PriceRange;

public class GoogleMapsApiUtils {
    public static int RADIUS = 50 * 1000;
    public static int RESTAURANT_RADIUS = 5 * 1000;
    public static TextSearchRequest getTextSearchRequest(GeoApiContext context, String attractionName, String cityName,
                                                         LatLng cityCenter, PriceRange priceRange, PlaceType type, PriceLevel priceLevel) {
        if(type.equals(PlaceType.GROCERY_OR_SUPERMARKET)) {
            return PlacesApi.textSearchQuery(context, "market" + "+" + cityName);
        }
        // DOCTOR == BEACH
        else if(type.equals(PlaceType.DOCTOR)) {
            return PlacesApi.textSearchQuery(context, "beach" + "+" + cityName);
        }
        // ATM == TOP SIGHT
        else if(type.equals(PlaceType.ATM)) {
            return PlacesApi.textSearchQuery(context, "top sights" + "+" + cityName);
        }
        //fetch amusement parks without a radius limit
        else if(type.equals(PlaceType.AMUSEMENT_PARK)){
            return PlacesApi.textSearchQuery(context, "amusement park" + "+" + cityName).type(type);
        }
        else if(type.equals(PlaceType.RESTAURANT)){
            return PlacesApi.textSearchQuery(context, attractionName).type(type).minPrice(priceLevel).maxPrice(priceLevel);
        }

        return PlacesApi.textSearchQuery(context, attractionName).type(type);
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

    public static NearbySearchRequest getNearByPlaces(GeoApiContext context, LatLng location){
        return PlacesApi.nearbySearchQuery(context, location).type(PlaceType.RESTAURANT).radius(RESTAURANT_RADIUS);
    }
}
