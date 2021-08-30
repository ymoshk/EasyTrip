package itinerary;

import connection.DataEngine;
import container.PriceRange;
import model.location.City;
import template.Flight;
import template.TripTag;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class QuestionsData {
    private final String country;
    private final String city;
    private final int adultsCount;
    private final int childrenCount;
    private final PriceRange priceRange;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final List<TripTag> favoriteAttractions;
    private final List<TripTag> tripVibes;
    private final List<TripTag> transportation;
    private template.Flight flight;

    public QuestionsData(String countryName, String cityName, int adultsCount, int childrenCount, int budget, LocalDateTime startDate,
                         LocalDateTime endDate, List<TripTag> favoriteAttractions, List<TripTag> tripVibes, List<TripTag> transportation,
                         template.Flight flight) {
        this.adultsCount = adultsCount;
        this.childrenCount = childrenCount;
        this.city = cityName;
        this.country = countryName;
        this.startDate = startDate.withHour(8).withMinute(0).withSecond(0).withNano(0);
        this.endDate = endDate.withHour(23).withMinute(59).withSecond(59).withNano(0);
        this.favoriteAttractions = favoriteAttractions;
        this.tripVibes = tripVibes;
        this.priceRange = new PriceRange(budget);
        this.transportation = transportation;
        this.flight = flight;
    }

    public String getCountry() {
        return country;
    }

    public City getCity() {
        return DataEngine.getInstance().getCity(this.city).orElse(null);
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

    public List<TripTag> getTransportation() {
        return transportation;
    }

    public Flight getFlight() {
        return flight;
    }
}
