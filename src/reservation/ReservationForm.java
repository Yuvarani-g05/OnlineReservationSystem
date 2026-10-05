package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReservationForm extends JFrame {

    JTextField passengerNameField;
    JTextField trainNumberField;
    JTextField trainNameField;
    JComboBox<String> classBox;
    JTextField journeyDateField;
    JTextField sourceField;
    JTextField destinationField;

    JButton bookButton;
    JButton cancelButton;

    public ReservationForm() {

        setTitle("Train Reservation System");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2, 10, 10));

        // Passenger Name
        JLabel passengerNameLabel = new JLabel("Passenger Name:");
        passengerNameField = new JTextField();

        // Train Number
        JLabel trainNumberLabel = new JLabel("Train Number:");
        trainNumberField = new JTextField();

        // Train Name
        JLabel trainNameLabel = new JLabel("Train Name:");
        trainNameField = new JTextField();
        trainNameField.setEditable(false);

        // Class Type
        JLabel classLabel = new JLabel("Class Type:");

        String[] classes = {
            "AC",
            "Sleeper",
            "Second Sitting"
        };

        classBox = new JComboBox<>(classes);

        // Journey Date
        JLabel journeyDateLabel = new JLabel("Journey Date:");
        journeyDateField = new JTextField();

        // Source
        JLabel sourceLabel = new JLabel("Source Station:");
        sourceField = new JTextField();

        // Destination
        JLabel destinationLabel = new JLabel("Destination Station:");
        destinationField = new JTextField();

        // Buttons
        bookButton = new JButton("BOOK TICKET");
        cancelButton = new JButton("CANCEL TICKET");

        // Add components
        panel.add(passengerNameLabel);
        panel.add(passengerNameField);

        panel.add(trainNumberLabel);
        panel.add(trainNumberField);

        panel.add(trainNameLabel);
        panel.add(trainNameField);

        panel.add(classLabel);
        panel.add(classBox);

        panel.add(journeyDateLabel);
        panel.add(journeyDateField);

        panel.add(sourceLabel);
        panel.add(sourceField);

        panel.add(destinationLabel);
        panel.add(destinationField);

        panel.add(new JLabel(""));
        panel.add(bookButton);

        panel.add(new JLabel(""));
        panel.add(cancelButton);

        add(panel);

        // Train number → train name
        trainNumberField.addActionListener(e -> findTrain());

        // Book ticket
        bookButton.addActionListener(e -> bookTicket());

        // Open cancellation form
        cancelButton.addActionListener(e -> {

            new CancellationForm();

        });

        setVisible(true);
    }

    // Find train name
    private void findTrain() {

        String trainNumberText =
                trainNumberField.getText().trim();

        if (trainNumberText.isEmpty()) {
            return;
        }

        try {

            int trainNumber =
                    Integer.parseInt(trainNumberText);

            String url =
                    "jdbc:sqlite:reservation.db";

            String sql =
                    "SELECT train_name FROM trains " +
                    "WHERE train_number = ?";

            Connection connection =
                    DriverManager.getConnection(url);

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, trainNumber);

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                trainNameField.setText(
                        result.getString("train_name")
                );

            } else {

                trainNameField.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Train number not found."
                );
            }

            result.close();
            statement.close();
            connection.close();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must contain only numbers."
            );

            trainNameField.setText("");

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error."
            );

            e.printStackTrace();
        }
    }

    // Book ticket
    private void bookTicket() {

        String passengerName =
                passengerNameField.getText().trim();

        String trainNumberText =
                trainNumberField.getText().trim();

        String trainName =
                trainNameField.getText().trim();

        String classType =
                (String) classBox.getSelectedItem();

        String journeyDate =
                journeyDateField.getText().trim();

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        // Validate empty fields
        if (passengerName.isEmpty() ||
            trainNumberText.isEmpty() ||
            trainName.isEmpty() ||
            journeyDate.isEmpty() ||
            source.isEmpty() ||
            destination.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all required fields."
            );

            return;
        }
     // Validate date format
        try {

            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

            java.time.LocalDate.parse(journeyDate, formatter);

        } catch (java.time.format.DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date! Please use DD-MM-YYYY format."
            );

            return;
        }

        int trainNumber;

        try {

            trainNumber =
                    Integer.parseInt(trainNumberText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must contain only numbers."
            );

            return;
        }

        String url =
                "jdbc:sqlite:reservation.db";

        String sql =
                "INSERT INTO reservations " +
                "(passenger_name, train_number, train_name, " +
                "class_type, journey_date, source, destination) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection connection =
                    DriverManager.getConnection(url);

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setString(1, passengerName);
            statement.setInt(2, trainNumber);
            statement.setString(3, trainName);
            statement.setString(4, classType);
            statement.setString(5, journeyDate);
            statement.setString(6, source);
            statement.setString(7, destination);

            statement.executeUpdate();

            ResultSet keys =
                    statement.getGeneratedKeys();

            int pnr = 0;

            if (keys.next()) {
                pnr = keys.getInt(1);
            }

            String message =
                    "Booking Successful!\n\n" +
                    "PNR: " + pnr + "\n" +
                    "Passenger: " + passengerName + "\n" +
                    "Train: " + trainNumber +
                    " - " + trainName + "\n" +
                    "Class: " + classType + "\n" +
                    "Journey Date: " + journeyDate + "\n" +
                    "From: " + source + "\n" +
                    "To: " + destination;

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Booking Confirmation",
                    JOptionPane.INFORMATION_MESSAGE
            );

            keys.close();
            statement.close();
            connection.close();

            // Clear form
            passengerNameField.setText("");
            trainNumberField.setText("");
            trainNameField.setText("");
            journeyDateField.setText("");
            sourceField.setText("");
            destinationField.setText("");

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking failed!"
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        new ReservationForm();
    }
}
