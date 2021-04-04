package rapidSkyScanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Scanner;

public class RapidSkyScannerAPI {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browsequotes/v1.0/US/USD/en-US/TLV-sky/JFK-sky/2021-05-10")
                .get()
                .addHeader("x-rapidapi-key", "f80a7dfe70msh30a4b45ccf1ac95p1aea23jsn80e59d1fe997")
                .addHeader("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        try(Scanner scanner = new Scanner(response.body().byteStream())){
            while(scanner.hasNext()){
                System.out.println(scanner.nextLine());
            }
        }
    }
}
