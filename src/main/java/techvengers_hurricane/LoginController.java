package techvengers_hurricane;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Handles navigation from the login screen to the home screen.
 * @author Team Tech-vengers
 */
public class LoginController {
    /**
     * Creates the controller for the login screen.
     */
    public LoginController() { }


    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
}
