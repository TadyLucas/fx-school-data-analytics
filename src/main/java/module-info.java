module com.example.fxdataanalytics {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fxdataanalytics to javafx.fxml;
    exports com.example.fxdataanalytics;
}