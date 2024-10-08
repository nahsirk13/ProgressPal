module com.example.javafxclassfiles2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.javafxclassfiles2 to javafx.fxml;
    exports com.example.javafxclassfiles2;
}