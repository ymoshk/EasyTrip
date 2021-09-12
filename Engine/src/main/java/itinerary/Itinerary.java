package itinerary;

import generator.GUID;
import model.attraction.Attraction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Itinerary {
    private final String itineraryId;
    private final List<ItineraryDay> itineraryDays;
    private final QuestionsData questionsData;
    private final int currentDayIndex;
    private HashMap<String, List<template.Attraction>> attractions;

    public Itinerary(HashMap<String, List<template.Attraction>> attractions, QuestionsData questionsData) {
        this.itineraryId = GUID.generate();
        this.itineraryDays = new ArrayList<>();
        if (attractions == null) {
            this.attractions = new HashMap<>();
        } else {
            this.attractions = new HashMap<>(attractions);
        }
        this.questionsData = questionsData;
        this.currentDayIndex = 0;
        setDays();
    }

    public int getCurrentDayIndex() {
        return currentDayIndex;
    }

    ItineraryDay getItineraryDay(LocalDate date) {
        return itineraryDays.stream().
                filter(itineraryDay ->
                        itineraryDay.getDate().isEqual(date)).findFirst().orElse(null);
    }

    private void setDays() {
        LocalDate start = this.questionsData.getStartDate().toLocalDate();
        LocalDate end = this.questionsData.getEndDate().toLocalDate();

        for (LocalDate current = start; current.isBefore(end.plusDays(1)); current = current.plusDays(1)) {
            this.itineraryDays.add(new ItineraryDay(current));
        }
    }

    public HashMap<String, List<template.Attraction>> getAttractions() {
        return attractions;
    }

    public void setAttractions(HashMap<String, List<template.Attraction>> attractions) {
        this.attractions = attractions;
    }

    public List<ItineraryDay> getItineraryDays() {
        return itineraryDays;
    }

    public QuestionsData getQuestionsData() {
        return questionsData;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    //  the first day is dayNumber = 0
    public void setItineraryDay(int dayNumber, ItineraryDay newItineraryDay) {
        itineraryDays.set(dayNumber, newItineraryDay);
    }

    public void addItineraryDay(ItineraryDay newItineraryDay) {
        itineraryDays.add(newItineraryDay);
    }

    public void addAttraction(Attraction attraction, LocalDateTime startTime, LocalDateTime endTime) {
        ItineraryDay dayToUpdate = getItineraryDay(startTime.toLocalDate());

        if (dayToUpdate == null) {
            itineraryDays.add(new ItineraryDay(startTime.toLocalDate()));
            dayToUpdate = getItineraryDay(startTime.toLocalDate());
        }

        dayToUpdate.addAttractionToEnd(attraction, startTime, endTime);
    }

    public void addStartDayPadding(LocalDateTime startTime, LocalDateTime endTime) {
        ItineraryDay itineraryDay = getItineraryDay(startTime.toLocalDate());
        ActivityNode activityNode = itineraryDay.getActivities().get(0);

        if (activityNode.getType().equals(ActivityNode.Types.FLIGHT)) {
            itineraryDay.getActivities().get(1).setEndTime(endTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            itineraryDay.getActivities().get(0).setEndTime(endTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    public void addTransportation(LocalDateTime startTime, LocalDateTime endTime, ActivityNode.Types type) {
        getItineraryDay(startTime.toLocalDate()).addTransportationTime(startTime, endTime, type);
    }

    public void addFreeTime(LocalDateTime startTime, LocalDateTime endTime) {
        getItineraryDay(startTime.toLocalDate()).addFreeTime(startTime, endTime);
    }

    public void addOutboundToItinerary() {
        template.Flight flight = questionsData.getFlight();
        ItineraryDay itineraryDay;
        LocalDateTime departureFromOrigin;
        LocalDateTime arrivalToDestination;

        if (flight != null) {
            departureFromOrigin = flight.getDepartureFromOrigin();
            arrivalToDestination = flight.getArrivalToDestination();

            itineraryDay = getItineraryDay(departureFromOrigin.toLocalDate());

            if (departureFromOrigin.toLocalTime().isAfter(LocalTime.of(8, 0, 0))) {
                // add padding free time before flight
                itineraryDay.getActivities().get(0).setEndTime(departureFromOrigin.format(DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                // remove free time before flight
                itineraryDay.getActivities().remove(0);
            }

            // check if flight continues to the next day
            if (arrivalToDestination.toLocalDate().isAfter(departureFromOrigin.toLocalDate())) {
                // split flight time into two days
                itineraryDay.addFlightTime(departureFromOrigin,
                        departureFromOrigin.withHour(23).withMinute(59).withSecond(0));

                itineraryDay = getItineraryDay(arrivalToDestination.toLocalDate());

                itineraryDay.addFlightTimeAtTheBeginning(arrivalToDestination.withHour(0).withMinute(0).withSecond(0),
                        arrivalToDestination);
            } else {
                itineraryDay.addFlightTime(departureFromOrigin, arrivalToDestination);
            }
        }
    }

    public void addReturnToItinerary() {
        template.Flight flight = getQuestionsData().getFlight();
        ItineraryDay itineraryDay;
        LocalDateTime departureFromDestination;
        LocalDateTime arrivalToOrigin;
        ActivityNode lastActivity;

        if (flight != null) {
            departureFromDestination = flight.getDepartureFromDestination();
            arrivalToOrigin = flight.getArrivalToOrigin();

            itineraryDay = getItineraryDay(departureFromDestination.toLocalDate());

            lastActivity = itineraryDay.getActivities().get(itineraryDay.getActivities().size() - 1);
            lastActivity.setEndTime(departureFromDestination.format(DateTimeFormatter.ofPattern("HH:mm")));

            // check if flight continues to the next day
            if (arrivalToOrigin.toLocalDate().isAfter(departureFromDestination.toLocalDate())) {
                // trim flight end in itinerary
                itineraryDay.addFlightTime(departureFromDestination,
                        departureFromDestination.withHour(23).withMinute(59).withSecond(0));
            } else {
                itineraryDay.addFlightTime(departureFromDestination, arrivalToOrigin);
                itineraryDay.addFreeTime(arrivalToOrigin, arrivalToOrigin);
            }
        }
    }

    public void fixTransportationNodes() {
        this.getItineraryDays().forEach(ItineraryDay::fixTransportationNodes);
    }
}
