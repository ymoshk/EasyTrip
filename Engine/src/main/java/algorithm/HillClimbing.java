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

        public boolean checkOpeningHours(Attraction attraction, LocalDateTime startTime, LocalDateTime endTime) {
            OpeningHours attractionOpeningHours =attraction.getOpeningHours();
            boolean startTimeInRange = true;

            if(attractionOpeningHours != null){
                if(attractionOpeningHours.permanentlyClosed != null && attractionOpeningHours.permanentlyClosed){
                    return false;
                }
                else{
                    OpeningHours.Period[] attractionPeriods = attractionOpeningHours.periods;

                    if(attractionPeriods != null){
                        DayOfWeek currentDay = startTime.getDayOfWeek();
                        //convert java day object to google day object according to ordinal
                        int googleDayIndex = (currentDay.ordinal() + 1) % 7;
                        OpeningHours.Period googlePeriodDay = attractionPeriods[currentDay.ordinal()];
                        startTimeInRange = startTime.toLocalTime().isAfter(googlePeriodDay.open.time);
                    }

                }
            }
            return startTimeInRange;
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

            System.out.println(currentState.getHeuristicValue());
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
            attractionToAdd = attractionList.get(rand.nextInt(15));
        }
        else{
            attractionList = placeTypeToAttraction.values().stream().flatMap(Collection::stream).
                    filter(this::isRelevantAttraction).collect(Collectors.toList());
            attractionToAdd = attractionList.get(rand.nextInt(500));
        }


        if(attractionToAdd != null){
            System.out.println(attractionToAdd.getName());
            attractionToBooleanMap.put(attractionToAdd.getId(), true);

            int attractionDurationMinutes = DefaultDurations.getESTOfAttraction(attractionToAdd);
            System.out.println("duration: " + attractionDurationMinutes);
            System.out.println(attractionToAdd.getPlaceType().toString());

            LocalDateTime attractionStartTime = currentTime;
            LocalDateTime attractionEndTime = currentTime.plusMinutes(attractionDurationMinutes);

            if(attractionStartTime.toLocalDate().isBefore(attractionEndTime.toLocalDate())){
                attractionEndTime = attractionEndTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
            }

            currentState.getItinerary().addAttraction(attractionToAdd,
                    attractionStartTime,
                    attractionEndTime);

            advanceCurrentTime(attractionDurationMinutes);
        }
    }

    private boolean isRelevantAttraction(Attraction attraction) {
        boolean notRestaurantAttraction = !attraction.getPlaceType().equals(PlaceType.RESTAURANT);
//        boolean isOpenNow = scheduleRestrictions.checkOpeningHours(attraction, currentTime,
//                currentTime.plusMinutes(DefaultDurations.getESTOfAttraction(attraction)));
//
//        return notRestaurantAttraction && isOpenNow;
        return notRestaurantAttraction;
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
