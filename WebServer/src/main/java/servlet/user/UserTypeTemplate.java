package servlet.user;

import model.user.GuestUser;
import model.user.User;

class UserTypeTemplate {
    Type type;
    UserTemplate user;

    public UserTypeTemplate(User user) {
        this.user = new UserTemplate(user);

        if (user.getClass().equals(GuestUser.class)) {
            this.type = Type.GUEST;
        } else if (user.isAdmin()) {
            this.type = Type.ADMIN;
        } else {
            this.type = Type.REGISTERED;
        }
    }

    private enum Type {
        GUEST, REGISTERED, ADMIN
    }

    private static class UserTemplate {
        private final String userName;
        private final String password;
        private final Long id;
        private final String name;
        private final boolean isAdmin;
        private final String sessionId;

        public UserTemplate(User user) {
            this.id = user.getId();
            this.userName = user.getUserName();
            this.password = user.getPassword();
            this.isAdmin = user.isAdmin();
            this.name = user.getName();
            this.sessionId = user.getSessionId();
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public String getSessionId() {
            return sessionId;
        }
    }
}