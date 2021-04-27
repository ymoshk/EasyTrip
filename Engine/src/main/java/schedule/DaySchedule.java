package schedule;

import schedule.node.ScheduleNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;

/**
 * A daily schedule from 6am to 6am
 */
public class DaySchedule {
    private LinkedList<ScheduleNode> nodesList;

    public DaySchedule() {
        this.nodesList = new LinkedList<>();
    }

    /**
     * @param dayOne some scheduleDay day.
     * @param dayTwo the next day after dayOne.
     * @return true if the last attraction of dayOne is the same as the first attraction of dayTwo, that can be
     * an hotel, flight, etc...
     */
    public static boolean checkContinuity(DaySchedule dayOne, DaySchedule dayTwo) {
        return dayOne.nodesList.getLast().equals(dayTwo.nodesList.getFirst());
    }

    public LinkedList<ScheduleNode> getNodesList() {
        return nodesList;
    }

    public boolean isValid() {
        // TODO
        return true;
    }

    public void addAttraction(ScheduleNode scheduleNode) {
        // TODO
    }

    public void removeAttraction(ScheduleNode scheduleNode) {
        // TODO
    }

    public void clean() {
        // TODO
    }

    public void updateAfterTimeChange() {
        // TODO
    }

    public void print() {
        int i = 0;
        this.nodesList.forEach(ScheduleNode::print);
    }
}
