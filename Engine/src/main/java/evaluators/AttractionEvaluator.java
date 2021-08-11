package evaluators;

import com.google.maps.model.PlaceType;
import model.attraction.Attraction;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AttractionEvaluator {

    public class ReviewsData{
        int maxReviews;
        int medianReviews;
        double averageReviews;
        double averageGap;

        public ReviewsData(int maxReviews, int medianReviews, double averageReviews, double averageGap) {
            this.maxReviews = maxReviews;
            this.medianReviews = medianReviews;
            this.averageReviews = averageReviews;
            this.averageGap = averageGap;
        }

        public int getMaxReviews() {
            return maxReviews;
        }

        public void setMaxReviews(int maxReviews) {
            this.maxReviews = maxReviews;
        }

        public int getMedianReviews() {
            return medianReviews;
        }

        public void setMedianReviews(int medianReviews) {
            this.medianReviews = medianReviews;
        }

        public double getAverageReviews() {
            return averageReviews;
        }

        public void setAverageReviews(double averageReviews) {
            this.averageReviews = averageReviews;
        }

        public double getAverageGap() {
            return averageGap;
        }

        public void setAverageGap(double averageGap) {
            this.averageGap = averageGap;
        }
    }

    private HashMap<PlaceType, ReviewsData> reviewsDataMap;


    public AttractionEvaluator(HashMap<PlaceType, List<Attraction>> placeTypeToAttractionMap) {
        reviewsDataMap = new HashMap<>();
        placeTypeToAttractionMap.forEach((placeType, attractionList) -> {
            ReviewsData reviewsDataOfType = calculateReviewsData(attractionList);
            reviewsDataMap.put(placeType, reviewsDataOfType);
        });

    }

    private ReviewsData calculateReviewsData(List<Attraction> attractionList) {
        int numberOfAttractions = attractionList.size();
        List<Integer> attractionsReviews = attractionList.stream().mapToInt(Attraction::getUserRatingsTotal).
                sorted().boxed().collect(Collectors.toList());

        int attractionsReviewsNumberSum = attractionsReviews.stream().reduce(0, Integer::sum);
        int maxNumberOfReviews = attractionsReviews.stream().reduce(0, Integer::max);
        int medianOfReviews = attractionsReviews.get(numberOfAttractions/2);
        double averageReviews = (double) attractionsReviewsNumberSum/numberOfAttractions;
        double averageGap = 0;

//        attractionsReviews = attractionsReviews.stream().
//                filter(value -> value > medianOfReviews).
//                collect(Collectors.toList());

        int prevReview = 0;
        for(int curReview: attractionsReviews){
            if(curReview > medianOfReviews){
                averageGap = curReview - prevReview;
                prevReview = curReview;
            }
        }
        averageGap /= numberOfAttractions;

        System.out.println("--------------------------------------------------------");
        System.out.println(attractionList.get(0).getPlaceType());
        System.out.println("max: " + maxNumberOfReviews);
        System.out.println("average: " + averageReviews);
        System.out.println("median: " + medianOfReviews);
        System.out.println("averageGap: " + averageGap);
        int sumGaps = 0;
        AtomicInteger i = new AtomicInteger();
        attractionsReviews.forEach(review ->{

            System.out.println( i + ". "  + review);
            i.getAndIncrement();
        });
        System.out.println("--------------------------------------------------------");

        return new ReviewsData(maxNumberOfReviews, medianOfReviews, averageReviews, averageGap);
    }

    public static double evaluateAttractionAccordingToGoogleRating(Attraction attraction){
        double attractionRating = attraction.getRating();
        double ratingExponent = customLog(4.65,100);
        double differenceExponent = (attractionRating - 4.65) * 3.7;
        double ratingWithExponent = Math.pow(attractionRating, ratingExponent);
        double differenceWithExponent = Math.pow(100, differenceExponent);

        return ratingWithExponent - differenceWithExponent;
    }


    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }


    public static void main(String[] args) {

    }
}
