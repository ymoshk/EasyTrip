package numbeo;

import com.google.gson.Gson;
import currency.FixerApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;

public class NumbeoApi {
    private static final String NUMBEO_API_KEY =  "aiv2tn9h10x9ul";
    private static final String URL_PREFIX =  "https://www.numbeo.com/api/";
    private OkHttpClient client = new OkHttpClient();
    private FixerApi currencyConvertor = new FixerApi();
    private Gson gson = new Gson();



    public PricesPerPlace getCityPrices(String countryName, String cityName, int cityID) throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX + "city_prices?api_key=" + NUMBEO_API_KEY + "&query=" + countryName +
                        ",%" + Integer.toString(cityID) +  cityName).get().build();
        Response response = client.newCall(request).execute();
        String cityPriceResponse = response.body().string();
        PricesPerPlace cityPrices = gson.fromJson(cityPriceResponse, PricesPerPlace.class);

        return cityPrices;
    }

    public PricesPerPlace getCountryPrices(String countryName) throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX + "country_prices?api_key=" + NUMBEO_API_KEY + "&country=" + countryName).get().build();
        Response response = client.newCall(request).execute();
        String countryPriceResponse = response.body().string();
        PricesPerPlace countryPrices = gson.fromJson(countryPriceResponse, PricesPerPlace.class);

        return countryPrices;
    }

    public static class PricesPerPlace {
        String name;
        String currency;

        public String getCurrency() {
            return currency;
        }

        int contributors;
        int monthLastUpdate;
        int yearLastUpdate;
        List<ItemPrice> prices = new ArrayList<>();
    }

    static class ItemPrice {
        int data_points;
        int item_id;
        double lowest_price;
        double average_price;
        double highest_price;
        String item_name;
    }

}
