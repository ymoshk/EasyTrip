package application;

import connection.DBContext;
import model.Country;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application is starting...");
        DBContext context = DBContext.getInstance();

        Country country = (Country) context.findById(Country.class, 30).orElse(null);
        context.delete(country);

        //        Airport airport = new Airport();
        //        airport.setAirportLocation(new LatLng(1.0, 1.3));
        //        airport.setAirportName("name");
        //        airport.setIATA("IATA");
        //        airport.setThreeLetterCode("TLV");
        //
        //        City city = new City();
        //        city.setAirportList(new ArrayList<>());
        //        city.getAirportList().add(airport);
        //        city.setAveragePricePerDay(100.0);
        //        city.setCityCenter(new LatLng(50.9, 55.9));
        //        city.setCityName("Tel Aviv");
        //
        //        Country country = new Country();
        //        country.setAveragePricePerDay(100);
        //        country.setCapital("Tel aviv");
        //        country.setCityList(new ArrayList<>());
        //        country.getCityList().add(city);
        //        country.setLocaleCode("5555");
        //
        //        context.insert(country);

        //        context.insert(airport);
        //
        //        EntityTest lst = (EntityTest) context.findById(EntityTest.class, 18).orElse(null);
        //
        //        lst.setName("newName");
        //        context.update(lst);

        //        Airport airport = (Airport) context.findById(Airport.class, 21).orElse(null);

    }
}

