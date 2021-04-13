package amadeus;

import com.amadeus.Amadeus;
import com.amadeus.Params;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Activity;
import com.amadeus.resources.Airline;
import com.amadeus.resources.FlightOfferSearch;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import log.LogsManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.LogManager;


//TODO maybe make the AmadeusApi singleton
public class AmadeusApi {
    public FlightOfferSearch[] searchFlights(String originLocationCode, String destinationLocationCode,
                                          LocalDate departureDate, LocalDate returnDate, int adults, boolean oneWay,
                                          int maxNumberOfResults) {
        //TODO maybe change to a class member
        Amadeus amadeus = Amadeus
                .builder(System.getenv().get("AMADEUS_CLIENT_ID"), System.getenv().get("AMADEUS_CLIENT_SECRET"))
                .build();
        FlightOfferSearch[] flightOffersSearches = null;

        try {
            if (oneWay) {
                flightOffersSearches = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", originLocationCode)
                                .and("destinationLocationCode", destinationLocationCode)
                                .and("departureDate", departureDate.toString())
                                .and("adults", adults)
                                .and("max", maxNumberOfResults));
            }
            else {
                flightOffersSearches = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", originLocationCode)
                                .and("destinationLocationCode", destinationLocationCode)
                                .and("departureDate", departureDate.toString())
                                .and("returnDate", returnDate.toString())
                                .and("adults", adults)
                                .and("max", maxNumberOfResults));
            }
        }catch (ResponseException exception){
            LogsManager.log(exception.getMessage());
        }
        //TODO make sure the return value isn't null
        return flightOffersSearches;
    }

}



