package itinerary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ItineraryTest {

    @Test
    public void testDaysIteration() {
        LocalDate start = LocalDate.of(2021, 8, 10);
        LocalDate end = LocalDate.of(2021, 8, 15);

        int i = 0;

        for (LocalDate current = start; current.isBefore(end.plusDays(1)); current = current.plusDays(1)) {
            System.out.println(current);
            i++;
        }

        Assertions.assertEquals(3, i);
    }

}