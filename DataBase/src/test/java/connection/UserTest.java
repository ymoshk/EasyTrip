package connection;

import model.user.GuestUser;
import model.user.RegisteredUser;
import model.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void getUsers() {
        List<User> lst = DataEngine.getInstance().getUsers();
        System.out.println("found " + lst.size() + " users.");
        lst.forEach(user -> System.out.println(user.getUserName()));
    }

    @Test
    void getUser() {
    }

    @Test
    void isUserExist() {
        User user = DataEngine.getInstance().getUser("myUserName","password123");
        System.out.println(user.getUserName());
    }

    @Test
    void addUser() {
        RegisteredUser registeredUser = new RegisteredUser("myUserName", "password123");
        GuestUser guestUser = new GuestUser();

        DBContext.getInstance().insert(registeredUser);
        DBContext.getInstance().insert(guestUser);
    }
}