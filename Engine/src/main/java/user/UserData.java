package user;

import model.user.User;

import java.time.LocalDateTime;
import java.util.*;

class UserData {
    private final Map<String, Map.Entry<LocalDateTime, User>> sessionUserMap;
    private final int NUMBER_OF_DAYS_TO_SAVE = 10;

    public UserData() {
        this.sessionUserMap = new HashMap<>();
    }

    public void addUser(String sessionId, User user) {
        this.sessionUserMap.put(sessionId,
                new AbstractMap.SimpleImmutableEntry<>(LocalDateTime.now(), user));
    }

    public Optional<User> getUser(String sessionId) {
        User result = null;

        if (this.sessionUserMap.containsKey(sessionId)) {
            result = this.sessionUserMap.get(sessionId).getValue();
            this.sessionUserMap.put(sessionId,
                    new AbstractMap.SimpleImmutableEntry<>(LocalDateTime.now(), result));
        }

        return Optional.ofNullable(result);
    }

    public void removeUnnecessary() {
        List<String> toRemove = new ArrayList<>();

        this.sessionUserMap.forEach((key, value) -> {
            if (value.getKey().plusDays(NUMBER_OF_DAYS_TO_SAVE).isBefore(LocalDateTime.now())) {
                toRemove.add(key);
            }
        });

        toRemove.forEach(this.sessionUserMap::remove);
    }
}