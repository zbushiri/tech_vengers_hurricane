package model;

import java.util.ArrayList; 

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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import location.Address;
import location.Coordinates;
import location.DrivingRoute;
import location.MapTools;

/**
 * Opens a map and finds the closest shelter.
 * @author Team Tech-vengers
 */
public class MapUI extends Application {
    /**
     * Creates the shelter-map window and its controls.
     */
    public MapUI() { }

    private final Label status = new Label("Finding the closest shelter...");
    private final TextArea directions = new TextArea();
    private final Canvas map = new Canvas(860, 420);

    /**
     * Opens the shelter-map window and loads driving directions in a background thread.
     * @param stage JavaFX window to display
     */
    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) {
        directions.setEditable(false);
        directions.setPrefRowCount(6);
        drawLoadingScreen();

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



    /** Finds the closest real shelter and gets directions to it. */
    private void loadShelter() {
        Address userAddress = new Address("80 Broad Street", "Charleston", "SC", "29401");
        
        try {
            ShelterList shelterList = DataLoader.getShelters(); 
            Shelter closest = shelterList.findClosestShelter(userAddress, new ArrayList<>(), 1);
            if (closest == null) {
                updateStatus("No open shelters.");
                return;
            }
            Address shelter = Address.fromString(closest.getAddress());

            MapTools tools = MapTools.getInstance();
            updateStatus("Loading directions to " + shelter + "...");
            String[] steps = tools.getDrivingDirections(userAddress, shelter);
            DrivingRoute route = tools.getDrivingRoute(userAddress, shelter);

            Platform.runLater(() -> {
                status.setText("Closest shelter: " + shelter);
                directions.setText(String.join("\n", steps));
                drawRoute(route);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                status.setText("Could not load the map.");
                directions.setText(e.getMessage());
                GraphicsContext pen = map.getGraphicsContext2D();
                pen.clearRect(0, 0, map.getWidth(), map.getHeight());
            });
        }
    }

    /** Shows that the map is loading. */
    private void drawLoadingScreen() {
        GraphicsContext pen = map.getGraphicsContext2D();
        pen.setFill(Color.web("#e8f2e8"));
        pen.fillRect(0, 0, map.getWidth(), map.getHeight());
        pen.setFill(Color.web("#345c48"));
        pen.setFont(new Font(20));
        pen.fillText("Loading shelter map...", 320, 210);
    }

    /** Changes the message at the top of the window. */
    private void updateStatus(String message) {
        Platform.runLater(() -> status.setText(message));
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

        if (route.getRoutePoints().isEmpty()) { status.setText("No route geometry returned."); return; }
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
        double startX = 0;
        double startY = 0;
        double endX = 0;
        double endY = 0;
        for (Coordinates point : route.getRoutePoints()) {
            double x = padding + (point.getLongitude() - minLon) / lonRange
                    * (map.getWidth() - padding * 2);
            double y = map.getHeight() - padding - (point.getLatitude() - minLat) / latRange
                    * (map.getHeight() - padding * 2);
            if (previous == null) {
                startX = x;
                startY = y;
            } else {
                double oldX = padding + (previous.getLongitude() - minLon) / lonRange
                        * (map.getWidth() - padding * 2);
                double oldY = map.getHeight() - padding - (previous.getLatitude() - minLat) / latRange
                        * (map.getHeight() - padding * 2);
                pen.strokeLine(oldX, oldY, x, y);
            }
            endX = x;
            endY = y;
            previous = point;
        }

        pen.setFill(Color.web("#2e7d32"));
        pen.fillOval(startX - 7, startY - 7, 14, 14);
        double startLabelX = startX + 10;
        if (startX > map.getWidth() / 2) {
            startLabelX = startX - 60;
        }
        pen.fillText("Start", startLabelX, startY + 5);
        pen.setFill(Color.web("#c62828"));
        pen.fillOval(endX - 7, endY - 7, 14, 14);
        double shelterLabelX = endX - 70;
        if (endX < map.getWidth() / 2) {
            shelterLabelX = endX + 10;
        }
        pen.fillText("Shelter", shelterLabelX, endY - 12);
    }

    /**
     * Launches the JavaFX shelter-map application.
     * @param args JavaFX launch arguments, including optional --from=address
     */
    public static void main(String[] args) {
        launch(args);
    }
}
