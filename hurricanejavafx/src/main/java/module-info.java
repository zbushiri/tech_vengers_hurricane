module com.techvengershurricane {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.techvengershurricane to javafx.fxml;
    opens com.techvengershurricane.model to com.google.gson;
    exports com.techvengershurricane;
    exports com.techvengershurricane.data;
    exports com.techvengershurricane.model;
    exports com.techvengershurricane.system;
}
