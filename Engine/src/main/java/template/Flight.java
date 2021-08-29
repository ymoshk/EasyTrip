package template;

import model.flightOffer.FlightOffer;

public class Flight {
    private Segment Outbound;
    private Segment Return;
    private String price;

    public Flight(FlightOffer flightOffer) {
        Outbound = new Segment(flightOffer, 0);
        Return = new Segment(flightOffer, 1);
        this.price = Double.toString(flightOffer.getPrice()) + " " + flightOffer.getCurrency();
    }
}
