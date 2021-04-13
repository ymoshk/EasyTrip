package util.google;

import com.google.maps.*;
import com.google.maps.model.PlaceType;
import com.google.maps.model.TravelMode;
import container.PriceRange;
import model.attraction.Attraction;

public class GoogleMapsApiUtils {

    public static TextSearchRequest getTextSearchRequest(GeoApiContext context, String attractionName, String cityName, PriceRange priceRange, PlaceType type) {
        return PlacesApi.textSearchQuery(context, attractionName + " in " + cityName)
                .minPrice(priceRange.getMin())
                .maxPrice(priceRange.getMax())
                .type(type);
    }

    public static TextSearchRequest getNextPageTextSearchRequest(GeoApiContext context, String pageToken) {
        return PlacesApi.textSearchQuery(context, "").pageToken(pageToken);
    }

    public static DistanceMatrixApiRequest getDistanceMatrixApiRequest(GeoApiContext context, Attraction source, Attraction dest, TravelMode mode){
        return DistanceMatrixApi.newRequest(context).mode(mode).origins(source.getGeometry().location).destinations(dest.getGeometry().location);
    }
}
