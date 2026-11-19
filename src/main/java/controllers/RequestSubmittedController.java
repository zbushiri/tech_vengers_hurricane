package controllers;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.ReliefRequest;

/**
 * Confirmation shown after a resident submits a request.
 * @author nate sheffield
 */
public class RequestSubmittedController extends PageController {

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public RequestSubmittedController() { }

    /**
     * Builds the confirmation card.
     */
    @Override
    protected void build() {
        ReliefRequest request = Pages.selectedRequest;
        Label check = Pages.label("✓", "pg-check");
        Label title = Pages.label("Your request was sent", "pg-h2");
        Label text = Pages.label("We'll text you when a volunteer accepts your request.", "rh-muted");

        GridPane details = new GridPane();
        details.setHgap(40);
        if (request != null) {
            addRow(details, 0, "Request type", Pages.label(Pages.typeName(request.getType()), "rh-value"));
            addRow(details, 1, "Request ID", Pages.label(Pages.code(request), "rh-value"));
            addRow(details, 2, "Status", Pages.pill(Pages.statusName(request.getStatus()),
                    Pages.statusTone(request.getStatus())));
        }

        Button view = new Button("View my request");
        view.getStyleClass().add("rh-btn-primary");
        view.setDisable(request == null);
        view.setOnAction(e -> Pages.go("requestdetails"));
        Button home = new Button("Back to home");
        home.getStyleClass().add("rh-btn-secondary");
        home.setOnAction(e -> Pages.go("rolehome"));

        VBox card = Pages.card(check, title, text, details, new HBox(12, view, home));
        card.setSpacing(14);
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(560);
        body.getChildren().add(card);
    }

    /**
     * Adds one detail row.
     * @param grid the grid
     * @param row row number
     * @param name left text
     * @param value right node
     */
    private static void addRow(GridPane grid, int row, String name, Node value) {
        Label left = Pages.label(name, "rh-muted");
        left.getStyleClass().add("pg-detail-row");
        left.setMinWidth(160);
        HBox right = new HBox(value);
        right.getStyleClass().add("pg-detail-row");
        right.setMinWidth(260);
        grid.add(left, 0, row);
        grid.add(right, 1, row);
    }
}
