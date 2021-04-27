package numbeo;

import com.google.gson.Gson;
import currency.FixerApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NumbeoApi {
    private static final String NUMBEO_API_KEY =  "?api_key=aiv2tn9h10x9ul";
    private static final String URL_PREFIX =  "https://www.numbeo.com/api/";
    private static final String CITY_PRICES = "city_prices";
    private static final String CITIES_ID = "cities";
    private static final String COUNTRY_PRICES = "country_prices";
    private static final String CURRENCY_RATES = "currency_exchange_rates";
    private OkHttpClient client = new OkHttpClient();
    private FixerApi currencyConvertor = new FixerApi();
    private Gson gson = new Gson();


    public List<CityLocation> getCitiesLocationsAndID() throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX + CITIES_ID + NUMBEO_API_KEY).get().build();
        Response response = client.newCall(request).execute();
        String citiesID = response.body().string();
        CitiesWrap citiesLocations = gson.fromJson(citiesID, CitiesWrap.class);
        AtomicInteger i = new AtomicInteger(1);
        citiesLocations.getCities().forEach(cityLocation -> {
            System.out.println( i + cityLocation.getCity());
            i.getAndIncrement();
        });
        return citiesLocations.getCities();
    }


    public PricesPerPlace getCityPrices(String countryName, String cityName, int cityID) throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX + CITY_PRICES + NUMBEO_API_KEY + "&query=" + countryName +
                        ",%" + cityID +  cityName).get().build();
        Response response = client.newCall(request).execute();
        String cityPriceResponse = response.body().string();
        PricesPerPlace cityPrices = gson.fromJson(cityPriceResponse, PricesPerPlace.class);

        return cityPrices;
    }

    public List<ExchangeRates> getCurrencyRates() throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX + CURRENCY_RATES + NUMBEO_API_KEY).get().build();
        Response response = client.newCall(request).execute();
        String currencyRatesResponse = response.body().string();
        ExchangeRates[] exchangeRates = gson.fromJson(currencyRatesResponse, ExchangeRates[].class);
        List<ExchangeRates> exchangeRatesList =  new ArrayList<ExchangeRates>(Arrays.asList(exchangeRates));

        return exchangeRatesList;
    }

//    public double convert(String sourceCurrency, String destinationCurrency, int amount) {
//        try {
//            List currencyRates = getCurrencyRates();
//
//            double sourceToBaseRatio = 1/entry.getValue();
//            double baseToDestinationRatio = entry.getValue();
//            res = amount * sourceToBaseRatio * baseToDestinationRatio;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public PricesPerPlace getCountryPrices(String countryName) throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX + COUNTRY_PRICES + NUMBEO_API_KEY + "&country=" + countryName).get().build();
        Response response = client.newCall(request).execute();
        String countryPriceResponse = response.body().string();
        PricesPerPlace countryPrices = gson.fromJson(countryPriceResponse, PricesPerPlace.class);

        return countryPrices;
    }


    public static void main(String[] args) {
        try {
            new NumbeoApi().getCitiesLocationsAndID();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PricesPerPlace {
        String name;
        String currency;
        int contributors;
        int monthLastUpdate;
        int yearLastUpdate;
        List<ItemPrice> prices = new ArrayList<>();

        public String getCurrency() {
            return currency;
        }

    }

    static class ItemPrice {
        int data_points;
        int item_id;
        double lowest_price;
        double average_price;
        double highest_price;
        String item_name;
    }

    static class ExchangeRates {
        double one_usd_to_currency;
        String currency;
        double one_eur_to_currency;
    }

    public static class CitiesWrap {
        List<CityLocation> cities = new ArrayList<>();

        public List<CityLocation> getCities() {
            return cities;
        }
    }

    public static class CityLocation{
        int city_id;
        double longitude;
        double latitude;
        String country;
        String city;

        public int getCity_id() {
            return city_id;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }
    }

}
