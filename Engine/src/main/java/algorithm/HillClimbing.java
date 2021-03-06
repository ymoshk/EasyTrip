package algorithm;

import com.google.maps.model.*;
import com.google.maps.model.Duration;
import connection.DataEngine;
import constant.DefaultBeaches;
import constant.DefaultDurations;
import distanceCalculator.DistanceCalculator;
import evaluators.AttractionEvaluator;
import itinerary.ActivityNode;
import itinerary.Itinerary;
import itinerary.ItineraryDay;
import itinerary.QuestionsData;
import model.attraction.Attraction;
import model.location.City;
import model.travel.Travel;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


//the Algorithm is based on this source
//https://www.baeldung.com/java-hill-climbing-algorithm
//https://www.geeksforgeeks.org/introduction-hill-climbing-artificial-intelligence/

//https://bigseventravel.com/most-visited-cities-in-europe/

public class HillClimbing {
    static private class ScheduleRestrictions {
        private LocalTime START_TIME;
        private LocalTime END_TIME;
        private LocalTime LUNCH_TIME;
        private boolean scheduledLunch;
        private LocalTime DINNER_TIME;
        private boolean scheduledDinner;
        private final LocalTime BAR_TIME;
        private final LocalTime NIGHT_CLUB_TIME;
        private final LocalTime BEACH_TIME;
        private boolean scheduledBeach;
        private final LocalTime PARK_TIME;
        private boolean scheduledCasino;
        private final boolean casinoIncluded;
        private boolean scheduledSpa;
        private final boolean spaIncluded;
        private final boolean isFamilyTrip;
        private final boolean amusementParkIncluded;
        private boolean scheduledAmusementPark;
        private boolean nightClubIncluded;
        private final City city;
        private final List<String> attractionTags;
        private final List<String> vibeTags;
        private final boolean hasFlight;
        private final LocalDateTime startVacationDateTime;
        private final LocalDateTime endVacationTime;

        public ScheduleRestrictions(QuestionsData preferences, List<String> attractionTags, List<String> vibeTags) {
            initTimeConstraints(preferences, vibeTags);
            this.city = preferences.getCity();
            this.BAR_TIME = LocalTime.of(16, 59,0,0);
            this.NIGHT_CLUB_TIME = LocalTime.of(20,59,0,0);
            this.BEACH_TIME = LocalTime.of(16, 1, 0, 0);
            this.scheduledBeach = false;
            this.PARK_TIME = LocalTime.of(19, 1, 0, 0);
            this.scheduledCasino = false;
            this.scheduledSpa = false;
            this.isFamilyTrip = preferences.getChildrenCount() > 0;
            this.attractionTags = attractionTags;
            this.vibeTags = vibeTags;
            this.amusementParkIncluded = attractionTags.contains("AMUSEMENTPARK") || preferences.getChildrenCount() > 0;
            this.spaIncluded = attractionTags.contains("SPA");
            this.casinoIncluded = attractionTags.contains("CASINO");
            this.nightClubIncluded = attractionTags.contains("NIGHTCLUB");
            this.hasFlight = preferences.getFlight() != null;
            this.startVacationDateTime = initStartVacationDateTime(preferences);
            this.endVacationTime = initEndVacationTime(preferences);
            initMealsRestrictions();
        }

        private void initMealsRestrictions(){
            if(hasFlight){
                if(startVacationDateTime.toLocalTime().isAfter(LUNCH_TIME.plusHours(2))){
                    this.scheduledLunch = true;
                }
                if(startVacationDateTime.toLocalTime().isAfter(DINNER_TIME.plusHours(2))){
                    this.scheduledDinner = true;
                }
            }
            else{
                this.scheduledLunch = false;
                this.scheduledDinner = false;
            }
        }

        private LocalDateTime initEndVacationTime(QuestionsData preferences){
            if(hasFlight){
                return preferences.getFlight().getDepartureFromDestination().minusHours(3);
            }
            else{
                return preferences.getEndDate();
            }
        }

        private LocalDateTime initStartVacationDateTime(QuestionsData preferences){
            if(hasFlight){
                return preferences.getFlight().getArrivalToDestination().plusHours(3);
            }
            else{
                return preferences.getStartDate();
            }
        }

        public void setScheduledCasino(boolean scheduledCasino) {
            this.scheduledCasino = scheduledCasino;
        }

