package com.example.javafx_test;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController {


    @FXML
    private Button atualizar_machine;

    @FXML
    private Button atualizar_order;

    @FXML
    private Button atualizar_piece;

    @FXML
    private Label label_arrival_raw1;

    @FXML
    private Label label_arrival_raw2;

    @FXML
    private Label label_dispatch_raw2;

    @FXML
    private Label label_expected_cost;

    @FXML
    private Label label_expected_delivery;

    @FXML
    private Label label_machine_broken;

    @FXML
    private Label label_machine_in_use;

    @FXML
    private Label label_number_pieces;

    @FXML
    private Label label_order_completed;

    @FXML
    private Label label_piece_detected;

    @FXML
    private Label label_piece_type;

    @FXML
    private Label label_pieces_arrival;

    @FXML
    private Label label_price_raw1;

    @FXML
    private Label label_price_raw2;

    @FXML
    private Label label_production_cost;

    @FXML
    private Label label_raw1;

    @FXML
    private Label label_raw2;

    @FXML
    private Label label_raw_cost;

    @FXML
    private Label label_raw_piece;

    @FXML
    private Label label_tool;

    @FXML
    private Label label_total_cost;

    @FXML
    private Label label_work_time;

    @FXML
    private Label lable_total_pieces;

    @FXML
    private Label labvel_dispatch_raw1;

    MyDB n = MyDB.getInstance();

    //Inicializar Machine, Order, Piece
    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //inicializar um controlador após o seu root element ja ter sido processado
        label_raw1.setText(String.valueOf(n.raw_1));

    }*/




}