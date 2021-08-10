package algorithm;

import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceType;
import constant.DefaultDurations;
import itinerary.Itinerary;
import itinerary.QuestionsData;
import model.attraction.Attraction;
import model.location.City;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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

    static private class ScheduleRestrictions {
        private LocalTime lunchTime;
        private boolean scheduledLunch;
        private LocalTime dinnerTime;
        private boolean scheduledDinner;

        public ScheduleRestrictions(QuestionsData preferences) {
            this.lunchTime = LocalTime.of(12,0, 0, 0);
            this.dinnerTime = LocalTime.of(19,0, 0, 0);
            this.scheduledLunch = false;
            this.scheduledDinner = false;
        }

        public LocalTime getLunchTime() {
            return lunchTime;
        }

        public boolean isScheduledLunch() {
            return scheduledLunch;
        }

        public LocalTime getDinnerTime() {
            return dinnerTime;
        }

        public boolean isScheduledDinner() {
            return scheduledDinner;
        }

        public void setScheduledLunch(boolean scheduledLunch) {
            this.scheduledLunch = scheduledLunch;
        }

        public void setScheduledDinner(boolean scheduledDinner) {
            this.scheduledDinner = scheduledDinner;
        }

        private boolean isRestaurantSchedule(LocalDateTime currentTime){
            boolean needLunch = !scheduledLunch && currentTime.toLocalTime().isAfter(lunchTime);
            boolean needDinner = !scheduledDinner && currentTime.toLocalTime().isAfter(dinnerTime);
            if(needLunch){
                scheduledLunch = true;
                return true;
            }
            if(needDinner){
                scheduledDinner = true;
                return true;
            }

            return false;
        }

        public boolean checkOpeningHours(Attraction attraction, LocalDateTime startTime) {
            boolean attractionInOpeningHours = true;
            LocalDateTime endTime = startTime.plusMinutes(DefaultDurations.getESTOfAttraction(attraction));
            OpeningHours attractionOpeningHours =attraction.getOpeningHours();

            if(attractionOpeningHours != null){
                if(attractionOpeningHours.permanentlyClosed != null && attractionOpeningHours.permanentlyClosed){
                    return false;
                }
                else{
                    OpeningHours.Period[] attractionPeriods = attractionOpeningHours.periods;

                    if(attractionPeriods != null){
                        DayOfWeek currentDay = startTime.getDayOfWeek();
                        //In case attraction periods exist check if open
                        attractionInOpeningHours = false;

                        // Open 24 hours is saved as "SUNDAY 00:00 - null"
                        if(attractionPeriods.length == 1){
                            OpeningHours.Period.OpenClose openClose = attractionPeriods[0].open;

                            if(openClose.day.getName().equalsIgnoreCase("Sunday")){
                                attractionInOpeningHours = openClose.time.getHour() == 0 && openClose.time.getMinute() == 0;
                            }

                            return attractionInOpeningHours;
                        }

                        for (OpeningHours.Period period:attractionPeriods) {
                            if(period.open != null){
                                if(period.open.day.getName().equalsIgnoreCase(currentDay.toString())){
                                    attractionInOpeningHours = checkAttractionInPeriod(period, startTime, endTime);
                                    break;
                                }
                            }
                        }
                    }

                }
            }

            return attractionInOpeningHours;
        }

        private boolean checkAttractionInPeriod(OpeningHours.Period period, LocalDateTime startTime, LocalDateTime endTime) {
            LocalTime periodStartTime = null;
            LocalTime periodEndTime = null;

            //cutting the end time to 23:59, although the attractions filter checks the whole durations
            if(startTime.toLocalDate().isBefore(endTime.toLocalDate())){
                endTime = endTime.withHour(23).withMinute(59).withSecond(0).withNano(0);
            }

            if(period.open.time !=null){
            //in case both attraction and period starts at the same time we want to make the period start a bit earlier
                periodStartTime = period.open.time.minusMinutes(1);
            }
            if(period.close.time !=null){
            //in case both attraction and period ends at the same time we want to make the period end a bit later
                periodEndTime = period.close.time.plusMinutes(1);
            }
            if(periodStartTime !=null && periodEndTime != null){
                return startTime.toLocalTime().isAfter(periodStartTime) &&
                        endTime.toLocalTime().isBefore(periodEndTime);
            }
            //no period start time or period end time means the place is opened 24 h probably
            else{
                return true;
            }
        }
    }

    private final QuestionsData preferences;
    private final double goalValue;
    private final HashMap<PlaceType, List<Attraction>> placeTypeToAttraction;
    private final HashMap<Long, Boolean> attractionToBooleanMap;
    private final Random rand;
    private LocalDateTime currentTime;
    private ScheduleRestrictions scheduleRestrictions;

    public HillClimbing(QuestionsData preferences, List<Attraction> attractionList, double goalValue) {
        this.preferences = preferences;
        this.goalValue = goalValue;
        this.placeTypeToAttraction = attractionListToAttractionHashMap(attractionList);
        this.attractionToBooleanMap = new HashMap<>();
        currentTime = getStartTimeByTravelerType();
        this.rand = new Random();
        this.scheduleRestrictions = new ScheduleRestrictions(preferences);
    }

    private LocalDateTime getStartTimeByTravelerType(){
        return preferences.getStartDate().withHour(8).withMinute(0).withSecond(0).withNano(0);
    }

    private HashMap<PlaceType, List<Attraction>> attractionListToAttractionHashMap(List<Attraction> attractionList){
        HashMap<PlaceType, List<Attraction>> res = new HashMap<>();

        attractionList.forEach(attraction -> {
            if(!res.containsKey(attraction.getPlaceType()))
                res.put(attraction.getPlaceType(), new ArrayList<>());

            res.get(attraction.getPlaceType()).add(attraction);
        });

        return res;
    }

    public Itinerary getItineraryWithHillClimbingAlgorithm(State initState) {
        State currentState = initState;

        while(currentState.getHeuristicValue() < goalValue && currentTime.isBefore(preferences.getEndDate())){
            double prevHeuristic = currentState.getHeuristicValue();
            findNextState(currentState);

            System.out.println("Heuristic: " + currentState.getHeuristicValue());
            if(currentState.getHeuristicValue() < prevHeuristic){
                // remove last attraction
            }
        }

        return currentState.getItinerary();
    }

    private void findNextState(State currentState){

        addAttraction(currentState);
        currentState.setHeuristicValue(currentState.getHeuristicValue() + evaluate(currentState));

        // find best attraction (X attractions)
        // add attraction to nextState
        // set nextState heuristic
        // return nextState
    }

    private List<Attraction> fetchAttractionByTypes(PlaceType placeType){
        return placeTypeToAttraction.get(placeType);
    }

    private void addAttraction(State currentState) {
//        int randomAttractionIndex = rand.nextInt(10);
        List<Attraction> attractionList;
        Attraction attractionToAdd;

//        Attraction attractionToAdd = placeTypeToAttraction.values().stream().flatMap(Collection::stream).
//                filter(attraction -> !attractionToBooleanMap.containsKey(attraction.getId())).findFirst().orElse(null);

        if(scheduleRestrictions.isRestaurantSchedule(currentTime)){
            attractionList = fetchAttractionByTypes(PlaceType.RESTAURANT);
            attractionList = attractionList.stream().
                    filter(attraction -> scheduleRestrictions.checkOpeningHours(attraction, currentTime)).
                    collect(Collectors.toList());
            attractionToAdd = attractionList.get(rand.nextInt(15));
        }
        else{
            attractionList = placeTypeToAttraction.values().stream().flatMap(Collection::stream).
                    filter(attraction -> !attraction.getPlaceType().equals(PlaceType.RESTAURANT) &&
                            scheduleRestrictions.checkOpeningHours(attraction, currentTime)).
                    collect(Collectors.toList());
            attractionToAdd = attractionList.get(rand.nextInt(300));
        }


        if(attractionToAdd != null){
            attractionToBooleanMap.put(attractionToAdd.getId(), true);

            int attractionDurationMinutes = DefaultDurations.getESTOfAttraction(attractionToAdd);

            LocalDateTime attractionStartTime = currentTime;
            LocalDateTime attractionEndTime = currentTime.plusMinutes(attractionDurationMinutes);

            debugPrintAttraction(attractionToAdd, attractionStartTime, attractionEndTime);

            //cutting the end time to 00:00, although the attractions filter checks the whole durations
            if(attractionStartTime.toLocalDate().isBefore(attractionEndTime.toLocalDate())){
                attractionEndTime = attractionEndTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            }

            currentState.getItinerary().addAttraction(attractionToAdd,
                    attractionStartTime,
                    attractionEndTime);

            advanceCurrentTime(attractionDurationMinutes);
        }
    }


    private void advanceCurrentTime(int attractionDurationMinutes){
        LocalDateTime prevTime = currentTime;

        currentTime = currentTime.plusMinutes(attractionDurationMinutes);
        // check if moved to the next day
        if(!prevTime.toLocalDate().isEqual(currentTime.toLocalDate())){
            currentTime = currentTime.withHour(8).withMinute(0).withSecond(0).withNano(0);
            scheduleRestrictions.setScheduledLunch(false);
            scheduleRestrictions.setScheduledDinner(false);
        }
    }

    private int evaluate(State currentState){
        return rand.nextInt(10);
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
    private void debugPrintAttraction(Attraction attractionToAdd,LocalDateTime attractionStartTime,LocalDateTime attractionEndTime){
        System.out.println("Attraction name: " + attractionToAdd.getName());
        System.out.println("Duration: " + DefaultDurations.getESTOfAttraction(attractionToAdd));
        System.out.println("Place Type: " + attractionToAdd.getPlaceType().toString());
        if(attractionToAdd.getOpeningHours() != null && attractionToAdd.getOpeningHours().weekdayText != null){
            StringBuilder weekText = new StringBuilder();
            for (String dayData: attractionToAdd.getOpeningHours().weekdayText) {
                weekText.append(dayData + '\n');
            }
            System.out.println("Opening Hours: " + weekText.toString());
        }
        else{
            System.out.println("No opening hours data");
        }

        System.out.println("Start time: " + attractionStartTime);
        System.out.println("End time: " + attractionEndTime);
        System.out.println("\n\n");
    }

    //TODO: attractionToAdd.getOpeningHours() == null && attractionToAdd.getOpeningHours().weekdayText == null --> lower rating
    public static void main(String[] args) {
        try{
            QuestionsData questionsData = new QuestionsData("Israel", "Tel Aviv", 2,
                    0, 3, LocalDateTime.now(), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                    new ArrayList<>());
            List<Attraction> attractionList = questionsData.getCity().getAttractionList();
            //TODO: convert to hashmap or retrieve by type from DB.
            HillClimbing hillClimbing = new HillClimbing(questionsData, attractionList, 200.0);
            State state = new State(new Itinerary(new HashMap<>(), questionsData), 0.0);


            hillClimbing.getItineraryWithHillClimbingAlgorithm(state);

        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}
