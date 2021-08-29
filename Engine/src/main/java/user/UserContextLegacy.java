//package user;
//
//import connection.DataEngine;
//import generator.Hash;
//import itinerary.Itinerary;
//import model.itinerary.ItineraryModel;
//import model.user.GuestUser;
//import model.user.RegisteredUser;
//import model.user.User;
//
//import java.io.Closeable;
//import java.util.*;
//
//public class UserContext implements Closeable {
//    private final UserData userData;
//    private final Timer cleanupTimer;
//
//    public UserContext() {
//        this.userData = new UserData();
//        this.cleanupTimer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                userData.removeUnnecessary();
//            }
//        };
//
//        int hour = 1000 * 60 * 60;
//        this.cleanupTimer.schedule(task, hour, hour);
//    }
//
//    public GuestUser createGuestUser(String sessionId) {
//        GuestUser user = new GuestUser(sessionId);
//        DataEngine.getInstance().addUser(user);
//        this.userData.addUser(sessionId, user);
//        return user;
//    }
//
//    public boolean isUserNameAvailable(String userName) {
//        return DataEngine.getInstance().getUsers()
//                .stream()
//                .noneMatch(user -> user.getUserName().equals(userName));
//    }
//
//    public Optional<RegisteredUser> createUser(String sessionId, String userName, String password, String name) {
//        RegisteredUser user = null;
//        if(isUserNameAvailable(userName)) {
//            Optional<User> maybeGuest = this.userData.getUser(sessionId);
//            user = new RegisteredUser(sessionId, userName, Hash.md5Hash(password), name);
//            DataEngine.getInstance().addUser(user);
//            this.userData.addUser(sessionId, user);
//
//            // Moving existing itineraries to the new user.
//            if (maybeGuest.isPresent()) {
//                User currentGuest = maybeGuest.get();
//                List<ItineraryModel> guestsTrips = currentGuest.getItineraryList();
//
//                for(ItineraryModel trip : guestsTrips){
//                    trip.setUser(user);
//                    DataEngine.getInstance().updateItinerary(trip);
//                }
//
//                DataEngine.getInstance().deleteUser(currentGuest);
//            }
//        }
//
//        return Optional.ofNullable(user);
//    }
//
//    public RegisteredUser createAdminUser(String sessionId, String userName, String password, String name) {
//        RegisteredUser user = new RegisteredUser(sessionId, userName, Hash.md5Hash(password), name, true);
//        DataEngine.getInstance().addUser(user);
//        this.userData.addUser(sessionId, user);
//        return user;
//    }
//
//    public Optional<User> getLoggedInUser(String sessionId) {
//        return this.userData.getUser(sessionId);
//    }
//
//    public GuestUser getGuestBySession(String sessionId) {
//        Optional<User> user = this.userData.getUser(sessionId);
//
//        if (user.isPresent()) {
//            User theUser = user.get();
//
//            if (theUser.getClass().equals(GuestUser.class)) {
//                return (GuestUser) theUser;
//            }
//        }
//
//        return loadGuestUser(sessionId).orElse(createGuestUser(sessionId));
//    }
//
//
//    private Optional<RegisteredUser> loadUser(String userName, String password) {
//        return DataEngine.getInstance().getUser(userName, password);
//    }
//
//    private Optional<GuestUser> loadGuestUser(String sessionId) {
//        return DataEngine.getInstance().getGuestUser(sessionId);
//    }
//
//    public Optional<RegisteredUser> getRegisteredUser(String sessionId, String userName, String password) {
//        Optional<User> maybeUser = this.userData.getUser(sessionId);
//
//        if (maybeUser.isPresent()) {
//            User user = maybeUser.get();
//
//            if (user.getUserName().equals(userName) &&
//                    user.getClass().equals(RegisteredUser.class) &&
//                    user.getPassword().equals(Hash.md5Hash(password))) {
//                return Optional.of((RegisteredUser) user);
//            }
//        }
//
//        Optional<RegisteredUser> user = loadUser(userName, password);
//        user.ifPresent(registeredUser -> DataEngine.getInstance().updateUserSessionId(registeredUser, sessionId));
//
//        return user;
//    }
//
//    public boolean isItineraryOwner(String sessionId, String itineraryId) {
//        Optional<User> user = this.userData.getUser(sessionId);
//
//        return user.filter(value -> DataEngine.getInstance().getUserItineraries(value.getUserName())
//                .stream()
//                .anyMatch(itineraryModel -> itineraryModel.getItineraryId().equals(itineraryId)))
//                .isPresent();
//    }
//
//    public boolean login(String sessionId, String userName, String password) {
//        Optional<RegisteredUser> maybeUser = getRegisteredUser(sessionId, userName, password);
//
//        if (maybeUser.isPresent()) {
//            this.userData.addUser(sessionId, maybeUser.get());
//            DataEngine.getInstance().removeGuestUsersBySession(sessionId);
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public void logout(String sessionId) {
//        this.userData.addUser(sessionId, createGuestUser(sessionId));
//    }
//
//    @Override
//    public void close() {
//        this.cleanupTimer.cancel();
//    }
//}