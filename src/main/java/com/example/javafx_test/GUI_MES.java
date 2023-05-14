package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.milo.opcua.stack.core.UaException;


import java.io.IOException;

public class GUI_MES extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI_MES.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {

        // 3 different threads
        Production prod = new Production();
//        GUI_MES gui = new GUI_MES();
        main comms= new main();

//        Stage stage = new Stage();

        prod.start();
        comms.start();
//        gui.start(stage);
        System.out.println("Is prod alive? " + prod.isAlive());

        launch();
    }
}