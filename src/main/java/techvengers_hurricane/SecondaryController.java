package techvengers_hurricane;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * secondary controller in the relief application.
 * @author Team Tech-vengers
 */
public class SecondaryController {
    /**
     * Creates a SecondaryController instance.
     */
    public SecondaryController() { }


    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}