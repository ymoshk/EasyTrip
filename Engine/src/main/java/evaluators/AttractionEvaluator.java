package evaluators;

import com.google.maps.model.PlaceType;
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
        List<String> topAttractions;

        public ReviewsData(int maxReviews, int medianReviews, double averageReviews, double step, List<String> topAttractions) {
            this.maxReviews = maxReviews;
            this.medianReviews = medianReviews;
            this.averageReviews = averageReviews;
            this.step = step;
            this.topAttractions = topAttractions;
        }

        public boolean isTopAttraction(String attractionPlaceId){
            return topAttractions.contains(attractionPlaceId);
        }

        public int getMedianReviews() {
            return medianReviews;
        }

        public double getAverageReviews() {
            return averageReviews;
        }
    }

    private HashMap<PlaceType, ReviewsData> reviewsDataMap;
    private HashMap<String, Integer> attractionToIndexMap;
    private final double TOP_ATTRACTION_SCORE = 100;
    private final double AVG_ATTRACTION_SCORE = 80;
    private final double MEDIAN_ATTRACTION_SCORE = 50;
    private final double BOTTOM_ATTRACTION_SCORE = 25;


    public AttractionEvaluator(HashMap<PlaceType, List<Attraction>> placeTypeToAttractionMap) {
        reviewsDataMap = new HashMap<>();
        attractionToIndexMap = new HashMap<>();
        placeTypeToAttractionMap.forEach((placeType, attractionList) -> {
            // sort attractions by number of reviews
            List <Attraction> sortedAttractionList = attractionList.stream().sorted(Comparator.
                    comparingInt(Attraction::getUserRatingsTotal)).collect(Collectors.toList());

            ReviewsData reviewsDataOfType = calculateReviewsData(sortedAttractionList);
            reviewsDataMap.put(placeType, reviewsDataOfType);

            // create indexes map for restaurants
            if(placeType.equals(PlaceType.RESTAURANT)){
                initialAttractionIndexMap(sortedAttractionList);
            }
        });
    }

    private void initialAttractionIndexMap(List<Attraction> attractionListSorted) {
        AtomicInteger index = new AtomicInteger(1);

        attractionListSorted.forEach(attraction -> {
            attractionToIndexMap.put(attraction.getPlaceId(), index.intValue());
            index.getAndIncrement();
        });
    }

    private ReviewsData calculateReviewsData(List<Attraction> attractionList) {
        int numberOfAttractions = attractionList.size();
        int topAttractionPercent = (int) Math.ceil(0.1 * (double)numberOfAttractions);

        List<Integer> attractionsReviewsInt = attractionList.stream().mapToInt(Attraction::getUserRatingsTotal).
                boxed().collect(Collectors.toList());

        List<String> topAttractions = attractionList.
                subList(numberOfAttractions - topAttractionPercent, numberOfAttractions).
                stream().
                map(Attraction::getPlaceId).
                collect(Collectors.toList());

        int attractionsReviewsNumberSum = attractionsReviewsInt.stream().reduce(0, Integer::sum);
        int maxNumberOfReviews = attractionsReviewsInt.stream().reduce(0, Integer::max);
        int medianOfReviews = attractionsReviewsInt.get(numberOfAttractions/2);
        double averageReviews = (double) attractionsReviewsNumberSum/numberOfAttractions;
        double step = 100.0/numberOfAttractions;


//        System.out.println("--------------------------------------------------------");
//        System.out.println(attractionList.get(0).getPlaceType());
//        System.out.println("max: " + maxNumberOfReviews);
//        System.out.println("average: " + averageReviews);
//        System.out.println("median: " + medianOfReviews);
//        System.out.println("averageGap: " + step);
//
//        AtomicInteger i = new AtomicInteger();
//        attractionsReviewsInt.forEach(review ->{
//
//            System.out.println( i + ". "  + review);
//            i.getAndIncrement();
//        });
//        System.out.println("--------------------------------------------------------");

        return new ReviewsData(maxNumberOfReviews, medianOfReviews, averageReviews, step, topAttractions);
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
        // restaurant has another type of evaluation
        if(attraction.getPlaceType().equals(PlaceType.RESTAURANT)){
            double placeTypeStep = reviewsDataMap.get(attraction.getPlaceType()).step;
            int attractionIndex = attractionToIndexMap.get(attraction.getPlaceId());

            return placeTypeStep * attractionIndex;
        }
        else{
            ReviewsData reviewsData = reviewsDataMap.get(attraction.getPlaceType());

            if(reviewsData.isTopAttraction(attraction.getPlaceId())){
                return TOP_ATTRACTION_SCORE;
            }
            else if(attraction.getUserRatingsTotal() >= reviewsData.getAverageReviews()){
                return AVG_ATTRACTION_SCORE;
            }
            else if(attraction.getUserRatingsTotal() >= reviewsData.getMedianReviews()){
                return MEDIAN_ATTRACTION_SCORE;
            }
            else{
                return BOTTOM_ATTRACTION_SCORE;
            }
        }
    }

    public double evaluateAttraction(Attraction attraction){
        double ratingScore = evaluateByRating(attraction);
//        double reviewsScore = evaluateByReviewsNumber(attraction);

//        return (0.5 * ratingScore) + (0.5 * reviewsScore);
        return ratingScore;
    }

    public int getIndex(Attraction attractionToAdd) {
        return attractionToIndexMap.get(attractionToAdd.getPlaceId());
    }


    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }


    public static void main(String[] args) {

    }
}
