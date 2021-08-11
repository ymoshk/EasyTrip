package evaluators;

import model.attraction.Attraction;

public class AttractionEvaluator {

    public static double evaluateAttractionAccordingToGoogleRating(Attraction attraction){
        double res = customLog(attraction.getRating(),4.62);
        return res;
    }


    private static double customLog(double base, double logNumber) {
        return Math.log(logNumber) / Math.log(base);
    }
}
