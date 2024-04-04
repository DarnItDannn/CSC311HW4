module org.example.csc311hw4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires com.healthmarketscience.jackcess;
    requires com.google.gson;

    opens org.example.csc311hw4 to javafx.fxml, com.google.gson;
    exports org.example.csc311hw4;
}