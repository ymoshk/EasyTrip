package user;

import connection.DataEngine;
import generator.Hash;
import model.user.GuestUser;
import model.user.RegisteredUser;
import model.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserContext {

    Map<String, User> sessionUserMap;

    public UserContext() {
        this.sessionUserMap = new HashMap<>();
    }

    public User createGuestUser(String sessionId) {
        GuestUser user = new GuestUser(sessionId);
        DataEngine.getInstance().addUser(user);
        this.sessionUserMap.put(sessionId, user);
        return user;
    }

    public User createUser(String sessionId, String userName, String password) {
        RegisteredUser user = new RegisteredUser(sessionId, userName, Hash.md5Hash(password));
        DataEngine.getInstance().addUser(user);
        this.sessionUserMap.put(sessionId, user);
        return user;
    }

    public User createAdminUser(String sessionId, String userName, String password) {
        RegisteredUser user = new RegisteredUser(sessionId, userName, Hash.md5Hash(password), true);
        DataEngine.getInstance().addUser(user);
        this.sessionUserMap.put(sessionId, user);
        return user;
    }

    public Optional<User> findGuestBySession(String sessionId) {
        return Optional.ofNullable(this.sessionUserMap.get(sessionId));
    }

    public Optional<User> findUser(String userName, String password) {
        //        if()
        return null;
    }
}
