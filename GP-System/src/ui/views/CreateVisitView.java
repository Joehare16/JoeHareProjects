package ui.views;

import javax.swing.*;

import database.entities.Patient;
import database.entities.Visit;
import engine.Engine;
import ui.UI;
import ui.UI.View;
import ui.structures.ViewPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class CreateVisitView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    // Date format for parsing and formatting dates
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CreateVisitView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Create New Visit");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(6, 2, 10, 10)); // Adjusted grid layout

        JLabel patientLabel = new JLabel("Patient");
        JComboBox<Patient> patientField = new JComboBox<>();

        for (Patient patient : this.engine.getPatients()) {
            patientField.addItem(patient);
        }

        JLabel dateLabel = new JLabel("Visit Date:");
        JFormattedTextField visitDateField = new JFormattedTextField(dateFormat);
        visitDateField.setColumns(10); // Adjust the width of the field
        visitDateField.setFocusLostBehavior(JFormattedTextField.COMMIT); // Set focus lost behavior

        JLabel reasonLabel = new JLabel("Reason for Visit:");
        JTextField reasonField = new JTextField();

        JButton submitButton = new JButton("Create Visit");
        submitButton.addActionListener(
                (ActionEvent evt) -> onSubmitButtonClicked(((Patient) patientField.getSelectedItem()).pid,
                        visitDateField.getText(), reasonField.getText()));

        JButton backHomeButton = new JButton("Back Home");
        backHomeButton.addActionListener((ActionEvent evt) -> this.uiManager.loadView(View.HOME));

        centerPanel.add(patientLabel);
        centerPanel.add(patientField);
        centerPanel.add(dateLabel);
        centerPanel.add(visitDateField);
        centerPanel.add(reasonLabel);
        centerPanel.add(reasonField);
        centerPanel.add(backHomeButton);
        centerPanel.add(submitButton);

        this.setBackground(Color.DARK_GRAY);

        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void onSubmitButtonClicked(int pid, String visitDate, String reason) {
        // Parse visit date
        Date parsedVisitDate;
        try {
            parsedVisitDate = Date.valueOf(visitDate);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Please enter date in yyyy-MM-dd format.");
            return;
        }

        // Add visit to database
        Visit visit = this.engine.addVisit(pid, parsedVisitDate, reason);

        if (visit == null) {
            JOptionPane.showMessageDialog(this, "Failed to create visit! Please try again.");
            return;
        }

        // Show success message
        JOptionPane.showMessageDialog(this, "Visit created successfully!");

        // Go back to the previous view
        this.uiManager.loadView(View.HOME);
    }

}
