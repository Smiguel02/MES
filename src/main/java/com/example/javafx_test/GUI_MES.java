package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;;
import javafx.stage.Stage;


import java.io.IOException;

public class GUI_MES extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(GUI_MES.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1214, 716);
        HelloController controller = fxmlLoader.getController();
        //controller.set_controller_values(prod);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }



    public static void main(String[] args) {
        launch();
    }
}