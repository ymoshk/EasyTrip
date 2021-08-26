package model.user;

import generator.GUID;
import generator.Hash;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "GuestUser")
public class GuestUser extends User {
    private static int counter = 0;

    public GuestUser(String sessionId) {
        super(sessionId, guestNameGenerator(), Hash.md5Hash(GUID.generate()), "Guest", false);
    }

    public GuestUser() {

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
