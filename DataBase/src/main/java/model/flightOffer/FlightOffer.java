package model.flightOffer;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import model.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class FlightOffer extends Model {
    private boolean oneWay;
    private LocalDate lastTicketingDate;
    private int numberOfBookableSeats;
    private double price;
    private Currency currency;
    List <Itinerary> itineraryList;

    protected FlightOffer(FlightOfferSearch flightOfferSearch){
        this.oneWay = flightOfferSearch.isOneWay();
        this.lastTicketingDate = LocalDate.parse(flightOfferSearch.getLastTicketingDate());
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

    public LocalDate getLastTicketingDate() {
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

    public static class Itinerary {
        private String durationTotal;
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
    }

    public static class Segment {
        private AirportInfo departure;
        private AirportInfo arrival;
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

    public static class AirportInfo {
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

    public static void main(String[] args) {
        Amadeus amadeus = Amadeus
                .builder(System.getenv().get("AMADEUS_CLIENT_ID"), System.getenv().get("AMADEUS_CLIENT_SECRET"))
                .build();

        List<FlightOffer> list = new ArrayList<>();

        try {
            FlightOfferSearch[] flightOffersSearches = amadeus.shopping.flightOffersSearch.get(
                    Params.with("originLocationCode", "TLV")
                            .and("destinationLocationCode", "JFK")
                            .and("departureDate", "2021-04-11")
                            .and("returnDate", "2021-04-17")
                            .and("adults", 2)
                            .and("max", 10));

            Arrays.stream(flightOffersSearches).forEach(flightOfferSearch -> {
                list.add(new FlightOffer(flightOfferSearch));
            });
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        list.forEach(System.out::println);
    }
}
