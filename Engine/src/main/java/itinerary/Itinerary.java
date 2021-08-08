package itinerary;

import generator.GUID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Itinerary {
    private final String itineraryId;
    private final HashMap<String, List<template.Attraction>> attractions;
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
}
