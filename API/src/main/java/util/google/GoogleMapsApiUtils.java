package util.google;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.model.PlaceType;
import container.PriceRange;

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
}
