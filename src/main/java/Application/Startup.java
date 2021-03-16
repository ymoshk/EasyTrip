package Application;


import connection.DBContext;
import connection.EntityTest;

import java.io.IOException;

public class Startup {

    public static void main(String[] args) {
        System.out.println("Application is starting...");

        try(DBContext dbContext = new DBContext()) {
            dbContext.addTest(new EntityTest("Jonson", 58));
        }

    }
}
