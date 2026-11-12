package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;
import techvengers_hurricane.App;

/**
 * Shows the information for the user who is logged in.
 * @author Team Tech-vengers
 */
public class UserHomeController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label rolesLabel;

    /** Adds the logged-in user's information to the screen. */
    @FXML
    private void initialize() {
        User user = App.getReliefApp().getCurrentUser();

        if (user == null) {
            welcomeLabel.setText("No user is logged in.");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName());
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        phoneLabel.setText(user.getPhoneNumber());
        addressLabel.setText(user.getAddress());
        rolesLabel.setText(user.getRoles().toString());
    }

    /** Logs out and returns to the welcome screen. */
    @FXML
    private void logout() throws IOException {
        App.getReliefApp().logout();
        App.setRoot("home");
    }
}
