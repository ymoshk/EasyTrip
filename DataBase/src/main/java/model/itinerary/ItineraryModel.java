package model.itinerary;

import model.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ItineraryModel extends Model {
    @Column(unique = true, nullable = false)
    private String itineraryId;
    @Column(length = 1024 * 2, unique = true, nullable = false)
    private String jsonData;

    public ItineraryModel() {
    }

    public ItineraryModel(String itineraryId, String jsonData) {
        this.itineraryId = itineraryId;
        this.jsonData = jsonData;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(String itineraryId) {
        this.itineraryId = itineraryId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}
