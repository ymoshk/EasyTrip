package schedule.node;


import model.Model;
import schedule.DaySchedule;

import java.time.LocalTime;

public class TransportNode extends ScheduleNode {
    public TransportNode(LocalTime startTime, LocalTime endTime, DaySchedule dayContainer, Model activity) {
        super(startTime, endTime, dayContainer, activity);
    }
}
