package techvengers_hurricane;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Handles navigation from the home screen to the login screen.
 * @author Team Tech-vengers
 */
public class HomeController {
    /**
     * Creates the controller for the home screen.
     */
    public HomeController() { }


    /**
     * Returns to the login screen.
     * @throws IOException if the login FXML resource cannot be loaded
     */
    @FXML
    private void goToLogin() throws IOException {
        App.setRoot("login");
    }
}
