package model.user;

import org.junit.jupiter.api.Test;

class GuestUserTest {

    @Test
    void testGuestNameGenerator() {
        for (int i = 0; i < 70; i++) {
            System.out.println(GuestUser.guestNameGenerator());
        }
    }
}