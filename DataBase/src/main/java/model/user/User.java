package model.user;

import model.Model;
import model.itinerary.ItineraryModel;

import javax.persistence.*;
import java.util.List;

@Entity()
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class User extends Model {
    @Column(length = 1024, nullable = false)
    private String password;
    private boolean isAdmin;
    private String sessionId;
    @JoinColumn(name = "user_id")
    @OneToMany(cascade = CascadeType.ALL)
    private List<ItineraryModel> itineraryList;
    @Column(unique = true, nullable = false)
    private String userName;
    private String name;

    public User() {
    }

    public User(String sessionId, String userName, String password, String name, boolean isAdmin) {
        this.userName = userName;
        this.password = password;
        this.isAdmin = isAdmin;
        this.sessionId = sessionId;
        this.name = name;
    }

    public User(String sessionId, String userName, String password, String name) {
        this(sessionId, userName, password, name, false);
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

