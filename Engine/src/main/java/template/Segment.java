package template;


import model.flightOffer.FlightOffer;

import java.time.LocalDateTime;
import java.time.Period;

public class Segment {
    private String departureAirport;
    private String arrivalAirport;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String stop;
    private String stopAirport;

    public Segment(FlightOffer flightOffer, int direction){
        LocalDateTime departureDateTime = flightOffer.getDepartureDateTime(direction);
        LocalDateTime arrivalDateTime = flightOffer.getArrivalDateTime(direction);

        this.departureAirport = flightOffer.getOriginLocationCode();
        this.arrivalAirport = flightOffer.getDestinationLocationCode();
        this.departureTime = departureDateTime.toLocalTime().toString();
        this.arrivalTime = arrivalDateTime.toLocalTime().toString();

        // check if arrival is on the sequence day
        if(arrivalDateTime.toLocalDate().isAfter(departureDateTime.toLocalDate())){
            Period period = Period.between(departureDateTime.toLocalDate(), arrivalDateTime.toLocalDate());
            int diff = Math.abs(period.getDays());

            this.arrivalTime = this.arrivalTime + " (+" + diff + ")";
        }
        this.duration = flightOffer.getItineraryList().get(direction).getDurationTotal();
        this.duration = this.duration.replace("PT", "").replace("H", "H ").toLowerCase();
        //TODO: check if flight has stops
        this.stop = "1 stop";
        this.stopAirport = "TLV";
    }
}
