package ui.views;

import javax.swing.*;

import database.entities.Patient;
import database.entities.Visit;
import engine.Engine;
import ui.UI;
import ui.structures.ViewPanel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.sql.Date;

public class VisitDetailsEditView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public VisitDetailsEditView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Edit Visit Details");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(6, 2, 10, 10));

        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton backHomeButton = new JButton("Back Home");
        backHomeButton.addActionListener((e) -> uiManager.loadView(UI.View.HOME));

        JLabel visitLabel = new JLabel("Choose Visit");
        JComboBox<Visit> visitField = new JComboBox<>();

        JLabel patientLabel = new JLabel("Patient");
        JComboBox<Patient> patientField = new JComboBox<>();
        patientField.setEnabled(false);

        JLabel dateLabel = new JLabel("Visit Date:");
        JFormattedTextField visitDateField = new JFormattedTextField(dateFormat);
        visitDateField.setColumns(10);
        visitDateField.setFocusLostBehavior(JFormattedTextField.COMMIT);
        visitDateField.setEnabled(false);

        JButton submitButton = new JButton("Update Visit");
        submitButton.setEnabled(false);
        submitButton.addActionListener((e) -> {
            Visit visit = (Visit) visitField.getSelectedItem();
            Patient patient = (Patient) patientField.getSelectedItem();
            Date visitDate = null;

            try {
                visitDate = Date.valueOf(visitDateField.getText());
            } catch (IllegalArgumentException ex) {
                System.out.println("[ui] VisitDetailsEditView: Error parsing date: " + ex.getMessage());
            }

            if (visit != null && patient != null && visitDate != null) {
                var success = this.engine.updateVisit(visit.visit_id, patient, visitDate);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Visit updated successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    uiManager.loadView(UI.View.HOME);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update visit", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        for (Visit visit : this.engine.getVisits(null)) {
            visitField.addItem(visit);
        }

        visitField.addActionListener((e) -> {
            Visit visit = (Visit) visitField.getSelectedItem();

            if (visit != null) {
                patientField.setSelectedItem(visit.patient);
                visitDateField.setValue(visit.visit_date);

                patientField.setEnabled(true);
                visitDateField.setEnabled(true);
                submitButton.setEnabled(true);

                for (Patient patient : this.engine.getPatients()) {
                    patientField.addItem(patient);
                }
            }
        });

        centerPanel.add(visitLabel);
        centerPanel.add(visitField);
        centerPanel.add(patientLabel);
        centerPanel.add(patientField);
        centerPanel.add(dateLabel);
        centerPanel.add(visitDateField);
        // centerPanel.add(reasonLabel);
        // centerPanel.add(reasonField);

        centerPanel.add(backHomeButton);
        centerPanel.add(submitButton);

        this.setBackground(Color.DARK_GRAY);

        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

}
