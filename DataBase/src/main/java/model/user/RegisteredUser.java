package model.user;

import generator.Hash;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RegisteredUser")
public class RegisteredUser extends User {

    public RegisteredUser(String userName, String password, boolean isAdmin) {
        super(userName, Hash.md5Hash(password), isAdmin);
    }

    public RegisteredUser(String userName, String password) {
        super(userName, Hash.md5Hash(password));
    }

    public RegisteredUser() {

    }
}
