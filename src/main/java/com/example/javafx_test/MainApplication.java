package com.example.javafx_test;

import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication {


public static void main(String[] args) throws IOException {

    // 3 different threads
    Production prod = new Production();
    GUI_MES gui = new GUI_MES();
//    Thread comms =  new OPC_UA();
    Stage stage = new Stage();

    prod.start();
    gui.start(stage);
    System.out.println("Is prod alive? " + prod.isAlive());



}



}
