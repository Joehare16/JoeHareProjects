package ui.views;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import engine.Engine;
import ui.UI;
import ui.structures.ViewPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterView extends ViewPanel implements ActionListener {
    private JTextField tfName, tfEmail;
    private JPasswordField pfPassword, pfConfirmPassword;
    private JButton btnClear, btnSignUp;
    private UI uiManager;
    private Engine engine;

    public RegisterView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        // Main panel with color and padding border
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 20)); // Adjusted gaps
        mainPanel.setBackground(new Color(230, 230, 250)); // Light lavender background
        Border padding = new EmptyBorder(20, 20, 20, 20); // Increased padding
        Border line = new LineBorder(new Color(106, 90, 205), 3, true); // Darker lavender line border
        mainPanel.setBorder(new CompoundBorder(line, padding));

        // Initialize components with custom font
        tfName = new JTextField();
        tfEmail = new JTextField();
        pfPassword = new JPasswordField();
        pfConfirmPassword = new JPasswordField();
        btnClear = new JButton("Clear");
        btnSignUp = new JButton("Sign Up");

        // Set component borders and background color
        setComponentStyling(tfName);
        setComponentStyling(tfEmail);
        setComponentStyling(pfPassword);
        setComponentStyling(pfConfirmPassword);

        // Add components to the main panel
        mainPanel.add(new JLabel("Name:"));
        mainPanel.add(tfName);
        mainPanel.add(new JLabel("Email:"));
        mainPanel.add(tfEmail);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(pfPassword);
        mainPanel.add(new JLabel("Confirm Password:"));
        mainPanel.add(pfConfirmPassword);
        mainPanel.add(btnClear);
        mainPanel.add(btnSignUp);

        // Add the main panel to the frame
        add(mainPanel);

        // Event listeners
        btnClear.addActionListener(this);
        btnSignUp.addActionListener(this);
    }

    private void setComponentStyling(JComponent component) {
        Border margin = new EmptyBorder(5, 10, 5, 10); // Adjusted margins
        Border compound = new CompoundBorder(new LineBorder(new Color(123, 104, 238), 1, true), margin); // Line border
                                                                                                         // with margin
        component.setBorder(compound);
        if (component instanceof JTextField || component instanceof JPasswordField) {
            component.setBackground(new Color(245, 245, 255)); // Very light lavender for text fields
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClear) {
            clearForm();
        } else if (e.getSource() == btnSignUp) {
            this.signUp();
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfEmail.setText("");
        pfPassword.setText("");
        pfConfirmPassword.setText("");
    }

    private void signUp() {
        var signedUp = this.engine.signupDoctor(tfEmail.getText().split("@")[0], new String(pfPassword
                .getPassword()),
                this.tfName.getText(), "");
        if (signedUp) {
            JOptionPane.showMessageDialog(this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            uiManager.loadView(UI.View.HOME);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to sign up!");
        }
    }

}