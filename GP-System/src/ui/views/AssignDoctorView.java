package ui.views;

import javax.swing.*;

import database.entities.Doctor;
import database.entities.Patient;
import engine.Engine;
import ui.UI;
import ui.UI.View;
import ui.structures.ViewPanel;

import java.awt.*;
import java.awt.event.ActionEvent;

public class AssignDoctorView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    public AssignDoctorView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Assign New Doctor");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.BLACK);

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel patientLabel = new JLabel("Patient: ");
        JComboBox<Patient> patientField = new JComboBox<>();

        for (Patient patient : this.engine.getPatients()) {
            patientField.addItem(patient);
        }
        JLabel doctorLabel = new JLabel("New Doctor: ");
        JComboBox<Doctor> doctorField = new JComboBox<>();

        for (Doctor doctor : this.engine.getDoctors()) {
            doctorField.addItem(doctor);
        }

        JButton assignButton = new JButton("Assign");
        assignButton.addActionListener((ActionEvent e) -> assign(((Patient) patientField.getSelectedItem()).pid,
                ((Doctor) doctorField.getSelectedItem()).did));

        JButton back = new JButton("Back Home");
        back.addActionListener(e -> {
            uiManager.loadView(UI.View.HOME);
        });

        content.add(patientLabel);
        content.add(patientField);

        content.add(doctorLabel);
        content.add(doctorField);

        content.add(assignButton);
        content.add(back);

        this.setBackground(Color.LIGHT_GRAY);

        add(title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    public void assign(int pid, int did) {
        var success = this.engine.updateDoctor(pid, did);

        if (success) {
            JOptionPane.showMessageDialog(this, "Doctor assigned successfully", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            uiManager.loadView(View.HOME);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign doctor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
