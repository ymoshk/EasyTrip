package model.location;

import com.google.gson.Gson;
import com.google.maps.model.LatLng;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllCountries {
    Gson gson = new Gson();
    List <Country> allCountries = new ArrayList<>();

    public void setCountriesList(){
        String file = "DataBase\\DataFiles\\countries.json";
        String json = null;
        try {
            json = readFileAsString(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonCountry[] countries = gson.fromJson(json, JsonCountry[].class);
        Arrays.asList(countries).forEach(country -> allCountries.add(new Country(country.timezones,
                new LatLng(country.latlng[0], country.latlng[1]),
                country.name, country.country_code, country.capital)));
    }
    public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    static class JsonCountry{
        List<String> timezones = new ArrayList<>();
        double[] latlng;
        String name;
        String country_code;
        String capital;
    }

    public static void main(String[] args) {
        new AllCountries().setCountriesList();
    }
}
