package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import techvengers_hurricane.App;

/**
 * Handles the login form and sends the user to the home screen after a successful login.
 * @author Team Tech-vengers
 */
public class LoginController {
    /**
     * Creates the controller for the login screen.
     */
    public LoginController() { }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Checks the entered credentials and opens the home screen when they match an account.
     * @throws IOException if the home FXML resource cannot be loaded
     */
    @FXML
    private void login() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        System.out.println("Your name is " + username + " pass " + password);

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your username and password.");
            return;
        }
    }
}
