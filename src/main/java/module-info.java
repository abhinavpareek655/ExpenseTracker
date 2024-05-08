module com.example.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;


    opens com.example.expensetracker to javafx.fxml;
    exports com.example.expensetracker;
}