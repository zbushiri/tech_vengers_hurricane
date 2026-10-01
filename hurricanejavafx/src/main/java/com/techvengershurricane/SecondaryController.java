package com.techvengershurricane;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        /* UI: returns to the starter screen. */
        App.setRoot("primary");
    }
}
