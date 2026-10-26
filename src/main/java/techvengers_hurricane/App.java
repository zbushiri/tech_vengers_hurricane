package techvengers_hurricane;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    /**
     * Loads the primary FXML screen and displays the template application window.
     * @param stage JavaFX window to display
     * @throws IOException if the primary FXML resource cannot be loaded
     */
    @Override
    @SuppressWarnings("exports")
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Replaces the current scene contents with another FXML screen.
     * @param fxml resource name without the .fxml extension
     * @throws IOException if the requested FXML resource cannot be loaded
     */
    static void setRoot(String fxml) throws IOException {
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