package techvengers_hurricane;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * primary controller in the relief application.
 * @author Team Tech-vengers
 */
public class PrimaryController {
    /**
     * Creates a PrimaryController instance.
     */
    public PrimaryController() { }


    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
