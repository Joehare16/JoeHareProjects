package ui.views;

import javax.swing.*;

import engine.Engine;
import ui.UI;
import ui.structures.ViewPanel;

import java.awt.*;
import java.awt.event.ActionEvent;

public class HomeView extends ViewPanel {
    Engine engine;
    UI uiManager;

    public HomeView(UI uiManager, Engine engine) {
        super();

        this.engine = engine;
        this.uiManager = uiManager;

        render();
    }

    public void render() {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome " + this.engine.state.user.username);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton viewBookingsButton = new JButton("View Bookings");
        viewBookingsButton.addActionListener((ActionEvent evt) -> uiManager.loadView(UI.View.LIST));

        JButton viewPatientsButton = new JButton("View Patients");
        viewPatientsButton.addActionListener((ActionEvent evt) -> uiManager.loadView(UI.View.PATIENTS));

        JButton createVisitButton = new JButton("Create Visit");
        createVisitButton.addActionListener((ActionEvent evt) -> uiManager.loadView(UI.View.CREATEVISIT));

        JButton editVisitButton = new JButton("Edit Visit");
        editVisitButton.addActionListener((ActionEvent evt) -> uiManager.loadView(UI.View.VISITDETAILSEDIT));

        JButton assignNewDoctorButton = new JButton("Assign New Doctor");
        assignNewDoctorButton.addActionListener((ActionEvent evt) -> uiManager.loadView(UI.View.ASSIGNDOCTOR));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener((ActionEvent evt) -> {
            engine.logout();
            uiManager.loadView(UI.View.LOGIN);
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        centerPanel.add(viewBookingsButton);
        centerPanel.add(viewPatientsButton);
        centerPanel.add(createVisitButton);
        centerPanel.add(editVisitButton);
        centerPanel.add(assignNewDoctorButton);

        setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);

    }
}