        public void setScheduledSpa(boolean scheduledSpa) {
            this.scheduledSpa = scheduledSpa;
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

        public LocalTime getSTART_TIME(LocalDate currentDay) {
            if(hasFlight){
                if(currentDay.isEqual(startVacationDateTime.toLocalDate())){
                    // flight arrival is before schedule beginning
                    if(startVacationDateTime.toLocalTime().isBefore(START_TIME.plusMinutes(1))){
                        return START_TIME;
                    }
                    else{
                        return startVacationDateTime.toLocalTime();
                    }
                }
                // current day isn't arriving day
                else{
                    return START_TIME;
                }
            }
            else{
                return START_TIME;
            }
        }

        public LocalTime getEND_TIME(LocalDate currentDay) {
            if(hasFlight && currentDay.isEqual(endVacationTime.toLocalDate())){
                    return endVacationTime.toLocalTime();
            }
            else{
                return END_TIME;
            }
        }

        public boolean isAmusementParkIncluded() {
            return amusementParkIncluded;
        }

        public boolean isFamilyTrip() {
            return isFamilyTrip;
        }

        public void initTimeConstraints(QuestionsData questionsData, List<String> vibeTags){

            this.START_TIME = initStartTimeByTraveler(vibeTags);

            if(questionsData.getChildrenCount() > 0){
                this.LUNCH_TIME = LocalTime.of(11,59, 0, 0);
                this.DINNER_TIME = LocalTime.of(17, 29, 0, 0);
                this.END_TIME = LocalTime.of(23, 0, 0, 0);
            }
            else{
                this.LUNCH_TIME = LocalTime.of(11,59, 0, 0);
                this.DINNER_TIME = LocalTime.of(17,59, 0, 0);
                this.END_TIME = LocalTime.of(23, 59, 0, 0);
            }
        }

        private LocalTime initStartTimeByTraveler(List<String> vibeTags) {
            if(vibeTags.contains("NightOwl")){
                return LocalTime.of(10,0,0,0);
            }
            else if(vibeTags.contains("EarlyBird")){
                return LocalTime.of(8,0,0,0);

            }
            else{
                //default start time
                return LocalTime.of(9,0,0,0);
            }
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

        public boolean isAmusementParkTime(LocalDateTime currentTime, LocalDateTime tripStart){
            //don't schedule amusement park on the first day
            if(currentTime.toLocalDate().isEqual(tripStart.toLocalDate())){
                return false;
            }
            //already scheduled amusement park
           if(scheduledAmusementPark){
               return false;
           }
           //schedule amusement park and don't schedule anymore
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
                    startTime.toLocalTime().isAfter(NIGHT_CLUB_TIME) && nightClubIncluded){
                return true;
            }

            return false;
        }

        public boolean isCasinoTime(Attraction attraction, LocalDateTime startTime){
            if(!attraction.getClass().getSimpleName().equalsIgnoreCase("Casino")){
                return true;
            }

            return casinoIncluded && !scheduledCasino;
        }

        public boolean isSpaTime(Attraction attraction, LocalDateTime startTime){
            if(!attraction.getClass().getSimpleName().equalsIgnoreCase("Spa")){
                return true;
            }

            return spaIncluded && !scheduledSpa;
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

        public boolean isParkTime(Attraction attraction, LocalDateTime startTime){
            if(!attraction.getClass().getSimpleName().equalsIgnoreCase("Park")){
                return true;
            }
            if(startTime.toLocalTime().isBefore(PARK_TIME)){
                return true;
            }

            return false;
        }

        // don't schedule two sequence cafes
        public boolean isCoffeeTime(Attraction lastAttraction, Attraction curAttraction){
            // first attraction on the day
            if(lastAttraction == null){
                return true;
            }
            if(lastAttraction.getClass().getSimpleName().equalsIgnoreCase("Cafe") &&
                curAttraction.getClass().getSimpleName().equalsIgnoreCase("Cafe")){
                return false;
            }

            return true;
        }

        private Attraction getTopAmusementParkAttraction(LocalDateTime currentTime,
                                                         LocalDateTime StartTrip,
                                                         HashMap<String, Boolean> attractionToBooleanMap,
                                                         AttractionEvaluator attractionEvaluator){

            Attraction topAmusementPark = attractionEvaluator.getTopAmusementPark();
            // no available amusement park
            if(topAmusementPark == null){
                return null;
            }

            //check if amusement park is open in the current day
            if(isAmusementParkTime(currentTime, StartTrip) &&
                !attractionToBooleanMap.containsKey(topAmusementPark.getPlaceId())){
                if(checkOpeningHours(topAmusementPark, currentTime.plusHours(3))){
                    setScheduledLunch(true);
                    return topAmusementPark;
                }
            }

            return null;
        }

        public List<Attraction> getNeighbourAttractions(LocalDateTime currentTime,
                                                        HashMap<String, List<Attraction>> placeTypeToAttraction,
                                                        HashMap<String, Boolean> attractionToBooleanMap,
                                                        Attraction lastAttraction,
                                                        LocalDateTime startTrip,
                                                        AttractionEvaluator attractionEvaluator) {
            List<Attraction> attractionList = new ArrayList<>();

            // add half day amusement park in case the user chose amusement park
            if(amusementParkIncluded){
                Attraction topAmusementPark = getTopAmusementParkAttraction(currentTime, startTrip,
                        attractionToBooleanMap, attractionEvaluator);
                if(topAmusementPark != null){
                    attractionList.add(topAmusementPark);
                    scheduledAmusementPark = true;
                    return attractionList;
                }
            }

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
                                isParkTime(attraction, currentTime) &&
                                isPartyTime(attraction, currentTime) &&
                                isCasinoTime(attraction, currentTime) &&
                                isSpaTime(attraction, currentTime) &&
                                checkDistanceFromCityCenter(attraction,lastAttraction) &&
                                isCoffeeTime(lastAttraction, attraction)).
                        collect(Collectors.toList());
            }

            return attractionList;
        }

