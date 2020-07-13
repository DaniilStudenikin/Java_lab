package JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleDataSource {

    public static Connection openConnection(String URL, String USER, String PASSWORD) {
        try {
            return DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
