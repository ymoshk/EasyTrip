package application;

import connection.DataEngine;
import model.location.Country;

import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Application is starting...");
        DataEngine data = DataEngine.getInstance();
        List<Country> lst = data.getCountries("");
        //        data.close();
    }
}

