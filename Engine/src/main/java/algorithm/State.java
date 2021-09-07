package algorithm;

import itinerary.Itinerary;

public class State {
    private Itinerary itinerary;
    private Double heuristicValue;
    private double distance;
    private int numOfAttractions;
    private int topAttractionIndex;
    private int preferencesCnt;

    public State(Itinerary itinerary) {
        this.itinerary = itinerary;
        this.heuristicValue = 0.0;
        this.distance = 0;
        numOfAttractions = 0;
        topAttractionIndex = 0;
        preferencesCnt = 0;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setHeuristicValue(Double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setNumOfAttractions(int numOfAttractions) {
        this.numOfAttractions = numOfAttractions;
    }

    public void setTopAttractionIndex(int topAttractionIndex) {
        this.topAttractionIndex = topAttractionIndex;
    }

    public void setPreferencesCnt(int preferencesCnt) {
        this.preferencesCnt = preferencesCnt;
    }

    public Double getHeuristicValue() {
        return heuristicValue;
    }

    public double getDistance() {
        return distance;
    }

    public int getNumOfAttractions() {
        return numOfAttractions;
    }

    public int getTopAttractionIndex() {
        return topAttractionIndex;
    }

    public int getPreferencesCnt() {
        return preferencesCnt;
    }

    @Override
    public String toString() {
        return "State{" +
                "itinerary=" + itinerary +
                ", heuristicValue=" + heuristicValue +
                ", distance=" + distance +
                ", numOfAttractions=" + numOfAttractions +
                ", topAttractionIndex=" + topAttractionIndex +
                ", preferencesCnt=" + preferencesCnt +
                ", Avg score=" + heuristicValue/numOfAttractions +
                '}';
    }
}
