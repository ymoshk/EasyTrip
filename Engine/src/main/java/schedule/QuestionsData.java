package schedule;

import connection.DataEngine;
import container.PriceRange;
import model.location.City;
import model.location.Country;
import template.TripTag;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class QuestionsData {
    private final Country country;
    private final City city;
    private final int adultsCount;
    private final int childrenCount;
    private final PriceRange priceRange;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final List<TripTag> favoriteAttractions;
    private final List<TripTag> tripVibes;

    public QuestionsData(String countryName, String cityName, int adultsCount, int childrenCount, int budget, LocalDateTime startDate,
                         LocalDateTime endDate, List<TripTag> favoriteAttractions, List<TripTag> tripVibes) throws Exception {
        this.adultsCount = adultsCount;
        this.childrenCount = childrenCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.favoriteAttractions = favoriteAttractions;
        this.tripVibes = tripVibes;

        DataEngine dataEngine = DataEngine.getInstance();
        List<Country> countryList = dataEngine.getCountries(countryName);
        List<City> cityList = dataEngine.getCities(cityName);

        if (countryList.size() != 1 || cityList.size() != 1) {
            throw new Exception("Found more than one city/country with that name");
        }

        this.city = cityList.get(0);
        this.country = countryList.get(0);
        this.priceRange = new PriceRange(budget);
    }

    public Country getCountry() {
        return country;
    }

    public City getCity() {
        return city;
    }

    public int getAdultsCount() {
        return adultsCount;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public List<TripTag> getFavoriteAttractions() {
        return Collections.unmodifiableList(favoriteAttractions);
    }

    public List<TripTag> getTripVibes() {
        return Collections.unmodifiableList(tripVibes);
    }
}
