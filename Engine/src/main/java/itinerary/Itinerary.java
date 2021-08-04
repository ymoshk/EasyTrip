package itinerary;

import generator.GUID;
import model.attraction.Attraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Itinerary {
    private final String itineraryId;
    private final HashMap<String, List<Attraction>> attractions;
    private final List<ItineraryDay> itineraryDays;
    private final QuestionsData questionsData;

    public Itinerary(HashMap<String, List<Attraction>> attractions, QuestionsData questionsData) {
        this.itineraryId = GUID.generate();
        this.itineraryDays = new ArrayList<>();
        this.attractions = new HashMap<>(attractions);
        this.questionsData = questionsData;
    }

    public HashMap<String, List<Attraction>> getAttractions() {
        return attractions;
    }

    public List<ItineraryDay> getItineraryDays() {
        return itineraryDays;
    }

    public QuestionsData getQuestionsData() {
        return questionsData;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    //  the first day is dayNumber = 0
    public void setItineraryDay(int dayNumber, ItineraryDay newItineraryDay) {
        itineraryDays.set(dayNumber, newItineraryDay);
    }

    public void addItineraryDay(ItineraryDay newItineraryDay) {
        itineraryDays.add(newItineraryDay);
    }
}
