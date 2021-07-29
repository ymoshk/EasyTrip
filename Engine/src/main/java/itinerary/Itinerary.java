package itinerary;

import algorithm.Algorithms;
import generator.GUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Itinerary {
    final String id;
    final QuestionsData questionsData;
    HashMap<String, List<template.Attraction>> attractions;
    List<ItineraryDay> itineraryDays;


    public Itinerary(QuestionsData questionsData){
        id = GUID.generate();
        this.questionsData = questionsData;
        attractions = Algorithms.classifyAttractions(this.questionsData);
        itineraryDays = new ArrayList<>();
    }

    //  the first day is dayNumber = 1
    public void setItineraryDay(int dayNumber, ItineraryDay newItineraryDay){
        itineraryDays.set(dayNumber - 1, newItineraryDay);
    }
}
