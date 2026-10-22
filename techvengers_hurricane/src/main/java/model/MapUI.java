package model;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import location.Address;
import location.Coordinates;
import location.DrivingRoute;
import location.MapTools;

/** Opens a map and finds the closest shelter. */
public class MapUI extends Application {
    private final Label status = new Label("Finding the closest shelter...");
    private final TextArea directions = new TextArea();
    private final Canvas map = new Canvas(860, 420);

    @Override
    public void start(Stage stage) {
        directions.setEditable(false);
        directions.setPrefRowCount(6);

        VBox page = new VBox(10, status, map, directions);
        page.setPadding(new Insets(12));
        VBox.setVgrow(directions, Priority.ALWAYS);

        stage.setTitle("Hurricane Shelter Map");
        stage.setScene(new Scene(page, 900, 650));
        stage.show();

        Thread worker = new Thread(this::loadShelter);
        worker.setDaemon(true);
        worker.start();
    }

    /** Gets the shelter information without freezing the window. */
    private void loadShelter() {
        Address userAddress = new Address("1 Vendue Range", "Charleston", "SC", "29401");
        Address[] shelters = {
                new Address("2950 Carner Avenue", "North Charleston", "SC", "29405"),
                new Address("5200 Lackawanna Boulevard", "North Charleston", "SC", "29405"),
                new Address("3841 Leeds Avenue", "North Charleston", "SC", "29405"),
                new Address("3625 Ashley Phosphate Road", "North Charleston", "SC", "29418"),
                new Address("6401 Dorchester Road", "North Charleston", "SC", "29418"),
                new Address("1624 State Road", "Summerville", "SC", "29483"),
                new Address("1053 Russellville Road", "St. Stephen", "SC", "29479"),
                new Address("200 Macedonia Foxes Circle", "St. Stephen", "SC", "29479"),
                new Address("101 Westview Boulevard", "Goose Creek", "SC", "29445"),
                new Address("1101 Boone Hill Road", "Summerville", "SC", "29483")
        };

        try {
            MapTools tools = MapTools.getInstance();
            Address shelter = tools.findClosestAddress(userAddress, shelters);
            String[] steps = tools.getDrivingDirections(userAddress, shelter);
            DrivingRoute route = tools.getDrivingRoute(userAddress, shelter);

            Platform.runLater(() -> {
                status.setText("Closest shelter: " + shelter);
                directions.setText(String.join("\n", steps));
                drawRoute(route);
            });
        } catch (Exception e) {
            Platform.runLater(() -> status.setText("Could not load the map: " + e.getMessage()));
        }
    }

    /** Draws the route using the coordinates returned by the map API. */
    private void drawRoute(DrivingRoute route) {
        GraphicsContext pen = map.getGraphicsContext2D();
        pen.setFill(Color.web("#e8f2e8"));
        pen.fillRect(0, 0, map.getWidth(), map.getHeight());

        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = -Double.MAX_VALUE;

        for (Coordinates point : route.getRoutePoints()) {
            minLat = Math.min(minLat, point.getLatitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLon = Math.max(maxLon, point.getLongitude());
        }

        double padding = 35;
        double latRange = Math.max(maxLat - minLat, 0.001);
        double lonRange = Math.max(maxLon - minLon, 0.001);
        pen.setStroke(Color.web("#1565c0"));
        pen.setLineWidth(5);

        Coordinates previous = null;
        for (Coordinates point : route.getRoutePoints()) {
            double x = padding + (point.getLongitude() - minLon) / lonRange
                    * (map.getWidth() - padding * 2);
            double y = map.getHeight() - padding - (point.getLatitude() - minLat) / latRange
                    * (map.getHeight() - padding * 2);
            if (previous != null) {
                double oldX = padding + (previous.getLongitude() - minLon) / lonRange
                        * (map.getWidth() - padding * 2);
                double oldY = map.getHeight() - padding - (previous.getLatitude() - minLat) / latRange
                        * (map.getHeight() - padding * 2);
                pen.strokeLine(oldX, oldY, x, y);
            }
            previous = point;
        }

        pen.setFill(Color.web("#2e7d32"));
        pen.fillOval(padding - 7, map.getHeight() - padding - 7, 14, 14);
        pen.fillText("Start", padding + 10, map.getHeight() - padding + 5);
        pen.setFill(Color.web("#c62828"));
        pen.fillOval(map.getWidth() - padding - 7, padding - 7, 14, 14);
        pen.fillText("Shelter", map.getWidth() - padding - 55, padding - 12);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
