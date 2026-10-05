package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {

    public static void main(String[] args) {

        String url = "jdbc:sqlite:reservation.db";

        try {
            Connection connection = DriverManager.getConnection(url);

            Statement statement = connection.createStatement();

            // Users table
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)"
            );

            // Trains table
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS trains (" +
                "train_number INTEGER PRIMARY KEY, " +
                "train_name TEXT NOT NULL)"
            );

            // Reservations table
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "pnr INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "passenger_name TEXT NOT NULL, " +
                "train_number INTEGER NOT NULL, " +
                "train_name TEXT NOT NULL, " +
                "class_type TEXT NOT NULL, " +
                "journey_date TEXT NOT NULL, " +
                "source TEXT NOT NULL, " +
                "destination TEXT NOT NULL)"
            );

            System.out.println("Database tables created successfully!");

            statement.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("Database setup failed!");
            e.printStackTrace();
        }
    }
}