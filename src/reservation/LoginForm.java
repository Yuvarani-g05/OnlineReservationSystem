package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    public LoginForm() {

        setTitle("Train Reservation - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        // Login button
        loginButton = new JButton("LOGIN");

        // Add components
        panel.add(usernameLabel);
        panel.add(usernameField);

        panel.add(passwordLabel);
        panel.add(passwordField);

        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        // Login button
        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password =
                new String(passwordField.getPassword());

        // Empty field validation
        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
            );

            return;
        }

        String url = "jdbc:sqlite:reservation.db";

        String sql =
                "SELECT * FROM users " +
                "WHERE username = ? AND password = ?";

        try {

            Connection connection =
                    DriverManager.getConnection(url);

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful!"
                );

                // Open Reservation Form
                new ReservationForm();

                // Close Login Form
                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password."
                );
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error."
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new LoginForm();
    }
}
