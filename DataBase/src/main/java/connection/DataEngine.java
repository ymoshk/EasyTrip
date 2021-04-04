package connection;

import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PriceLevel;
import constant.Constants;
import log.LogsManager;
import model.attraction.Attraction;
import model.attraction.AttractionsFactory;
import model.attraction.hotel.Hotel;
import model.location.City;
import model.location.Country;
import sun.security.pkcs11.Secmod;
import util.GoogleMapsApiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataEngine {
    // this class will be use by the client to get data informations
    private static final int pageCountToGet = 5; // total of 100 results
    private static final int nextPageDelay = 2000; // millisec

    // main for testing
    //TODO - DELETE the main
    public static void main(String[] args) {
        DataEngine de = new DataEngine();
        DBContext context = DBContext.getInstance();
        List<City> res = (List<City>) de.getCities("Tel Aviv");
        int x = 5;
    }

    public List<City> getCities(String cityPrefix) {
        return (List<City>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + cityPrefix + "%'");
    }

    public List<City> getCountries(String countryPrefix) {
        return (List<City>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + countryPrefix + "%'");
    }

    public List<Attraction> getAttractions(Class<?> classObj, PlaceType type, City city, PriceLevel priceLevel) {
        List<Attraction> res = new ArrayList<>();

        res = (List<Attraction>) DBContext.getInstance().selectQuery(
                "FROM " + classObj.getSimpleName() +
                        " WHERE city_id = " + city.getId());
        if (res.isEmpty()) {
            try {
                res = addAttractionsToTheDb(Constants.getSaharApiKey(), priceLevel, type, city);
            } catch (IOException ioException) {
                LogsManager.log(ioException.getMessage());
            }
        }

        res = (List<Attraction>) res.stream().filter(attr -> attr.getPriceLevel().equals(priceLevel));

        return res;
    }

    private List<Attraction> addAttractionsToTheDb(String apiKey, PriceLevel priceLevel, PlaceType type, City city) throws IOException {
        List<Attraction> attToAdd = getAttractionInStandardTextSearch(apiKey,
                type.name().replace("_", " "), priceLevel, type, city);

        attToAdd.forEach(attraction -> {
            DBContext.getInstance().insert(attraction);
        });

        return attToAdd;
    }

    private boolean isAttractionExist(String tableName, String placeId) {
        return !DBContext.getInstance().selectQuery(
                "FROM " + tableName +
                        " WHERE placeId LIKE " + "'" + placeId + "'").isEmpty();
    }

    private List<Attraction> getAttractionInStandardTextSearch(String apiKey, String attractionName, PriceLevel priceLevel, PlaceType type, City city) throws IOException {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        List<Attraction> result = new ArrayList<>();

        try {
            PlacesSearchResponse resp = GoogleMapsApiUtils
                    .getTextSearchRequest(context, attractionName, city.getCityName(), priceLevel, type)
                    .await();

            int i = 0;
            do {
                Arrays.stream(resp.results)
                        .forEach(singleRes -> result.add(AttractionsFactory.getAttraction(singleRes, type, priceLevel, city)));
                Thread.sleep(nextPageDelay);
                resp = GoogleMapsApiUtils.getNextPageTextSearchRequest(context, resp.nextPageToken).await();
                i++;
            } while (i <= pageCountToGet);
        } catch (Exception e) {
            LogsManager.log(e.getMessage());
        } finally {
            context.shutdown();
            return result;
        }
    }
}
