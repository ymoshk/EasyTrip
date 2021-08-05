package algorithm;

import itinerary.Itinerary;

public class State {
    private Itinerary itinerary;
    private Double heuristicValue;

    public State(Itinerary itinerary, Double heuristicValue) {
        this.itinerary = itinerary;
        this.heuristicValue = heuristicValue;
    }

//    public State(State state) {
//        this.itinerary = new Itinerary(state.itinerary);
//        this.heuristicValue = state.heuristicValue;
//    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public void setHeuristicValue(Double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public Double getHeuristicValue() {
        return heuristicValue;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }
}
