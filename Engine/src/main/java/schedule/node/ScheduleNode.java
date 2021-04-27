package schedule.node;

import model.Model;
import org.openqa.selenium.InvalidArgumentException;
import schedule.DaySchedule;

import java.time.LocalTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;

public class ScheduleNode {
    private final String id = UUID.randomUUID().toString();
    private final DaySchedule dayContainer;
    private final Model activity;
    private final String TIME_ACCESS_EXCEPTION_ERROR = "You're trying to change time of an unchangeable node.";
    protected boolean isTimeChangeable = true;
    private LocalTime startTime;
    private LocalTime endTime;

    protected ScheduleNode(LocalTime startTime, LocalTime endTime, DaySchedule dayContainer, Model activity) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayContainer = dayContainer;
        this.activity = activity;
    }

    public static int subtractTimesToMinutes(LocalTime l1, LocalTime l2) {
        return Math.toIntExact(l1.until(l2, MINUTES));
    }

    public String getId() {
        return id;
    }

    public void addToStart(int minutes) throws IllegalAccessException {
        if (this.isTimeChangeable) {
            this.startTime = this.startTime.plusMinutes(minutes);
            this.dayContainer.updateAfterTimeChange();
        } else {
            throw new IllegalAccessException(TIME_ACCESS_EXCEPTION_ERROR);
        }
    }

    public void reduceFromStart(int minutes) throws IllegalAccessException {
        if (this.isTimeChangeable) {
            this.startTime = this.startTime.minusMinutes(minutes);
            this.dayContainer.updateAfterTimeChange();
        } else {
            throw new IllegalAccessException(TIME_ACCESS_EXCEPTION_ERROR);
        }
    }

    public void addToEnd(int minutes) throws IllegalAccessException {
        if (this.isTimeChangeable) {
            this.endTime = this.endTime.plusMinutes(minutes);
            this.dayContainer.updateAfterTimeChange();
        } else {
            throw new IllegalAccessException(TIME_ACCESS_EXCEPTION_ERROR);
        }
    }

    public void reduceFromEnd(int minutes) throws IllegalAccessException {
        if (this.isTimeChangeable) {
            this.endTime = this.endTime.minusMinutes(minutes);
            this.dayContainer.updateAfterTimeChange();
        } else {
            throw new IllegalAccessException(TIME_ACCESS_EXCEPTION_ERROR);
        }
    }

    public void setStartTime(LocalTime startTime) throws IllegalAccessException {
        if (this.isTimeChangeable) {
            this.startTime = startTime;
            this.dayContainer.updateAfterTimeChange();
        } else {
            throw new IllegalAccessException(TIME_ACCESS_EXCEPTION_ERROR);
        }
    }

    public void setEndTime(LocalTime endTime) throws IllegalAccessException {
        if (this.isTimeChangeable) {
            this.endTime = endTime;
            this.dayContainer.updateAfterTimeChange();
        } else {
            throw new IllegalAccessException(TIME_ACCESS_EXCEPTION_ERROR);
        }
    }

    /**
     * Split node in the middle.
     */
    public ScheduleNode split() throws IllegalAccessException {
        int middle = subtractTimesToMinutes(this.startTime, this.endTime) / 2;
        return split(middle);
    }

    public ScheduleNode split(LocalTime splitTime) throws IllegalAccessException {

        if (splitTime.isBefore(this.startTime) || splitTime.isAfter(this.endTime)) {
            throw new InvalidArgumentException("The split time arg must be a time which is " +
                    "between the start time and the end time.");
        }

        ScheduleNode copy = clone();
        copy.setStartTime(splitTime);
        this.setEndTime(splitTime);

        this.dayContainer.addAttraction(copy);
        return copy;
    }

    public ScheduleNode split(int afterMinutes) throws IllegalAccessException {

        if (afterMinutes >= subtractTimesToMinutes(this.startTime, this.endTime)) {
            throw new InvalidArgumentException("The minutes amount can't be more then the node duration.");
        }

        ScheduleNode copy = clone();
        copy.setStartTime(copy.startTime.plusMinutes(afterMinutes));
        this.setEndTime(copy.startTime);

        this.dayContainer.addAttraction(copy);
        return copy;
    }

    public ScheduleNode splitAndAddMiddleNode(ScheduleNode middle) {
        if (middle.startTime.isAfter(this.startTime) && middle.endTime.isBefore(this.endTime)) {
            ScheduleNode copy = clone();
            this.endTime = middle.startTime;
            copy.startTime = middle.endTime;
            this.dayContainer.addAttraction(copy);
            this.dayContainer.addAttraction(middle);
            return copy;
        } else {
            throw new InvalidArgumentException("To split a node with another node, the given node times must be" +
                    " contained inside the splatted node times");
        }
    }

    @Override
    protected ScheduleNode clone() {
        return new ScheduleNode(this.startTime, this.endTime, this.dayContainer, this.activity);
    }

    public void print() {
        String result = "Scheduled Node\n" +
                "ID: " + this.id + "\n" +
                "Activity Id " + (this.activity != null ? this.activity.getId() : "null activity") + "\n" +
                "Start Time " + this.startTime.toString() + "\n" +
                "End Time " + this.endTime.toString() + "\n" +
                "_________________________________________________\n";

        System.out.println(result);
    }
}
