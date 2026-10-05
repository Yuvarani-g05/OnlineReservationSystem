package reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InsertUser {

    public static void main(String[] args) {

        String url = "jdbc:sqlite:reservation.db";

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try {
            Connection connection = DriverManager.getConnection(url);

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, "admin");
            statement.setString(2, "1234");

            statement.executeUpdate();

            System.out.println("User added successfully!");

            statement.close();
            connection.close();

        } catch (Exception e) {
            System.out.println("User insertion failed!");
            e.printStackTrace();
        }
    }
}