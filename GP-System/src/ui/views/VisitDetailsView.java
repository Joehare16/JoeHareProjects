package ui.views;

import javax.swing.*;

import engine.Engine;
import ui.UI;
import ui.structures.ViewPanel;

import java.awt.*;
import java.awt.event.ActionEvent;

public class VisitDetailsView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    public VisitDetailsView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Enter Visit Details and Prescriptions");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel visitDetailsLabel = new JLabel("Visit Details:");
        JTextField visitDetailsField = new JTextField();

        JLabel prescriptionsLabel = new JLabel("Prescriptions:");
        JTextField prescriptionsField = new JTextField();

        JButton submitButton = new JButton("Submit Details");
        submitButton.addActionListener(
                (ActionEvent evt) -> onSubmitButtonClicked(visitDetailsField.getText(), prescriptionsField.getText()));

        centerPanel.add(visitDetailsLabel);
        centerPanel.add(visitDetailsField);
        centerPanel.add(prescriptionsLabel);
        centerPanel.add(prescriptionsField);
        centerPanel.add(new JLabel());
        centerPanel.add(submitButton);

        this.setBackground(Color.DARK_GRAY);

        add(welcomeLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void onSubmitButtonClicked(String visitDetails, String prescriptions) {
        // TODO
    }

}
