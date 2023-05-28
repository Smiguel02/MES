package com.example.javafx_test;


import Model.OrderERP;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    int a, b, c;

    /*private DataModel dataModel = new DataModel();
    private Timeline timeline;*/

    MyDB n = MyDB.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            n.connect();
            List <Machine> mach = new ArrayList<>();
            mach.clear();
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
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try{
            n.connect();
            List <Order> ord =  new ArrayList<>();
            ord.clear();
            b = n.queryTestOrder();
            System.out.println("Order_Label");
            if(b < 0){
                System.out.printf("Error in query test");
            }
            else{
                label_piece_type_1.setText(String.valueOf(ord.get(0).piece_type));
                label_piece_type_2.setText(String.valueOf(ord.get(1).piece_type));
                label_raw_piece_1.setText(String.valueOf(ord.get(0).raw_piece));
                label_raw_piece_2.setText(String.valueOf(ord.get(1).raw_piece));
                label_raw_cost_1.setText(String.valueOf(ord.get(0).raw_cost));
                label_raw_cost_2.setText(String.valueOf(ord.get(1).raw_cost));
                label_pieces_arrival_1.setText(String.valueOf(ord.get(0).pieces_arrival));
                label_pieces_arrival_2.setText(String.valueOf(ord.get(1).pieces_arrival));
                label_number_pieces_1.setText(String.valueOf(ord.get(0).number_of_pieces));
                label_number_pieces_2.setText(String.valueOf(ord.get(1).number_of_pieces));
                label_order_completed_1.setText(String.valueOf(ord.get(0).completed)); //nao  consigo ir buscar se ja foi completa quantas peças na order
                label_order_completed_2.setText(String.valueOf(ord.get(1).completed));
                label_expected_delivery_1.setText(String.valueOf(ord.get(0).expected_delivery));
                label_expected_delivery_2.setText(String.valueOf(ord.get(1).expected_delivery));
                label_expected_cost_1.setText(String.valueOf(ord.get(0).expected_cost));
                label_expected_cost_2.setText(String.valueOf(ord.get(1).expected_cost));
                label_production_cost_1.setText(String.valueOf(ord.get(0).production_cost));
                label_production_cost_2.setText(String.valueOf(ord.get(1).production_cost));
                label_total_cost_1.setText(String.valueOf(ord.get(0).total_cost));
                label_total_cost_2.setText(String.valueOf(ord.get(1).total_cost));

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*try{
            /*n.connect();
            List <Piece_new> pieces_war = new ArrayList<>();
            pieces_war.clear();
            c = n.queryTestPiece();
            if(c < 0){
                System.out.printf("Error in query test");
            }
            else{

            }*/


        /*}
        catch (SQLException e) {
            throw new RuntimeException(e);
        }*/

        /*timeline = new Timeline(new KeyFrame(Duration.seconds(10), event)){
            initializeData();
            timeline.setCycleCount(Timeline.INDEFINITE);
        }));*/

    }

    /*private void  initializeData(){
        String newData = fetchDataFromDatabase();
        dataModel.setData(newData);
    }*/
}
