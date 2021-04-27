package currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class FixerApi {
private static final String FIXER_API_KEY =  "39683ea637caf7e98647ea85fe415768";
    private static final String URL_PREFIX =  "http://data.fixer.io/api/latest?access_key=" + FIXER_API_KEY;
    private OkHttpClient client = new OkHttpClient();
    private Map<String, LinkedHashMap> responseAsMap;

    public double convert(String sourceCurrency, String destinationCurrency, int amount) throws IOException {
        Request request = new Request.Builder()
                .url(URL_PREFIX +"&symbols=" + sourceCurrency + "," + destinationCurrency).get().build();
        Response response = client.newCall(request).execute();
        String responseAsString = "";
        double res = 0;
        //TODO - what should we do in case response fails
        if(response.isSuccessful())
            responseAsString =  response.body().string();
        try {
            responseAsMap = new ObjectMapper().readValue(responseAsString, HashMap.class);
            Iterator<Map.Entry<String, Double>> iterator = responseAsMap.get("rates").entrySet().iterator();
            Map.Entry<String, Double> entry =  iterator.next();
            //first value of the map is the rate between the base currency and the source currency
            double sourceToBaseRatio = 1/entry.getValue();
            entry = iterator.next();
            //second value of the map is the rate between the base currency and the destination currency
            double baseToDestinationRatio = entry.getValue();
            res = amount * sourceToBaseRatio * baseToDestinationRatio;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void main(String[] args) {
        try {
            new FixerApi().convert("USD", "ILS", 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
