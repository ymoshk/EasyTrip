package itinerary;

import generator.GUID;
import model.attraction.Attraction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Itinerary {
    private final String itineraryId;
    private HashMap<String, List<template.Attraction>> attractions;
    private final List<ItineraryDay> itineraryDays;
    private final QuestionsData questionsData;
    private final int currentDayIndex;

    public Itinerary(HashMap<String, List<template.Attraction>> attractions, QuestionsData questionsData) {
        this.itineraryId = GUID.generate();
        this.itineraryDays = new ArrayList<>();
        this.attractions = new HashMap<>(attractions);
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

    public void addStartDayPadding(LocalDateTime startTime, LocalDateTime endTime){
        ItineraryDay itineraryDay = getItineraryDay(startTime.toLocalDate());

        itineraryDay.getActivities().get(0).setEndTime(endTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public void addTransportation(LocalDateTime startTime, LocalDateTime endTime, ActivityNode.Types type){
        getItineraryDay(startTime.toLocalDate()).addTransportationTime(startTime, endTime, type);
    }

    public void addFreeTime(LocalDateTime startTime, LocalDateTime endTime){
        getItineraryDay(startTime.toLocalDate()).addFreeTime(startTime, endTime);
    }

    public void setAttractions(HashMap<String, List<template.Attraction>> attractions) {
        this.attractions = attractions;
    }

    public void addOutboundToItinerary(){
        template.Flight flight = questionsData.getFlight();
        ItineraryDay itineraryDay;
        LocalDateTime departureFromOrigin;
        LocalDateTime arrivalToDestination;

        if(flight != null){
            departureFromOrigin = flight.getDepartureFromOrigin();
            arrivalToDestination = flight.getArrivalToDestination();

            itineraryDay = getItineraryDay(departureFromOrigin.toLocalDate());
            itineraryDay.getActivities().get(0).setEndTime(departureFromOrigin.format(DateTimeFormatter.ofPattern("HH:mm")));

            // check if flight continues to the next day
            if(arrivalToDestination.toLocalDate().isAfter(departureFromOrigin.toLocalDate())){
                // split flight time into two days
                itineraryDay.addFlightTime(departureFromOrigin,
                        arrivalToDestination.withHour(23).withMinute(59).withSecond(0));

                itineraryDay = getItineraryDay(arrivalToDestination.toLocalDate());

                itineraryDay.addFlightTime(arrivalToDestination.withHour(0).withMinute(0).withSecond(0),
                        arrivalToDestination);
            }
            else{
                itineraryDay.addFlightTime(departureFromOrigin, arrivalToDestination);
            }
        }
    }
}