        private boolean checkDistanceFromCityCenter(Attraction attraction, Attraction lastAttraction) {
            if(lastAttraction != null){
                return true;
            }
            return DataEngine.calculateDistance(city, attraction) < 15;
        }

        public void resetRestrictions(LocalDate currentDate){
            // apply restrictions to arrival day
            if(hasFlight && currentDate.isEqual(startVacationDateTime.toLocalDate())){
                if(startVacationDateTime.toLocalTime().isAfter(LUNCH_TIME.plusHours(2))){
                    setScheduledLunch(true);
                }
                if(startVacationDateTime.toLocalTime().isAfter(DINNER_TIME.plusHours(2))){
                    setScheduledDinner(true);
                }
            }
            else{
                setScheduledLunch(false);
                setScheduledDinner(false);
            }

            setScheduledBeach(false);
            setScheduledCasino(false);
            setScheduledSpa(false);
        }

        public boolean isHasFlight() {
            return hasFlight;
        }

        public LocalDateTime getStartVacationDateTime() {
            return startVacationDateTime;
        }
    }

    private final QuestionsData preferences;
    AttractionEvaluator attractionEvaluator;
    private final HashMap<String, List<Attraction>> placeTypeToAttraction;
    private HashMap<String, Boolean> attractionToBooleanMap;
    DataEngine dataEngine;
    private LocalDateTime currentTime;
    private Attraction lastAttraction;
    private ScheduleRestrictions scheduleRestrictions;
    private final int EARTH_RADIUS = 6371; // Radius of the earth in km
    private final int TOP_SIGHTS_NUM = 7;
    private final long WALKABLE_TIME = 15 * 60;   // seconds
    private long BREAK_TIME_FACTOR;
    private final boolean hasCar;
    private final List<String> attractionTags;
    private final List<String> vibeTags;
    private final List<String> transportationTags;
    private boolean isFirstRouteAttraction;
    private int topAttractionIndex;
    private List<Attraction> topSightAttractions;
    private double totalRouteDistance;
    private int numOfAttractionsInRoute;
    private double sumAttractionEvaluation;
    private int counterPreferredAttraction;

    public HillClimbing(QuestionsData preferences, List<Attraction> attractionList) {
        this.preferences = preferences;
        this.attractionTags = new ArrayList<>();
        this.vibeTags = new ArrayList<>();
        this.transportationTags = new ArrayList<>();
        initTagsList(preferences);
        this.scheduleRestrictions = new ScheduleRestrictions(preferences, attractionTags, vibeTags);
        this.placeTypeToAttraction = attractionListToAttractionHashMap(attractionList);
        this.attractionEvaluator = new AttractionEvaluator(placeTypeToAttraction);
        this.attractionToBooleanMap = new HashMap<>();
        this.currentTime = getStartTimeByTravelerPreferences();
        this.dataEngine = DataEngine.getInstance();
        this.lastAttraction = null;
        removeAttractionDuplicationFromTouristAttraction();
        topSightAttractions = initTopSights(TOP_SIGHTS_NUM);
        removeDuplicateRestaurant();
        this.hasCar = transportationTags.contains("Car");
        //in seconds
        this.BREAK_TIME_FACTOR = calculateBreakTimeByTravelerType(vibeTags);
        this.isFirstRouteAttraction = true;
        this.totalRouteDistance = 0;
        this.numOfAttractionsInRoute = 0;
        this.sumAttractionEvaluation = 0;
        this.counterPreferredAttraction = 0;
    }

    private void resetState(){
        this.scheduleRestrictions = new ScheduleRestrictions(preferences, attractionTags, vibeTags);
        this.attractionToBooleanMap = new HashMap<>();
        this.currentTime = getStartTimeByTravelerPreferences();
        this.lastAttraction = null;
        this.isFirstRouteAttraction = true;
        this.totalRouteDistance = 0;
        this.numOfAttractionsInRoute = 0;
        this.sumAttractionEvaluation = 0;
        this.counterPreferredAttraction = 0;
    }

    private long calculateBreakTimeByTravelerType(List<String> vibeTags){
        if(vibeTags.contains("Chill")){
            return 25 * 60;
        }
        else if(vibeTags.contains("FastPaced")){
            return 5 * 60;
        }
        else{
            return 15 * 60;
        }
    }

    public  List<String> getAttractionTags() {
        return attractionTags;
    }

    public List<String> getVibeTags(){
        return vibeTags;
    }

    public AttractionEvaluator getAttractionEvaluator() {
        return attractionEvaluator;
    }

    private void initTagsList(QuestionsData preferences){
        preferences.getFavoriteAttractions().forEach(tripTag -> {
            attractionTags.add(tripTag.getTagName().replaceAll(" ", "").toUpperCase());
        });
        String touristAttractionTag = "TouristAttraction";
        attractionTags.add(touristAttractionTag.toUpperCase());
//        attractionTags.add("MARKET");
//        attractionTags.add("BAR");
//        attractionTags.add("BEACH");
//        attractionTags.add("MUSEUM");
//        attractionTags.add("ZOO");
//        attractionTags.add("AMUSEMENTPARK");
//        attractionTags.add("PARK");
//        attractionTags.add("NIGHTCLUB");
//        attractionTags.add("SPA");
//        attractionTags.add("CASINO");
//        attractionTags.add("AQUARIUM");
//        attractionTags.add("SHOPPINGMALL");


        preferences.getTripVibes().forEach(tripTag -> {
            vibeTags.add(tripTag.getTagName().replaceAll(" ", ""));
        });

        preferences.getTransportation().forEach(tripTag -> {
            transportationTags.add(tripTag.getTagName().replaceAll(" ", ""));
        });

//        attractionTags.forEach(System.out::println);
//        vibeTags.forEach(System.out::println);
//        transportationTags.forEach(System.out::println);
    }

