package com.example.javafx_test;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Label information_test1;

    @FXML
    private Label information_test2;

    private Production prod;

    /****************************************************************
     * NEVER CHANGE ANY PROD VALUE FROM HERE. ONLY TAKE INFORMATION *
     ****************************************************************/

    public HelloController(){
        System.out.println("HelloController constructor");
    }

    public void set_controller_values(Production p){
        System.out.println("HelloController values set");
        this.prod = p;
    }

    @FXML
    protected void onHelloButtonClick() {

        int number_of_machines = prod.Machines.size();

        welcomeText.setText("Welcome to JavaFX Application!");
        information_test1.setText("Heyyyy we have " + number_of_machines +" Machines in use!");
        information_test2.setText("Heyyyy I am 2");
    }
}