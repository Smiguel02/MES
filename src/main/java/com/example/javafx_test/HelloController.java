package com.example.javafx_test;


import Model.OrderERP;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Label label_cor;

    @FXML
    private Label label_day;

    @FXML
    private Label label_expected_cost_1;

    @FXML
    private Label label_expected_cost_2;

    @FXML
    private Label label_expected_delivery_1;

    @FXML
    private Label label_expected_delivery_2;

    @FXML
    private Label label_machine_in_use_1;

    @FXML
    private Label label_machine_in_use_2;

    @FXML
    private Label label_machine_in_use_3;

    @FXML
    private Label label_machine_in_use_4;

    @FXML
    private Label label_number_pieces_1;

    @FXML
    private Label label_number_pieces_2;

    @FXML
    private Label label_order_completed_1;

    @FXML
    private Label label_order_completed_2;

    @FXML
    private Label label_piece_detected_1;

    @FXML
    private Label label_piece_detected_2;

    @FXML
    private Label label_piece_detected_3;

    @FXML
    private Label label_piece_detected_4;

    @FXML
    private Label label_piece_type_1;

    @FXML
    private Label label_piece_type_2;

    @FXML
    private Label label_pieces_arrival_1;

    @FXML
    private Label label_pieces_arrival_2;

    @FXML
    private Label label_production_cost_1;

    @FXML
    private Label label_production_cost_2;

    @FXML
    private Label label_raw_cost_1;

    @FXML
    private Label label_raw_cost_2;

    @FXML
    private Label label_raw_piece_1;

    @FXML
    private Label label_raw_piece_2;

    @FXML
    private Label label_tool_1;

    @FXML
    private Label label_tool_2;

    @FXML
    private Label label_tool_3;

    @FXML
    private Label label_tool_4;

    @FXML
    private Label label_total_cost_1;

    @FXML
    private Label label_total_cost_2;

    @FXML
    private Label label_total_time;

    @FXML
    private Label label_war_p1;

    @FXML
    private Label label_war_p2;

    @FXML
    private Label label_war_p3;

    @FXML
    private Label label_war_p4;

    @FXML
    private Label label_war_p5;

    @FXML
    private Label label_war_p6;

    @FXML
    private Label label_war_p7;

    @FXML
    private Label label_war_p8;

    @FXML
    private Label label_war_p9;

    @FXML
    private Label label_work_time_1;

    @FXML
    private Label label_work_time_2;

    @FXML
    private Label label_work_time_3;

    @FXML
    private Label label_work_time_4;

    int a;

    MyDB n = MyDB.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            n.connect();
            List <Machine> mach = new ArrayList<>();
            a = n.queryTestMachine();
            System.out.println("Machine_Label");
            if(a<0){
                System.out.printf("Error in query test");
            }
            else{
                System.out.println("Machine_Label_Preencher");
                //Machine
                //Tool
                label_tool_1.setText(String.valueOf(mach.get(0).current_tool));
                label_tool_2.setText(String.valueOf(mach.get(1).current_tool));
                label_tool_3.setText(String.valueOf(mach.get(2).current_tool));
                label_tool_4.setText(String.valueOf(mach.get(3).current_tool));
                //In_use
                label_machine_in_use_1.setText(String.valueOf(mach.get(0).in_use));
                label_machine_in_use_2.setText(String.valueOf(mach.get(1).in_use));
                label_machine_in_use_3.setText(String.valueOf(mach.get(2).in_use));
                label_machine_in_use_4.setText(String.valueOf(mach.get(3).in_use));
                //Piece_detected
                label_piece_detected_1.setText(String.valueOf(mach.get(0).piece_detected));
                label_piece_detected_2.setText(String.valueOf(mach.get(1).piece_detected));
                label_piece_detected_3.setText(String.valueOf(mach.get(2).piece_detected));
                label_piece_detected_4.setText(String.valueOf(mach.get(3).piece_detected));
                //Work_time
                label_work_time_1.setText(String.valueOf(mach.get(0).work_time()));
                label_work_time_2.setText(String.valueOf(mach.get(1).work_time()));
                label_work_time_3.setText(String.valueOf(mach.get(2).work_time()));
                label_work_time_4.setText(String.valueOf(mach.get(3).work_time()));

            }
            mach.clear();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try{
            n.connect();


        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
    /*@FXML
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

        int result_query_machine;

        int result_query_order;

        int result_query_piece;

        MyDB n = MyDB.getInstance();*/


//Inicializar Machine, Order, Piece
//inicializar um controlador após o seu root element ja ter sido processado
/*@Override
public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
        n.connect();
        System.out.println("passou_aqui");
        result_query_machine = n.queryTestMachine();
        System.out.println("Machine");
        System.out.println(n.id_machine);
        if ((result_query_machine < 0)) {
        System.out.printf("Error in query test");
        } else {
        //Machine
        label_tool.setText(String.valueOf(n.tool));
        System.out.println(n.tool);
        label_machine_in_use.setText(String.valueOf(n.in_use));
        System.out.println(n.in_use);
        label_machine_broken.setText(String.valueOf(n.broken));
        System.out.println(n.broken);
        label_piece_detected.setText(String.valueOf(n.piece_detected));
        label_work_time.setText(String.valueOf(n.work_time));

        }

        } catch (SQLException e) {
        throw new RuntimeException(e);
        }
        try {
        n.connect();
        System.out.println("passou_aqui");
        System.out.println("Estou aqui1111");
        result_query_piece = n.queryTestPiece();
        System.out.println("Piece");
        System.out.println(n.id_piece);
        if ((result_query_piece < 0)) {
        System.out.printf("Error in query test");
        } else {
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

        }
        } catch (SQLException e) {
        throw new RuntimeException(e);
        }

        try {

        n.connect();
        System.out.println("passou_aqui");
        System.out.println("Estou aqui2222");
        result_query_order = n.queryTestOrder();
        System.out.println("Order");
        System.out.println(n.id_order);
        if ((result_query_order < 0)) {
        System.out.printf("Error in query test");
        } else {
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
        }
        } catch (SQLException e) {
        throw new RuntimeException(e);
        }
        try {
        n.connect();

//Mudar a cor do bakckground daquela label branca

//Dia

//Total time

            if((n.total_time/2) >= n.time_real ){ -> se for na segunda metade do dia
               label_cor.setBackground(#FF0000); -> vermelho
            }
           else{ -> primeira metade do dia
               label_cor.setBackground(#00a000); -> verde
            }
       }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/