    void removeDuplicateRestaurant(){
        List<Attraction> attractionList = placeTypeToAttraction.get("Restaurant");

        List<Attraction> filteredRestaurant = new ArrayList<>();
        HashMap<String, Boolean> attractionIdToBoolean = new HashMap<>();

        for (Attraction attraction : attractionList){
            if(!attractionIdToBoolean.containsKey(attraction.getPlaceId())){
                filteredRestaurant.add(attraction);
                attractionIdToBoolean.put(attraction.getPlaceId(), true);
            }
        }

        placeTypeToAttraction.put("Restaurant", filteredRestaurant);
    }

    List<Attraction> initTopSights(int numOfAttractions){
        List<Attraction> attractionList = placeTypeToAttraction.get("TopSight");

        // TODO: temporary band aid - remove duplicates
        List<Attraction> filteredAttraction = new ArrayList<>();
        HashMap<String, Boolean> attractionIdToBoolean = new HashMap<>();

        for (Attraction attraction : attractionList){
            if(!attractionIdToBoolean.containsKey(attraction.getPlaceId())){
                filteredAttraction.add(attraction);
                attractionIdToBoolean.put(attraction.getPlaceId(), true);
            }
        }

        if(filteredAttraction.size() < numOfAttractions){
            numOfAttractions = filteredAttraction.size();
        }

        filteredAttraction = filteredAttraction.stream().sorted(Comparator.
                comparingDouble(attraction -> attractionEvaluator.evaluateAttraction(attraction))).
                collect(Collectors.toList()).
                subList(filteredAttraction.size() - numOfAttractions, filteredAttraction.size());

        // pass top attraction to attraction evaluator
        attractionEvaluator.setTopAttractionIdList(filteredAttraction);
        System.out.println("TOP SIGHTS");
        filteredAttraction.forEach(attraction -> System.out.println(attraction.getName()));
        System.out.println("--------------------------------------------");

//        placeTypeToAttraction.remove("TopSight");
        Collections.reverse(filteredAttraction);
        return filteredAttraction;
    }

    void removeAttractionDuplicationFromTouristAttraction(){
        List<Attraction> attractionList = placeTypeToAttraction.get("TouristAttraction");

        attractionList = attractionList.stream().
                filter(attraction -> !attraction.getName().toUpperCase().contains("PARK")).
                collect(Collectors.toList());

        placeTypeToAttraction.put("TouristAttraction", attractionList);
    }

    private LocalDateTime getStartTimeByTravelerPreferences(){
        LocalDateTime startVacationDateTime = preferences.getStartDate();
        LocalDate currentDate = preferences.getStartDate().toLocalDate();

        return startVacationDateTime.with(scheduleRestrictions.getSTART_TIME(currentDate));
    }

    private boolean addAttractionToHashMap(String attractionType){
        if(attractionType.equalsIgnoreCase("Beach")){
            return DefaultBeaches.cityHasBeach(preferences.getCity().getCityName());
        }
        // Don't add hotels to itinerary
        else if(attractionType.equalsIgnoreCase("Hotel")){
            return false;
        }
        else if(!scheduleRestrictions.isFamilyTrip()){
            return true;
        }
        // restrict family trip
        else{
            if(attractionType.equalsIgnoreCase("NightClub")){
                return false;
            }
            // add bar only if user asked for
            else if(attractionType.equalsIgnoreCase("Bar")){
                return attractionTags.contains("BAR");
            }
            else{
                return true;
            }
        }
    }

    private HashMap<String, List<Attraction>> attractionListToAttractionHashMap(List<Attraction> attractionList){
        HashMap<String, List<Attraction>> res = new HashMap<>();
        String attractionSimpleName;

        for(Attraction attraction : attractionList){
            attractionSimpleName = attraction.getClass().getSimpleName();

            if(addAttractionToHashMap(attractionSimpleName)){
                if(!res.containsKey(attractionSimpleName)){
                    res.put(attractionSimpleName, new ArrayList<>());
                }

                res.get(attractionSimpleName).add(attraction);
            }
        }

//        attractionList.stream().filter(attraction -> addAttractionToHashMap(attraction.getClass().getSimpleName())).
//                forEach(attraction -> {
//                    if(!res.containsKey(attraction.getClass().getSimpleName()))
//                        res.put(attraction.getClass().getSimpleName(), new ArrayList<>());
//
//                    res.get(attraction.getClass().getSimpleName()).add(attraction);
//        });

        return res;
    }

