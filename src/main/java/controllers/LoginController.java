package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import techvengers_hurricane.App;

/**
 * Handles the login form and sends the user to their account screen.
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
     * Checks the entered credentials and opens the user home screen when they match an account.
     * @throws IOException if the user home FXML resource cannot be loaded
     */
    @FXML
    private void login() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            errorLabel.setText("Please enter your username or email.");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            errorLabel.setText("Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        if (App.getReliefApp().login(username, password)) {
            App.setRoot("rolehome");
        } else {
            errorLabel.setText("Incorrect username, email, or password.");
            passwordField.clear();
        }
    }

    /**
     * Opens the sign up screen so a new user can create an account.
     * @throws IOException if the signup FXML resource cannot be loaded
     */
    @FXML
    private void openSignUp() throws IOException {
        App.setRoot("signup");
    }

}
