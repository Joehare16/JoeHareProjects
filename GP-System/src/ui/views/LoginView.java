package ui.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import ui.UI;
import ui.structures.ViewPanel;
import engine.Engine;

public class LoginView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    public LoginView(UI uiManager, Engine engine) {
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

        JLabel userLabel = new JLabel("Username: ");
        JTextField usernameField = new JTextField(30);
        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField(30);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (engine.login(username, password) == true) {
                uiManager.loadView(UI.View.HOME);
            } else {
                JOptionPane.showMessageDialog(LoginView.this, "Incorrect Username or Password entered");
            }
        });

        JButton somewhere = new JButton("Go to signup");

        somewhere.addActionListener(e -> {
            uiManager.loadView(UI.View.SIGNUP);
        });

        JButton clear = new JButton("clear");

        clear.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        this.add(userLabel);
        gbc.gridy = 1;
        this.add(usernameField, gbc);
        gbc.gridy = 2;
        this.add(passwordLabel, gbc);
        gbc.gridy = 3;
        this.add(passwordField, gbc);
        gbc.gridy = 4;
        this.add(loginButton, gbc);
        gbc.gridy = 5;
        this.add(somewhere, gbc);
    }

}