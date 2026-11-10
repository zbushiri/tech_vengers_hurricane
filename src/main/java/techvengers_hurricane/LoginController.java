package techvengers_hurricane;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Handles navigation from the secondary JavaFX template screen to the primary screen.
 * @author Team Tech-vengers
 */
public class LoginController {
    /**
     * Creates the controller for the secondary template screen.
     */
    public LoginController() { }


    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}