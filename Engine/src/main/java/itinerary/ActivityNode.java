package itinerary;

import template.Attraction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ActivityNode {

    public Types type;
    private String startTime;
    private String endTime;
    private Attraction attraction;

    public ActivityNode(LocalTime startTime, LocalTime endTime, Types type, Attraction attraction) {
        this.startTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.endTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        this.type = type;
        this.attraction = attraction;
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

    public enum Types {
        FREE_TIME, CAR, BICYCLE, WALK, ATTRACTION
    }
}