    private void addArrangementTimeAfterFlight(State currentState){
        LocalDateTime startTime = preferences.getFlight().getArrivalToDestination();
        LocalDateTime endTime = preferences.getFlight().getArrivalToDestination().plusHours(3);

        // if arrival is before the day starts don't add free time
        if(preferences.getFlight().getArrivalToDestination().toLocalTime().isBefore(LocalTime.of(8,0,0))){
            return;
        }

        if(endTime.toLocalDate().isAfter(startTime.toLocalDate())){
            endTime = endTime.withHour(23).withMinute(59).withSecond(0);
        }

        currentState.getItinerary().addFreeTime(startTime, endTime);
    }

    public void getItineraryWithHillClimbingAlgorithm(State state, boolean isOptimizationRun, int topAttractionIndex) {
        if(preferences.getFlight() != null){
            addArrangementTimeAfterFlight(state);
            // if the traveller arrives in destination in the following day
            if(currentTime.toLocalDate().isBefore(scheduleRestrictions.getStartVacationDateTime().toLocalDate())){
                currentTime = currentTime.plusDays(1);
            }
        }

        // schedule attraction until vacation is over
        while(currentTime.isBefore(preferences.getEndDate())){
            addAttraction(state, isOptimizationRun, topAttractionIndex);

            if(scheduleRestrictions.isHasFlight() && currentTime.isAfter(scheduleRestrictions.endVacationTime)){
                break;
            }
        }
    }

    private Itinerary optimize(Itinerary itinerary) {
        List<ItineraryDay> itineraryDays = itinerary.getItineraryDays();
        List <ActivityNode> activityNodes;
        List<String> visitedAttraction = new ArrayList<>();
        template.Attraction previous = null, current = null,  next = null;
        for (ItineraryDay itineraryDay:itineraryDays) {
            activityNodes = itineraryDay.getActivities();
            int sizeOfDay = activityNodes.size();
            for (ActivityNode activityNode:activityNodes) {
                if(activityNode.getType().equals(ActivityNode.Types.ATTRACTION)){
                    current = activityNode.getAttraction();
                    int currentIndex = activityNodes.indexOf(activityNode);
                    if(currentIndex < sizeOfDay - 4){
                        next = activityNodes.get(currentIndex + 4).getAttraction();
                        if(previous != null){
                            check(previous, current, next, visitedAttraction,
                                    itineraryDay.getDate().atTime(LocalTime.parse(activityNode.getStartTime())));
                        }
                    }
                    previous = current;
                    visitedAttraction.add(previous.id);
                }
            }
            previous = null;
        }

        return  itinerary;
    }

    private void check(template.Attraction previous, template.Attraction current,
                       template.Attraction next, List<String> visitedAttraction, LocalDateTime startTime) {

        List<Attraction> attractions = placeTypeToAttraction.values()
                .stream().flatMap(Collection::stream).collect(Collectors.toList());

        Attraction previousAttraction = attractions.stream().filter(attraction ->
                attraction.getPlaceId().equalsIgnoreCase(previous.id)).findFirst().orElse(null);
        Attraction currentAttraction = attractions.stream().filter(attraction ->
                attraction.getPlaceId().equalsIgnoreCase(current.id)).findFirst().orElse(null);
        Attraction nextAttraction = attractions.stream().filter(attraction ->
                attraction.getPlaceId().equalsIgnoreCase(next.id)).findFirst().orElse(null);

        List<Attraction> filteredAttractions = attractions.stream().filter(attraction ->
                scheduleRestrictions.checkOpeningHours(attraction, startTime) &&
                !visitedAttraction.contains(attraction.getPlaceId())).collect(Collectors.toList());


        filteredAttractions.forEach(attraction -> {
            double possibleReplaceDistance = calculateDistance(previousAttraction, attraction) +
                    calculateDistance(attraction, nextAttraction);
            double originDistance = calculateDistance(previousAttraction, currentAttraction) +
                    calculateDistance(currentAttraction, nextAttraction);
            if(possibleReplaceDistance < originDistance){
                double possibleReplaceScore = attractionEvaluator.evaluateAttraction(attraction);
                double originScore = attractionEvaluator.evaluateAttraction(currentAttraction);
//                if(possibleReplaceScore + 5 >= originScore){
                    if(DefaultDurations.getESTOfAttraction(attraction) ==
                    DefaultDurations.getESTOfAttraction(currentAttraction)){
                        if(currentAttraction.getClass().getSimpleName().
                                equalsIgnoreCase("Restaurant")
                        && attraction.getClass().getSimpleName().
                                equalsIgnoreCase("Restaurant")){
                            System.out.println("Possible Restaurant replace for " + currentAttraction.getName()
                                    + " is " + attraction.getName() + " with improvement of " +
                                    (originDistance - possibleReplaceDistance));
                        }
//                        else if(!currentAttraction.getClass().getSimpleName().
//                                equalsIgnoreCase("Restaurant")
//                                && !attraction.getClass().getSimpleName().
//                                equalsIgnoreCase("Restaurant")){
//                            System.out.println("Possible replace for " + currentAttraction.getName()
//                                    + " is " + attraction.getName() + " with improvement of " +
//                                    (originDistance - possibleReplaceDistance));
//                        }
                    }
//                }
            }
        });

    }


