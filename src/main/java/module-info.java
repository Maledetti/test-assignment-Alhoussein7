module com.example.ai_for_data_science {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires commons.math3;
    requires json.simple;
    requires junit;
    requires com.opencsv;


    opens com.example.ai_for_data_science to javafx.fxml;
    exports com.example.ai_for_data_science;
    exports com.example.ai_for_