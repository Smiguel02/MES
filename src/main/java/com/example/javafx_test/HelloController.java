package com.example.javafx_test;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

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
    private Label label_total_pieces;

    @FXML
    private Label label_dispatch_raw1;

    @FXML
    private Label label_total_time;

    @FXML
    private Label label_cor; //se for na primeira metade do dia background -> #00a000; na segunda metade do dia background -> FF0000


    @FXML
    private Label label_day;

    private Stage stage;

    private Parent root;

    int result_query;

    MyDB n = MyDB.getInstance();



    //Inicializar Machine, Order, Piece
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            n.connect();
            System.out.println("passou_aqui");
            result_query = n.queryTest();

            System.out.println("Estou aqui");
            //inicializar um controlador após o seu root element ja ter sido processado
            //Piece
            label_raw1.setText(String.valueOf(n.raw_1));
            System.out.println(n.raw_1);
            label_raw2.setText(String.valueOf(n.raw_2));
            label_arrival_raw1.setText(String.valueOf(n.raw_1_arrival));
            label_arrival_raw2.setText(String.valueOf(n.raw_2_arrival));
            label_dispatch_raw1.setText(String.valueOf(n.raw_1_dispatch));
            label_dispatch_raw2.setText(String.valueOf(n.raw_2_dispatch));
            label_price_raw1.setText(String.valueOf(n.raw_1_price));
            label_price_raw2.setText(String.valueOf(n.raw_2_price));
            label_total_pieces.setText(String.valueOf(n.total_system_pieces));

            //Order
            label_piece_type.setText(String.valueOf(n.piece_type));
            label_raw_piece.setText(String.valueOf(n.raw_piece));
            label_raw_cost.setText(String.valueOf(n.raw_cost));
            label_pieces_arrival.setText(String.valueOf(n.pieces_arrival));
            label_number_pieces.setText(String.valueOf(n.number_pieces));
            label_order_completed.setText(String.valueOf(n.order_completed));
            label_expected_delivery.setText(String.valueOf(n.expected_delivery));
            label_expected_cost.setText(String.valueOf(n.expected_cost));
            label_production_cost.setText(String.valueOf(n.production_cost));
            label_total_cost.setText(String.valueOf(n.total_cost));

            //Machine
            label_tool.setText(String.valueOf(n.tool));
            System.out.println(n.tool);
            label_machine_in_use.setText(String.valueOf(n.in_use));
            System.out.println(n.in_use);
            label_machine_broken.setText(String.valueOf(n.broken));
            System.out.println(n.broken);
            label_piece_detected.setText(String.valueOf(n.piece_detected));
            label_work_time.setText(String.valueOf(n.work_time));

            //Dia

            //Total time

            /*if((n.total_time/2) >= n.time_real ){
                label_cor.setBackground(#FF0000);
            }
            else{
                label_cor.setBackground(#00a000);
            }*/


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}