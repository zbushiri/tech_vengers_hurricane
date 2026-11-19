package controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.ReliefRequest;

/**
 * Table of the resident's requests, with an empty state.
 * @author nate sheffield
 */
public class MyRequestsController extends PageController {
    private static final double[] WIDTHS = {28, 20, 16, 18, 18};

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public MyRequestsController() { }

    /**
     * Builds the requests table.
     */
    @Override
    protected void build() {
        showHeaderButton("Request help", () -> {
            Pages.editing = false;
            Pages.go("requestform");
        });

        List<ReliefRequest> requests = new ArrayList<>(Pages.app().getMyRequests());
        requests.sort(Comparator.comparing(ReliefRequest::getDateSubmitted,
                Comparator.nullsLast(Comparator.reverseOrder())));

        VBox table = new VBox();
        table.getStyleClass().add("pg-table");
        GridPane head = row(Pages.label("Request"), Pages.label("Submitted"), Pages.label("Helper"),
                Pages.label("Status"), Pages.label("Action"));
        head.getStyleClass().add("pg-table-head");
        table.getChildren().add(head);

        for (ReliefRequest request : requests) {
            Button view = new Button("View");
            view.getStyleClass().add("rh-btn-secondary");
            view.setOnAction(e -> {
                Pages.selectedRequest = request;
                Pages.go("requestdetails");
            });
            GridPane line = row(
                    Pages.label(Pages.typeName(request.getType()) + " · " + Pages.code(request), "rh-value"),
                    Pages.label(Pages.when(request.getDateSubmitted()), "rh-muted"),
                    Pages.label(Pages.helper(request), "rh-muted"),
                    Pages.pill(Pages.statusName(request.getStatus()), Pages.statusTone(request.getStatus())),
                    view);
            line.getStyleClass().add("pg-table-row");
            table.getChildren().add(line);
        }

        if (requests.isEmpty()) {
            VBox empty = new VBox(10, Pages.label("☰", "pg-empty-icon"),
                    Pages.label("You haven't submitted any requests yet", "pg-h2"),
                    Pages.label("Select Request help to submit your first request.", "rh-muted"));
            empty.setAlignment(Pos.CENTER);
            empty.setMinHeight(320);
            empty.getStyleClass().add("pg-table-row");
            table.getChildren().add(empty);
        }
        body.getChildren().add(table);
    }

    /**
     * Lays out one table row with fixed column widths.
     * @param cells the five cells
     * @return the row
     */
    private static GridPane row(Node... cells) {
        GridPane grid = new GridPane();
        for (int i = 0; i < cells.length; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(WIDTHS[i]);
            grid.getColumnConstraints().add(column);
            grid.add(cells[i], i, 0);
        }
        grid.setAlignment(Pos.CENTER_LEFT);
        return grid;
    }
}
