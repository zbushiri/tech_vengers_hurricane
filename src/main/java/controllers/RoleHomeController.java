package controllers;

import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.AccountRole;
import model.ReliefRequest;
import model.Shelter;
import model.User;
import techvengers_hurricane.App;

/**
 * Shows the home screen for a logged-in resident or volunteer.
 * @author nate sheffield
 */
public class RoleHomeController {
    @FXML private Label initialsLabel, sideNameLabel, sideEmailLabel, eyebrowLabel, titleLabel, badgeLabel;
    @FXML private Label welcomeLabel, usernameLabel, emailLabel, phoneLabel, addressLabel, typeLabel;
    @FXML private Label sideKickerLabel, sideTitleLabel, sidePillLabel, sideBodyLabel, sideNoteLabel;
    @FXML private Label bottomKickerLabel, bottomTitleLabel, bottomPillLabel, bottomBodyLabel;
    @FXML private Button navSecond, sideButton, primaryButton, secondaryButton;

    private ReliefRequest openRequest;

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public RoleHomeController() { }

    /**
     * Fills in the account card and the cards for the user's role.
     */
    @FXML
    private void initialize() {
        Platform.runLater(this::growWindow);
        User user = App.getReliefApp().getCurrentUser();
        if (user == null) {
            welcomeLabel.setText("No user is logged in.");
            return;
        }
        String first = user.getFirstName();
        String last = user.getLastName();
        initialsLabel.setText(initial(first) + initial(last));
        sideNameLabel.setText(first + " " + last);
        sideEmailLabel.setText(user.getEmail());
        welcomeLabel.setText("Welcome, " + first);
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        phoneLabel.setText(user.getPhoneNumber());
        addressLabel.setText(user.getAddress());

        if (user.hasRole(AccountRole.VOLUNTEER)) {
            showVolunteer(user);
        } else {
            showResident();
        }
    }

    /**
     * Sets up the screen for a resident: nearest shelter and request help cards.
     */
    private void showResident() {
        eyebrowLabel.setText("Emergency relief resources");
        titleLabel.setText("Resident Home");
        badgeLabel.setText("Resident");
        typeLabel.setText("Resident");
        navSecond.setText("My Requests");

        sideKickerLabel.setText("NEAREST SHELTER");
        sideTitleLabel.setText("Finding shelters...");
        setPill(sidePillLabel, "Verify status", "rh-pill-gold");
        sideBodyLabel.setText("");
        sideNoteLabel.setText("Shelters open during emergencies. Confirm status before traveling.");
        sideButton.setText("View shelters");
        loadNearestShelter();

        bottomKickerLabel.setText("RELIEF REQUESTS");
        int active = App.getReliefApp().getMyRequests().size();
        bottomTitleLabel.setText(active == 0 ? "Do you need help?" : "You have " + active + " request(s)");
        bottomPillLabel.setVisible(false);
        bottomBodyLabel.setText("Ask the relief network for food, transportation, medical assistance, or emergency supplies.");
        primaryButton.setText("Request help");
        secondaryButton.setText("View my requests");
    }

    /**
     * Sets up the screen for a volunteer: status and open request cards.
     * @param user the logged-in volunteer
     */
    private void showVolunteer(User user) {
        eyebrowLabel.setText("Resident requests ready for response");
        titleLabel.setText("Volunteer Home");
        badgeLabel.setText("Relief volunteer");
        typeLabel.setText("Volunteer");
        navSecond.setText("Assignments");

        boolean canClaim = user.getVolunteerProfile() != null && user.getVolunteerProfile().canClaimRequests();
        sideKickerLabel.setText("MY STATUS");
        sideTitleLabel.setText(canClaim ? "Available for assignments" : "Not able to claim yet");
        setPill(sidePillLabel, canClaim ? "● Active" : "Pending", canClaim ? "rh-pill-green" : "rh-pill-gold");
        sideBodyLabel.setText(canClaim ? "" : "You must be available and have a verified background check.");
        int assigned = App.getReliefApp().getMyAssignments().size();
        sideNoteLabel.setText(assigned + " assignment(s) in progress\nClaimed requests will show here.");
        sideButton.setText("Go unavailable");

        bottomKickerLabel.setText("OPEN REQUESTS");
        secondaryButton.setText("View all requests");
        showOpenRequest();
    }

    /**
     * Shows the most urgent open request, or an empty message if there is none.
     */
    private void showOpenRequest() {
        List<ReliefRequest> open = App.getReliefApp().getOpenRequestsForVolunteers();
        openRequest = open.isEmpty() ? null : open.get(0);
        if (openRequest == null) {
            bottomTitleLabel.setText("Nothing waiting right now");
            bottomPillLabel.setVisible(false);
            bottomBodyLabel.setText("When a resident submits a request, it will appear here.");
            primaryButton.setVisible(false);
            return;
        }
        bottomTitleLabel.setText(String.valueOf(openRequest.getType()));
        setPill(bottomPillLabel, openRequest.getPriority() + " priority", "rh-pill-alert");
        bottomBodyLabel.setText(openRequest.getLocation() + " · " + openRequest.getNumberOfPeopleNeeded()
                + " people · " + openRequest.getNumberOfAnimals() + " pets\n" + openRequest.getDescription());
        primaryButton.setText("Claim request");
    }

    /**
     * Looks up the nearest shelter off the UI thread so a slow map lookup does not freeze the screen.
     */
    private void loadNearestShelter() {
        Thread lookup = new Thread(() -> {
            Shelter nearest = null;
            try {
                List<Shelter> shelters = App.getReliefApp().findNearestShelters("", 0);
                if (shelters != null && !shelters.isEmpty()) nearest = shelters.get(0);
            } catch (RuntimeException e) {
                nearest = null;
            }
            Shelter found = nearest;
            Platform.runLater(() -> {
                sideTitleLabel.setText(found == null ? "No shelter found nearby" : found.getName());
                sideBodyLabel.setText(found == null ? "" : found.getAddress());
            });
        });
        lookup.setDaemon(true);
        lookup.start();
    }

    /**
     * Resident: opens the request flow (not built yet). Volunteer: claims the shown request.
     */
    @FXML
    private void primaryAction() {
        if (openRequest != null && App.getReliefApp().claimRequest(openRequest.getRequestId())) {
            App.getReliefApp().saveData();
            int assigned = App.getReliefApp().getMyAssignments().size();
            sideNoteLabel.setText(assigned + " assignment(s) in progress\nClaimed requests will show here.");
            showOpenRequest();
        }
    }

    /**
     * Logs out and returns to the welcome screen.
     * @throws IOException if the welcome screen cannot be loaded
     */
    @FXML
    private void logout() throws IOException {
        App.getReliefApp().logout();
        App.setRoot("home");
    }

    /**
     * Sets a pill label's text and color style.
     * @param pill the label to change
     * @param text the text to show
     * @param style the color style class
     */
    private void setPill(Label pill, String text, String style) {
        pill.setText(text);
        pill.getStyleClass().setAll("label", "rh-pill", style);
    }

    /**
     * Makes the window big enough for the sidebar layout.
     */
    private void growWindow() {
        if (titleLabel.getScene() == null) return;
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        if (stage.getWidth() < 960) stage.setWidth(960);
        if (stage.getHeight() < 640) stage.setHeight(640);
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
