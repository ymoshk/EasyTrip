package schedule.node;

import model.Model;
import schedule.DaySchedule;

import java.time.LocalTime;

public class AttractionNode extends ScheduleNode {
    public AttractionNode(LocalTime startTime, LocalTime endTime, DaySchedule dayContainer, Model activity) {
        super(startTime, endTime, dayContainer, activity);
    }
}
