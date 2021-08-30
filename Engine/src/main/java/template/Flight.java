package template;

import model.flightOffer.FlightOffer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Flight implements Serializable {
    private Segment Outbound;
    private Segment Return;
    private String price;
    private LocalDateTime departureFromOrigin;
    private LocalDateTime arrivalToDestination;
    private LocalDateTime departureFromDestination;
    private LocalDateTime arrivalToOrigin;

    public Flight(FlightOffer flightOffer) {
        this.Outbound = new Segment(flightOffer, 0);
        this.Return = new Segment(flightOffer, 1);
        this.departureFromOrigin = flightOffer.getDepartureDateTime(0);
        this.arrivalToDestination = flightOffer.getArrivalDateTime(0);
        this.departureFromDestination = flightOffer.getDepartureDateTime(1);
        this.arrivalToOrigin = flightOffer.getArrivalDateTime(1);
        this.price = Double.toString(flightOffer.getPrice()) + " " + flightOffer.getCurrency();
    }

    public LocalDateTime getDepartureFromOrigin() {
        return departureFromOrigin;
    }

    public LocalDateTime getArrivalToDestination() {
        return arrivalToDestination;
    }

    public LocalDateTime getDepartureFromDestination() {
        return departureFromDestination;
    }

    public LocalDateTime getArrivalToOrigin() {
        return arrivalToOrigin;
    }
}
