package evaluators;

import model.attraction.Attraction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
            double placeTypeStep = reviewsDataMap.get(attraction.getClass().getSimpleName()).step;
            int attractionIndex = attractionToIndexMap.get(generateAttractionKey(attraction));
            ReviewsData reviewsData = reviewsDataMap.get(attraction.getClass().getSimpleName());

            // Give boost to attractions above average or median
            if(attraction.getUserRatingsTotal() >= reviewsData.getAverageReviews()){
                return 0.85 * (placeTypeStep * attractionIndex) + 0.15 * 100;
            }
            else if(attraction.getUserRatingsTotal() >= reviewsData.getMedianReviews()){
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
       if( numberOfTags > 5){
           numberOfTags = 5;
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


    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }

    public Attraction getTopAmusementPark() {
        return topAmusementPark;
    }

    public static void main(String[] args) {

    }
}
