package amadeus;

import com.amadeus.Amadeus;
import com.amadeus.Params;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import log.LogsManager;

import java.time.LocalDate;


//TODO maybe make the AmadeusApi singleton
public class AmadeusApi {
    // Initialize using parameters (Must define keys in global environment in order to use this API)
    Amadeus amadeus = Amadeus
            .builder("QGTezduNRqkNWfFXkJuQ9hLGw6gkMH7j", "xH6NK70tdHkOMiSw")
            .build();

    /**
     * This function returns an array of FlightOfferSearch objects using amadeus API.
     *
     * @param originLocationCode airport IATA code.
     * @param destinationLocationCode airport IATA code.
     * @param departureDate .
     * @param returnDate .
     * @param adults number of passengers.
     * @param oneWay 1 => One way, 0 => Round-trip
     * @param maxNumberOfResults define the maximum number of flights returned.
     * @return FlightOfferSearch array (amadeus object) OR null
     */
    public FlightOfferSearch[] searchFlights(String originLocationCode, String destinationLocationCode,
                                             LocalDate departureDate, LocalDate returnDate, int adults, boolean oneWay,
                                             int maxNumberOfResults) {
        FlightOfferSearch[] flightOffersSearches = null;

        try {
            if (oneWay) {
                flightOffersSearches = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", originLocationCode)
                                .and("destinationLocationCode", destinationLocationCode)
                                .and("departureDate", departureDate.toString())
                                .and("adults", adults)
                                .and("max", maxNumberOfResults));
            } else {
                flightOffersSearches = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", originLocationCode)
                                .and("destinationLocationCode", destinationLocationCode)
                                .and("departureDate", departureDate.toString())
                                .and("returnDate", returnDate.toString())
                                .and("adults", adults)
                                .and("max", maxNumberOfResults));
            }
        } catch (ResponseException exception) {
            LogsManager.log(exception.getMessage());
        }

        // The returned value might be null.
        return flightOffersSearches;
    }
}



