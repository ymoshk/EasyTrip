package application;

import connection.DBContext;
import connection.DataEngine;
import model.location.City;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application is starting...");
//        DBContext context = DBContext.getInstance();
        DataEngine data = new DataEngine();

        List<City> lst = data.getCities("tel");
        lst.forEach(city -> System.out.println(city.getCityName()));

        //        Airport airport = new Airport();
        //        airport.setAirportLocation(new LatLng(5.0, 5.3));
        //        airport.setAirportName("TEST");
        //        airport.setIATA("IATATEST");
        //        airport.setThreeLetterCode("TLVTEST");


        //        City city = (City) context.findById(City.class, 34).orElse(null);
        //
        //        context.update(city);
        //        context.insert(testHotel);


        //        City city = new City();
        //        city.setAirportList(new ArrayList<>());
        //        city.getAirportList().add(airport);
        //        city.setAveragePricePerDay(100.0);
        //        city.setCityCenter(new LatLng(22, 22));
        //        city.setCityName("Tel Aviv");
        //
        //        Country country = new Country();
        //        country.setAveragePricePerDay(100);
        //        country.setCapital("Tel aviv");
        //        country.setCityList(new ArrayList<>());
        //        country.getCityList().add(city);
        //        country.setLocaleCode("5555");
        //        country.getTimezones().add("1");
        //        country.getTimezones().add("2");
        //
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

