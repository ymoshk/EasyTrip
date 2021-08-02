package itinerary;

import generator.GUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Itinerary {
    final String id;
    HashMap<String, List<template.Attraction>> attractions;
    List<ItineraryDay> itineraryDays;


    public Itinerary(){
        id = GUID.generate();
        itineraryDays = new ArrayList<>();
    }

    //  the first day is dayNumber = 0
    public void setItineraryDay(int dayNumber, ItineraryDay newItineraryDay){
        itineraryDays.set(dayNumber, newItineraryDay);
    }

    public void addItineraryDay(ItineraryDay newItineraryDay){
        itineraryDays.add(newItineraryDay);
    }
}
