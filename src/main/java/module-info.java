module com.example.javafx_test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.eclipse.milo.opcua.stack.core;
    requires reload4j;
    requires org.eclipse.milo.opcua.sdk.client;
    requires java.sql;
    requires org.slf4j;
    requires com.google.common;
    requires com.google.gson;


    opens com.example.javafx_test to javafx.fxml;
    exports com.example.javafx_test;
    exports jsoncomms;
    opens jsoncomms to javafx.fxml;
}