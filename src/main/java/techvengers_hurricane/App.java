package techvengers_hurricane;

import java.io.IOException;
import java.util.ArrayList;

import controllers.MapController;
import location.Address;
import model.Shelter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.HurricaneReliefApp;

/**
 * Launches the original two-screen JavaFX template application.
 * @author Team Tech-vengers
 */
public class App extends Application {
    /**
     * Creates the JavaFX template application.
     */
    public App() { }


    private static Scene scene;
    private static HurricaneReliefApp reliefApp;

    /**
     * Returns the shared application facade, loading the JSON data the first time it is needed.
     * @return the facade used by every screen
     */
    public static HurricaneReliefApp getReliefApp() {
        if (reliefApp == null) {
            reliefApp = new HurricaneReliefApp();
            reliefApp.initialize();
        }
        return reliefApp;
    }

    /**
     * Loads the welcome screen and displays the application window.
     * @param stage JavaFX window to display
     * @throws IOException if the home FXML resource cannot be loaded
     */
    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("home"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Switches to the map screen and pins every shelter.
     * @throws IOException if map_view.fxml cannot be loaded
     */
    public static void showShelterMap() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("map_view.fxml"));
        Parent root = loader.load();
        MapController controller = loader.getController();
        scene.setRoot(root);

        ArrayList<Address> addresses = new ArrayList<>();
        for (Shelter shelter : getReliefApp().getShelters()) {
            Address address = Address.fromString(shelter.getAddress());
            if (address != null) addresses.add(address);
        }
        controller.showAddressesOnMap(addresses.toArray(new Address[0]));
    }
    
    /**
     * Replaces the current scene contents with another FXML screen.
     * @param fxml resource name without the .fxml extension
     * @throws IOException if the requested FXML resource cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    /**
     * Loads an FXML resource.
     * @param fxml the name of the FXML resource to load
     * @return the loaded parent node
     * @throws IOException if the FXML resource cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Launches the original JavaFX template application.
     * @param args command-line arguments; unused by this entry point
     */
    public static void main(String[] args) {
        launch();
    }

}
