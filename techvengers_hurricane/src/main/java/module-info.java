module techvengers_hurricane {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    
    opens techvengers_hurricane to javafx.fxml;
    exports techvengers_hurricane;
}