    public Attraction findBestAttraction(List<Attraction> attractionList){
        if(attractionList.isEmpty()){
            return null;
        }

        boolean evaluateByDistance = true;
        Attraction maxAttraction = attractionList.get(0);
        Attraction topAmusementPark;
        double distance = calculateDistance(lastAttraction, maxAttraction);
        double maxValue = attractionEvaluator.evaluateAttraction(maxAttraction, distance, attractionTags, vibeTags);

        // if the user is in the park, bring the user back to the city
        topAmusementPark = attractionEvaluator.getTopAmusementPark();
        if(topAmusementPark != null && lastAttraction != null){
            if(lastAttraction.getPlaceId().equals(topAmusementPark.getPlaceId())){
                evaluateByDistance = false;
            }
        }

        Attraction curAttraction;
        double curValue;

        for (Attraction attraction : attractionList) {
            curAttraction = attraction;

            distance = calculateDistance(lastAttraction, curAttraction);
            if(evaluateByDistance){
                curValue = attractionEvaluator.evaluateAttraction(curAttraction, distance, attractionTags, vibeTags);
            }
            else{
                curValue = attractionEvaluator.evaluateAttraction(curAttraction);
            }

            if (curValue > maxValue) {
                maxAttraction = curAttraction;
                maxValue = curValue;
            }
        }

        return maxAttraction;
    }

    long estimateDurationByDistance(double distance, TravelMode travelMode){
        long seconds = 0;

        switch(travelMode){
            case WALKING:
                seconds = (long) (distance * 12);
                break;
            case TRANSIT:
                seconds = (long) (distance * 6);
                break;
            case DRIVING:
                seconds = (long) (distance * 4);
                break;
        }

        return seconds * 60;
    }

    Travel getTravelTime(Attraction attractionDestination, TravelMode travelMode, boolean isOptimizationRun){
        Travel travel;
        DistanceMatrixElement distanceMatrixElement;
        Duration duration;
        double distance;

        // fetch from API on the final run
        if(!isOptimizationRun){
            travel = dataEngine.getTravelFromApi(
                    lastAttraction.getGeometry().location,
                    attractionDestination.getGeometry().location, travelMode);
        }
        else{
            distanceMatrixElement = new DistanceMatrixElement();
            duration = new Duration();
            distance = DistanceCalculator.calculateDistance(lastAttraction.getGeometry().location,
                    attractionDestination.getGeometry().location) * 1.5;  // multiply by factor absolute distance

            duration.inSeconds = estimateDurationByDistance(distance, travelMode);

            distanceMatrixElement.duration = duration;
            distanceMatrixElement.duration.humanReadable = String.valueOf(duration.inSeconds / 60) + "mins";
            distanceMatrixElement.distance = new Distance();
            distanceMatrixElement.distance.humanReadable = String.valueOf(distance);
            distanceMatrixElement.status = DistanceMatrixElementStatus.OK;

            travel = new Travel(lastAttraction.getGeometry().location,
                    attractionDestination.getGeometry().location,
                    travelMode,
                    distanceMatrixElement);
        }

        return travel;
    }

    Travel getTransportationTime(Attraction attractionDestination, boolean isOptimizationRun){
        // let assume the distance is walkable
        Travel travel = getTravelTime(attractionDestination, TravelMode.WALKING, isOptimizationRun);

        if(travel.getDistanceMatrixElement().duration.inSeconds > WALKABLE_TIME){
            if(hasCar){
                travel = getTravelTime(attractionDestination, TravelMode.DRIVING, isOptimizationRun);
            }
            else{
                travel = getTravelTime(attractionDestination, TravelMode.TRANSIT, isOptimizationRun);
            }
        }

//        System.out.println(travel.getDistanceMatrixElement());
//        System.out.println(travel.getMode());
        return travel;
    }

    private void addBreakTime(Travel travel){
        int minutes;
        int minutesUnits;
        int minutesToAdd;
        long travelTimeSeconds;

        // add break time depends on travel time
        if(travel.getDistanceMatrixElement().status.equals(DistanceMatrixElementStatus.OK)){
            travelTimeSeconds = travel.getDistanceMatrixElement().duration.inSeconds;

            if(travelTimeSeconds < 15 * 60) {
                currentTime = currentTime.plusSeconds(BREAK_TIME_FACTOR);
            }
            else if(travelTimeSeconds < 25 * 60){   // for longer transportation times
                currentTime = currentTime.plusSeconds(BREAK_TIME_FACTOR / 2);
            }
            else{
                currentTime = currentTime.plusMinutes(5);
            }
        }

        // round minutes units to 10
        minutes = currentTime.toLocalTime().getMinute();
        if(minutes % 10 != 0){
            minutesUnits = minutes % 10;
            minutesToAdd = 10 - minutesUnits;

            currentTime = currentTime.plusMinutes(minutesToAdd);
        }

        currentTime = currentTime.withSecond(0).withNano(0);
    }

    private boolean addAmusementParkExtraTime(Attraction attraction){
        Attraction topAmusement = attractionEvaluator.getTopAmusementPark();

        if(topAmusement != null){
            return attraction.getPlaceId().equals(topAmusement.getPlaceId());
        }

        return false;
    }

