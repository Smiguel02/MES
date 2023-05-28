package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsoncomms.Client;
import jsoncomms.Server;


import java.io.IOException;

public class GUI_MES extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Object lock = new Object();

        // 3 different threads
        CommsController comms= new CommsController(lock);
        Production prod = new Production(lock, comms);
        Server server = Server.getInstance();
        Thread thread_server = new Thread(server);

        prod.setName("Thread - MES");
        comms.setName("Thread - OPC_UA");
        thread_server.setName("Thread - Server");
        Thread.currentThread().setName("Thread - GUI");
        comms.start();
        prod.start();
        thread_server.start();
        System.out.println("Is prod alive? " + prod.isAlive());

        FXMLLoader fxmlLoader = new FXMLLoader(GUI_MES.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        HelloController controller = fxmlLoader.getController();
        controller.set_controller_values(prod);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch();
    }
}