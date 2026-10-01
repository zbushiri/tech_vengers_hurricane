package com.techvengershurricane;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        /* TODO UI: replace this with a facade call. */
        App.setRoot("secondary");
    }
}
