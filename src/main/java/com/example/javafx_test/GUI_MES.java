package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


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

        Object lock = new Object();

        // 3 different threads
        OPCUA_Controller comms= new OPCUA_Controller(lock);
        Production prod = new Production(lock, comms);



        prod.setName("Thread - MES");
        comms.setName("Thread - OPC_UA");
        Thread.currentThread().setName("Thread - GUI");
        prod.start();
        comms.start();
        System.out.println("Is prod alive? " + prod.isAlive());

        launch();
    }
}