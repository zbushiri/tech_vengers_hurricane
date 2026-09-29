module com.techvengershurricane {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.techvengershurricane to javafx.fxml;
    exports com.techvengershurricane;
}
