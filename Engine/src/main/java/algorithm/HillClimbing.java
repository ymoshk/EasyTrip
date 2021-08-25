package algorithm;

import com.google.maps.model.*;
import connection.DataEngine;
import constant.DefaultDurations;
import evaluators.AttractionEvaluator;
import itinerary.ActivityNode;
import itinerary.Itinerary;
import itinerary.QuestionsData;
import model.attraction.Attraction;
import model.location.City;
import model.travel.Travel;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


//the Algorithm is based on this source
//https://www.baeldung.com/java-hill-climbing-algorithm
//https://www.geeksforgeeks.org/introduction-hill-climbing-artificial-intelligence/

//https://bigseventravel.com/most-visited-cities-in-europe/

public class HillClimbing {
    //TODO: fetch 10 cities & check them + Tel Aviv + Jerusalem
    //TODO: add breaks depending on traveler type - depends on transportation time
    //TODO: budget --> filter?
    //TODO: attraction tags --> relative weights
    //TODO: vibe tags: early bird / night owl, luxury / street food, fast-paced / chill
    //TODO: save attraction name as constants
    //TODO: remove beaches from cities without beach
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

        public ScheduleRestrictions(QuestionsData preferences, List<String> attractionTags, List<String> vibeTags) {
            initTimeConstraints(preferences, vibeTags);
            this.city = preferences.getCity();
            this.scheduledLunch = false;
            this.scheduledDinner = false;
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
            this.amusementParkIncluded = attractionTags.contains("AMUSEMENTPARK");
            this.spaIncluded = attractionTags.contains("SPA");
            this.casinoIncluded = attractionTags.contains("CASINO");
            this.nightClubIncluded = attractionTags.contains("NIGHTCLUB");
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

        public LocalTime getSTART_TIME() {
            return START_TIME;
        }

        public LocalTime getEND_TIME() {
            return END_TIME;
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
                this.LUNCH_TIME = LocalTime.of(12,29, 0, 0);
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

        // TODO: limit attraction list to X attractions?
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

        public void resetRestrictions(){
            setScheduledLunch(false);
            setScheduledDinner(false);
            setScheduledBeach(false);
            setScheduledCasino(false);
            setScheduledSpa(false);
        }
    }

    private final QuestionsData preferences;
    AttractionEvaluator attractionEvaluator;
    private final HashMap<String, List<Attraction>> placeTypeToAttraction;
    private final HashMap<String, Boolean> attractionToBooleanMap;
    private final Random rand;
    DataEngine dataEngine;
    private LocalDateTime currentTime;
    private Attraction lastAttraction;
    private final ScheduleRestrictions scheduleRestrictions;
    private final int EARTH_RADIUS = 6371; // Radius of the earth in km
    private final int TOP_SIGHTS_NUM = 7;
    private final long WALKABLE_TIME = 15 * 60;   // seconds
    private long BREAK_TIME_FACTOR;
    private final boolean hasCar;
    private final List<String> attractionTags;
    private final List<String> vibeTags;


    public HillClimbing(QuestionsData preferences, List<Attraction> attractionList) {
        this.preferences = preferences;
        this.attractionTags = new ArrayList<>();
        this.vibeTags = new ArrayList<>();
        initTagsList(preferences);
        this.scheduleRestrictions = new ScheduleRestrictions(preferences, attractionTags, vibeTags);
        this.placeTypeToAttraction = attractionListToAttractionHashMap(attractionList);
        this.attractionEvaluator = new AttractionEvaluator(placeTypeToAttraction);
        this.attractionToBooleanMap = new HashMap<>();
        this.currentTime = getStartTimeByTravelerType();
        this.rand = new Random();
        this.dataEngine = DataEngine.getInstance();
        this.lastAttraction = null;
        removeAttractionDuplicationFromTouristAttraction();
        initTopSights(TOP_SIGHTS_NUM);
        removeDuplicateRestaurant();
        this.hasCar = false;
        //in seconds
        this.BREAK_TIME_FACTOR = calculateBreakTimeByTravelerType(vibeTags);
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

    private void initTagsList(QuestionsData preferences){
        preferences.getFavoriteAttractions().forEach(tripTag -> {
            attractionTags.add(tripTag.getTagName().replaceAll(" ", "").toUpperCase());
        });
        String touristAttractionTag = "TouristAttraction";
        attractionTags.add(touristAttractionTag.toUpperCase());

        preferences.getTripVibes().forEach(tripTag -> {
            vibeTags.add(tripTag.getTagName().replaceAll(" ", ""));
        });
//        vibeTags.add("Foody");
        vibeTags.add("StreetFood");

        attractionTags.forEach(System.out::println);
        vibeTags.forEach(System.out::println);
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

    void initTopSights(int numOfAttractions){
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

        placeTypeToAttraction.remove("TopSight");
    }

    void removeAttractionDuplicationFromTouristAttraction(){
        List<Attraction> attractionList = placeTypeToAttraction.get("TouristAttraction");

        attractionList = attractionList.stream().
                filter(attraction -> !attraction.getName().toUpperCase().contains("PARK")).
                collect(Collectors.toList());

        placeTypeToAttraction.put("TouristAttraction", attractionList);
    }

    private LocalDateTime getStartTimeByTravelerType(){
        return preferences.getStartDate().with(scheduleRestrictions.getSTART_TIME());
    }

    private boolean addAttractionToHashMap(String attractionType){
        // Don't add hotels to itinerary
        if(attractionType.equalsIgnoreCase("Hotel")){
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

        attractionList.stream().filter(attraction -> addAttractionToHashMap(attraction.getClass().getSimpleName())).
                forEach(attraction -> {
                    if(!res.containsKey(attraction.getClass().getSimpleName()))
                        res.put(attraction.getClass().getSimpleName(), new ArrayList<>());

                    res.get(attraction.getClass().getSimpleName()).add(attraction);
        });

        return res;
    }

    public Itinerary getItineraryWithHillClimbingAlgorithm(State initState) {
        State currentState = initState;

        // schedule attraction until vacation is over
        while(currentTime.isBefore(preferences.getEndDate())){
            addAttraction(currentState);
        }

        return currentState.getItinerary();
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

    Travel getTransportationTime(Attraction attractionDestination){
        // let assume the distance is walkable
        Travel travel = dataEngine.getTravel(
                lastAttraction.getGeometry().location,
                attractionDestination.getGeometry().location, TravelMode.WALKING);

        if(travel.getDistanceMatrixElement().duration.inSeconds > WALKABLE_TIME){
            if(hasCar){
                travel = dataEngine.getTravel(
                        lastAttraction.getGeometry().location,
                        attractionDestination.getGeometry().location, TravelMode.DRIVING);
            }
            else{
                travel = dataEngine.getTravel(
                        lastAttraction.getGeometry().location,
                        attractionDestination.getGeometry().location, TravelMode.TRANSIT);
            }
        }

        System.out.println(travel.getDistanceMatrixElement());
        System.out.println(travel.getMode());
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

    private Attraction addAttraction(State currentState) {
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
        attractionToAdd = findBestAttraction(attractionList);

        // configure start time of the day
        if(lastAttraction == null){
            currentState.getItinerary().addStartDayPadding(currentTime, currentTime.with(scheduleRestrictions.getSTART_TIME()));
            currentTime = currentTime.with(scheduleRestrictions.getSTART_TIME());
        }

        if(attractionToAdd != null){
            // exclude first attraction transportation time
            if(lastAttraction != null){
                transportationStartTime = currentTime;
                travelTime = getTransportationTime(attractionToAdd);
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
                attractionDurationMinutes += 5 * 60;
            }

            attractionStartTime = currentTime;
            currentTime = currentTime.plusMinutes(attractionDurationMinutes);
            attractionEndTime = currentTime;

            if(lastAttraction != null && moveToNextDay(transportationStartTime)){
                resetNextDay();
                // add padding to the end of the day
                currentState.getItinerary().addFreeTime(transportationStartTime, transportationStartTime);
                return lastAttraction;
            }

            if(lastAttraction != null) {
                System.out.println(calculateDistance(lastAttraction, attractionToAdd));
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

    private boolean moveToNextDay(LocalDateTime prevTime){
        if(!prevTime.toLocalDate().isEqual(currentTime.toLocalDate())){
            return true;
        }
        else if(scheduleRestrictions.isFamilyTrip && currentTime.toLocalTime().isAfter(scheduleRestrictions.getEND_TIME())){
            currentTime = currentTime.plusDays(1);
            return true;
        }

        return false;
    }

    private void resetNextDay(){
        LocalTime startTime = scheduleRestrictions.getSTART_TIME();

        currentTime = currentTime.with(startTime);
        scheduleRestrictions.resetRestrictions();
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
                    childrenCount, budget, LocalDateTime.now().plusDays(0), LocalDateTime.now().plusDays(4), new ArrayList<>(),
                    new ArrayList<>());
            List<Attraction> attractionList = questionsData.getCity().getAttractionList();

            HillClimbing hillClimbing = new HillClimbing(questionsData, attractionList);
            State state = new State(new Itinerary(new HashMap<>(), questionsData), 0.0);

            hillClimbing.getItineraryWithHillClimbingAlgorithm(state);

        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}
