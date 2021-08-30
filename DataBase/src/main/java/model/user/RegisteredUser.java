package model.user;

import javax.persistence.Entity;

@Entity
//@Table(name = "RegisteredUser")
public class RegisteredUser extends User {

    public RegisteredUser(String sessionId, String userName, String password, String name, boolean isAdmin) {
        super(sessionId, userName, password, name, isAdmin);
    }

    public RegisteredUser(String sessionId, String userName, String password, String name) {
        super(sessionId, userName, password, name);
    }

    public RegisteredUser() {

    }
}
