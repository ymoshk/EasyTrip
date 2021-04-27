package connection;

import model.location.Country;
import numbeo.NumbeoApi;

import java.io.IOException;
import java.util.List;

public class PricesEngine {
    private DBContext context;
    private NumbeoApi numbeoApi = new NumbeoApi();



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
