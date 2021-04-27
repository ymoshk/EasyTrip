package model.user;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RegisteredUser")
public class RegisteredUser extends User {

    public RegisteredUser(String userName, String password, boolean isAdmin) {
        super(userName, password, isAdmin);
    }

    public RegisteredUser(String userName, String password) {
        super(userName, password);
    }
}
