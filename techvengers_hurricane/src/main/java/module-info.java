module techvengers_hurricane {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires javafx.web;
    requires json.simple;
    
    opens techvengers_hurricane to javafx.fxml;
    exports techvengers_hurricane;

    opens model to javafx.fxml;
    exports model;
    exports location;
}
