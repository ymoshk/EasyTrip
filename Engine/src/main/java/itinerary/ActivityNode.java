package itinerary;

import template.Attraction;

import java.time.LocalTime;

public class ActivityNode {
    private Attraction attraction;
    private AdditionalNode additionalSlot; // This might be a free time or mobility slot
    private LocalTime startTime;
    private LocalTime endTime;

    public Attraction getAttraction() {
        return attraction;
    }

    public AdditionalNode getAdditionalSlot() {
        return additionalSlot;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
