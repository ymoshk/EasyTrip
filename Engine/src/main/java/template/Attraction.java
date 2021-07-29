package template;


import com.google.gson.Gson;
import com.google.maps.model.OpeningHours;

import java.time.LocalTime;
import java.util.HashMap;

public class Attraction {
    public String id;
    public String name;
    public boolean isRecommended;
    public String type;
    public float rating;
    public int userTotalRating;
    public String image;
    public boolean isTemporarilyClose;
    public boolean isPermanentlyClosed;
    public int priceRangeMin;
    public int priceRangeMax;
    public String openingHoursText;
    public String openingHoursData;
    public double lat;
    public double lng;
    public String address;
    public String reviews;
    public String localNumber;
    public String internationalNumber;
    public String website;
    public int priceLevel;

    public Attraction(model.attraction.Attraction attraction, boolean isRecommended) {
        this.id = attraction.getPlaceId();
        this.name = attraction.getName();
        this.isRecommended = isRecommended;
        this.type = attraction.getPlaceType().toString();
        this.rating = attraction.getRating();
        this.userTotalRating = attraction.getUserRatingsTotal();
        this.image = attraction.getPhotoReference();
        this.isPermanentlyClosed = attraction.getBusinessStatus().equals("CLOSED_PERMANENTLY");
        this.isTemporarilyClose = attraction.getBusinessStatus().equals("CLOSED_TEMPORARILY");
        this.priceRangeMin = attraction.getPriceRange().getMin().ordinal();
        this.priceRangeMax = attraction.getPriceRange().getMin().ordinal();
        Gson gson = new Gson();
        if (attraction.getOpeningHours() != null) {
            this.openingHoursText = gson.toJson(new OpeningHoursTextContainer(attraction));
            this.openingHoursData = gson.toJson(parseOpeningHours(attraction.getOpeningHours().periods));
        }
        this.lat = attraction.getLat();
        this.lng = attraction.getLng();
        this.address = attraction.getFormattedAddress();
        this.localNumber = attraction.getLocalNumber();
        this.internationalNumber = attraction.getInternationalNumber();
        this.website = attraction.getWebsite();
        this.reviews = gson.toJson(attraction.getReviews());
        this.priceLevel = attraction.getPriceLevel().ordinal();
    }

    private HashMap<OpeningHours.Period.OpenClose.DayOfWeek, LocalTime[]>
    parseOpeningHours(OpeningHours.Period[] periods) {
        HashMap<OpeningHours.Period.OpenClose.DayOfWeek, LocalTime[]> result = new HashMap<>();
        if (periods != null) {
            for (OpeningHours.Period period : periods) {
                if (!result.containsKey(period.close.day)) {
                    result.put(period.close.day, new LocalTime[2]);
                }
                result.get(period.close.day)[0] = period.open.time;
                result.get(period.close.day)[1] = period.close.time;
            }
        }
        return result;
    }

    public static class OpeningHoursTextContainer {
        public String sunday;
        public String monday;
        public String tuesday;
        public String wednesday;
        public String thursday;
        public String friday;
        public String saturday;

        public OpeningHoursTextContainer(model.attraction.Attraction attraction) {
            try {
                if (attraction.getOpeningHours().weekdayText != null) {
                    this.sunday = attraction.getOpeningHours().weekdayText[0];
                    this.monday = attraction.getOpeningHours().weekdayText[1];
                    this.tuesday = attraction.getOpeningHours().weekdayText[2];
                    this.wednesday = attraction.getOpeningHours().weekdayText[3];
                    this.thursday = attraction.getOpeningHours().weekdayText[4];
                    this.friday = attraction.getOpeningHours().weekdayText[5];
                    this.saturday = attraction.getOpeningHours().weekdayText[6];
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
