package controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Accommodation;
import model.Shelter;
import model.ShelterStatus;

/**
 * Searchable shelter list with filters and a preview of the selected shelter.
 * @author nate sheffield
 */
public class SheltersController extends PageController {
    private TextField search;
    private ToggleButton openOnly, petsOnly, medicalOnly;
    private ComboBox<String> distanceBox;
    private Label openCount;
    private VBox list;
    private VBox detail;

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    public SheltersController() { }

    /**
     * Builds the heading, filters and list.
     */
    @Override
    protected void build() {
        openCount = Pages.pill("", "green");
        VBox titles = new VBox(2, Pages.label("Available emergency shelter locations", "rh-eyebrow"),
                Pages.label("Find a safe place nearby", "pg-h2"));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox heading = new HBox(titles, gap, openCount);
        heading.setAlignment(Pos.CENTER_LEFT);

        search = new TextField();
        search.setPromptText("Search by shelter name or address");
        search.getStyleClass().add("pg-input");
        HBox.setHgrow(search, Priority.ALWAYS);
        openOnly = toggle("✓ Open only");
        petsOnly = toggle("Pets allowed");
        medicalOnly = toggle("Medical care");
        distanceBox = new ComboBox<>(FXCollections.observableArrayList(
                "Any distance", "Within 3 miles", "Within 5 miles", "Within 10 miles"));
        distanceBox.setValue("Any distance");
        distanceBox.setOnAction(e -> refresh());
        search.textProperty().addListener((o, a, b) -> refresh());
        search.setMaxWidth(Double.MAX_VALUE);
        HBox toggles = new HBox(12, openOnly, petsOnly, medicalOnly, distanceBox);
        toggles.setAlignment(Pos.CENTER_LEFT);
        VBox filters = new VBox(10, search, toggles);
        filters.getStyleClass().add("pg-filter-bar");

        list = new VBox(12);
        list.setPrefWidth(320);
        list.setMinWidth(280);
        detail = new VBox();
        HBox.setHgrow(detail, Priority.ALWAYS);
        HBox grid = new HBox(18, list, detail);

        body.getChildren().addAll(heading, filters, grid);
        refresh();
        Pages.loadDistances(this::refresh);
    }

    /**
     * Makes a filter toggle.
     * @param text toggle text
     * @return the toggle
     */
    private ToggleButton toggle(String text) {
        ToggleButton toggle = new ToggleButton(text);
        toggle.getStyleClass().add("pg-toggle");
        toggle.setOnAction(e -> refresh());
        return toggle;
    }

