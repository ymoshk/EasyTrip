package model.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.maps.model.LatLng;
import connection.DBContext;
import log.LogsManager;
import model.Model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FixedDataHandler {
    private static final String ALL_COUNTRIES_FILE = "DataBase/src/main/resources/DataFiles/countries.json";
    private static final String ALL_CITIES_FILE = "DataBase/src/main/resources/DataFiles/cities500.json";
    private static final String ALL_AIRPORTS_FILE = "DataBase/src/main/resources/DataFiles/airports.json";
    private final Gson gson = new Gson();
    private final List<Country> countries = new ArrayList<>();
    private final List<City> cities = new ArrayList<>();
    private final Collection<Model> airports = new ArrayList<>();
    private JsonCountry[] jsonCountries;
    private JsonCity[] jsonCities;
    private Map<String, LinkedHashMap> airportMap;

    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public static void main(String[] args) {
        new FixedDataHandler().saveToDataBase();
    }

    private Country getCountryByCountryCode(String countryCode) {
        return countries.stream().filter(country ->
                country.getLocaleCode().equalsIgnoreCase(countryCode)).findFirst().orElse(null);
    }

    private City getCityByName(List<City> cityList, String cityName) {
        if (cityList == null)
            cityList = new ArrayList<>();
        return cityList.stream().filter(city ->
                city.getCityName().equalsIgnoreCase(cityName)).findFirst().orElse(null);
    }

    public void saveToDataBase() {
        DBContext context = DBContext.getInstance();
        initJsonObjectsArrays();
        initProjectObjectLists();

        for (int i = 0; i < countries.size(); i++) {
            if (!(countries.get(i).getCountryName().toLowerCase().equals("israel"))) {
                continue;
            }
            System.out.println(i);
            System.out.println(countries.get(i).countryName);
            try {
                context.insert(countries.get(i));
                break;
            } catch (Exception ex) {
                LogsManager.log("Country failed: " + countries.get(i).getCountryName() + " index: " + i);
            }
        }
    }

    private boolean isCountryExists(Country country) {
        DBContext context = DBContext.getInstance();
        List<Country> lst = (List<Country>) context
                .selectQuery("FROM Country WHERE localeCode = '" + country.localeCode + "'");
        return lst.size() != 0;
    }

    //    private boolean isValidAirportName(String name) {
    //        if(name.equals("Hedley Airport"))
    //            return false;
    //        else if(name.equals("Seronera Airport"))
    //            return false;
    //        else if (name.equals("Maquela do Zombo Airport"))
    //            return false;
    //        else if (name.equals("Ambergris Cay International Airport"))
    //            return false;
    //        else if (name.equals("Bailey Airport"))
    //            return false;
    //        else if(name.equals("Luzamba Airport"))
    //            return false;
    //        else if(name.equals("Nansio Airport"))
    //            return false;
    //        else if(name.equals("Porto Amboim Airport"))
    //            return false;
    //        else if(name.equals("Sanza Pombo Airport"))
    //            return false;
    //        else if(name.equals("Negage Airport"))
    //            return false;
    //        else if(name.equals("Guangzhou MR Air Base"))
    //            return false;
    //        return true;
    //    }

    private void initProjectObjectLists() {
        Arrays.asList(jsonCountries).forEach(jsonCountry -> countries.add(new Country(jsonCountry.timezones,
                new LatLng(jsonCountry.latlng[0], jsonCountry.latlng[1]),
                jsonCountry.name, jsonCountry.country_code, jsonCountry.capital)));

        Arrays.stream(jsonCities).filter(jsonCity -> isValidCountryCode(jsonCity.country)).forEach(city -> {
            Country countryToUpdate = getCountryByCountryCode(city.country);
            City cityToAdd = new City(city.name,
                    new LatLng(Double.parseDouble(city.lat), Double.parseDouble(city.lon)),
                    countryToUpdate);
            countryToUpdate.addCityToCityList(cityToAdd);
            cities.add(cityToAdd);
        });
        airportMap.values().stream().filter(jsonAirport ->
                isValidAirport((String) jsonAirport.get("country"), (String) jsonAirport.get("name"))).
                forEach(airport -> {
                    Country countryOfAirport = getCountryByCountryCode((String) airport.get("country"));
                    String cityName = getValidCityName((String) airport.get("city"));
                    City cityToUpdate = getCityByName(countryOfAirport.getCityList(), cityName);
                    LatLng location;
                    try {
                        location = new LatLng((double) airport.get("lat"), (double) airport.get("lon"));
                    } catch (ClassCastException e) {
                        double lat;
                        double lon;
                        int temp;
                        if (airport.get("lat") instanceof Double)
                            lat = (Double) airport.get("lat");
                        else {
                            temp = (int) airport.get("lat");
                            lat = temp;
                        }
                        if (airport.get("lon") instanceof Double)
                            lon = (Double) airport.get("lon");
                        else {
                            temp = (int) airport.get("lon");
                            lon = temp;
                        }
                        location = new LatLng(lat, lon);
                    }
                    if (cityToUpdate == null) {
                        if (airport.get("city") == "") {
                            cityToUpdate = new City((String) airport.get("name"), location, countryOfAirport);
                        } else {
                            cityToUpdate = new City((String) airport.get("city"), location, countryOfAirport);
                        }
                        countryOfAirport.addCityToCityList(cityToUpdate);
                    }
                    Airport airportToAdd = new Airport((String) airport.get("name"), cityToUpdate, (String) airport.get("iata"),
                            (String) airport.get("icao"), location);
                    airports.add(airportToAdd);
                    cityToUpdate.addAirportToAirportList(airportToAdd);
                });
    }

    private boolean isValidAirport(String country, String name) {
        return isValidCountryCode(country);
    }

    private String getValidCityName(String city) {
        if (city.equals("Kuito"))
            return "Cuito";
        else if (city.equals("Chake"))
            return "Chake Chake";


        return city;
    }

    private boolean isValidCountryCode(String country) {
        switch (country) {
            case "BQ":
            case "KS":
            case "SH":
                return false;
            default:
                return true;
        }
    }

    private void initJsonObjectsArrays() {
        String countriesJson = null;
        String citiesJson = null;
        String airportsJson = null;
        try {
            countriesJson = readFileAsString(ALL_COUNTRIES_FILE);
            citiesJson = readFileAsString(ALL_CITIES_FILE);
            airportsJson = readFileAsString(ALL_AIRPORTS_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            airportMap = new ObjectMapper().readValue(airportsJson, HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        jsonCountries = gson.fromJson(countriesJson, JsonCountry[].class);
        jsonCities = gson.fromJson(citiesJson, JsonCity[].class);
    }

    static class JsonCountry {
        List<String> timezones = new ArrayList<>();
        double[] latlng;
        String name;
        String country_code; //iso 3166-1 alpha-2 country code
        String capital;
    }

    static class JsonCity {
        String id;//inner id
        String name;//city name
        String country;//iso 3166-1 alpha-2 country code
        String admin1;//state
        String lat;//latitude
        String lon;//longtitude
        String pop;//population
    }

    static class JsonAirport {
        String icao;//ICAO code
        String iata;// IATA code
        String name;//airport name
        String city;
        String state;
        String country;
        int elevation;//height above sea level in feet
        double lat;
        double lon;
        String tz;//timezone
    }
}