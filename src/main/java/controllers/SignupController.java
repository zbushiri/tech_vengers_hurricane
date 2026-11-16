package controllers;

import java.io.IOException;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.AccountRole;
import model.HurricaneReliefApp.RegistrationResult;
import techvengers_hurricane.App;

public class SignupController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private VBox volunteerFields;
    @FXML private TextField skillsField;
    @FXML private TextField equipmentField;
    @FXML private CheckBox transportationBox;
    @FXML private ComboBox<String> roleBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        roleBox.getItems().setAll("Resident", "Volunteer");
        volunteerFields.visibleProperty().bind(roleBox.valueProperty().isEqualTo("Volunteer"));
        volunteerFields.managedProperty().bind(volunteerFields.visibleProperty());
    }

    @FXML
    private void createAccount() throws IOException {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String selectedRole = roleBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isBlank() || selectedRole == null) {
            errorLabel.setText("Enter your name, username, email, and password, and choose an account type.");
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

        RegistrationResult result = App.getReliefApp().registerAccount(role, Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "username", username,
                "password", password,
                "email", email,
                "phoneNumber", phoneField.getText().trim(),
                "address", addressField.getText().trim(),
                "skills", skillsField.getText(),
                "equipment", equipmentField.getText(),
                "transportationAccess", transportationBox.isSelected()
        ));

        switch (result) {
            case CREATED -> backToLogin();
            case USERNAME_TAKEN -> errorLabel.setText("That username is taken. Choose a different one.");
            case EMAIL_TAKEN -> errorLabel.setText("That email is already in use for sign-in.");
            case INVALID_EMAIL -> errorLabel.setText("Enter a valid email address.");
            case INVALID_DETAILS -> errorLabel.setText("Choose a username with at least six characters.");
            case SAVE_FAILED -> errorLabel.setText("Could not save your account. Please try again.");
        }
    }

    @FXML
    private void backToLogin() throws IOException {
        App.setRoot("login");
    }
}
