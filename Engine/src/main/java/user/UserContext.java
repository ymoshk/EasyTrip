package user;

import connection.DataEngine;
import generator.Hash;
import model.itinerary.ItineraryModel;
import model.user.GuestUser;
import model.user.RegisteredUser;
import model.user.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserContext {
    private final Timer cleanupTimer;


    public UserContext() {

        this.cleanupTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                removeOldEntries();
            }
        };

        int hour = 1000 * 60 * 60;
        this.cleanupTimer.schedule(task, hour, hour);
    }

    private void removeOldEntries() {
        DataEngine dataEngine = DataEngine.getInstance();

        List<GuestUser> guestUsers = dataEngine.getGuestUsers();
        guestUsers.stream()
                .filter(guestUser -> guestUser.getUpdateTime().plusMonths(1).isBefore(LocalDateTime.now()))
                .forEach(dataEngine::removeUser);
    }


    /**
     * functions related to guest users.
     */
    private GuestUser createGuestUser(String sessionId) {
        GuestUser user = new GuestUser(sessionId);
        DataEngine.getInstance().addUser(user);
        return user;
    }

    public User getUserBySessionId(String sessionId) {
        DataEngine dataEngine = DataEngine.getInstance();
        User user;

        List<User> users = dataEngine.getUsers()
                .stream()
                .filter(entry -> entry.getSessionId().equals(sessionId))
                .collect(Collectors.toList());

        if (users.size() == 1) {
            user = users.get(0);
            dataEngine.refreshModelUpdateTime(user);
            return user;
        } else {
            user = createGuestUser(sessionId);
        }

        return user;
    }

    /**
     * Users must be in the DB
     */
    private void grabAndRemoveGuest(RegisteredUser registeredUser, GuestUser guest) {
        DataEngine dataEngine = DataEngine.getInstance();
        List<ItineraryModel> itineraryModelList = guest.getItineraryList();
        List<ItineraryModel> cloneList = new ArrayList<>();

        if (itineraryModelList != null) {
            for (ItineraryModel itinerary : itineraryModelList) {
                ItineraryModel clone = new
                        ItineraryModel(itinerary.getItineraryId(), itinerary.getJsonData(), registeredUser);

                clone.setStatus(itinerary.getStatus());
                cloneList.add(clone);
            }
        }
        dataEngine.removeUser(guest);
        cloneList.forEach(dataEngine::saveItinerary);
    }

    public Optional<RegisteredUser> createRegisteredUser(String sessionId, String userName, String password, String name) {
        Optional<RegisteredUser> result;
        DataEngine dataEngine = DataEngine.getInstance();
        GuestUser currentUser;

        RegisteredUser user = new RegisteredUser(sessionId, userName, Hash.md5Hash(password), name); // Password is being hashed
        if (dataEngine.addUser(user)) {

            Optional<GuestUser> maybeUser = dataEngine.getGuestUser(sessionId);

            if (maybeUser.isPresent()) {
                currentUser = maybeUser.get();
                grabAndRemoveGuest(user, currentUser);
            }

            result = Optional.of(user);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    public Optional<RegisteredUser> login(String sessionId, String userName, String password) {
        Optional<RegisteredUser> result;
        DataEngine dataEngine = DataEngine.getInstance();

        Optional<RegisteredUser> maybeUser = dataEngine.getUser(userName, password);

        if (maybeUser.isPresent()) {
            RegisteredUser user = maybeUser.get();
            User currentGuest = getUserBySessionId(sessionId);

            if (currentGuest.getClass().equals(GuestUser.class)) {
                grabAndRemoveGuest(user, (GuestUser) currentGuest);
            }

            user.setSessionId(sessionId);
            user.setUpdateTime(LocalDateTime.now());
            dataEngine.updateUser(user);
            result = Optional.of(user);


        } else {
            result = Optional.empty();
        }
        return result;
    }

    public GuestUser logout(String sessionId) {
        User user = getUserBySessionId(sessionId);
        DataEngine dataEngine = DataEngine.getInstance();

        if (user.getClass().equals(RegisteredUser.class)) {
            user.setSessionId("");
            dataEngine.updateUser(user);
        }

        return createGuestUser(sessionId);
    }

    public boolean isItineraryOwner(String sessionId, String itineraryId) {
        // TODO - process with a direct query to get a faster method
        User user = getUserBySessionId(sessionId);

        return user.getItineraryList()
                .stream()
                .anyMatch(itineraryModel -> itineraryModel.getItineraryId().equals(itineraryId));
    }
}
