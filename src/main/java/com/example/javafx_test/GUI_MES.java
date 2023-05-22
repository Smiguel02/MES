package com.example.javafx_test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;;
import javafx.stage.Stage;


import java.io.IOException;

public class GUI_MES extends Application {

    private static Stage stg;

    @Override
    public void start(Stage stage) throws Exception {

        /*Object lock = new Object();

        // 3 different threads
        CommsController comms= new CommsController(lock);
        Production prod = new Production(lock, comms);


        prod.setName("Thread - MES");
        comms.setName("Thread - OPC_UA");
        Thread.currentThread().setName("Thread - GUI");
        prod.start();
        comms.start();
        System.out.println("Is prod alive? " + prod.isAlive());*/

        //FXMLLoader fxmlLoader = new FXMLLoader(GUI_MES.class.getResource("hello-view.fxml"));
        /*Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        HelloController controller = fxmlLoader.getController();
        //controller.set_controller_values(prod);
        //stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();*/

        FXMLLoader fxmlLoader = new FXMLLoader(GUI_MES.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1214, 716);
        HelloController controller = fxmlLoader.getController();
        //controller.set_controller_values(prod);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        /*stg = primarystage;
        primarystage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        primarystage.setTitle("");
        primarystage.setScene(new Scene(root, 1214,716));
        primarystage.show();*/

    }



    public static void main(String[] args) {
        launch();
    }
}