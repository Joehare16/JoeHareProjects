package ui.views;

import ui.UI;
import ui.structures.ViewPanel;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import database.entities.Visit;
import database.entities.Visit.Status;

import javax.swing.JTable;

import engine.Engine;

public class ListBookingsView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    public ListBookingsView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        setLayout(new GridLayout(2, 1));

        String[] columnNames = { "Booking ID", "Customer Name", "Date" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        // Clear previous content
        tableModel.setRowCount(0);

        // Get bookings from the engine
        ArrayList<Visit> bookings = engine.getVisits(Status.SCHEDULED);

        // Populate the table with bookings
        for (var booking : bookings) {
            tableModel.addRow(
                    new Object[] { booking.visit_id, booking.patient.name, booking.visit_date });
        }

        JButton backButton = new JButton("Back Home");
        backButton.addActionListener((evt) -> uiManager.loadView(UI.View.HOME));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttonPanel.add(backButton);

        add(buttonPanel);
    }
}