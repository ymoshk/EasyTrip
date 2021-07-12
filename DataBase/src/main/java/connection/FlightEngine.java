package connection;

import amadeus.AmadeusApi;
import com.amadeus.resources.FlightOfferSearch;
import model.flightOffer.FlightOffer;

import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FlightEngine {
    private DBContext context;
    private AmadeusApi amadeusApi = new AmadeusApi();
    private final static int MAX_NUMBER_OF_RESULTS = 10;


    public List<FlightOffer> getFlightsFromDB(String originLocationCode, String destinationLocationCode, LocalDateTime departureDate,
                                              LocalDateTime returnDate, boolean oneWay, int numberOfPassengers){
        Query q = DBContext.getInstance().createQuery(
                "FROM FlightOffer WHERE " +
                        "originLocationCode = :originLocationCode AND " +
                        "destinationLocationCode = :destinationLocationCode AND " +
                        "departureDate = :departureDate AND " +
                        "returnDate = :returnDate AND " +
                        "oneWay =: oneWay");
        q.setParameter("originLocationCode", originLocationCode);
        q.setParameter("destinationLocationCode", destinationLocationCode);
        q.setParameter("departureDate", departureDate);
        q.setParameter("returnDate", returnDate);
        q.setParameter("oneWay", oneWay);
        q.setMaxResults(MAX_NUMBER_OF_RESULTS);

        return (List<FlightOffer>) DBContext.getInstance().selectQuery(q);
    }

    public List <FlightOffer> findFlights(String originLocationCode, String destinationLocationCode, LocalDate departureDate,
                                          LocalDate returnDate, boolean oneWay, int numberOfPassengers){

        List <FlightOffer> flightOfferList = getFlightsFromDB(originLocationCode, destinationLocationCode, departureDate.atTime(12, 0),
                returnDate.atTime(12, 0), oneWay, numberOfPassengers);

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

    // TODO: remove this method
    public static void main(String[] args) {
        new FlightEngine().findFlights("TLV", "JFK", LocalDate.parse("2021-07-14"),
                LocalDate.parse("2021-07-23"), true, 1);
    }
}
