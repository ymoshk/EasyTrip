package itinerary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ItineraryDay {
    private final LocalDate date;
    private final List<ActivityNode> activities;

    public ItineraryDay(LocalDate date) {
        this.date = date;
        this.activities = new ArrayList<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public List<ActivityNode> getActivities() {
        return activities;
    }

    public String getStringDate() {
        return this.date.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
    }

}
