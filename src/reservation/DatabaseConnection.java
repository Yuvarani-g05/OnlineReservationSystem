package reservation;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static void main(String[] args) {

        String url = "jdbc:sqlite:reservation.db";

        try {
            Class.forName("org.sqlite.JDBC");

            Connection connection = DriverManager.getConnection(url);

            System.out.println("Database connected successfully!");

            connection.close();

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}