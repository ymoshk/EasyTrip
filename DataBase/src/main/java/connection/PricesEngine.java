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
    private static PricesEngine instance = null;


    //empty constructor just to make sure the class is a singleton
    private PricesEngine() {}

    private void updateCitiesID(){
        try {
            List<NumbeoApi.CityLocation> cityLocationList = numbeoApi.getCitiesLocationsAndID();
            cityLocationList.forEach(cityLocation -> {
                context = DBContext.getInstance();
                dataEngine = DataEngine.getInstance();
                City cityToUpdate =  dataEngine.getCity(cityLocation.getCity()).orElse(null);
                if(cityToUpdate != null){
                    cityToUpdate.setCityNumbeoID(cityLocation.getCity_id());
                    context.update(cityToUpdate);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // only one thread can execute this method at the same time.
    static synchronized PricesEngine getInstance() {
        if (instance == null) {
            instance = new PricesEngine();
        }
        return instance;
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