    private void addStartDayPadding(State currentState){
        // traveller doesn't have flight
        if(!scheduleRestrictions.isHasFlight()){
            currentState.getItinerary().addStartDayPadding(currentTime, currentTime.with(
                    scheduleRestrictions.getSTART_TIME(currentTime.toLocalDate())));
            return;
        }
        // if it's arrival day, padding is already added
        if(!currentTime.toLocalDate().isEqual(scheduleRestrictions.getStartVacationDateTime().toLocalDate())){
            currentState.getItinerary().addStartDayPadding(currentTime, currentTime.with(
                    scheduleRestrictions.getSTART_TIME(currentTime.toLocalDate())));
        }
    }

    private Attraction addAttraction(State currentState, boolean isOptimizationRun, int topAttractionIndex) {
        List<Attraction> attractionList;
        Attraction attractionToAdd;
        Travel travelTime = null;   // because initialized inside if statement
        int attractionDurationMinutes;
        LocalDateTime attractionStartTime;
        LocalDateTime attractionEndTime;
        LocalDateTime transportationStartTime = null;    // because initialized inside if statement
        LocalDateTime transportationEndTime = null;      // because initialized inside if statement

        attractionList = scheduleRestrictions.getNeighbourAttractions(currentTime, placeTypeToAttraction,
                attractionToBooleanMap, lastAttraction, preferences.getStartDate(), attractionEvaluator);

        if(isFirstRouteAttraction){
            // TODO: check if returned null
            attractionToAdd = topSightAttractions.get(topAttractionIndex);
            isFirstRouteAttraction = false;
        }
        else{
            attractionToAdd = findBestAttraction(attractionList);
        }

        // configure start time of the day
        if(lastAttraction == null){
            addStartDayPadding(currentState);
            currentTime = currentTime.with(scheduleRestrictions.getSTART_TIME(currentTime.toLocalDate()));
        }

        if(attractionToAdd != null){
            // exclude first attraction transportation time
            if(lastAttraction != null){
                transportationStartTime = currentTime;
                travelTime = getTransportationTime(attractionToAdd, isOptimizationRun);
                if(travelTime.getDistanceMatrixElement().status.equals(DistanceMatrixElementStatus.OK)){
                    currentTime = currentTime.plusSeconds(travelTime.getDistanceMatrixElement().duration.inSeconds);
                }
                currentTime = currentTime.truncatedTo(ChronoUnit.MINUTES);
                transportationEndTime = currentTime;

                addBreakTime(travelTime);
            }

            attractionDurationMinutes = DefaultDurations.getESTOfAttraction(attractionToAdd);

            //case of a big amusement park when spending there most of the day
            if(scheduleRestrictions.isAmusementParkIncluded() && addAmusementParkExtraTime(attractionToAdd)){
                attractionDurationMinutes += 4 * 60;
            }

            attractionStartTime = currentTime;
            currentTime = currentTime.plusMinutes(attractionDurationMinutes);
            attractionEndTime = currentTime;

            // end vacation in case the user has flight and it's time to go to the airport
            if(scheduleRestrictions.isHasFlight() && currentTime.isAfter(scheduleRestrictions.endVacationTime)){
                return lastAttraction;
            }

            if(lastAttraction != null && moveToNextDay(transportationStartTime)){
                resetNextDay();
                return lastAttraction;
            }

            if(lastAttraction != null) {
//                System.out.println(calculateDistance(lastAttraction, attractionToAdd));
                if(travelTime.getDistanceMatrixElement().status.equals(DistanceMatrixElementStatus.OK)){
                    currentState.getItinerary().addTransportation(transportationStartTime, transportationEndTime,
                            ActivityNode.googleTravelToActivityType(travelTime.getMode()));
                }

                // add padding between transportation time and next attraction
                currentState.getItinerary().addFreeTime(transportationEndTime, attractionStartTime);
            }

            //case of adding night club, we want to end the day with a night club
            if(attractionToAdd.getClass().getSimpleName().equalsIgnoreCase("NightClub")){
                attractionEndTime = attractionEndTime.withHour(23).withMinute(59);
                currentTime = attractionEndTime;
            }

            currentState.getItinerary().addAttraction(attractionToAdd,
                    attractionStartTime,
                    attractionEndTime);

            // add attraction statistics
            if(isOptimizationRun){
                updateRouteMetaData(lastAttraction, attractionToAdd);
            }

            // update restrictions
            if(attractionToAdd.getClass().getSimpleName().equalsIgnoreCase("Spa")){
                scheduleRestrictions.setScheduledSpa(true);
            }
            if(attractionToAdd.getClass().getSimpleName().equalsIgnoreCase("Beach")){
                scheduleRestrictions.setScheduledBeach(true);
            }
            if(attractionToAdd.getClass().getSimpleName().equalsIgnoreCase("Casino")){
                scheduleRestrictions.setScheduledCasino(true);
            }

            // add padding after attraction
            currentState.getItinerary().addFreeTime(currentTime, currentTime);

            //after adding the attraction, it's now the last attraction we added.
            attractionToBooleanMap.put(attractionToAdd.getPlaceId(), true);
            lastAttraction = attractionToAdd;
        }

        return attractionToAdd;
    }

