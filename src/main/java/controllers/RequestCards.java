package controllers;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.PriorityLevel;
import model.ReliefRequest;

/**
 * Builds the request cards that volunteers see on Open requests and My assignments.
 * @author nate sheffield
 */
final class RequestCards {
    private RequestCards() { }

    /**
     * Builds a request card with the Review request toggle; extra controls go at the bottom.
     * @param request the request
     * @param subtitle text after the resident's name
     * @param extras extra rows such as the shelter picker
     * @param actions buttons placed after Review request
     * @return the card
     */
    static VBox card(ReliefRequest request, String subtitle, Node[] extras, Button... actions) {
        PriorityLevel level = request.getPriority();
        String priority = level == null ? "Medium" : level.name().charAt(0) + level.name().substring(1).toLowerCase();
        boolean urgent = level == PriorityLevel.HIGH || level == PriorityLevel.CRITICAL;
        HBox label = new HBox(10, Pages.pill(priority + " priority", urgent ? "alert" : "gold"),
                Pages.label(Pages.requester(request) + " · " + subtitle, "rh-muted"));
        label.setAlignment(Pos.CENTER_LEFT);

        GridPane facts = new GridPane();
        facts.setHgap(40);
        facts.add(fact("HOUSEHOLD", Pages.household(request)), 0, 0);
        facts.add(fact("MEDICAL NEEDS", Pages.medical(request)), 1, 0);

        VBox reason = new VBox(3, Pages.label("Resident description", "rh-value"), Pages.label(Pages.story(request), "rh-muted"));
        reason.getStyleClass().add("pg-note-box");

        GridPane review = new GridPane();
        review.setHgap(30);
        review.setVgap(10);
        review.add(fact("PHONE", Pages.phone(request)), 0, 0);
        review.add(fact("SUBMITTED", Pages.when(request.getDateSubmitted())), 1, 0);
        review.add(fact("PICKUP LOCATION", request.getLocation()), 0, 1, 2, 1);
        review.add(fact("REQUEST TYPE", Pages.typeName(request.getType())), 0, 2);
        review.add(fact("HOUSEHOLD", Pages.household(request)), 1, 2);
        review.add(fact("STATUS", Pages.statusName(request.getStatus())), 0, 3);
        review.getStyleClass().add("pg-review");
        review.setVisible(false);
        review.setManaged(false);

        Button toggle = new Button("Review request");
        toggle.getStyleClass().add("rh-btn-secondary");
        toggle.setOnAction(e -> {
            boolean show = !review.isVisible();
            review.setVisible(show);
            review.setManaged(show);
            toggle.setText(show ? "Hide details" : "Review request");
        });
        HBox buttons = new HBox(10, toggle);
        buttons.getChildren().addAll(actions);

        VBox card = Pages.card(label, Pages.label(Pages.typeName(request.getType()), "pg-h2"),
                Pages.label(request.getLocation(), "rh-muted"), facts, reason, review);
        card.getChildren().addAll(extras);
        card.getChildren().add(buttons);
        card.setSpacing(10);
        card.setStyle(urgent ? "-fx-border-color: #dedede #dedede #dedede #b83d42; -fx-border-width: 1 1 1 4;" : "");
        return card;
    }

    /**
     * A small grey heading with a bold value under it.
     * @param name heading
     * @param value value
     * @return the fact
     */
    static VBox fact(String name, String value) {
        return new VBox(2, Pages.label(name, "rh-kicker"), Pages.label(value, "rh-value"));
    }

    /**
     * Grey "nothing here" card.
     * @param title big text
     * @param text small text
     * @param button optional button, or null
     * @return the card
     */
    static VBox empty(String title, String text, Button button) {
        VBox card = Pages.card(Pages.label("☰", "pg-empty-icon"), Pages.label(title, "pg-h3"), Pages.label(text, "rh-muted"));
        if (button != null) card.getChildren().add(button);
        card.setAlignment(Pos.CENTER);
        card.setMinHeight(220);
        return card;
    }

    /**
     * Summary bar shown at the top of the volunteer pages.
     * @param title bold text
     * @param text small text
     * @param right node on the right
     * @return the bar
     */
    static HBox summary(String title, String text, Node right) {
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bar = new HBox(new VBox(2, Pages.label(title, "rh-value"), Pages.label(text, "pg-small")), gap, right);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("pg-summary");
        return bar;
    }

    /**
     * Message label used after an action.
     * @return the label
     */
    static Label message() {
        Label label = Pages.label("", "rh-value");
        label.setVisible(false);
        label.setManaged(false);
        return label;
    }

    /**
     * Shows a message.
     * @param label the message label
     * @param text the text
     */
    static void say(Label label, String text) {
        label.setText(text);
        label.setVisible(true);
        label.setManaged(true);
    }
}
