package controllers;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Accommodation;
import model.Shelter;
import model.User;
import techvengers_hurricane.App;

/**
 * Full details for one shelter: availability, route, services and what to bring.
 * @author nate sheffield
 */
public class ShelterDetailsController extends PageController {

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public ShelterDetailsController() { }

    /**
     * Builds the shelter detail cards.
     */
    @Override
    protected void build() {
        Button back = new Button("← Back to shelters");
        back.getStyleClass().add("pg-text-button");
        back.setOnAction(e -> Pages.go("shelters"));
        body.getChildren().add(back);
        Shelter shelter = Pages.selectedShelter;
        if (shelter == null) return;
        boolean open = Pages.isOpen(shelter);
        boolean pets = shelter.getAccommodations().contains(Accommodation.PET_FRIENDLY);
        boolean medical = shelter.getAccommodations().contains(Accommodation.MEDICAL_SUPPORT);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox heading = new HBox(new VBox(2, Pages.label(Pages.distance(shelter) + " away", "rh-kicker"),
                Pages.label(shelter.getName(), "pg-h2")), gap, SheltersController.statusPill(shelter));
        int capacity = Math.max(1, shelter.getCapacity());
        int occupied = shelter.getOccupancy();
        ProgressBar bar = new ProgressBar(Math.min(1.0, occupied / (double) capacity));
        bar.setMaxWidth(Double.MAX_VALUE);
        Region availabilityGap = new Region();
        HBox.setHgrow(availabilityGap, Priority.ALWAYS);
        VBox availability = new VBox(8,
                new HBox(Pages.label("Current availability", "rh-muted"), availabilityGap,
                        Pages.label(open ? shelter.getAvailableSpaces() + " beds left" : "Not accepting residents", "rh-value")),
                bar, Pages.label(occupied + " of " + shelter.getCapacity() + " spaces currently occupied", "pg-small"));
        availability.getStyleClass().add("pg-review");
        Label copied = Pages.label("", "pg-small");
        Button directions = new Button(open ? "Get directions" : "Directions unavailable");
        directions.getStyleClass().add("rh-btn-primary");
        directions.setDisable(!open);
        directions.setOnAction(e -> {
            Pages.copyDirections(shelter);
            copied.setText("Google Maps directions link copied. Paste it into your browser.");
        });
        VBox overview = Pages.card(heading, Pages.label(shelter.getAddress(), "rh-muted"), availability, directions, copied);

        Button showRoute = new Button("Show route on map");
        showRoute.getStyleClass().add("rh-btn-primary");
        showRoute.setOnAction(e -> {
            try {
                App.showRouteMap(shelter);
            } catch (IOException ex) {
                copied.setText("Could not open the map.");
            }
        });
        StackPane map = new StackPane(showRoute);
        map.getStyleClass().add("pg-map");
        User user = Pages.user();
        VBox points = new VBox(10,
                new VBox(2, Pages.label("Your location", "pg-small"),
                        Pages.label(user == null ? "" : user.getDepartureAddress(), "rh-value")),
                new VBox(2, Pages.label("Destination", "pg-small"), Pages.label(shelter.getName(), "rh-value")));
        points.setStyle("-fx-padding: 14 18 14 18;");
        VBox route = new VBox(map, points);
        route.getStyleClass().add("rh-card");
        route.setStyle("-fx-padding: 0;");

        GridPane services = new GridPane();
        services.setHgap(10);
        services.setVgap(10);
        services.add(service("Pets allowed", pets), 0, 0);
        services.add(service("Medical care", medical), 1, 0);
        services.add(service("Wheelchair accessible",
                shelter.getAccommodations().contains(Accommodation.WHEELCHAIR_ACCESSIBLE)), 0, 1);
        VBox servicesCard = Pages.card(Pages.label("Services and accessibility", "pg-h3"),
                Pages.label("Confirm services with the shelter before traveling.", "rh-muted"), services);

        VBox bring = new VBox(4, Pages.label("• Photo ID if available", "rh-muted"),
                Pages.label("• Essential medication", "rh-muted"));
        if (pets) bring.getChildren().add(Pages.label("• Pet carrier or leash", "rh-muted"));
        VBox requirements = Pages.card(Pages.label("What to bring", "pg-h3"), bring);
        String verified = shelter.getLastUpdated() == null ? "recently"
                : shelter.getLastUpdated().format(DateTimeFormatter.ofPattern("MMM d 'at' h:mm a"));
        VBox warning = new VBox(3, Pages.label("⚠ Availability can change quickly", "rh-value"),
                Pages.label("Last verified " + verified + ". Call before traveling when possible.", "pg-small"));
        warning.getStyleClass().add("pg-warn");

        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(20);
        javafx.scene.layout.ColumnConstraints wide = new javafx.scene.layout.ColumnConstraints();
        wide.setPercentWidth(58);
        javafx.scene.layout.ColumnConstraints narrow = new javafx.scene.layout.ColumnConstraints();
        narrow.setPercentWidth(42);
        grid.getColumnConstraints().addAll(wide, narrow);
        grid.add(overview, 0, 0);
        grid.add(route, 1, 0);
        grid.add(servicesCard, 0, 1);
        grid.add(new VBox(12, requirements, warning), 1, 1);
        body.getChildren().add(grid);
    }

    /**
     * One service tile.
     * @param name service name
     * @param available whether the shelter offers it
     * @return the tile
     */
    private static HBox service(String name, boolean available) {
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        Label mark = Pages.label(available ? "✓" : "○", available ? "pg-ok" : "pg-small");
        HBox tile = new HBox(8, new VBox(2, Pages.label(name, "rh-value"),
                Pages.label(available ? "Available" : "Not listed", "pg-small")), gap, mark);
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.getStyleClass().add("pg-service");
        if (!available) tile.getStyleClass().add("pg-service-off");
        tile.setMinWidth(140);
        return tile;
    }
}
