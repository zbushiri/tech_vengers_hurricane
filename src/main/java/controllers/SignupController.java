package controllers;

import java.io.IOException;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.AccountRole;
import techvengers_hurricane.App;

public class SignupController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private ComboBox<String> roleBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        roleBox.getItems().setAll("Resident", "Volunteer");
    }

    @FXML
    private void createAccount() throws IOException {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String selectedRole = roleBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || password.isBlank() || selectedRole == null) {
            errorLabel.setText("Enter your name, username, and password, and choose an account type.");
            return;
        }

        if (!password.equals(confirmPasswordField.getText())) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (!password.equals(password.trim())) {
            errorLabel.setText("Remove any spaces at the beginning or end of your password.");
            return;
        }

        AccountRole role = "Volunteer".equals(selectedRole)
                ? AccountRole.VOLUNTEER : AccountRole.RESIDENT;

        boolean created = App.getReliefApp().createAccount(role, Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "username", username,
                "password", password
        ));

        if (!created) {
            errorLabel.setText("That username is taken. Choose a different one.");
            return;
        }

        backToLogin();
    }

    @FXML
    private void backToLogin() throws IOException {
        App.setRoot("login");
    }
}