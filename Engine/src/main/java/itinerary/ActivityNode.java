package itinerary;

import model.Model;
import template.Attraction;

import java.time.LocalTime;

public class ActivityNode {
    private final Attraction attraction;
    private Model additionalSlot; // This might be a free time or mobility slot
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;


    public ActivityNode(String key, Attraction attraction, LocalTime startTime, LocalTime endTime) {
        this.attraction = attraction;
        this.startTime = startTime;
        this.endTime = endTime;
        this.additionalSlot = null; // TODO
        this.slotStartTime = endTime;
        this.slotEndTime = this.slotStartTime.plusMinutes(5);
    }


    public Attraction getAttraction() {
        return attraction;
    }

    public Model getAdditionalSlot() {
        return additionalSlot;
    }

    public void setAdditionalSlot(Model additionalSlot) {
        this.additionalSlot = additionalSlot;
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

    public LocalTime getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(LocalTime slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public LocalTime getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(LocalTime slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    //    public void AddToEndTime(int minutes) {
    //        this.endTime = this.endTime.plusMinutes(minutes);
    //        this.slotStartTime = this.slotStartTime.plusMinutes(minutes);
    //
    //        if(!this.slotEndTime.isAfter(this.slotStartTime)){
    //
    //        }
    //    }
}
