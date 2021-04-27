package schedule;

import com.google.maps.model.TravelMode;
import model.flightOffer.FlightOffer;
import schedule.node.ScheduleNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TripSchedule {
    private final List<FlightOffer> flights;
    private final TravelMode defaultTransportation;
    // TODO - Add a trip owner as we implement the User class.
    private final List<DaySchedule> days;


    public TripSchedule(List<FlightOffer> flights, TravelMode defaultTransportation) {
        this.flights = flights;
        this.defaultTransportation = defaultTransportation;
        this.days = new ArrayList<>();
        init();
    }

    /**
     * Actions:
     * <p>
     * 1. Create DaySchedule objects and add them to the days list according to the flights details.
     * 2. Create FlightNodes for each flight in the flights list.
     * 3. Init each day with default startPos and endPos.
     */
    private void init() {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Add/Remove days after a change in the flights list.
     */
    private void refreshFlightNodes() {
        // TODO
        throw new NotImplementedException();
    }

    public void save() {
        // TODO
        throw new NotImplementedException();
    }

    private boolean isValid() {
        boolean result = true;

        for (DaySchedule day : this.days) {
            if (!day.isValid()) {
                result = false;
                break;
            }
        }

        for (int i = 0; i < days.size() - 1; i++) {
            if (!DaySchedule.checkContinuity(this.days.get(i), this.days.get(i + 1))) {
                result = false;
                break;
            }
        }

        return result;
    }

    public void addNewFlight(FlightOffer newFlight) {
        this.flights.add(newFlight);
        refreshFlightNodes();
    }

    public void removeFlight(FlightOffer newFlight) {
        if (this.flights.remove(newFlight)) {
            refreshFlightNodes();
        }
    }

    public void addAttraction(ScheduleNode attraction, int dayIndex) {
        this.days.get(dayIndex).addAttraction(attraction);
    }

    public void removeAttraction(ScheduleNode attraction, int dayIndex) {
        this.days.get(dayIndex).removeAttraction(attraction);
    }

    public void cleanDay(int dayIndex) {
        this.days.get(dayIndex).clean();
    }

    public void clean() {
        this.days.forEach(DaySchedule::clean);
    }

    public ScheduleNode findNodeById(String id) {
        List<ScheduleNode> lst = new ArrayList<>();
        days.forEach(day -> lst.addAll(day.getNodesList()));
        return lst.stream().filter(node -> node.getId().equals(id)).findFirst().orElse(null);
    }

    public void print() {
        AtomicInteger i = new AtomicInteger();
        this.days.forEach(daySchedule -> {
            System.out.println("Day number: " + i.incrementAndGet());
            daySchedule.print();
        });
    }
}
