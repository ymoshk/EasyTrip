package connection;

import amadeus.AmadeusApi;
import com.amadeus.resources.FlightOfferSearch;
import log.LogsManager;
import model.IATACode.IATACode;
import model.flightOffer.FlightOffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class FlightEngine {
    private DBContext context;
    final private AmadeusApi amadeusApi = new AmadeusApi();
    private final static int MAX_NUMBER_OF_RESULTS = 5;

    /**
     * This method find flights using:
     * 1. 'locationToIATACode' to parse country & city to IATA code.
     * 2. 'findFlightsByIATACode' to fetch flights from DB or amadeus API.
     *
     * @param originCountry .
     * @param originCity .
     * @param destinationCountry .
     * @param destinationCity .
     * @param departureDate LocalDate.
     * @param returnDate LocalDate.
     * @param oneWay 1 => one way, 0 => round-trip.
     * @param numberOfPassengers not used right now, might help for filter in the future.
     * @return List of flights that match the search result OR empty list.
     */
    public List <FlightOffer> findFlights(String originCountry, String originCity, String destinationCountry,
                                          String destinationCity, LocalDate departureDate, LocalDate returnDate,
                                          boolean oneWay, int numberOfPassengers){
        List <FlightOffer> flightOfferList = new ArrayList<>();
        List <String> originCodeList = locationToIATACode(originCountry, originCity);
        List <String> destinationCodeList = locationToIATACode(destinationCountry, destinationCity);

        if(originCodeList.isEmpty()){
            LogsManager.log("Couldn't find country: " + originCountry + ", city: " + originCity + " airports");
        }
        if(destinationCodeList.isEmpty()){
            LogsManager.log("Couldn't find country: " + destinationCountry + ", city: " + destinationCity + " airports");
        }

        // Find flights for all airports combinations, in case there are multiple airports in the cities.
        for(String originCode : originCodeList){
            for(String destinationCode : destinationCodeList){
                flightOfferList.addAll(
                        findFlightsByIATACode(originCode,
                                destinationCode,
                                departureDate,
                                returnDate,
                                oneWay,
                                numberOfPassengers));
            }
        }

        return flightOfferList;
    }

    static private void saveToDBCityIATATable() throws IOException {
        DBContext context = DBContext.getInstance();
        File file = new File("C:\\2021\\airport_list_final.txt");
        int counter = 1;
        String city = "";
        String country = "";
        String code = "";
        IATACode iataCode;

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            //System.out.println(st);

            if (counter == 2) {
                city = st;
            } else if (counter == 3) {
                country = st;
            } else if (counter == 4) {
                code = st;
            }
            if (counter == 4) {
                counter = 0;
                iataCode = new IATACode(city, country, code);
                context.insert(iataCode);
                //System.out.println(iataCode);
            }

            counter++;
        }
    }

    /**
     * This method fetches flights from database OR amadeus API.
     *
     * @param originIATACode airport code.
     * @param destinationIATACode airport code.
     * @param departureDate LocalDate.
     * @param returnDate LocalDate.
     * @param oneWay 1 => one way, 0 => round-trip.
     * @param numberOfPassengers not used right now, might help for filter in the future.
     * @return List of flights OR empty list.
     */
    private List <FlightOffer> findFlightsByIATACode(String originIATACode, String destinationIATACode, LocalDate departureDate,
                                          LocalDate returnDate, boolean oneWay, int numberOfPassengers){
        // Try to get flights from DB:
        List <FlightOffer> flightOfferList = new ArrayList<>();

        // In case there no flights in the DB, fetch flight via amadeusApi:
        FlightOfferSearch[] flightsFromApi = amadeusApi.searchFlights(originIATACode,
                destinationIATACode,
                departureDate,
                returnDate,
                numberOfPassengers,
                oneWay,
                MAX_NUMBER_OF_RESULTS);

        if(flightsFromApi != null && flightsFromApi.length > 0){
            Arrays.stream(flightsFromApi).forEach(flightOffer ->{
                flightOfferList.add(new FlightOffer(flightOffer,
                        originIATACode,
                        destinationIATACode,
                        departureDate,
                        returnDate,
                        numberOfPassengers));
            });
        }

        return flightOfferList;
    }

    /**
     * This method parsing country & city to airports IATA code (e.g {"Israel", "Tel Aviv"} => "TLV")
     *
     * @param country country is insensitive case.
     * @param city city is insensitive case.
     * @return list of airports IATA code OR empty list
     * Notice: table was generated out of an HTML page via sed & regex, in case airport not found check the table in DB
     * or reach Ori for further help
     */
    private List <String> locationToIATACode(String country, String city){
        List<IATACode> IATACodeList = (List<IATACode>) DBContext.getInstance().selectQuery(
                "FROM IATACode WHERE country LIKE '" + country + "%' AND city LIKE '" + city + "%'");

        if (IATACodeList.isEmpty()) {
            LogsManager.log("Couldn't find country: " + country + ", city: " + city + " airports");
        }

        return IATACodeList.stream().map(IATACode::getCode).collect(Collectors.toList());
    }

    /**
     * @param originLocationCode      airport IATA code (e.g TLV)
     * @param destinationLocationCode airport IATA code (e.g NYC)
     * @param departureDate           LocalDateTime.
     * @param returnDate              LocalDateTime.
     * @param oneWay                  1 => one way, 0 => round-trip.
     * @param numberOfPassengers      not used right now, might help for filter in the future.
     * @return List of flights that match the search result OR empty list.
     */
    private List<FlightOffer> getFlightsFromDB(String originLocationCode, String destinationLocationCode, LocalDateTime departureDate,
                                               LocalDateTime returnDate, boolean oneWay, int numberOfPassengers) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("FROM FlightOffer WHERE ");
        queryString.append("originLocationCode = ").append(originLocationCode).append(" AND ");
        queryString.append("destinationLocationCode = ").append(destinationLocationCode).append(" AND ");
        queryString.append("departureDate = ").append(departureDate).append(" AND ");
        queryString.append("returnDate = ").append(returnDate).append(" AND ");
        queryString.append("oneWay = ").append(oneWay);

        return (List<FlightOffer>) DBContext.getInstance()
                .selectQuery(queryString.toString(), MAX_NUMBER_OF_RESULTS);
    }

    public static void main(String[] args) {
        FlightEngine flightEngine = new FlightEngine();
        List<FlightOffer> flightOfferList = flightEngine.findFlights("Israel",
                "Tel Aviv",
                "Germany",
                "Berlin",
                LocalDate.parse("2021-08-30"),
                LocalDate.parse("2021-09-05"),
                false,
                1);

        flightOfferList.forEach(flightOffer -> {
            System.out.println(flightOffer);
            System.out.println(flightOffer.getDepartureDateTime(0));
            System.out.println(flightOffer.getArrivalDateTime(0));
            System.out.println(flightOffer.getDepartureDateTime(1));
            System.out.println(flightOffer.getArrivalDateTime(1));
        });
    }
}
