package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsoncomms.Client;
import jsoncomms.Server;


import java.io.IOException;

public class GUI_MES extends Application {
    MyDB n = MyDB.getInstance();

    @Override
    public void start(Stage primaryStage) throws IOException {

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
    }

    public static void main(String[] args) {
        launch();
    }
}