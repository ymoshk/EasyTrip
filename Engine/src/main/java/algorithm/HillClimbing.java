package algorithm;

import com.google.maps.model.LatLng;
import com.google.maps.model.OpeningHours;
import constant.DefaultDurations;
import evaluators.AttractionEvaluator;
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

    //TODO: pick best of 10 attraction depends on: rating, raters No.
    //TODO: consider distance
    //TODO: add breaks depending on traveler type - depends on transportation time
    //TODO: consider user preferences
    //TODO: change attraction durations, lunch & dinner
    //TODO: save attraction name as constants
    static private class ScheduleRestrictions {
        private final LocalTime LUNCH_TIME;
        private boolean scheduledLunch;
        private final LocalTime DINNER_TIME;
        private boolean scheduledDinner;
        private final LocalTime BAR_TIME;
        private final LocalTime NIGHT_CLUB_TIME;
        private final LocalTime BEACH_TIME;
        private boolean scheduledBeach;

        public ScheduleRestrictions(QuestionsData preferences) {
            this.LUNCH_TIME = LocalTime.of(11,59, 0, 0);
            this.DINNER_TIME = LocalTime.of(18,59, 0, 0);
            this.scheduledLunch = false;
            this.scheduledDinner = false;
            this.BAR_TIME = LocalTime.of(16, 59,0,0);
            this.NIGHT_CLUB_TIME = LocalTime.of(20,59,0,0);
            this.BEACH_TIME = LocalTime.of(16, 1, 0, 0);
            this.scheduledBeach = false;
        }

        public LocalTime getLUNCH_TIME() {
            return LUNCH_TIME;
        }

        public boolean isScheduledLunch() {
            return scheduledLunch;
        }

        public LocalTime getDINNER_TIME() {
            return DINNER_TIME;
        }

        public boolean isScheduledDinner() {
            return scheduledDinner;
        }

        public boolean isScheduledBeach() {
            return scheduledBeach;
        }

        public void setScheduledBeach(boolean scheduledBeach) {
            this.scheduledBeach = scheduledBeach;
        }

        public void setScheduledLunch(boolean scheduledLunch) {
            this.scheduledLunch = scheduledLunch;
        }

        public void setScheduledDinner(boolean scheduledDinner) {
            this.scheduledDinner = scheduledDinner;
        }

        private boolean isRestaurantSchedule(LocalDateTime currentTime){
            boolean needLunch = !scheduledLunch && currentTime.toLocalTime().isAfter(LUNCH_TIME);
            boolean needDinner = !scheduledDinner && currentTime.toLocalTime().isAfter(DINNER_TIME);
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

        public boolean isPartyTime(Attraction attraction, LocalDateTime startTime){
            if(!attraction.getClass().getSimpleName().equalsIgnoreCase("Bar") &&
                    !attraction.getClass().getSimpleName().equalsIgnoreCase("NightClub")){
                return true;
            }
            if(attraction.getClass().getSimpleName().equalsIgnoreCase("Bar") &&
                    startTime.toLocalTime().isAfter(BAR_TIME)){
                return true;
            }
            if(attraction.getClass().getSimpleName().equalsIgnoreCase("NightClub") &&
                    startTime.toLocalTime().isAfter(NIGHT_CLUB_TIME)){
                return true;
            }

            return false;
        }

        public boolean isBeachTime(Attraction attraction, LocalDateTime startTime){
            if(!attraction.getClass().getSimpleName().equalsIgnoreCase("Beach")){
                return true;
            }
            if(scheduledBeach){
                return false;
            }
            if(startTime.toLocalTime().isBefore(BEACH_TIME)){
                return true;
            }

            return false;
        }

        // TODO: limit attraction list to X attractions?
        public List<Attraction> getNeighbourAttractions(LocalDateTime currentTime,
                                                        HashMap<String, List<Attraction>> placeTypeToAttraction,
                                                        HashMap<String, Boolean> attractionToBooleanMap) {
            List<Attraction> attractionList;

            if(isRestaurantSchedule(currentTime)){
                attractionList = placeTypeToAttraction.get("Restaurant");
                attractionList = attractionList.stream().
                        filter(attraction -> !attractionToBooleanMap.containsKey(attraction.getPlaceId()) &&
                                checkOpeningHours(attraction, currentTime)).
                        collect(Collectors.toList());
            }
            else{
                attractionList = placeTypeToAttraction.values().stream().flatMap(Collection::stream).
                        filter(attraction -> !attractionToBooleanMap.containsKey(attraction.getPlaceId()) &&
                                !attraction.getClass().getSimpleName().equalsIgnoreCase("Restaurant") &&
                                checkOpeningHours(attraction, currentTime) &&
                                isBeachTime(attraction, currentTime) &&
                                isPartyTime(attraction, currentTime)).
                        collect(Collectors.toList());
            }

            return attractionList;
        }
    }

    private final QuestionsData preferences;
    AttractionEvaluator attractionEvaluator;
    private final double goalValue;
    private final HashMap<String, List<Attraction>> placeTypeToAttraction;
    private final HashMap<String, Boolean> attractionToBooleanMap;
    private final Random rand;
    private LocalDateTime currentTime;
    private Attraction lastAttraction;
    private final ScheduleRestrictions scheduleRestrictions;
    private final int NUM_NEIGHBOURS_TO_CHECK = 15;
    private final int EARTH_RADIUS = 6371; // Radius of the earth in km


    public HillClimbing(QuestionsData preferences, List<Attraction> attractionList, double goalValue) {
        this.preferences = preferences;
        this.goalValue = goalValue;
        this.placeTypeToAttraction = attractionListToAttractionHashMap(attractionList);
        this.attractionEvaluator = new AttractionEvaluator(placeTypeToAttraction);
        this.attractionToBooleanMap = new HashMap<>();
        currentTime = getStartTimeByTravelerType();
        this.rand = new Random();
        this.lastAttraction = null;
        this.scheduleRestrictions = new ScheduleRestrictions(preferences);

    }

    private LocalDateTime getStartTimeByTravelerType(){
        return preferences.getStartDate().withHour(8).withMinute(0).withSecond(0).withNano(0);
    }

    private HashMap<String, List<Attraction>> attractionListToAttractionHashMap(List<Attraction> attractionList){
        HashMap<String, List<Attraction>> res = new HashMap<>();

        attractionList.forEach(attraction -> {
            // don't add hotels to itinerary
            if(!attraction.getClass().getSimpleName().equalsIgnoreCase("Hotel")){
                if(!res.containsKey(attraction.getClass().getSimpleName()))
                    res.put(attraction.getClass().getSimpleName(), new ArrayList<>());

                res.get(attraction.getClass().getSimpleName()).add(attraction);
            }
        });

        return res;
    }

    public Itinerary getItineraryWithHillClimbingAlgorithm(State initState) {
        State currentState = initState;

        while(currentTime.isBefore(preferences.getEndDate())){
            addAttraction(currentState);
        }

        return currentState.getItinerary();
    }

    private void findNextState(State currentState){

        addAttraction(currentState);
//        currentState.setHeuristicValue(currentState.getHeuristicValue() + evaluate(currentState));

        // find best attraction (X attractions)
        // add attraction to nextState
        // set nextState heuristic
        // return nextState
    }


    public Attraction findBestAttraction(List<Attraction> attractionList){
        if(attractionList.isEmpty()){
            return null;
        }

        int attractionListSize = attractionList.size();
        Attraction maxAttraction = attractionList.get(rand.nextInt(attractionListSize - 1));
        double maxValue = attractionEvaluator.evaluateAttraction(maxAttraction);
        Attraction curAttraction;
        double curValue;
        double distance;

        for(int i=0; i<NUM_NEIGHBOURS_TO_CHECK; i++){
            curAttraction = attractionList.get(rand.nextInt(attractionListSize - 1));
            distance = calculateDistance(lastAttraction, curAttraction);
            curValue = attractionEvaluator.evaluateAttraction(curAttraction, distance);
//            System.out.println("type: " + curAttraction.getPlaceType() + " name: " + curAttraction.getName() +
//                    " grade: " + curValue);

            if(curValue > maxValue){
                maxAttraction = curAttraction;
                maxValue = curValue;
            }
        }

        return maxAttraction;
    }

    private void addAttraction(State currentState) {
        List<Attraction> attractionList;
        Attraction attractionToAdd;

        attractionList = scheduleRestrictions.getNeighbourAttractions(currentTime, placeTypeToAttraction, attractionToBooleanMap);
        attractionToAdd = findBestAttraction(attractionList);

        if(attractionToAdd != null){
            attractionToBooleanMap.put(attractionToAdd.getPlaceId(), true);

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

            //after adding the attraction, it's now the last attraction we added.
            lastAttraction = attractionToAdd;
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
            scheduleRestrictions.setScheduledBeach(false);
            lastAttraction = null;
        }
    }

//    https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
    private double calculateDistance(Attraction source, Attraction destination){
        //case we initial an empty route, and there's no last attraction
        if(source == null){
            return 0;
        }

        double latDelta = deg2rad(destination.getLat() - source.getLat());
        double longDelta = deg2rad(destination.getLng() - source.getLng());

        double a = Math.sin(latDelta/2) * Math.sin(latDelta/2) +
                Math.cos(deg2rad(destination.getLat())) * Math.cos(deg2rad(source.getLat())) *
                        Math.sin(longDelta/2) * Math.sin(longDelta/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double result = EARTH_RADIUS * c;
//        System.out.println("source: " + source.getName());
//        System.out.println("destination: " + destination.getName());
//        System.out.println("distance: " + result + "KM");

        return result;
    }

    private double deg2rad(double degree) {
        return degree * (Math.PI/180);
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
        if(lastAttraction != null){
            System.out.println("last attraction: " + lastAttraction.getName());
        }
        if(lastAttraction !=null && !lastAttraction.equals(attractionToAdd)){
            System.out.println("Attraction name: " + attractionToAdd.getName());
            System.out.println("Distance from last attraction: " + calculateDistance(lastAttraction, attractionToAdd));
            System.out.println("Algorithm Grade: " + attractionEvaluator.evaluateAttraction(attractionToAdd));
            System.out.println("Algorithm Grade with distance: " + attractionEvaluator.evaluateAttraction(attractionToAdd, calculateDistance(lastAttraction, attractionToAdd)));
        }
//        System.out.println("Duration: " + DefaultDurations.getESTOfAttraction(attractionToAdd));
        System.out.println("Place Type: " + attractionToAdd.getClass().getSimpleName());
//        if(attractionToAdd.getOpeningHours() != null && attractionToAdd.getOpeningHours().weekdayText != null){
//            StringBuilder weekText = new StringBuilder();
//            for (String dayData: attractionToAdd.getOpeningHours().weekdayText) {
//                weekText.append(dayData + '\n');
//            }
//            System.out.println("Opening Hours: " + weekText.toString());
//        }
//        else{
//            System.out.println("No opening hours data");
//        }

        System.out.println("Start time: " + attractionStartTime);
        System.out.println("End time: " + attractionEndTime);
        double attractionRate = AttractionEvaluator.evaluateByRating(attractionToAdd);
//        System.out.println("Attraction Rate: " + attractionRate + "\n" + "Google Rate: "  + attractionToAdd.getRating() + "\n"
//        + "Reviews No. " + attractionToAdd.getUserRatingsTotal());
//        System.out.println("Algorithm Grade: " + attractionEvaluator.evaluateAttraction(attractionToAdd));
//        System.out.println("Algorithm Grade: " + attractionEvaluator.evaluateAttraction(attractionToAdd));

//        System.out.println("Reviews Grade: " + attractionEvaluator.evaluateRestaurantByToNumOfReviews(attractionToAdd));
//        System.out.println("Attraction  place: " +attractionEvaluator.getIndex(attractionToAdd) + "/" +
//                placeTypeToAttraction.get(attractionToAdd.getPlaceType()).size() );

        System.out.println("\n\n");
    }

//    private double calculateDistanceBetweenAttractions(Attraction source, Attraction destination){
//
//    }

    //TODO: attractionToAdd.getOpeningHours() == null && attractionToAdd.getOpeningHours().weekdayText == null --> lower rating
    public static void main(String[] args) {
        try{
            QuestionsData questionsData = new QuestionsData("Israel", "Tel Aviv", 2,
                    0, 3, LocalDateTime.now(), LocalDateTime.now().plusDays(2), new ArrayList<>(),
                    new ArrayList<>());
            List<Attraction> attractionList = questionsData.getCity().getAttractionList();
//            attractionList.forEach(attraction -> System.out.println(attraction.getName()));

            //TODO: convert to hashmap or retrieve by type from DB.
            HillClimbing hillClimbing = new HillClimbing(questionsData, attractionList, 200.0);
            State state = new State(new Itinerary(new HashMap<>(), questionsData), 0.0);
//            AttractionEvaluator attractionEvaluator = new AttractionEvaluator(hillClimbing.placeTypeToAttraction);

            hillClimbing.getItineraryWithHillClimbingAlgorithm(state);

        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}
