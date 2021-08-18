package model.user;

import model.Model;
import model.itinerary.ItineraryModel;

import javax.persistence.*;
import java.util.List;

@Entity()
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class User extends Model {
    @Column(nullable = false)
    String password;
    boolean isAdmin;
    String sessionId;
    @JoinColumn(name = "user_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<ItineraryModel> itineraryList;
    @Column(nullable = false)
    private String userName;

    public User() {
    }

    public User(String sessionId, String userName, String password, boolean isAdmin) {
        this.userName = userName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.sessionId = sessionId;
    }

    public User(String sessionId, String userName, String password) {
        this(sessionId, userName, password, false);
    }

    public List<ItineraryModel> getItineraryList() {
        return itineraryList;
    }

    public void setItineraryList(List<ItineraryModel> itineraryList) {
        this.itineraryList = itineraryList;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}

