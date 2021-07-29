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

    //    public void swap(int firstIndex, int secondIndex){
    //        ActivityNode firstNode = this.activities.get(firstIndex);
    //        ActivityNode secondNode = this.activities.get(secondIndex);
    //
    //        // Swap start time
    //        LocalTime temp = firstNode.getStartTime();
    //        firstNode.setStartTime(secondNode.getStartTime());
    //        secondNode.setStartTime(temp);
    //        // Swap end time
    //        temp = firstNode.getEndTime();
    //        firstNode.setEndTime(secondNode.getEndTime());
    //        secondNode.setEndTime(temp);
    //        // Swap extra slot start time
    //        temp = firstNode.getSlotStartTime();
    //        firstNode.setSlotStartTime(secondNode.getSlotStartTime());
    //        secondNode.setSlotStartTime(temp);
    //        // Swap extra slot end time
    //        temp = firstNode.getSlotEndTime();
    //        firstNode.setSlotEndTime(secondNode.getSlotEndTime());
    //        secondNode.setSlotEndTime(temp);
    //
    //        this.activities.set(firstIndex, secondNode);
    //        this.activities.set(secondIndex, firstNode);
    //    }
    //
    //    public void AddToEndTime(int minutes){
    //
    //    }
}
