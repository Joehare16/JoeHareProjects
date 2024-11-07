package ui.views;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import database.entities.Patient;

import java.awt.*;

import ui.UI;
import ui.structures.ViewPanel;
import engine.Engine;

//author jh2199
public class ListPatientsView extends ViewPanel {
    private UI uiManager;
    private Engine engine;
    private DefaultTableModel tableModel;

    public ListPatientsView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        Font titleFont = new Font("Arial", Font.BOLD, 20);

        JLabel titleLabel = new JLabel("View all Patients");
        titleLabel.setFont(titleFont);
        tableModel = new DefaultTableModel();

        String[] columnNames = { "pid", "Name", "Email", "Phone Number" };
        tableModel.setColumnIdentifiers(columnNames);

        JTable table = new JTable(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        // takes user back to home page
        JButton backHome = new JButton("Back Home");
        backHome.addActionListener(e -> {
            uiManager.loadView(UI.View.HOME);
        });

        JButton personalPatients = new JButton("Load Personal Patients");
        personalPatients.addActionListener(e -> {
            if (personalPatients.getText().equals("Load Personal Patients")) {
                List<Patient> Patients = engine.getPersonalPatients();
                updateTable(Patients);

                personalPatients.setText("Load All Patients");
            } else {
                List<Patient> Patients = engine.getPatients();
                updateTable(Patients);

                personalPatients.setText("Load Personal Patients");
            }
        });
        JButton assignDoctorButton = new JButton("Assign Doctor");
        assignDoctorButton.addActionListener(e -> uiManager.loadView(UI.View.ASSIGNDOCTOR));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(backHome);
        buttonsPanel.add(personalPatients);
        buttonsPanel.add(assignDoctorButton);

        this.add(titleLabel);
        gbc.gridy = 1;
        this.add(table, gbc);
        gbc.gridy = 2;
        this.add(buttonsPanel, gbc);

        List<Patient> Patients = engine.getPatients();
        updateTable(Patients);
    }

    public void updateTable(List<Patient> patients) {
        tableModel.setRowCount(0);

        for (Patient patient : patients) {
            Object[] rowData = new Object[4]; // 4 columns
            rowData[0] = patient.pid;
            rowData[1] = patient.name;
            rowData[2] = patient.email;
            rowData[3] = patient.phone_number;
            tableModel.addRow(rowData);
        }
    }

}