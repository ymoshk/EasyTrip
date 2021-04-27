package schedule.node;

import model.Model;
import schedule.DaySchedule;

import java.time.LocalTime;

public class FlightNode extends ScheduleNode {

    public FlightNode(LocalTime startTime, LocalTime endTime, DaySchedule dayContainer, Model activity) {
        super(startTime, endTime, dayContainer, activity);
        this.isTimeChangeable = false;
    }
}
