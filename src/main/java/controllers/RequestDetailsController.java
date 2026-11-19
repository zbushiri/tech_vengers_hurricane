package controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.ReliefRequest;
import model.RequestStatus;
import model.Shelter;

/**
 * One request's progress and details, with edit and cancel.
 * @author nate sheffield
 */
public class RequestDetailsController extends PageController {

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public RequestDetailsController() { }

    /**
     * Builds the progress and details cards.
     */
    @Override
    protected void build() {
        ReliefRequest request = Pages.selectedRequest;
        if (request == null) {
            body.getChildren().add(Pages.label("Choose a request from My requests.", "rh-muted"));
            return;
        }
        HBox heading = new HBox(12, Pages.label(Pages.typeName(request.getType()) + " · " + Pages.code(request), "pg-h2"),
                Pages.pill(Pages.statusName(request.getStatus()), Pages.statusTone(request.getStatus())));
        heading.setAlignment(Pos.CENTER_LEFT);

        VBox progress = Pages.card(Pages.label("Progress", "pg-h3"), timeline(request));

        Shelter going = Pages.shelter(request.getDestinationShelterId());
        String pets = request.getAnimalNotes() == null || request.getAnimalNotes().isBlank()
                ? "" : " + " + request.getAnimalNotes();
        GridPane rows = new GridPane();
        rows.setHgap(20);
        String[][] data = {
            {"Pickup", request.getLocation()},
            {"People", request.getNumberOfPeopleNeeded() + pets},
            {"Medical needs", Pages.medical(request)},
            {"Phone", Pages.phone(request)},
            {"Going to", going == null ? "Not chosen yet" : going.getName()},
        };
        for (int i = 0; i < data.length; i++) {
            Label name = Pages.label(data[i][0], "rh-muted", "pg-detail-row");
            name.setMinWidth(110);
            name.setMaxHeight(Double.MAX_VALUE);
            Label value = Pages.label(data[i][1], "rh-value", "pg-detail-row");
            value.setMaxWidth(Double.MAX_VALUE);
            rows.add(name, 0, i);
            rows.add(value, 1, i);
        }

        Button edit = new Button("Edit request");
        edit.getStyleClass().add("rh-btn-secondary");
        edit.setDisable(!request.isOpen());
        edit.setOnAction(e -> {
            Pages.editing = true;
            Pages.go("requestform");
        });
        Button cancel = new Button("Cancel request");
        cancel.getStyleClass().add("rh-btn-secondary");
        cancel.setDisable(!request.isOpen());
        cancel.setOnAction(e -> confirmCancel(request));
        VBox details = Pages.card(Pages.label("Details", "pg-h3"), rows, new HBox(12, edit, cancel));

        HBox cards = new HBox(18, progress, details);
        HBox.setHgrow(progress, Priority.ALWAYS);
        HBox.setHgrow(details, Priority.ALWAYS);
        progress.setPrefWidth(340);
        details.setPrefWidth(400);
        body.getChildren().addAll(heading, cards);
    }

    /**
     * Builds the Submitted, Assigned, Complete steps.
     * @param request the request
     * @return the timeline
     */
    private VBox timeline(ReliefRequest request) {
        RequestStatus status = request.getStatus();
        boolean assigned = status == RequestStatus.ASSIGNED || status == RequestStatus.IN_PROGRESS;
        boolean done = status == RequestStatus.FULFILLED || status == RequestStatus.COMPLETE;
        String helper = Pages.helper(request);
        VBox steps = new VBox(18);
        steps.getChildren().add(step("done", "Submitted", Pages.when(request.getDateSubmitted())));
        if (status == RequestStatus.CANCELLED) {
            steps.getChildren().add(step("current", "Cancelled", "You cancelled this request"));
            return steps;
        }
        steps.getChildren().add(step(done ? "done" : assigned ? "current" : "pending",
                assigned || done ? "Assigned to " + helper : "Assigned",
                assigned || done ? "A volunteer is on the way" : "Waiting for a volunteer"));
        steps.getChildren().add(step(done ? "done" : "pending", "Complete",
                done ? "Done" : helper.equals("Pending") ? "Pending" : helper + " marks this when you arrive"));
        return steps;
    }

    /**
     * Builds one timeline step.
     * @param state done, current or pending
     * @param title step name
     * @param detail smaller text
     * @return the step
     */
    private static HBox step(String state, String title, String detail) {
        Label dot = new Label(state.equals("done") ? "✓" : state.equals("current") ? "●" : "");
        dot.getStyleClass().addAll("pg-step-dot", "pg-step-" + state);
        HBox row = new HBox(12, dot, new VBox(2, Pages.label(title, "rh-value"), Pages.label(detail, "pg-small")));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Shows the "Cancel this request?" pop-up over the page.
     * @param request the request to cancel
     */
    private void confirmCancel(ReliefRequest request) {
        String helper = Pages.helper(request);
        String text = (helper.equals("Pending") ? "" : helper + " will be told you no longer need help. ")
                + "You can submit a new request anytime.";
        Button keep = new Button("Keep request");
        keep.getStyleClass().add("rh-btn-secondary");
        Button cancel = new Button("Cancel request");
        cancel.getStyleClass().add("rh-btn-primary");
        HBox buttons = new HBox(12, keep, cancel);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox dialog = new VBox(12, Pages.label("Cancel this request?", "pg-h2"), Pages.label(text, "rh-muted"), buttons);
        dialog.getStyleClass().add("pg-dialog");
        dialog.setMaxWidth(420);
        dialog.setMaxHeight(VBox.USE_PREF_SIZE);
        StackPane overlay = new StackPane(dialog);
        overlay.getStyleClass().add("pg-overlay");
        keep.setOnAction(e -> root.getChildren().remove(overlay));
        cancel.setOnAction(e -> {
            Pages.app().cancelRequest(request.getRequestId());
            Pages.save();
            Pages.go("myrequests");
        });
        root.getChildren().add(overlay);
    }
}
