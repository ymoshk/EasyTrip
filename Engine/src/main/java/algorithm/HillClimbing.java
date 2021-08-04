package algorithm;

import itinerary.Itinerary;
import itinerary.QuestionsData;
import model.attraction.Attraction;
import model.location.City;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


//the Algorithm is based on this source
//https://www.baeldung.com/java-hill-climbing-algorithm
//https://www.geeksforgeeks.org/introduction-hill-climbing-artificial-intelligence/

public class HillClimbing {
    //    Define the current state as an initial state
    //    Loop until the goal state is achieved or no more operators can be applied on the current state:
    //    Apply an operation to current state and get a new state
    //    Compare the new state with the goal
    //    Quit if the goal state is achieved
    //    Evaluate new state with heuristic function and compare it with the current state
    //    If the newer state is closer to the goal compared to current state, update the current state

    private final QuestionsData preferences;
    private final double goalValue;
    private final List<Attraction> attractionsList;
    private final HashMap<Attraction, Boolean> attractionToBooleanMap;

    public HillClimbing(QuestionsData preferences, List<Attraction> attractionList, double goalValue) {
        this.preferences = preferences;
        this.goalValue = goalValue;
        this.attractionsList = attractionList;
        this.attractionToBooleanMap = new HashMap<>();
    }

    public Itinerary getItineraryWithHillClimbingAlgorithm(State initState) {
        List<State> stateList = new ArrayList<>();

        State currentState = initState;
        boolean noStateFound = false;

        // TODO: add a timer - maximum time instead of noStateFound
        while (currentState.getHeuristicValue() < goalValue || noStateFound) {
            noStateFound = true;
            State nextState = findNextState(currentState);
            if (nextState != null) {
                noStateFound = false;
                currentState = nextState;
            }
        }

        return null;
    }

    private State findNextState(State currentState) {

        return null;
    }

    public static HashMap<String, List<template.Attraction>> classifyAttractions(QuestionsData questionsData) {
        City city = questionsData.getCity();
        HashMap<String, List<template.Attraction>> res = null;

        if (city != null) {
            // TODO this method returns any attraction marked as recommended - change it
            res = new HashMap<>();
            List<Attraction> attractionList = city.getAttractionList();
            List<template.Attraction> attractionsTemplatesList = attractionList.stream()
                    .map(attraction -> new template.Attraction(attraction, true))
                    .collect(Collectors.toList());

            for (template.Attraction attraction : attractionsTemplatesList) {
                if (!res.containsKey(attraction.getClass().getSimpleName())) {
                    res.put(attraction.getClass().getSimpleName(), new ArrayList<>());
                }

                res.get(attraction.getClass().getSimpleName()).add(attraction);
            }
        }

        return res;
    }

    public static void main(String[] args) {
        try{
            QuestionsData questionsData = new QuestionsData("Israel", "Tel Aviv", 2,
                    0, 3, LocalDateTime.now(), LocalDateTime.now().plusDays(1), new ArrayList<>(),
                    new ArrayList<>());
            List<Attraction> attractionList = questionsData.getCity().getAttractionList();
            HillClimbing hillClimbing = new HillClimbing(questionsData, attractionList, 100.0);
            State state = new State(new Itinerary(new HashMap<>(), questionsData), 0.0);

            hillClimbing.getItineraryWithHillClimbingAlgorithm(state);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}
