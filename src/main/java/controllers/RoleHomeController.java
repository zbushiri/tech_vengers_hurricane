package controllers;

import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.AccountRole;
import model.AvailabilityStatus;
import model.ReliefRequest;
import model.Shelter;
import model.User;
import model.VolunteerProfile;
import techvengers_hurricane.App;

/**
 * Shows the home screen for a logged-in resident or volunteer.
 * @author nate sheffield
 */
public class RoleHomeController {
    @FXML private Label eyebrowLabel, titleLabel, badgeLabel;
    @FXML private Label welcomeLabel, usernameLabel, emailLabel, phoneLabel, addressLabel, typeLabel;
    @FXML private Label sideKickerLabel, sideTitleLabel, sidePillLabel, sideBodyLabel, sideNoteLabel;
    @FXML private Label bottomKickerLabel, bottomTitleLabel, bottomPillLabel, bottomBodyLabel;
    @FXML private Button sideButton, primaryButton, secondaryButton;

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
        Pages.current = "rolehome";
        Platform.runLater(this::growWindow);
        User user = App.getReliefApp().getCurrentUser();
        if (user == null) {
            welcomeLabel.setText("No user is logged in.");
            return;
        }
        String first = user.getFirstName();
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

        boolean canClaim = user.getVolunteerProfile() != null && user.getVolunteerProfile().canClaimRequests();
        sideKickerLabel.setText("MY STATUS");
        sideTitleLabel.setText(canClaim ? "Available for assignments" : "Not able to claim yet");
        setPill(sidePillLabel, canClaim ? "● Active" : "Pending", canClaim ? "rh-pill-green" : "rh-pill-gold");
        sideBodyLabel.setText(canClaim ? "" : "You must be available and have a verified background check.");
        int assigned = App.getReliefApp().getMyAssignments().size();
        sideNoteLabel.setText(assigned + " assignment(s) in progress\nClaimed requests will show here.");
        boolean available = user.getVolunteerProfile() != null
                && user.getVolunteerProfile().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;
        sideButton.setText(available ? "Go unavailable" : "Go available");

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
        bottomTitleLabel.setText(Pages.typeName(openRequest.getType()));
        setPill(bottomPillLabel, openRequest.getPriority().name().charAt(0) + openRequest.getPriority().name().substring(1).toLowerCase() + " priority", "rh-pill-alert");
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
     * Resident: opens the request form. Volunteer: claims the shown request.
     */
    @FXML
    private void primaryAction() {
        if (!Pages.isVolunteer()) {
            Pages.editing = false;
            Pages.go("requestform");
            return;
        }
        if (openRequest != null && App.getReliefApp().claimRequest(openRequest.getRequestId())) {
            App.getReliefApp().saveData();
            int assigned = App.getReliefApp().getMyAssignments().size();
            sideNoteLabel.setText(assigned + " assignment(s) in progress\nClaimed requests will show here.");
            showOpenRequest();
        }
    }

    /**
     * Resident: opens Find shelters. Volunteer: switches between available and unavailable.
     * @author nate sheffield
     */
    @FXML
    private void sideAction() {
        if (!Pages.isVolunteer()) {
            Pages.go("shelters");
            return;
        }
        VolunteerProfile me = Pages.user().getVolunteerProfile();
        me.setAvailability(me.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE
                ? AvailabilityStatus.UNAVAILABLE : AvailabilityStatus.AVAILABLE);
        Pages.save();
        Pages.go("rolehome");
    }

    /**
     * Resident: opens My requests. Volunteer: opens Open requests.
     * @author nate sheffield
     */
    @FXML
    private void secondaryAction() {
        Pages.go(Pages.isVolunteer() ? "openrequests" : "myrequests");
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
        pill.setMinWidth(Label.USE_PREF_SIZE);
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
}
