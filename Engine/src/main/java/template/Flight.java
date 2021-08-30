package template;

import model.flightOffer.FlightOffer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Flight implements Serializable {
    private Segment Outbound;
    private Segment Return;
    private String price;

    public Flight(FlightOffer flightOffer) {
        this.Outbound = new Segment(flightOffer, 0);
        this.Return = new Segment(flightOffer, 1);
        this.price = Double.toString(flightOffer.getPrice()) + " " + flightOffer.getCurrency();
    }
}
