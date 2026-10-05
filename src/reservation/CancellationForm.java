package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancellationForm extends JFrame {

    JTextField pnrField;
    JTextField passengerNameField;
    JTextField trainField;
    JTextField classField;
    JTextField dateField;
    JTextField sourceField;
    JTextField destinationField;

    JButton fetchButton;
    JButton cancelButton;

    public CancellationForm() {

        setTitle("Train Reservation - Cancel Ticket");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2, 10, 10));

        // PNR
        JLabel pnrLabel = new JLabel("PNR Number:");
        pnrField = new JTextField();

        fetchButton = new JButton("FETCH");

        // Passenger
        JLabel passengerLabel = new JLabel("Passenger:");
        passengerNameField = new JTextField();
        passengerNameField.setEditable(false);

        // Train
        JLabel trainLabel = new JLabel("Train:");
        trainField = new JTextField();
        trainField.setEditable(false);

        // Class
        JLabel classLabel = new JLabel("Class:");
        classField = new JTextField();
        classField.setEditable(false);

        // Date
        JLabel dateLabel = new JLabel("Journey Date:");
        dateField = new JTextField();
        dateField.setEditable(false);

        // Source
        JLabel sourceLabel = new JLabel("From:");
        sourceField = new JTextField();
        sourceField.setEditable(false);

        // Destination
        JLabel destinationLabel = new JLabel("To:");
        destinationField = new JTextField();
        destinationField.setEditable(false);

        // Cancel button
        cancelButton = new JButton("CANCEL TICKET");

        // Add components
        panel.add(pnrLabel);
        panel.add(pnrField);

        panel.add(new JLabel(""));
        panel.add(fetchButton);

        panel.add(passengerLabel);
        panel.add(passengerNameField);

        panel.add(trainLabel);
        panel.add(trainField);

        panel.add(classLabel);
        panel.add(classField);

        panel.add(dateLabel);
        panel.add(dateField);

        panel.add(sourceLabel);
        panel.add(sourceField);

        panel.add(destinationLabel);
        panel.add(destinationField);

        panel.add(new JLabel(""));
        panel.add(cancelButton);

        add(panel);

        // Fetch button
        fetchButton.addActionListener(e -> fetchBooking());

        // Cancel button
        cancelButton.addActionListener(e -> cancelTicket());

        setVisible(true);
    }

    // Fetch booking using PNR
    private void fetchBooking() {

        String pnrText = pnrField.getText().trim();

        if (pnrText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR number."
            );

            return;
        }

        int pnr;

        try {

            pnr = Integer.parseInt(pnrText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "PNR must contain only numbers."
            );

            return;
        }

        String url = "jdbc:sqlite:reservation.db";

        String sql =
                "SELECT * FROM reservations WHERE pnr = ?";

        try {

            Connection connection =
                    DriverManager.getConnection(url);

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, pnr);

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                passengerNameField.setText(
                        result.getString("passenger_name")
                );

                trainField.setText(
                        result.getInt("train_number")
                        + " - "
                        + result.getString("train_name")
                );

                classField.setText(
                        result.getString("class_type")
                );

                dateField.setText(
                        result.getString("journey_date")
                );

                sourceField.setText(
                        result.getString("source")
                );

                destinationField.setText(
                        result.getString("destination")
                );

            } else {

                clearBookingDetails();

                JOptionPane.showMessageDialog(
                        this,
                        "No booking found for this PNR."
                );
            }

            result.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error."
            );

            e.printStackTrace();
        }
    }

    // Cancel ticket
    private void cancelTicket() {

        String pnrText = pnrField.getText().trim();

        if (pnrText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a PNR number."
            );

            return;
        }

        if (passengerNameField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fetch the booking first."
            );

            return;
        }

        int pnr;

        try {

            pnr = Integer.parseInt(pnrText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "PNR must contain only numbers."
            );

            return;
        }

        // Confirmation dialog
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this ticket?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {

            return;
        }

        String url = "jdbc:sqlite:reservation.db";

        String sql =
                "DELETE FROM reservations WHERE pnr = ?";

        try {

            Connection connection =
                    DriverManager.getConnection(url);

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, pnr);

            int rowsDeleted =
                    statement.executeUpdate();

            if (rowsDeleted > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket cancelled successfully!"
                );

                clearBookingDetails();
                pnrField.setText("");

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No booking found for this PNR."
                );
            }

            statement.close();
            connection.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed."
            );

            e.printStackTrace();
        }
    }

    // Clear booking details
    private void clearBookingDetails() {

        passengerNameField.setText("");
        trainField.setText("");
        classField.setText("");
        dateField.setText("");
        sourceField.setText("");
        destinationField.setText("");
    }

    public static void main(String[] args) {

        new CancellationForm();
    }
}
