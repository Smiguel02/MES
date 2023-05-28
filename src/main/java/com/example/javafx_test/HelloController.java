package com.example.javafx_test;


import Model.Order;
import Model.OrderERP;
import Model.Machine;
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

    /*private DataModel dataModel = new DataModel();*/
    //private Timeline timeline;

    private List <Machine> mach;
    private List <Order> ord;

    MyDB n = MyDB.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            n.connect();
            //mach.clear();
            a = n.getInfoMachine();
            System.out.println(a);
            mach = todasmachines();
            if (a < 0) {
                System.out.printf("Error in query test");
            } else {
                System.out.println("Machine_Label_Preencher");
                //Machine
                //Tool
                label_tool_1.setText(String.valueOf(mach.get(0).getTool()));
                label_tool_2.setText(String.valueOf(mach.get(1).getTool()));
                label_tool_3.setText(String.valueOf(mach.get(2).getTool()));
                label_tool_4.setText(String.valueOf(mach.get(3).getTool()));
                //In_use
                label_machine_in_use_1.setText(String.valueOf(mach.get(0).isIn_use()));
                label_machine_in_use_2.setText(String.valueOf(mach.get(1).isIn_use()));
                label_machine_in_use_3.setText(String.valueOf(mach.get(2).isIn_use()));
                label_machine_in_use_4.setText(String.valueOf(mach.get(3).isIn_use()));
                //Piece_detected
                label_piece_detected_1.setText(String.valueOf(mach.get(0).getPiece_detected()));
                label_piece_detected_2.setText(String.valueOf(mach.get(1).getPiece_detected()));
                label_piece_detected_3.setText(String.valueOf(mach.get(2).getPiece_detected()));
                label_piece_detected_4.setText(String.valueOf(mach.get(3).getPiece_detected()));
                //Work_time
                label_work_time_1.setText(String.valueOf(mach.get(0).getWork_time()));
                label_work_time_2.setText(String.valueOf(mach.get(1).getWork_time()));
                label_work_time_3.setText(String.valueOf(mach.get(2).getWork_time()));
                label_work_time_4.setText(String.valueOf(mach.get(3).getWork_time()));

            }

            //ord.clear();
            b = n.getInfoOrder();
            System.out.println(b);
            ord = todasorders();
            System.out.println("Order_Label");
            if (b < 0) {
                System.out.printf("Error in query test");
            } else {
                label_piece_type_1.setText(String.valueOf(ord.get(0).getPiece_type()));
                label_piece_type_2.setText(String.valueOf(ord.get(1).getPiece_type()));
                label_raw_piece_1.setText(String.valueOf(ord.get(0).getRaw_piece()));
                label_raw_piece_2.setText(String.valueOf(ord.get(1).getRaw_piece()));
                label_raw_cost_1.setText(String.valueOf(ord.get(0).getRaw_cost()));
                label_raw_cost_2.setText(String.valueOf(ord.get(1).getRaw_cost()));
                label_pieces_arrival_1.setText(String.valueOf(ord.get(0).getPieces_arrival()));
                label_pieces_arrival_2.setText(String.valueOf(ord.get(1).getPieces_arrival()));
                label_number_pieces_1.setText(String.valueOf(ord.get(0).getNumber_pieces()));
                label_number_pieces_2.setText(String.valueOf(ord.get(1).getNumber_pieces()));
                label_order_completed_1.setText(String.valueOf(ord.get(0).getOrder_completed())); //nao  consigo ir buscar se ja foi completa quantas peças na order
                label_order_completed_2.setText(String.valueOf(ord.get(1).getOrder_completed()));
                label_expected_delivery_1.setText(String.valueOf(ord.get(0).getExpected_delivery()));
                label_expected_delivery_2.setText(String.valueOf(ord.get(1).getExpected_delivery()));
                label_expected_cost_1.setText(String.valueOf(ord.get(0).getExpected_cost()));
                label_expected_cost_2.setText(String.valueOf(ord.get(1).getExpected_cost()));
                label_production_cost_1.setText(String.valueOf(ord.get(0).getProduction_cost()));
                label_production_cost_2.setText(String.valueOf(ord.get(1).getProduction_cost()));
                label_total_cost_1.setText(String.valueOf(ord.get(0).getTotal_cost()));
                label_total_cost_2.setText(String.valueOf(ord.get(1).getTotal_cost()));
            }
        } catch (SQLException e) {
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

        /*if(tempo atual> tempo total/2){ //ver variaveis do miguel
            label_cor.setBackground(#FF0000);
        }
        else{
            label_cor.setBackground(#00a000);
        }
         */


        try {
            n.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ord.clear();
        mach.clear();

    }

    /*private void initializeData() {
        // Perform your initialization code here
        // Retrieve data from the SQL database and update the data model
        String newData = fetchDataFromDatabase();
        dataModel.setData(newData);
    }*/

    private List<Machine> todasmachines() throws SQLException{
        a = n.getInfoMachine();
        List <Machine> as = new ArrayList<>();
        if(a<0){
            Machine machhhhh = new Machine();
            as.add(machhhhh);
            return as;
        }
        for(int i= 0; i< n.machine_info.size(); i++){
            Machine machh = new Machine();
            machh.setId_machine(n.machine_info.get(i).getId_machine());
            machh.setId_order(n.machine_info.get(i).getId_order());
            machh.setTool(n.machine_info.get(i).getTool());
            machh.setIn_use(n.machine_info.get(i).isIn_use());
            machh.setBroken(n.machine_info.get(i).isBroken());
            machh.setPiece_detected(n.machine_info.get(i).getPiece_detected());
            machh.setWork_time(n.machine_info.get(i).getWork_time());
            as.add(machh);

        }
        return as;
    }
    private List<Order> todasorders() throws SQLException{
        b = n.getInfoOrder();
        List<Order> as = new ArrayList<>();
        if(b<0){
            Order orddddd = new Order();
            as.add(orddddd);
            return as;

        }
        for(int i=0; i<n.order_info.size();i++){
            Order ordd = new Order();
            ordd.setId_order(n.order_info.get(i).getId_order());
            ordd.setPiece_type(n.order_info.get(i).getPiece_type());
            ordd.setRaw_piece(n.order_info.get(i).getRaw_piece());
            ordd.setRaw_cost(n.order_info.get(i).getRaw_cost());
            ordd.setPieces_arrival(n.order_info.get(i).getPieces_arrival());
            ordd.setNumber_pieces(n.order_info.get(i).getNumber_pieces());
            ordd.setOrder_completed(n.order_info.get(i).getOrder_completed());
            ordd.setExpected_delivery(n.order_info.get(i).getExpected_delivery());
            ordd.setProduction_cost(n.order_info.get(i).getProduction_cost());
            ordd.setTotal_cost(n.order_info.get(i).getTotal_cost());
            as.add(ordd);

        }
        return as;
    }
}
