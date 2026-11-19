package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.User;
import techvengers_hurricane.App;

/**
 * Builds the shared sidebar with the links for the logged-in user's role.
 * @author nate sheffield
 */
public class SidebarController {
    @FXML private VBox navBox;
    @FXML private Label initialsLabel, nameLabel, emailLabel;

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public SidebarController() { }

    /**
     * Adds the role's links and the user's name to the sidebar.
     */
    @FXML
    private void initialize() {
        if (Pages.isVolunteer()) {
            link("Home", "rolehome");
            link("Open requests", "openrequests");
            link("My assignments", "myassignments");
            link("Trip sheet", "tripsheet");
            link("Find shelters", "shelters", "shelterdetails");
        } else {
            link("Home", "rolehome");
            link("My requests", "myrequests", "requestform", "requestsubmitted", "requestdetails");
            link("Find shelters", "shelters", "shelterdetails");
        }
        Button logout = new Button("Log Out");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.getStyleClass().add("rh-nav");
        logout.setOnAction(e -> logout());
        navBox.getChildren().add(logout);

        User user = Pages.user();
        if (user != null) {
            initialsLabel.setText(initial(user.getFirstName()) + initial(user.getLastName()));
            nameLabel.setText(user.getFirstName() + " " + user.getLastName());
            emailLabel.setText(user.getEmail());
        }
    }

    /**
     * Adds one link, highlighted when its page (or a sub-page) is showing.
     * @param text link text
     * @param page page to open
     * @param alsoActive other pages that highlight this link
     */
    private void link(String text, String page, String... alsoActive) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("rh-nav");
        boolean active = page.equals(Pages.current);
        for (String other : alsoActive) active |= other.equals(Pages.current);
        if (active) button.getStyleClass().add("rh-nav-active");
        button.setOnAction(e -> Pages.go(page));
        navBox.getChildren().add(button);
    }

    /**
     * Logs out and returns to the welcome screen.
     */
    private void logout() {
        Pages.app().logout();
        Pages.current = "rolehome";
        Pages.selectedRequest = null;
        Pages.selectedShelter = null;
        try {
            App.setRoot("home");
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /**
     * Returns the first letter of a name in upper case.
     * @param name the name
     * @return the initial, or an empty string
     */
    private static String initial(String name) {
        return name == null || name.isEmpty() ? "" : name.substring(0, 1).toUpperCase();
    }
}
