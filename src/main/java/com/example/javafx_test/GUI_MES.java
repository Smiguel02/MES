package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.SQLException;

public class GUI_MES extends Application  {

    MyDB n = MyDB.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {


        /*FXMLLoader fxmlLoader = new FXMLLoader(GUI_MES.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1214, 716);
        HelloController controller = fxmlLoader.getController();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();*/


        try {
            primaryStage.setResizable(false);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui_example.fxml"));
            Parent root = loader.load();
            HelloController controller = loader.getController();
            // Start the label update in the controller
            //controller.startLabelUpdate();
            primaryStage.setTitle("");
            primaryStage.setScene(new Scene(root, 1214, 716));
            n.connect();
            controller.initialize();
            primaryStage.setOnCloseRequest(event -> controller.stopDataUpdate());
            primaryStage.show();


        }catch (Exception e){
            e.printStackTrace();
        }

        /*FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("hello-view.fxml"));
        root=fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();
        stage.setScene(new Scene(root, 1214,716));
        stage.show();*/

        /*FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1214, 716);
        stage.setScene(scene);
        stage.show();*/

    }




    public static void main(String[] args) {
        launch();
    }
}