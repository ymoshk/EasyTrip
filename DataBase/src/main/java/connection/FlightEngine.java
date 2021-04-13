package connection;

import amadeus.AmadeusApi;
import com.amadeus.resources.FlightOfferSearch;
import model.flightOffer.FlightOffer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightEngine {
    private DBContext context;
    private AmadeusApi amadeusApi = new AmadeusApi();
    private final static int MAX_NUMBER_OF_RESULTS = 10;


    public List <FlightOffer> findFlights(String originLocationCode, String destinationLocationCode, LocalDate departureDate,
                                   LocalDate returnDate, boolean oneWay, int numberOfPassengers){

        List <FlightOffer> flightOfferList = new ArrayList<>();//get flights from data base.

        if(flightOfferList.isEmpty()){
             FlightOfferSearch[] FlightsFromApi = amadeusApi.searchFlights(originLocationCode, destinationLocationCode, departureDate, returnDate,
                     numberOfPassengers, oneWay, MAX_NUMBER_OF_RESULTS);
             if(FlightsFromApi.length > 0){
                 Arrays.stream(FlightsFromApi).forEach(flightOffer ->{
                     flightOfferList.add(new FlightOffer(flightOffer, originLocationCode, destinationLocationCode,
                             departureDate, returnDate, numberOfPassengers));
                 });
                 context = DBContext.getInstance();
                 flightOfferList.forEach( flightOffer -> {
                     context.insert(flightOffer);
                 });

             }
        }
        return flightOfferList;
    }

    public static void main(String[] args) {
        new FlightEngine().findFlights("TLV", "NYC", LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(6), false, 1);
    }

}
