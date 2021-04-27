package connection;

import model.location.City;
import model.location.Country;
import numbeo.NumbeoApi;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PricesEngine {
    private DBContext context;
    private DataEngine dataEngine;
    private NumbeoApi numbeoApi = new NumbeoApi();



    public PricesEngine() {
        try {
            List<NumbeoApi.CityLocation> cityLocationList = numbeoApi.getCitiesLocationsAndID();
            cityLocationList.forEach(cityLocation -> {
                context = DBContext.getInstance();
                dataEngine = DataEngine.getInstance();
                City cityToUpdate =  dataEngine.getCity(cityLocation.getCity()).orElse(null);
                if(cityToUpdate != null){
//                    cityToUpdate.set
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void test(){
        List<Country> countries = new FixedDataHandler().getCountries();
        countries.forEach(country -> {
            updateCountryPrices(country.getCountryName());
        });
    }

    public void updateCountryPrices(String countryName){
        try {
            NumbeoApi.PricesPerPlace countryPrices = numbeoApi.getCountryPrices(countryName);
            System.out.println(countryPrices.getCurrency());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCityPrices(String countryName, String cityName, int cityID){
        try {
            NumbeoApi.PricesPerPlace cityPrices = numbeoApi.getCityPrices(countryName, cityName, cityID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new PricesEngine().test();
    }

}
