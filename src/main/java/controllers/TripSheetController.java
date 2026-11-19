package controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import model.ReliefRequest;
import model.Shelter;

/**
 * Exports a printable trip sheet (pickup and shelter directions) for each assignment.
 * @author nate sheffield
 */
public class TripSheetController extends PageController {

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public TripSheetController() { }

    /**
     * Builds one card per assignment.
     */
    @Override
    protected void build() {
        eyebrowLabel.setText("Directions for your assignments");
        if (Pages.app().getMyAssignments().isEmpty()) {
            Button open = new Button("Open requests");
            open.getStyleClass().add("rh-btn-primary");
            open.setOnAction(e -> Pages.go("openrequests"));
            body.getChildren().add(RequestCards.empty("No trips yet",
                    "Claim a request and choose a shelter to make a trip sheet.", open));
            return;
        }
        for (ReliefRequest request : Pages.app().getMyAssignments()) {
            Shelter shelter = Pages.shelter(request.getDestinationShelterId());
            Label status = Pages.label(shelter == null ? "Choose a shelter in My assignments first." : "", "pg-small");
            TextArea sheet = new TextArea();
            sheet.setEditable(false);
            sheet.setWrapText(true);
            sheet.setPrefRowCount(12);
            sheet.getStyleClass().add("pg-trip");
            sheet.setVisible(false);
            sheet.setManaged(false);
            Button export = new Button("Export trip sheet");
            export.getStyleClass().add("rh-btn-primary");
            export.setDisable(shelter == null);
            export.setOnAction(e -> export(request, export, status, sheet));
            body.getChildren().add(Pages.card(
                    Pages.label(Pages.typeName(request.getType()) + " · " + Pages.code(request), "pg-h3"),
                    RequestCards.fact("PICKUP", request.getLocation()),
                    RequestCards.fact("SHELTER", shelter == null ? "Not chosen yet" : shelter.getName() + " · " + shelter.getAddress()),
                    export, status, sheet));
        }
    }

    /**
     * Saves the trip sheet in the background (it uses the map API) and shows it.
     * @param request the assignment
     * @param button the export button
     * @param status status text
     * @param sheet where the sheet is shown
     */
    private void export(ReliefRequest request, Button button, Label status, TextArea sheet) {
        button.setDisable(true);
        status.setText("Getting directions...");
        Thread worker = new Thread(() -> {
            String text;
            String note;
            try {
                Path file = Pages.app().exportTripSheet(request.getRequestId(), Path.of("tripsheets"));
                text = Files.readString(file);
                note = "Saved to " + file.toAbsolutePath();
            } catch (Exception ex) {
                text = null;
                note = "Could not make the trip sheet: " + ex.getMessage();
            }
            String shown = text;
            String saved = note;
            Platform.runLater(() -> {
                button.setDisable(false);
                status.setText(saved);
                if (shown != null) {
                    sheet.setText(shown);
                    sheet.setVisible(true);
                    sheet.setManaged(true);
                }
            });
        });
        worker.setDaemon(true);
        worker.start();
    }
}
