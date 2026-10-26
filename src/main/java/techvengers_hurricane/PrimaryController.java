package techvengers_hurricane;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Handles navigation from the primary JavaFX template screen to the secondary screen.
 * @author Team Tech-vengers
 */
public class PrimaryController {
    /**
     * Creates the controller for the primary template screen.
     */
    public PrimaryController() { }


    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
