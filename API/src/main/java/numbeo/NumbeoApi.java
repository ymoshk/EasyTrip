package numbeo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Scanner;

public class NumbeoApi {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(" https://www.numbeo.com/api/cities?api_key=aiv2tn9h10x9ul").get().build();

        Response response = client.newCall(request).execute();
        try(Scanner scanner = new Scanner(response.body().byteStream())){
            while(scanner.hasNext()){
                System.out.println(scanner.nextLine());
            }
        }
    }

}
