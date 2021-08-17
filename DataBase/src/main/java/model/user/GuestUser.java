package model.user;

import generator.GUID;
import generator.Hash;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "GuestUser")
public class GuestUser extends User {
    private static int counter = 0;

    public GuestUser() {
        setUserName(guestNameGenerator());
        setPassword(Hash.md5Hash(GUID.generate()));
        setAdmin(false);
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
