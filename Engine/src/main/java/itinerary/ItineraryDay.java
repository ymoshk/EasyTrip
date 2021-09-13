package itinerary;

import generator.GUID;
import model.attraction.Attraction;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ItineraryDay {
    private final LocalDate date;
    private List<ActivityNode> activities;

    public ItineraryDay(LocalDate date) {
        this(date, 8);
    }

    public ItineraryDay(LocalDate date, int startHour) {
        this.date = date;
        this.activities = new ArrayList<>();

        ActivityNode padding = new ActivityNode(LocalTime.of(8, 0),
                LocalTime.of(startHour, 0), ActivityNode.Types.FREE_TIME, null);

        this.activities.add(padding);
    }


    public LocalDate getDate() {
        return date;
    }

    public List<ActivityNode> getActivities() {
        return Collections.unmodifiableList(activities);
    }

    public void setActivities(List<ActivityNode> activities) {
        this.activities = activities;
    }

    public String getStringDate() {
        return this.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void addAttractionToEnd(Attraction attraction, LocalDateTime startTime, LocalDateTime endTime) {
        activities.add(new ActivityNode(startTime.toLocalTime(),
                endTime.toLocalTime(),
                ActivityNode.Types.ATTRACTION,
                new template.Attraction(attraction, true)));
    }

    public void addTransportationTime(LocalDateTime startTime, LocalDateTime endTime, ActivityNode.Types type) {
        activities.add(new ActivityNode(startTime.toLocalTime(),
                endTime.toLocalTime(),
                type,
                null));
    }

    public void addFreeTime(LocalDateTime startTime, LocalDateTime endTime) {
        activities.add(new ActivityNode(startTime.toLocalTime(), endTime.toLocalTime(), ActivityNode.Types.FREE_TIME, null));
    }

    public void addFlightTime(LocalDateTime startTime, LocalDateTime endTime) {
        activities.add(new ActivityNode(startTime.toLocalTime(), endTime.toLocalTime(), ActivityNode.Types.FLIGHT, null));
    }

    public void addFlightTimeAtTheBeginning(LocalDateTime startTime, LocalDateTime endTime) {
        activities.add(new ActivityNode(startTime.toLocalTime(), endTime.toLocalTime(), ActivityNode.Types.FLIGHT, null));
        activities.add(new ActivityNode(endTime.toLocalTime(), endTime.toLocalTime(), ActivityNode.Types.FREE_TIME, null));
    }

    public void clean() {
        this.activities.clear();

        ActivityNode padding = new ActivityNode(LocalTime.of(8, 0),
                LocalTime.of(8, 0), ActivityNode.Types.FREE_TIME, null);

        this.activities.add(padding);
    }

    public void fixTransportationNodes() {
        List<ActivityNode> list = new ArrayList<>();

        ActivityNode last = this.activities.get(this.activities.size() - 1);

        if (last.getType() != ActivityNode.Types.FREE_TIME) {
            activities.add(new ActivityNode(timeStringToLocalTime(last.getEndTime()),
                    timeStringToLocalTime(last.getEndTime()), ActivityNode.Types.FREE_TIME, null));
            last = this.activities.get(this.activities.size() - 1);
        }

        if (timeStringToLocalTime(last.getEndTime()).isAfter(LocalTime.of(0, 0)) &&
                timeStringToLocalTime(last.getEndTime()).isBefore(LocalTime.of(6, 0))) {
            activities.remove(activities.size() - 1);
            activities.remove(activities.size() - 1);
        }

        for (int i = 0; i < this.getActivities().size(); i++) {
            if (this.getActivities().get(i).getType() == ActivityNode.Types.ATTRACTION ||
                    this.getActivities().get(i).getType() == ActivityNode.Types.FREE_TIME ||
                    this.getActivities().get(i).getType() == ActivityNode.Types.FLIGHT) {

                this.getActivities().get(i).setUniqueKey(GUID.generate());
                list.add(this.getActivities().get(i));

            } else {
                ActivityNode attractionBefore = this.getActivities().get(i - 2);
                ActivityNode paddingBefore = this.getActivities().get(i - 1);
                ActivityNode transportation = this.getActivities().get(i);
                ActivityNode paddingAfter = this.getActivities().get(i + 1);
                ActivityNode nextActivity = this.getActivities().get(i + 2);

                long paddingBeforeDuration = getNodeDuration(paddingBefore);
                long paddingAfterDuration = getNodeDuration(paddingAfter);
                long transportationDuration = getNodeDuration(transportation);

                String newPaddingBeforeEndTime = timeStringToLocalTime(paddingBefore.getStartTime())
                        .plusMinutes(paddingAfterDuration)
                        .plusMinutes(paddingBeforeDuration)
                        .toString();

                paddingBefore.setEndTime(newPaddingBeforeEndTime);

                String nextActivityStartTime = timeStringToLocalTime(nextActivity.getStartTime())
                        .minusMinutes(transportationDuration)
                        .toString();

                nextActivity.setStartTime(nextActivityStartTime);

                HashMap<ActivityNode.Types, Long> durations = new HashMap<>();
                durations.put(transportation.getType(), transportationDuration);
                nextActivity.setTransportation(transportation.getType(), durations,
                        attractionBefore.getAttraction().lng, attractionBefore.getAttraction().lat);

                paddingBefore.setUniqueKey(GUID.generate());
                nextActivity.setUniqueKey(GUID.generate());

                list.remove(list.size() - 1);
                list.add(paddingBefore);
                list.add(nextActivity);
                i += 2;
            }
        }
        setActivities(list);
    }

    private LocalTime timeStringToLocalTime(String timeString) {
        return LocalTime.parse(timeString);
    }

    private long getNodeDuration(ActivityNode node) {
        LocalTime startTime = timeStringToLocalTime(node.getStartTime());
        LocalTime endTime = timeStringToLocalTime(node.getEndTime());

        return Duration.between(startTime, endTime).toMinutes();
    }
}
