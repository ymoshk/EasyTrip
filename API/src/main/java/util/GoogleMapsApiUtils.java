package util;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PriceLevel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sun.plugin.com.Utils;

import java.io.IOException;

public class GoogleMapsApiUtils {

//    public List<Attraction> getAttractionInStandardTextSearch(String apiKey, String query, PriceLevel priceLevel, PlaceType type, City city) throws IOException {
    //        GeoApiContext context = new GeoApiContext.Builder()
    //                .apiKey(apiKey)
    //                .build();
    //
    //        List<Attraction> result = new ArrayList<>();
    //
    //        try {
    //            PlacesSearchResponse req = getTextSearchRequest(context, query, priceLevel, type)
    //                    .await();
    //
    //            Arrays.stream(req.results)
    //                    .forEach(singleRes -> result.add(AttractionsFactory.getAttraction(singleRes, type, priceLevel, city)));
    //        } catch (ApiException e) {
    //            //TODO - Maybe to print into a log file
    //            e.printStackTrace();
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        } finally {
    //            context.shutdown();
    //            return result;
    //        }
    //    }


    public static TextSearchRequest getTextSearchRequest(GeoApiContext context, String attractionName,String cityName ,PriceLevel priceLevel, PlaceType type) {
        TextSearchRequest res = PlacesApi.textSearchQuery(context,attractionName + " in " + cityName)
                .minPrice(priceLevel)
                .maxPrice(priceLevel)
                .type(type);

        return res;
    }

    public static TextSearchRequest getNextPageTextSearchRequest(GeoApiContext context, String pageToken) {
        TextSearchRequest res = PlacesApi.textSearchQuery(context, "").pageToken(pageToken);

        return res;
    }

    public static String getPhotoUrl(String photoReference, String apiKey) {
        String reqUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference +
                "&key=" + apiKey;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(reqUrl).build();
        Response response;
        String res = null;
        try {
            response = client.newCall(request).execute();
            res = response.request().url().toString();
        } catch (IOException ioException) {

        }

        return res;
    }
}
