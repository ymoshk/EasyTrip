package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

    private final String url = "jdbc:mysql://83-229-71-203.cloud-xip.io/EasyTrip";
    private final String user = "root";
    private final String password = "easyPassword2021";

    public DbConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection cnn = DriverManager.getConnection(url, user, password);
            Statement statement = cnn.createStatement();
            statement.execute("CREATE TABLE Person (PersonID int)");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
