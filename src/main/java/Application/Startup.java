package Application;

import connection.DbConnection;

public class Startup {

    public static void main(String[] args) {
        System.out.println("Application is starting...");
        DbConnection cnn = new DbConnection();
    }
}
