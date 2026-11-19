package controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.ReliefRequest;
import model.Shelter;

/**
 * Volunteer's claimed requests, where a shelter is chosen for each one.
 * @author nate sheffield
 */
public class MyAssignmentsController extends PageController {
    private final VBox list = new VBox(18);
    private final Label message = RequestCards.message();

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public MyAssignmentsController() { }

    /**
     * Builds the page.
     */
    @Override
    protected void build() {
        eyebrowLabel.setText("Requests you claimed");
        body.getChildren().addAll(message, list);
        refresh();
    }

    /**
     * Redraws the assignment cards.
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
        List<ReliefRequest> mine = Pages.app().getMyAssignments();
        list.getChildren().add(RequestCards.summary(
                mine.size() + (mine.size() == 1 ? " assignment in progress" : " assignments in progress"),
                "Choose a shelter for each household you are helping",
                Pages.pill("● On assignment", "green")));
        if (mine.isEmpty()) {
            Button open = new Button("Open requests");
            open.getStyleClass().add("rh-btn-primary");
            open.setOnAction(e -> Pages.go("openrequests"));
            list.getChildren().add(RequestCards.empty("No assignments yet",
                    "Claim a request from Open requests to see it here.", open));
            return;
        }
        for (ReliefRequest request : mine) {
            Shelter current = Pages.shelter(request.getDestinationShelterId());
            List<Shelter> choices = new ArrayList<>();
            for (Shelter shelter : Pages.shelters()) {
                if (shelter == current || shelter.canAccommodate(request)) choices.add(shelter);
            }
            ComboBox<Shelter> picker = new ComboBox<>(FXCollections.observableArrayList(choices));
            picker.setPromptText("Choose a shelter");
            picker.setConverter(new StringConverter<>() {
                @Override public String toString(Shelter shelter) { return shelter == null ? "" : shelter.getName(); }
                @Override public Shelter fromString(String text) { return null; }
            });
            picker.setValue(current);
            picker.setMaxWidth(Double.MAX_VALUE);

            Button assign = new Button(current == null ? "Assign to shelter" : "Update shelter assignment");
            assign.getStyleClass().add(current == null ? "rh-btn-primary" : "rh-btn-secondary");
            assign.disableProperty().bind(picker.valueProperty().isNull());
            assign.setOnAction(e -> {
                Shelter chosen = picker.getValue();
                if (Pages.app().selectShelter(request.getRequestId(), chosen)) {
                    Pages.save();
                    RequestCards.say(message, "Request assigned to " + chosen.getName() + ".");
                } else {
                    RequestCards.say(message, chosen.getName() + " can't take this household. Choose another shelter.");
                }
                refresh();
            });

            VBox panel = new VBox(6, Pages.label("Assign this request to a shelter", "rh-value"), picker);
            if (current != null) panel.getChildren().add(RequestCards.fact("ASSIGNED SHELTER", current.getName()));
            if (choices.isEmpty()) panel.getChildren().add(Pages.label("No shelter has room for this household right now.", "pg-error-text"));
            panel.getStyleClass().add("pg-review");
            HBox status = new HBox(Pages.pill(Pages.statusName(request.getStatus()), Pages.statusTone(request.getStatus())));
            status.setAlignment(Pos.CENTER_LEFT);
            list.getChildren().add(RequestCards.card(request, "Volunteer assigned", new Node[] {status, panel}, assign));
        }
    }
}
