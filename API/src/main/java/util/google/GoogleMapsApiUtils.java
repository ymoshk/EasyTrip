package util.google;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.model.PlaceType;
import container.PriceRange;
import log.LogsManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    public static String getPhotoUrl(String photoReference) {
        String resultPhotoUrl = null;

        try {
            String reqUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                    photoReference + "&key=" + Keys.getKey();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(reqUrl).build();
            Response response = client.newCall(request).execute();
            resultPhotoUrl = response.request().url().toString();
        } catch (Exception e) {
            LogsManager.logException(e);
        }

        return resultPhotoUrl;
    }
}
