package techvengers_hurricane;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.HurricaneReliefApp;

import java.io.IOException;

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
     * Loads the login FXML screen and displays the template application window.
     * @param stage JavaFX window to display
     * @throws IOException if the login FXML resource cannot be loaded
     */
    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Replaces the current scene contents with another FXML screen.
     * @param fxml resource name without the .fxml extension
     * @throws IOException if the requested FXML resource cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

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
