package model.itinerary;

import model.Model;
import model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;


@Entity
@Table(name = "ItineraryModel")
public class ItineraryModel extends Model {
    @Column(unique = true, nullable = false)
    String itineraryId;
    @Type(type = "text")
    @Column(nullable = false)
    String jsonData;
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    User user;
    ItineraryStatus status;

    public ItineraryModel() {
        this.status = ItineraryStatus.EDIT;
    }

    public ItineraryModel(String itineraryId, String jsonData, User user) {
        this.itineraryId = itineraryId;
        this.jsonData = jsonData;
        this.status = ItineraryStatus.EDIT;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ItineraryStatus getStatus() {
        return status;
    }

    public void setStatus(ItineraryStatus status) {
        this.status = status;
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