    private void updateRouteMetaData(Attraction lastAttraction, Attraction attractionToAdd) {
        double distance;

        numOfAttractionsInRoute = numOfAttractionsInRoute + 1;

        if(lastAttraction == null){
            distance = DistanceCalculator.calculateDistance(null, attractionToAdd.getGeometry().location);
        }
        else{
            distance = DistanceCalculator.calculateDistance(lastAttraction.getGeometry().location, attractionToAdd.getGeometry().location);
        }

        totalRouteDistance = totalRouteDistance + distance;
        sumAttractionEvaluation += attractionEvaluator.evaluateAttraction(attractionToAdd, distance, attractionTags, vibeTags);

        if(attractionTags.contains(attractionToAdd.getClass().getSimpleName().toUpperCase())
        && !attractionToAdd.getClass().getSimpleName().equalsIgnoreCase("TouristAttraction")){
            counterPreferredAttraction = counterPreferredAttraction + 1;
        }
    }

    private boolean moveToNextDay(LocalDateTime prevTime){
        if(!prevTime.toLocalDate().isEqual(currentTime.toLocalDate())){
            return true;
        }
        else if(scheduleRestrictions.isFamilyTrip && currentTime.toLocalTime().
                isAfter(scheduleRestrictions.getEND_TIME(currentTime.toLocalDate()))){
            currentTime = currentTime.plusDays(1);
            return true;
        }
        else if(currentTime.toLocalTime().isAfter(scheduleRestrictions.getEND_TIME(currentTime.toLocalDate()))){
            currentTime = currentTime.plusDays(1);
            return true;
        }

        return false;
    }

    private void resetNextDay(){
        LocalTime startTime = scheduleRestrictions.getSTART_TIME(currentTime.toLocalDate());

        currentTime = currentTime.with(startTime);
        scheduleRestrictions.resetRestrictions(currentTime.toLocalDate());
        lastAttraction = null;
    }

//    https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
    public double calculateDistance(Attraction source, Attraction destination){
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

        return EARTH_RADIUS * c;
    }

    private double deg2rad(double degree) {
        return degree * (Math.PI/180);
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

    public void getBestItinerary(Itinerary itinerary){
        List<State> stateList = new ArrayList<>();
        State state;
        int bestItineraryIndex;

        for(int i=0; i < TOP_SIGHTS_NUM; i++){
            state = new State(new Itinerary(new HashMap<>(), itinerary.getQuestionsData()));
            getItineraryWithHillClimbingAlgorithm(state, true, i);

            // update state metadata
            state.setHeuristicValue(sumAttractionEvaluation);
            state.setDistance(totalRouteDistance);
            state.setNumOfAttractions(numOfAttractionsInRoute);
            state.setPreferencesCnt(counterPreferredAttraction);
            state.setTopAttractionIndex(i);

            stateList.add(state);

            resetState();
//            System.out.println("-----------------------------------------Break----------------------------------------");
        }

//        stateList.forEach(System.out::println);
        bestItineraryIndex = attractionEvaluator.findBestState(stateList);

//        System.out.println(stateList.get(bestItineraryIndex));

        // build itinerary with API and best attraction as first one
        state = new State(itinerary);
        getItineraryWithHillClimbingAlgorithm(state, false, bestItineraryIndex);
    }

    public static void main(String[] args) {
        try{

            String country, city;
            int adultsCount = 1, childrenCount, budget;
            List <String> attractionTripTags =
                    new ArrayList<>(Arrays.asList("Museum", "Amusement park", "Aquarium", "Art Gallery",
                            "Bar", "Night Club", "Casino", "Park", "Shopping mall",
                            "Zoo", "Spa", "Beach", "Market"));
            List <String> vibeTripTags = new ArrayList<>();

            Scanner scanner = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Country: ");
            country = scanner.nextLine();
            System.out.println("City: ");
            city = scanner.nextLine();
            System.out.println("Children: ");
            childrenCount = Integer.parseInt(scanner.nextLine());
            System.out.println("Budget: ");
            budget = Integer.parseInt(scanner.nextLine());

            QuestionsData questionsData = new QuestionsData(country, city, adultsCount,
                    childrenCount, budget, LocalDateTime.now().plusDays(0), LocalDateTime.now().plusDays(3), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), null);
            DataEngine dataEngine = DataEngine.getInstance();
            Itinerary itinerary = new Itinerary(new HashMap<>(), questionsData);

            List<Attraction> attractionList = questionsData.getCity().getAttractionList();
            List<Attraction> newAttractionList = new ArrayList<>();
            newAttractionList.addAll(attractionList);

            Timestamp tsBefore = Timestamp.from(Instant.now());
            HillClimbing hillClimbing = new HillClimbing(questionsData, newAttractionList);
            hillClimbing.getBestItinerary(itinerary);

//            State state = new State(new Itinerary(new HashMap<>(), questionsData));
//            hillClimbing.getItineraryWithHillClimbingAlgorithm(state, true, 0);
//            System.out.println("Num: " + hillClimbing.numOfAttractionsInRoute + " Km: " + hillClimbing.totalRouteDistance +
//                    " sum: " + hillClimbing.sumAttractionEvaluation + " cnt: " + hillClimbing.counterPreferredAttraction);

            Timestamp tsAfter = Timestamp.from(Instant.now());
            System.out.println("Run Time: " + ((tsAfter.getTime() - tsBefore.getTime()) / 1000));

        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }

}
