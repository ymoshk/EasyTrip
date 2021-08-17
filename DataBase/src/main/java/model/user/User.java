package model.user;

import model.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity()
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class User extends Model {
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    private boolean isAdmin;
    private String sessionId;

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

