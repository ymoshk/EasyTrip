package model.user;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "GuestUser")
public class GuestUser extends User {
    private static int counter = 0;

    public GuestUser() {
        setUserName(guestNameGenerator());
        setPassword("");
        setAdmin(false);
        setActive(false);
    }

    public static String guestNameGenerator() {
        long unixTime = System.currentTimeMillis();
        return "guest" + unixTime + getCounter();
    }

    private static String getCounter() {
        int current = counter++;
        counter = counter % 50;
        return Integer.toString(current);
    }
}
