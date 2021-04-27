package connection;

import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import container.PriceRange;
import log.LogsManager;
import model.Model;
import model.attraction.Attraction;
import model.attraction.AttractionImage;
import model.attraction.AttractionsFactory;
import model.location.City;
import model.location.Country;
import model.travel.Travel;
import util.google.GoogleMapsApiUtils;
import util.google.Keys;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is responsible for retrieving the data from the API services or from the database.
 */
public class DataEngine implements Closeable {
    private static final int PAGE_COUNT_TO_GET = 3; // total of 60 results
    private static final int NEXT_PAGE_DELAY = 2000; // milli sec
    private static final int MIN_SIZE_COLLECTION = 3;
    private static DataEngine instance = null;

    //empty constructor just to make sure the class is a singleton
    private DataEngine() {
    }

    // only one thread can execute this method at the same time.
    static synchronized DataEngine getInstance() {
        if (instance == null) {
            instance = new DataEngine();
        }
        return instance;
    }

    /**
     * @param cityPrefix The name or a part of the name of the requested city.
     * @return List of cities that match the search result.
     */
    public List<City> getCities(String cityPrefix) {
        return (List<City>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + cityPrefix + "%'");
    }

    /**
     * @param countryPrefix The name or a part of the name of the requested country.
     * @return List of countries that match the search result.
     */
    public List<Country> getCountries(String countryPrefix) {
        return (List<Country>) DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + countryPrefix + "%'");
    }

    /**
     * @param cityName The exact name of the desired city.
     * @return an Optional<City> object that contains the desired city or null.
     */
    public Optional<City> getCity(String cityName) {
        List<? extends Model> cityAsList = DBContext.getInstance().selectQuery(
                "FROM City WHERE cityName LIKE '" + cityName + "'");

        return cityAsList.isEmpty() ? Optional.empty() : Optional.ofNullable((City) cityAsList.get(0));
    }

    /**
     * This method will return a list of attractions that match the search args.
     * There are 3 scenarios:
     * <p>
     * 1. If the relevant attractions have been already saved to the DB and are updated, it will return the saved data.
     * 2. Relevant attractions already been saved to the DB but the data isn't updated or less than MIN_SIZE_COLLECTION
     * records were found, then the current data will be removed and the method will continue by scenario 3.
     * 3. Couldn't find any relevant data inside the DB, the method will call google API to get the relevant data using
     * 'getAttractionsAndSaveToDB' method.
     *
     * @param type       The PlaceType of the requested attractions.
     * @param cityName   The name of the city where the requested attractions should be located.
     * @param priceRange A price range object to limit the price range of the requested attractions.
     * @return A list of attraction that match the search args.
     */
    public List<Attraction> getAttractions(PlaceType type, String cityName, PriceRange priceRange) {
        DBContext dbContext = DBContext.getInstance();
        City theCity = getCity(cityName).orElse(null);
        List<Attraction> res = new ArrayList<>();

        if (theCity != null) {
            res = theCity.getAttractionList().stream()
                    .filter(attr -> attr.getPlaceType().equals(type) && attr.getPriceRange().containedIn(priceRange))
                    .collect(Collectors.toList());

            try {
                if (res.isEmpty()) {
                    res = getAttractionsAndSaveToDB(priceRange, type, theCity);
                } else if (res.size() <= MIN_SIZE_COLLECTION || !Model.isCollectionUpdated(res)) {
                    res.forEach(theCity::removeAttraction);
                    dbContext.update(theCity);
                    res = getAttractionsAndSaveToDB(priceRange, type, theCity);
                }
            } catch (Exception e) {
                LogsManager.logException(e);
            }
        }

        return res;
    }

    /**
     * This method will call google API to find attractions that match the search args and save the result to the DB.
     *
     * @param priceRange A price range object to limit the price range of the requested attractions.
     * @param city       The name of the city where the requested attractions should be located.
     * @param type       The PlaceType of the requested attractions.
     * @return a list of attractions that match the search args.
     */
    private List<Attraction> getAttractionsAndSaveToDB(PriceRange priceRange, PlaceType type, City city) {
        List<Attraction> attractionsToAdd =
                getAttractionInStandardTextSearch(type.name().replace("_", " "), priceRange, type, city);

        DBContext.getInstance().insertAll(attractionsToAdd);

        return attractionsToAdd;
    }

    /**
     * @param attractionName The type name of the attraction as a string.
     * @param priceRange     A price range object to limit the price range of the requested attractions.
     * @param type           The PlaceType of the requested attractions.
     * @param city           The name of the city where the requested attractions should be located.
     * @return a list of attractions that match the search args.
     */
    private List<Attraction> getAttractionInStandardTextSearch(String attractionName, PriceRange priceRange, PlaceType type, City city) {
        List<Attraction> result = new ArrayList<>();
        GeoApiContext context = null;

        try {
            int i = 0;
            context = new GeoApiContext.Builder()
                    .apiKey(Keys.getKey())
                    .build();

            PlacesSearchResponse resp = GoogleMapsApiUtils
                    .getTextSearchRequest(context, attractionName, city.getCityName(), priceRange, type)
                    .await();
            do {
                Arrays.stream(resp.results)
                        .forEach(singleRes -> result.add(AttractionsFactory.getAttraction(singleRes, type, priceRange, city)));
                Thread.sleep(NEXT_PAGE_DELAY);

                if (resp.nextPageToken == null) {
                    break;
                }

                resp = GoogleMapsApiUtils.getNextPageTextSearchRequest(context, resp.nextPageToken).await();
            } while (++i <= PAGE_COUNT_TO_GET);
        } catch (Exception e) {
            LogsManager.logException(e);
        } finally {
            if (context != null) {
                context.shutdown();
            }
        }
        return result;
    }

    private boolean isAttractionExist(String tableName, String placeId) {
        return !DBContext.getInstance().selectQuery(
                "FROM " + tableName +
                        " WHERE placeId LIKE " + "'" + placeId + "'").isEmpty();
    }

    /**
     * @param attraction the attraction to get it's image.
     * @return an AttractionImage object which contains the photo bytes array.
     */
    public AttractionImage getAttractionImage(Attraction attraction) {
        AttractionImage result = null;
        List<? extends Model> results = DBContext.getInstance()
                .selectQuery("FROM AttractionImage WHERE placeId = " + "'" + attraction.getPlaceId() + "'");

        if (results.isEmpty()) {
            result = new AttractionImage(attraction);
            DBContext.getInstance().insert(result);
        } else {
            result = (AttractionImage) results.get(0);
        }
        return result;
    }

    /**
     * @param source the The place you want to get from.
     * @param dest   the The place you want to get to.
     * @return an DistanceMatrixElement object which contains the travel time and distance.
     */
    public Travel getTravel(LatLng source, LatLng dest, TravelMode mode) {
        Travel res = getTravelFromDB(source, dest, mode);

        if (res == null) {
            DistanceMatrixElement distanceMatrixElement =
                    getDistanceMatrixElementFromGoogleApi(source, dest, mode);

            if (distanceMatrixElement != null) {
                res = new Travel(source, dest, mode, distanceMatrixElement);
                DBContext.getInstance().insert(res);
            }
        }

        return res;
    }

    private Travel getTravelFromDB(LatLng source, LatLng dest, TravelMode mode) {
        DBContext dbContext = DBContext.getInstance();

        List<Travel> travels = (List<Travel>) dbContext.selectQuery(
                "FROM Travel WHERE " +
                        "destLat = " + dest.lat + " AND destLng = " + dest.lng +
                        " AND sourceLat = " + source.lat + " AND sourceLng = " + source.lng);

        Travel res = travels.stream().filter(travel -> travel.getMode() == mode).findFirst().orElse(null);

        return res;
    }

    private DistanceMatrixElement getDistanceMatrixElementFromGoogleApi(LatLng source, LatLng dest, TravelMode mode) {
        GeoApiContext context = null;
        DistanceMatrixElement res = null;

        context = new GeoApiContext.Builder()
                .apiKey(Keys.getKey())
                .build();

        try {
            DistanceMatrix response = GoogleMapsApiUtils.getDistanceMatrixApiRequest(context, source, dest, mode).await();

            if (response.rows.length != 0 && response.rows[0].elements.length != 0) {
                res = response.rows[0].elements[0];
            }
        } catch (Exception e) {
            LogsManager.logException(e);
        } finally {
            context.shutdown();

            return res;
        }
    }

    public List<Attraction> getAttractionInSquare(LatLng origin, double km) {
        double factor = (km / 1.11) * 0.01;
        LatLng topRight = new LatLng(origin.lat + factor, origin.lng + factor);
        LatLng topLeft = new LatLng(origin.lat + factor, origin.lng - factor);
        LatLng bottomLeft = new LatLng(origin.lat - factor, origin.lng - factor);

        List<Attraction> res = (List<Attraction>) DBContext.getInstance().selectQuery("FROM Attraction WHERE " +
                "lat > " + bottomLeft.lat + " AND lat < " + topLeft.lat +
                " AND lng > " + topLeft.lng + " AND lng < " + topRight.lng);

        return res;
    }

    @Override
    public void close() {
        // TODO - make sure to call this method as the server shut down.
        DBContext.getInstance().close();
    }
}
