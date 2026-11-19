package controllers;

import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.ReliefRequest;
import model.VolunteerProfile;

/**
 * Volunteer list of open resident requests that can be reviewed and claimed.
 * @author nate sheffield
 */
public class OpenRequestsController extends PageController {
    private final VBox list = new VBox(18);
    private final Label message = RequestCards.message();

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public OpenRequestsController() { }

    /**
     * Builds the page.
     */
    @Override
    protected void build() {
        eyebrowLabel.setText("Resident requests ready for response");
        body.getChildren().addAll(message, list);
        refresh();
    }

    /**
     * Redraws the summary and request cards.
     */
    private void refresh() {
        draw();
        Pages.fit(list);
    }

    /**
     * Builds the summary and cards.
     */
    private void draw() {
        list.getChildren().clear();
        List<ReliefRequest> open = Pages.app().getOpenRequestsForVolunteers();
        VolunteerProfile me = Pages.user() == null ? null : Pages.user().getVolunteerProfile();
        boolean available = me != null && me.canClaimRequests();
        list.getChildren().add(RequestCards.summary(
                open.isEmpty() ? "No resident requests available"
                        : open.size() + (open.size() == 1 ? " resident request available" : " resident requests available"),
                "Only requests submitted by residents appear here",
                Pages.pill(available ? "● Available for assignments" : "Not available to claim", available ? "green" : "gold")));
        if (open.isEmpty()) {
            list.getChildren().add(RequestCards.empty("Nothing waiting right now",
                    "When a resident submits a request, it will appear here for review.", null));
            return;
        }
        for (ReliefRequest request : open) {
            Button claim = new Button("Claim request");
            claim.getStyleClass().add("rh-btn-primary");
            claim.setOnAction(e -> {
                if (Pages.app().claimRequest(request.getRequestId())) {
                    Pages.save();
                    RequestCards.say(message, "Request claimed. Find it in My assignments to choose a shelter.");
                } else {
                    RequestCards.say(message, "You must be available and have a verified background check to claim requests.");
                }
                refresh();
            });
            list.getChildren().add(RequestCards.card(request, "Resident submitted", new javafx.scene.Node[0], claim));
        }
    }
}
