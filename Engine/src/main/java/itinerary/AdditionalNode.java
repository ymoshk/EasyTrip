package itinerary;

import java.time.LocalTime;

public class AdditionalNode {

    private LocalTime startTime;
    private LocalTime endTime;
    public Types type;

    public void setType(Types type) {
        this.type = type;
    }

    public Types getType() {
        return this.type;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public enum Types{
        FREE_TIME, CAR, BICYCLE, WALK
    }
}
