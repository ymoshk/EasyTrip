package model.flightOffer;

import com.amadeus.resources.FlightOfferSearch;
import log.LogsManager;
import model.Model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;


@Entity
public class FlightOffer extends Model {
    private String originLocationCode;
    private String destinationLocationCode;
    private LocalDateTime departureDate;
    private int numberOfPassengers;
    //Might be null, according to oneWay
    private LocalDateTime returnDate;
    private boolean oneWay;
    private LocalDateTime lastTicketingDate;
    private int numberOfBookableSeats;
    private double price;
    private Currency currency;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "flightOffer_id", insertable = true, updatable = true)
    private List <Itinerary> itineraryList;

    static final int TO_DESTINATION = 0;

    public FlightOffer(FlightOfferSearch flightOfferSearch, String originLocationCode, String destinationLocationCode,
                       LocalDate departureDate, LocalDate returnDate, int numberOfPassengers){
        this.originLocationCode = originLocationCode;
        this.destinationLocationCode = destinationLocationCode;
        // set time to 12:00, in order to save on the data base:
        this.departureDate = departureDate.atTime(12, 0);
        this.returnDate = returnDate.atTime(12, 0);
        this.numberOfPassengers = numberOfPassengers;
        // FlightOfferSearch method isOneWay returns always false --> each object in Itineraries array represent one direction
        this.oneWay = flightOfferSearch.getItineraries().length == 1;
        this.lastTicketingDate = LocalDate.parse(flightOfferSearch.getLastTicketingDate()).atStartOfDay();
        this.numberOfBookableSeats = flightOfferSearch.getNumberOfBookableSeats();
        this.price = flightOfferSearch.getPrice().getTotal();
        this.currency = Currency.getInstance(flightOfferSearch.getPrice().getCurrency());
        this.itineraryList = new ArrayList<>();
        Arrays.stream(flightOfferSearch.getItineraries()).forEach(itinerary -> {
            itineraryList.add( new Itinerary(itinerary));
        });
    }

    protected FlightOffer() {
    }

    @Override
    public String toString() {
        return "FlightOffer{" +
                "oneWay=" + oneWay +
                ", lastTicketingDate=" + lastTicketingDate +
                ", numberOfBookableSeats=" + numberOfBookableSeats +
                ", price=" + price +
                ", currency=" + currency +
                ", itineraryList=" + itineraryList +
                '}';
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public LocalDateTime getLastTicketingDate() {
        return lastTicketingDate;
    }

    public int getNumberOfBookableSeats() {
        return numberOfBookableSeats;
    }

    public double getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public List<Itinerary> getItineraryList() {
        return itineraryList;
    }

    /**
     *
     * @param direction 0 => TO_DESTINATION, 1 => FROM_DESTINATION.
     * @return departure date.
     * Notice: in case parameter direction equals 1 (FROM_DESTINATION) and flight is one-way then the current time is returned.
     */
    public LocalDateTime getDepartureDateTime(int direction){
        if(direction == TO_DESTINATION){
            return itineraryList.get(0).getDepartureDate();
        }
        else { // FROM_DESTINATION
            if(itineraryList.size() == 2){  // Round Trip
                return itineraryList.get(1).getDepartureDate();
            }
            else{
                LogsManager.log("In function 'getDepartureDateTime' direction=FROM_DESTINATION but flight is one-way");
                return LocalDateTime.now();
            }
        }
    }

    /**
     *
     * @param direction 0 => TO_DESTINATION, 1 => FROM_DESTINATION.
     * @return arrival date.
     * Notice: in case parameter direction equals 1 (FROM_DESTINATION) and flight is one-way then the current time is returned.
     */
    public LocalDateTime getArrivalDateTime(int direction){
        if(direction == TO_DESTINATION){
            return itineraryList.get(0).getArrivalDate();
        }
        else { // FROM_DESTINATION
            if(itineraryList.size() == 2){  // Round Trip
                return itineraryList.get(1).getArrivalDate();
            }
            else{
                LogsManager.log("In function 'getArrivalDateTime' direction=FROM_DESTINATION but flight is one-way");
                return LocalDateTime.now();
            }
        }
    }


    @Entity
    public static class Itinerary extends Model{

        private String durationTotal;
        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "itinerary_id", insertable = true, updatable = true)
        List<Segment> segmentList;

        protected Itinerary() {
        }

        public Itinerary(FlightOfferSearch.Itinerary itinerary) {
            this.durationTotal = itinerary.getDuration();
            this.segmentList = new ArrayList<>();
            Arrays.stream(itinerary.getSegments()).forEach(searchSegment -> {
                segmentList.add(new Segment(searchSegment));
            });
        }

        @Override
        public String toString() {
            return "Itinerary{" +
                    "durationTotal='" + durationTotal + '\'' +
                    ", segmentList=" + segmentList +
                    '}';
        }

        public String getDurationTotal() {
            return durationTotal;
        }

        public List<Segment> getSegmentList() {
            return segmentList;
        }

        /**
         * First item in segment list is the first flight in itinerary
         * @return departure time at origin
         */
        public LocalDateTime getDepartureDate(){
            return segmentList.get(0).getDeparture().getAt();
        }

        /**
         * Last item in segment list is the last flight in itinerary
         * @return arrival time at destination
         */
        public LocalDateTime getArrivalDate(){
            return segmentList.get(segmentList.size() - 1).getArrival().getAt();
        }
    }

    @Entity
    public static class Segment extends Model{
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "airportInfo_arrival_id", insertable = true, updatable = true)
        private AirportInfo arrival;
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "airportInfo_depature_id", insertable = true, updatable = true)
        private AirportInfo departure;
        private String carrierCode;
        private String flightNumber;
        private String duration;

        protected Segment() {
        }

        public Segment(FlightOfferSearch.SearchSegment searchSegment) {
            this.departure = new AirportInfo(searchSegment.getDeparture());
            this.arrival = new AirportInfo(searchSegment.getArrival());
            this.carrierCode = searchSegment.getCarrierCode();
            this.flightNumber = searchSegment.getNumber();
            this.duration = searchSegment.getDuration();
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "departure=" + departure +
                    ", arrival=" + arrival +
                    ", carrierCode='" + carrierCode + '\'' +
                    ", flightNumber='" + flightNumber + '\'' +
                    ", duration='" + duration + '\'' +
                    '}';
        }

        public AirportInfo getDeparture() {
            return departure;
        }

        public AirportInfo getArrival() {
            return arrival;
        }

        public String getCarrierCode() {
            return carrierCode;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public String getDuration() {
            return duration;
        }
    }

    @Entity
    public static class AirportInfo extends Model {
        private String iataCode;
        private String terminal;
        private LocalDateTime at;

        protected AirportInfo() {
        }

        public AirportInfo(FlightOfferSearch.AirportInfo airportInfo) {
            this.iataCode = airportInfo.getIataCode();
            this.terminal = airportInfo.getTerminal();
            this.at = LocalDateTime.parse(airportInfo.getAt());
        }

        @Override
        public String toString() {
            return "AirportInfo{" +
                    "iataCode='" + iataCode + '\'' +
                    ", terminal='" + terminal + '\'' +
                    ", at=" + at +
                    '}';
        }

        public String getIataCode() {
            return iataCode;
        }

        public String getTerminal() {
            return terminal;
        }

        public LocalDateTime getAt() {
            return at;
        }
    }
}
