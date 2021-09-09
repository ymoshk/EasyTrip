package evaluators;

import algorithm.State;
import distanceCalculator.DistanceCalculator;
import model.attraction.Attraction;
import model.location.City;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AttractionEvaluator {

    public class ReviewsData{
        int maxReviews;
        int medianReviews;
        double averageReviews;
        double step;

        public ReviewsData(int maxReviews, int medianReviews, double averageReviews, double step) {
            this.maxReviews = maxReviews;
            this.medianReviews = medianReviews;
            this.averageReviews = averageReviews;
            this.step = step;
        }

        public int getMedianReviews() {
            return medianReviews;
        }

        public double getAverageReviews() {
            return averageReviews;
        }
    }

    private HashMap<String, ReviewsData> reviewsDataMap;
    private HashMap<String, Integer> attractionToIndexMap;
    private List<String> topAttractionIdList;
    private Attraction topAmusementPark;
    private final double TOP_ATTRACTION_SCORE = 100;


    public AttractionEvaluator(HashMap<String, List<Attraction>> placeTypeToAttractionMap) {
        reviewsDataMap = new HashMap<>();
        attractionToIndexMap = new HashMap<>();
        placeTypeToAttractionMap.forEach((placeType, attractionList) -> {
            // sort attractions by number of reviews
            List <Attraction> sortedAttractionList = attractionList.stream().sorted(Comparator.
                    comparingInt(Attraction::getUserRatingsTotal)).collect(Collectors.toList());

            if(placeType.equalsIgnoreCase("AmusementPark")){
                this.topAmusementPark = sortedAttractionList.get(sortedAttractionList.size() - 1);
            }

            ReviewsData reviewsDataOfType = calculateReviewsData(sortedAttractionList);
            reviewsDataMap.put(placeType, reviewsDataOfType);

            // create indexes map for restaurants
            initialAttractionIndexMap(sortedAttractionList);
        });
    }

    public boolean isTopAttraction(Attraction attraction){
        return topAttractionIdList.contains(attraction.getPlaceId());
    }

    public void setTopAttractionIdList(List<Attraction> topAttractionIdList) {
        this.topAttractionIdList = topAttractionIdList.stream().map(Attraction::getPlaceId).collect(Collectors.toList());;
    }

    private String generateAttractionKey(Attraction attraction){
        return attraction.getPlaceId() + attraction.getClass().getSimpleName();
    }

    private void initialAttractionIndexMap(List<Attraction> attractionListSorted) {
        AtomicInteger index = new AtomicInteger(1);

        attractionListSorted.forEach(attraction -> {
            //we wish to make every attraction unique per type
            attractionToIndexMap.put(generateAttractionKey(attraction), index.intValue());
            index.getAndIncrement();
        });
    }

    private ReviewsData calculateReviewsData(List<Attraction> attractionList) {
        int numberOfAttractions = attractionList.size();

        List<Integer> attractionsReviewsInt = attractionList.stream().mapToInt(Attraction::getUserRatingsTotal).
                boxed().collect(Collectors.toList());

        int attractionsReviewsNumberSum = attractionsReviewsInt.stream().reduce(0, Integer::sum);
        int maxNumberOfReviews = attractionsReviewsInt.stream().reduce(0, Integer::max);
        int medianOfReviews = attractionsReviewsInt.get(numberOfAttractions/2);
        double averageReviews = (double) attractionsReviewsNumberSum/numberOfAttractions;
        double step = 100.0/numberOfAttractions;

        return new ReviewsData(maxNumberOfReviews, medianOfReviews, averageReviews, step);
    }

    public static double evaluateByRating(Attraction attraction){
        double attractionRating = attraction.getRating();
        double ratingExponent = customLog(4.65,100);
        double differenceExponent = (attractionRating - 4.65) * 3.7;
        double ratingWithExponent = Math.pow(attractionRating, ratingExponent);
        double differenceWithExponent = Math.pow(100, differenceExponent);

        double result = ratingWithExponent - differenceWithExponent;

        if(result > 100.0){
            result = 100.0;
        }

        return result;
    }

    public double evaluateByReviewsNumber(Attraction attraction) {
        // in case attraction isn't mapped, set 'placeTypeStep' & 'attractionIndex' to 0
        ReviewsData reviewsData = reviewsDataMap.get(attraction.getClass().getSimpleName());
        double placeTypeStep = reviewsData != null ? reviewsData.step : 0;
        int attractionIndex = attractionToIndexMap.getOrDefault(generateAttractionKey(attraction), 0);

            // Give boost to attractions above average or median
            if(reviewsData != null && attraction.getUserRatingsTotal() >= reviewsData.getAverageReviews()){
                return 0.85 * (placeTypeStep * attractionIndex) + 0.15 * 100;
            }
            else if(reviewsData != null && attraction.getUserRatingsTotal() >= reviewsData.getMedianReviews()){
                return 0.9 * (placeTypeStep * attractionIndex) + 0.1 * 100;
            }

            return placeTypeStep * attractionIndex;
    }

    public double evaluateByUserPreferences(Attraction attraction, List<String> attractionTags){
        // TouristAttraction is added on HillClimbing constructor
       int numberOfTags = attractionTags.size();
        if( numberOfTags == 1 || attractionTags.isEmpty()){
            return 100.0;
        }
        // user chose many tags - normalize scores
       if(numberOfTags > 5){
           numberOfTags = 5;
       }

       if(attraction.getClass().getSimpleName().equalsIgnoreCase("Nightclub")){
           if(attractionTags.contains(attraction.getClass().getSimpleName().toUpperCase())){
               return 100.0;
           }
       }

        if(attraction.getClass().getSimpleName().equalsIgnoreCase("Aquarium")){
            if(attractionTags.contains(attraction.getClass().getSimpleName().toUpperCase())){
                return 100.0;
            }
        }

        if (attraction.getClass().getSimpleName().equalsIgnoreCase("Museum")){
            if (attractionTags.contains("ART")){
                return 100.0 / numberOfTags;
            }
        }

        if(attractionTags.contains(attraction.getClass().getSimpleName().toUpperCase())){
            return 100.0 / numberOfTags;
        }
        else{
            return 0;
        }
    }

    public double evaluateRestaurantByPreferences(Attraction attraction, List<String> vibeTags){
        // no information about price level
        if(attraction.getPriceLevel() == null){
            return 50;
        }

        if(vibeTags.contains("Foody")){
            switch(attraction.getPriceLevel()){
                case INEXPENSIVE: return 20;
                case MODERATE: return 60;
                case EXPENSIVE: return 95;
                case VERY_EXPENSIVE: return 100;
                default: return 0;
            }
        }
        else if(vibeTags.contains("StreetFood")){
            switch(attraction.getPriceLevel()){
                case INEXPENSIVE: return 100;
                case MODERATE: return 55;
                case EXPENSIVE: return 25;
                case VERY_EXPENSIVE: return 0;
                default: return 0;
            }
        }
        else{
            switch(attraction.getPriceLevel()){
                case INEXPENSIVE:
                case MODERATE:
                    return 100;
                case EXPENSIVE:
                    return 75;
                case VERY_EXPENSIVE:
                    return 50;
                default: return 0;
            }
        }
    }

    public double evaluateAttraction(Attraction attraction, double distance, List<String> attractionTags, List<String> vibeTags){
        if(attraction.getClass().getSimpleName().equalsIgnoreCase("Restaurant")){
            return evaluateRestaurant(attraction, distance, vibeTags);
        }
        else{
            return evaluateAttraction(attraction, distance, attractionTags);
        }
    }

    public double evaluateAttraction(Attraction attraction, double distance, List<String> attractionTags){
        double ratingScore;
        double reviewsScore;
        double distanceScore = evaluateByDistance(distance);
        double preferencesScore = evaluateByUserPreferences(attraction, attractionTags);

        if(isTopAttraction(attraction)){
            ratingScore = TOP_ATTRACTION_SCORE;
            reviewsScore = TOP_ATTRACTION_SCORE;
        }
        else{
            ratingScore = evaluateByRating(attraction);
            reviewsScore = evaluateByReviewsNumber(attraction);
        }

        return 0.3 * ratingScore + 0.3 * reviewsScore + 0.2 * distanceScore + 0.2 * preferencesScore;
    }

    public double evaluateRestaurant(Attraction attraction, double distance, List<String> vibeTags){
        double ratingScore = evaluateByRating(attraction);
        double reviewsScore = evaluateByReviewsNumber(attraction);
        double distanceScore = evaluateByDistance(distance);
        double preferencesScore = evaluateRestaurantByPreferences(attraction, vibeTags);

        return 0.25 * ratingScore + 0.3 * reviewsScore + 0.25 * distanceScore + 0.2 * preferencesScore;
    }

    public double evaluateAttraction(Attraction attraction){
        if(topAttractionIdList != null &&
                isTopAttraction(attraction)){
            return TOP_ATTRACTION_SCORE;
        }

        double ratingScore = evaluateByRating(attraction);
        double reviewsScore = evaluateByReviewsNumber(attraction);

        return (0.5 * ratingScore) + (0.5 * reviewsScore);
    }

    public double evaluateAttraction(Attraction attraction, double distance) {
        double ratingScore;
        double reviewsScore;
        double distanceScore = evaluateByDistance(distance);

        if(isTopAttraction(attraction)){
            ratingScore = TOP_ATTRACTION_SCORE;
            reviewsScore = TOP_ATTRACTION_SCORE;
        }
        else{
            ratingScore = evaluateByRating(attraction);
            reviewsScore = evaluateByReviewsNumber(attraction);
        }

        return 0.35 * ratingScore + 0.35 * reviewsScore + 0.3 * distanceScore;
    }

    private double evaluateByDistance(double distance) {
        return (1 - distance/6) * 100;
    }

    public int getIndex(Attraction attractionToAdd) {
        return attractionToIndexMap.get(generateAttractionKey(attractionToAdd));
    }

    public void setRecommendedAttractions(List<template.Attraction> attractions, boolean isPreferred){
        int listSize = attractions.size();
        int percentageOfRecommended = isPreferred ? 5 : 10;
        int recommendedIndex = listSize/percentageOfRecommended  == 0 ? 1: listSize/percentageOfRecommended;
        for(int i = 0; i < recommendedIndex; i++){
            attractions.get(i).isRecommended = true;
        }

    }

    public boolean isRecommended(Attraction attraction, List<String> vibeTags) {
        if(!attractionToIndexMap.containsKey(attraction.getPlaceId() + attraction.getClass().getSimpleName())){
            return false;
        }
        double score = getRecommendationScore(attraction, vibeTags);

        return score > 90;
    }

    public double getRecommendationScore(Attraction attraction, List<String> vibeTags){
        if(!attractionToIndexMap.containsKey(attraction.getPlaceId() + attraction.getClass().getSimpleName())){
            return 0;
        }
        double ratingScore = evaluateByRating(attraction);
        double reviewsScore = evaluateByReviewsNumber(attraction);
        double preferencesScore = evaluateRestaurantByPreferences(attraction, vibeTags);


        return  0.4 * reviewsScore + 0.3 * ratingScore + 0.3 * preferencesScore;

    }

    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }

    public Attraction getTopAmusementPark() {
        return topAmusementPark;
    }

    private double calculateStateScore(State state, double avgDistance){
        int distanceScore = state.getDistance() > avgDistance ? -1 : 1;

        return state.getHeuristicValue()/state.getNumOfAttractions() + state.getPreferencesCnt() + distanceScore;
    }

    public int findBestState(List<State> stateList){
        OptionalDouble avgDistance = stateList.stream().mapToDouble(State::getDistance).average();
        int bestStateIndex = 0;
        // TODO: check exception
        double bestStateScore = calculateStateScore(stateList.get(0), avgDistance.getAsDouble());
        double currentScore = 0;

        for(int i = 1; i < stateList.size(); i++){
            currentScore = calculateStateScore(stateList.get(i), avgDistance.getAsDouble());

            if(currentScore > bestStateScore){
                bestStateIndex = i;
                bestStateScore = currentScore;
            }
        }

        return bestStateIndex;
    }

    public static void main(String[] args) {

    }
}