    /**
     * Applies the filters and redraws the list and preview.
     */
    private void refresh() {
        String query = search.getText().trim().toLowerCase();
        double max = switch (distanceBox.getValue()) {
            case "Within 3 miles" -> 3;
            case "Within 5 miles" -> 5;
            case "Within 10 miles" -> 10;
            default -> Double.MAX_VALUE;
        };
        List<Shelter> shown = new ArrayList<>();
        for (Shelter shelter : Pages.shelters()) {
            Double miles = Pages.miles(shelter);
            boolean matches = query.isEmpty() || shelter.getName().toLowerCase().contains(query)
                    || shelter.getAddress().toLowerCase().contains(query);
            if (matches && (max == Double.MAX_VALUE || (miles != null && miles <= max))
                    && (!openOnly.isSelected() || Pages.isOpen(shelter))
                    && (!petsOnly.isSelected() || shelter.getAccommodations().contains(Accommodation.PET_FRIENDLY))
                    && (!medicalOnly.isSelected() || shelter.getAccommodations().contains(Accommodation.MEDICAL_SUPPORT))) {
                shown.add(shelter);
            }
        }
        shown.sort(Comparator.comparing(s -> Pages.miles(s) == null ? Double.MAX_VALUE : Pages.miles(s)));
        openCount.setText(shown.stream().filter(Pages::isOpen).count() + " open");

        if (!shown.contains(Pages.selectedShelter)) {
            Pages.selectedShelter = shown.isEmpty() ? null : shown.get(0);
        }
        list.getChildren().clear();
        for (Shelter shelter : shown) {
            VBox meta = new VBox(statusPill(shelter));
            meta.setAlignment(Pos.TOP_RIGHT);
            meta.setMinWidth(Region.USE_PREF_SIZE);
            VBox text = new VBox(6, Pages.label(shelter.getName(), "pg-h3"), Pages.label(shelter.getAddress(), "rh-muted"),
                    Pages.label(Pages.distance(shelter), "rh-value"));
            HBox.setHgrow(text, Priority.ALWAYS);
            HBox option = new HBox(12, text, meta);
            option.getStyleClass().add("pg-option");
            if (shelter == Pages.selectedShelter) option.getStyleClass().add("pg-option-selected");
            option.setOnMouseClicked(e -> {
                Pages.selectedShelter = shelter;
                refresh();
            });
            list.getChildren().add(option);
        }
        if (shown.isEmpty()) {
            VBox empty = Pages.card(Pages.label("No shelters match your search", "pg-h3"),
                    Pages.label("Try another name, increase the distance, or turn off a service filter.", "rh-muted"));
            empty.setAlignment(Pos.CENTER);
            empty.setMinHeight(220);
            list.getChildren().add(empty);
        }
        showDetail(Pages.selectedShelter);
        Pages.fit(body);
    }

    /**
     * Shows the preview card for the selected shelter.
     * @param shelter the shelter, or null
     */
    private void showDetail(Shelter shelter) {
        detail.getChildren().clear();
        if (shelter == null) return;
        StackPane map = new StackPane(Pages.label("Route preview"));
        map.getStyleClass().add("pg-map");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox heading = new HBox(new VBox(2, Pages.label(Pages.distance(shelter) + " away", "rh-kicker"),
                Pages.label(shelter.getName(), "pg-h3")), gap, statusPill(shelter));

        HBox facts = new HBox(12, Pages.label(shelter.getAvailableSpaces() + " beds left", "rh-value"));
        facts.setAlignment(Pos.CENTER_LEFT);
        facts.getChildren().add(shelter.getAccommodations().contains(Accommodation.PET_FRIENDLY)
                ? Pages.pill("Pets allowed", "grey") : Pages.label("No pets listed", "pg-small"));
        if (shelter.getAccommodations().contains(Accommodation.MEDICAL_SUPPORT)) {
            facts.getChildren().add(Pages.pill("Medical care", "blue"));
        }

        Label copied = Pages.label("", "pg-small");
        Button directions = new Button("Get directions");
        directions.getStyleClass().add("rh-btn-primary");
        directions.setOnAction(e -> {
            Pages.copyDirections(shelter);
            copied.setText("Google Maps directions link copied. Paste it into your browser.");
        });
        Button more = new Button("View shelter details");
        more.getStyleClass().add("rh-btn-secondary");
        more.setOnAction(e -> {
            Pages.selectedShelter = shelter;
            Pages.go("shelterdetails");
        });

        VBox content = new VBox(10, heading, Pages.label(shelter.getAddress(), "rh-muted"), facts,
                new HBox(8, directions, more), copied);
        content.setStyle("-fx-padding: 20;");
        VBox card = new VBox(map, content);
        card.getStyleClass().add("rh-card");
        card.setStyle("-fx-padding: 0;");
        detail.getChildren().add(card);
    }

    /**
     * Open, Full or Closed pill.
     * @param shelter the shelter
     * @return the pill
     */
    static Label statusPill(Shelter shelter) {
        ShelterStatus status = shelter.getStatus();
        if (status == ShelterStatus.OPEN) return Pages.pill("Open", "green");
        if (status == ShelterStatus.FULL) return Pages.pill("Full", "gold");
        return Pages.pill("Closed", "alert");
    }
}
