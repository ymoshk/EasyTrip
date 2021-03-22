package application;

import connection.DBContext;

public class Application {

    public static void main(String[] args) {
        System.out.println("Application is starting...");
        DBContext context = DBContext.getInstance();
    }
}

