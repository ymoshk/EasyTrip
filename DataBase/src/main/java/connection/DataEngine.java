package connection;

import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PriceLevel;
import constant.Constants;
import log.LogsManager;
import model.Model;
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
import java.util.stream.Collectors;

public class DataEngine {
    // this class will be use by the client to get data informations
    private static final int PAGE_COUNT_TO_GET = 3; // total of 60 results
    private static final int NEXT_PAGE_DELAY = 2000; // millisec
    private static final int MIN_SIZE_COLLECTION = 11;

    // main for testing
    //TODO - DELETE the main
    public static void main(String[] args) {
        DataEngine de = new DataEngine();

        List<Attraction> atts = de.getAttractions(PlaceType.ZOO, "RAMAT GAN", PriceLevel.UNKNOWN);
        int x = 3;
    }

    public List<City> getCities(String cityPrefix) {
        return (List<City>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + cityPrefix + "%'");
    }

    public List<Country> getCountries(String countryPrefix) {
        return (List<Country>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + countryPrefix + "%'");
    }

    public City getCity(String cityName) {
        List<City> cityAsList = (List<City>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + cityName + "'");

        return cityAsList.isEmpty() ? null : cityAsList.get(0);
    }

    public List<Attraction> getAttractions(PlaceType type, String cityName, PriceLevel priceLevel) {
        DBContext dbContext = DBContext.getInstance();
        City theCity = getCity(cityName);

        List<Attraction> res = null;

        if(theCity != null) {
            res = theCity.getAttractionList().stream()
                    .filter(attr -> attr.getPlaceType().equals(type) && attr.getPriceLevel().equals(priceLevel))
                    .collect(Collectors.toList());

            try {
                if (res.isEmpty()) {
                    res = addAttractionsToTheDb(Constants.getSaharApiKey(), priceLevel, type, theCity);
                } else if (res.size() <= MIN_SIZE_COLLECTION || !Model.isCollectionUpdated(res)) {
                    dbContext.deleteAll(res);
                    res = addAttractionsToTheDb(Constants.getSaharApiKey(), priceLevel, type, theCity);
                }
            } catch (Exception e) {
                LogsManager.logException(e);
            }
        }

        return res;
    }

    private List<Attraction> addAttractionsToTheDb(String apiKey, PriceLevel priceLevel, PlaceType type, City city) {
        List<Attraction> attractionsToAdd = getAttractionInStandardTextSearch(apiKey,
                type.name().replace("_", " "), priceLevel, type, city);

        DBContext.getInstance().insertAll(attractionsToAdd);

        return attractionsToAdd;
    }

    private List<Attraction> getAttractionInStandardTextSearch(String apiKey, String attractionName, PriceLevel priceLevel, PlaceType type, City city) {
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
                Thread.sleep(NEXT_PAGE_DELAY);

                if (resp.nextPageToken == null) {
                    break;
                }

                resp = GoogleMapsApiUtils.getNextPageTextSearchRequest(context, resp.nextPageToken).await();
                i++;
            } while (i <= PAGE_COUNT_TO_GET);
        } catch (Exception e) {
            LogsManager.logException(e);
        } finally {
            context.shutdown();
            return result;
        }
    }

    private boolean isAttractionExist(String tableName, String placeId) {
        return !DBContext.getInstance().selectQuery(
                "FROM " + tableName +
                        " WHERE placeId LIKE " + "'" + placeId + "'").isEmpty();
    }
}
