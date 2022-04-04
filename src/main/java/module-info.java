module com.example.java2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.java2 to javafx.fxml;
    exports com.example.java2;
}