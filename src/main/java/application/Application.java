package application;

import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;
import connection.DBContext;
import constant.Constants;
import model.attraction.hotel.Hotel;
import model.location.Airport;
import model.location.City;
import model.location.Country;
import util.GoogleMapsApiUtils;

import java.util.ArrayList;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application is starting...");
        DBContext context = DBContext.getInstance();

//        Airport airport = new Airport();
//        airport.setAirportLocation(new LatLng(5.0, 5.3));
//        airport.setAirportName("TEST");
//        airport.setIATA("IATATEST");
//        airport.setThreeLetterCode("TLVTEST");


        City city = (City) context.findById(City.class, 34).orElse(null);
        Hotel testHotel = new Hotel("TestHotel", city);

        city.getAttractionList().add(testHotel);
        context.update(city);
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

