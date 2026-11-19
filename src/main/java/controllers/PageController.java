package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.User;

/**
 * Base class for the pages after login: fills in the header and asks the page to build its body.
 * @author nate sheffield
 */
public abstract class PageController {
    /** Whole page, used to show pop-up dialogs on top. */
    @FXML protected StackPane root;
    /** Small grey text above the title. */
    @FXML protected Label eyebrowLabel;
    /** Page title. */
    @FXML protected Label titleLabel;
    /** Logged-in user's name in the header. */
    @FXML protected Label userLabel;
    /** Optional button in the header. */
    @FXML protected Button headerButton;
    /** Page contents. */
    @FXML protected VBox body;

    /**
     * Creates the controller; JavaFX calls this when loading the FXML.
     */
    protected PageController() { }

    /**
     * Shows the user's name, then builds the page.
     */
    @FXML
    private void initialize() {
        User user = Pages.user();
        userLabel.setText(user == null ? "" : user.getFirstName() + " " + user.getLastName());
        build();
        Pages.fit(body);
    }

    /**
     * Shows the header button.
     * @param text button text
     * @param action what the button does
     */
    protected void showHeaderButton(String text, Runnable action) {
        headerButton.setText(text);
        headerButton.setVisible(true);
        headerButton.setManaged(true);
        headerButton.setOnAction(e -> action.run());
    }

    /**
     * Builds the page body.
     */
    protected abstract void build();
}
