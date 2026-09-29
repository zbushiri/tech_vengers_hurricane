module com.relief {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.relief to javafx.fxml;
    exports com.relief;
}
