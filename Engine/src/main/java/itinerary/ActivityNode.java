package itinerary;

import com.google.maps.model.TravelMode;
import generator.GUID;
import template.Attraction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ActivityNode {

    private Types type;
    private String startTime;
    private String endTime;
    private Attraction attraction;
    private String uniqueKey;
    private Map<Types, Long> transDuration;

    public ActivityNode(LocalTime startTime, LocalTime endTime, Types type, Attraction attraction) {
        this.startTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.endTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.type = type;
        this.attraction = attraction;
        this.uniqueKey = GUID.generate();

        if (type != Types.ATTRACTION && type != Types.FREE_TIME) {
            this.transDuration = new HashMap<>();
        }
    }

    public Map<Types, Long> getTransDuration() {
        return transDuration;
    }

    public void setTransDuration(Map<Types, Long> transDuration) {
        this.transDuration = transDuration;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Types getType() {
        return this.type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public static Types googleTravelToActivityType(TravelMode travelMode) {
        if (travelMode.equals(TravelMode.WALKING)) {
            return Types.WALK;
        } else if (travelMode.equals(TravelMode.DRIVING)) {
            return Types.CAR;
        } else if (travelMode.equals(TravelMode.TRANSIT)) {
            return Types.TRANSIT;
        } else {
            return Types.BICYCLE;
        }
    }

    public enum Types {
        FREE_TIME, CAR, BICYCLE, WALK, TRANSIT, ATTRACTION, FLIGHT
    }
}
