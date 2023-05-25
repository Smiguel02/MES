package com.example.javafx_test;

import Model.Machine;
import Model.Order;
import Model.OrderERP;
import Model.Piece;

import java.sql.*;
import java.util.*;
import java.util.jar.JarOutputStream;


public class MyDB {

    /* syntax for DB_URL:         jdbc:postgresql://<database_host>:<port>/<database_name> */
    //Info
    static final String db_url = "jdbc:postgresql://10.227.240.130:5432/pswa0201";

    static final String user = "pswa0201";

    static final String passwd = "studybud12345";

    static String getInfoMachine = "SELECT * FROM infi2023.machine";

    static String getInfoPiece = "SELECT * FROM infi2023.piece";

    static String getInfoOrder = "SELECT * FROM infi2023.order";

    static String getInfoOrderERP = "SELECT * FROM infi2023.ordererp";

    //Update
    static String update_machine = null;

    static String update_order = null;

    static String update_piece = null;

    static String update_order_erp = null;
    //         updateEdit = "UPDATE studybud.favs SET tipo= ? , idaluno= ?  WHERE idaluno= ?  AND tipo= ?  ";


    //Machine
    int id_machine, id_m_order, tool, piece_detected;
    boolean in_use, broken;
    float work_time;
    //Order
    int id_order, piece_type, raw_piece, raw_cost, pieces_arrival, number_pieces, order_completed, expected_delivery;
    float expected_cost, production_cost, total_cost;
    //Piece
    int id_piece, raw_1, raw_2, raw_1_arrival, raw_2_arrival, raw_1_dispatch, raw_2_dispatch, raw_1_price, raw_2_price, total_system_pieces;
    //Order ERP
    int id_order_erp, work_piece, start_piece, quantity, due_date, late_penalties, early_penalties, expected_profits;
    String client_name;

    private static MyDB single_instance = null;

    public MyDB(){}


    // Static method to create instance of Singleton class
    public static MyDB getInstance() {
        if (single_instance == null) single_instance = new MyDB();
        System.out.println("OLA!");
        return single_instance;
    }

    // Declaring a variable of type String
    public Connection conn = null;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself

    public Connection connect() throws SQLException {
        System.out.println("Hello1!");
        conn = DriverManager.getConnection(db_url, user, passwd);
        System.out.println("Hello2!");
        System.out.println(conn);
        return conn;
    }
    public void disconnect() throws SQLException {
        System.out.println("Hello3!");
        if (conn != null)
            conn.close();
    }

    public int queryTestMachine(int id_machine) throws SQLException {

        if (conn == null)
            throw new SQLException("Call connect before querying...");

        Statement stmt = conn.createStatement();
        ResultSet rs_machine = stmt.executeQuery(getInfoMachine);
        System.out.println("passou query 1");
        while (rs_machine.next()) {
            getInfo(rs_machine, "machine");
        }
        disconnect();
        return id_machine;
    }

    public int queryTestOrder(int id_order) throws SQLException {
        if (conn == null)
            throw new SQLException("Call connect before querying...");

        Statement stmt = conn.createStatement();
        ResultSet rs_order = stmt.executeQuery(getInfoOrder);
        System.out.println("passou query 2");
        while(rs_order.next()){
            getInfo(rs_order, "order");
        }
        disconnect();
        return id_order;
    }

    public int queryTestPiece(int id_piece) throws SQLException {
        if (conn == null)
            throw new SQLException("Call connect before querying...");

        Statement stmt = conn.createStatement();
        ResultSet rs_piece = stmt.executeQuery(getInfoPiece);
        System.out.println("passou query 3");
        while(rs_piece.next()){
            getInfo(rs_piece, "piece");
        }
        disconnect();
        return id_piece;
    }

    public int queryTestOrderERP(int id_order_erp) throws SQLException {
        if (conn == null)
            throw new SQLException("Call connect before querying...");

        Statement stmt = conn.createStatement();
        ResultSet rs_order_erp = stmt.executeQuery(getInfoOrderERP);
        System.out.println("passou query 4");
        while(rs_order_erp.next()){
            getInfo(rs_order_erp, "ordererp");
        }

        disconnect();
        return id_order_erp;
    }


    //Falta update da informação.

    //Get da informação
    public void getInfo(ResultSet rs, String type) throws SQLException {

        if(Objects.equals(type, "machine")) {

            id_machine = rs.getInt("id_machine");
            id_m_order = rs.getInt("id_order");
            tool = rs.getInt("tool");
            //System.out.println(tool);
            piece_detected = rs.getInt("piece_detected");
            in_use = rs.getBoolean("in_use");
            //System.out.println(in_use);
            broken = rs.getBoolean("broken");
            work_time = rs.getFloat("work_time");
        }
        else if(Objects.equals(type, "order")) {

            id_order = rs.getInt("id_order");
            piece_type = rs.getInt("piece_type");
            raw_piece = rs.getInt("raw_piece");
            raw_cost = rs.getInt("raw_cost");
            pieces_arrival = rs.getInt("pieces_arrival");
            number_pieces = rs.getInt("number_pieces");
            order_completed = rs.getInt("order_completed");
            expected_delivery = rs.getInt("expected_delivery");
            expected_cost = rs.getFloat("expected_cost");
            production_cost = rs.getFloat("production_cost");
            total_cost = rs.getFloat("total_cost");
        }
        else if(Objects.equals(type, "piece")) {

            id_piece = rs.getInt("id_piece");
            raw_1 = rs.getInt("raw_1");
            raw_2 = rs.getInt("raw_2");
            raw_1_arrival = rs.getInt("raw_1_arrival");
            raw_2_arrival = rs.getInt("raw_2_arrival");
            raw_1_dispatch = rs.getInt("raw_1_dispatch");
            raw_2_dispatch = rs.getInt("raw_2_dispatch");
            raw_1_price = rs.getInt("raw_1_price");
            raw_2_price = rs.getInt("raw_2_price");
            total_system_pieces = rs.getInt("total_system_pieces");
        }
        else if(Objects.equals(type, "ordererp")){

            id_order_erp = rs.getInt("id_order_number");
            client_name = rs.getString("client_name");
            work_piece = rs.getInt("work_piece");
            start_piece = rs.getInt("start_piece");
            quantity = rs.getInt("quantity");
            due_date = rs.getInt("due_date");
            late_penalties = rs.getInt("late_penalties");
            early_penalties = rs.getInt("early_penalties");
            expected_profits = rs.getInt("expected_profits");

        }

    }


    //Informação get and set
    //Machine ///////////////////////////////////////////////////////////////////////////
    //Obter a informação da máquina

    ArrayList <Machine> machine_info = new ArrayList<>();

    public int getInfoMachine(int id_machine) throws SQLException {
        machine_info.clear();
        getInfoMachine = "SELECT * FROM infi2023.machine";
        try (Connection con = connect()) {
            System.out.println("Deu connect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getInfoMachine);
            while (rs.next()) {
                if ((rs.getInt("id_machine") == id_machine)) {
                    Machine m = new Machine();
                    m.setId_order(rs.getInt("id_order"));
                    m.setBroken(rs.getBoolean("broken"));
                    m.setTool(rs.getInt("tool"));
                    m.setIn_use(rs.getBoolean("in_use"));
                    m.setPiece_detected(rs.getInt("piece_detected"));
                    m.setWork_time(rs.getFloat("work_time"));
                    m.setId_machine(id_machine);
                    machine_info.add(m);
                    disconnect();
                    return 0;
                }
            }
        }
        catch (SQLException ex){
            System.out.println(ex.getMessage());
            disconnect();
            return -1;
        }
        return -1;

    }

    //Order ///////////////////////////////////////////////////////////////////////////
    //Obter a informação da encomenda

    ArrayList <Order> order_info = new ArrayList<>();
    public int getInfoOrder(int id_order) throws SQLException{
        order_info.clear();
        getInfoOrder = "SELECT * FROM infi2023.order";
        try (Connection con = connect()) {
            System.out.println("Deu connect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getInfoOrder);
            while (rs.next()) {
                if ((rs.getInt("id_order") == id_order)) {
                    Order o = new Order();
                    o.setPiece_type(rs.getInt("piece_type"));
                    o.setRaw_piece(rs.getInt("raw_piece"));
                    o.setRaw_cost(rs.getInt("raw_cost"));
                    o.setPieces_arrival(rs.getInt("pieces_arrival"));
                    o.setNumber_pieces(rs.getInt("number_pieces"));
                    o.setOrder_completed(rs.getInt("order_completed"));
                    o.setExpected_delivery(rs.getInt("expected_delivery"));
                    o.setExpected_cost(rs.getFloat("expected_cost"));
                    o.setProduction_cost(rs.getFloat("production_cost"));
                    o.setTotal_cost(rs.getFloat("total_cost"));
                    o.setId_order(id_order);
                    order_info.add(o);
                    disconnect();
                    return 0;
                }
            }

        }
        catch (SQLException ex){
            System.out.println(ex.getMessage());
            disconnect();
            return -1;
        }
        return -1;

    }

    //Piece ///////////////////////////////////////////////////////////////////////////
    //Obter a informação da peça

    ArrayList <Piece> piece_info = new ArrayList<>();
    public int getInfoPiece(int id_piece) throws SQLException{
        piece_info.clear();
        getInfoPiece = "SELECT * FROM infi2023.piece";
        try (Connection con = connect()) {
            System.out.println("Deu connect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getInfoOrder);
            while (rs.next()) {
                Piece p = new Piece();
                p.setRaw_1(rs.getInt("raw_1"));
                p.setRaw_2(rs.getInt("raw_2"));
                p.setRaw_1_arrival(rs.getInt("raw_1_arrival"));
                p.setRaw_2_arrival(rs.getInt("raw_2_arrival"));
                p.setRaw_1_dispatch(rs.getInt("raw_1_dispatch"));
                p.setRaw_2_dispatch(rs.getInt("raw_2_dispatch"));
                p.setRaw_1_price(rs.getInt("raw_1_price"));
                p.setRaw_2_price(rs.getInt("raw_2_price"));
                p.setTotal_system_pieces(rs.getInt("total_system_pieces"));
                p.setId_piece(id_piece);
                piece_info.add(p);
                disconnect();
                return 0;
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
            disconnect();
            return -1;
        }
        return -1;

    }

    //Order ERP
    //Obter a informação da encomenda ERP

    ArrayList <OrderERP> order_erp_info = new ArrayList<>();
    public int getInfoOrderERP(int id_order_number) throws SQLException{
        order_erp_info.clear();
        getInfoOrderERP = "SELECT * FROM infi2023.ordererp";
        try (Connection con = connect()) {
            System.out.println("Deu connect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getInfoOrderERP);
            while (rs.next()) {
                if ((rs.getInt("id_order_number") == id_order_number)) {
                    OrderERP e = new OrderERP();
                    e.setClient_name(rs.getString("client_name"));
                    e.setWork_piece(rs.getInt("work_piece"));
                    e.setStart_piece(rs.getInt("start_piece"));
                    e.setQuantity(rs.getInt("quantity"));
                    e.setDue_date(rs.getInt("due_date"));
                    e.setLate_penalties(rs.getInt("late_penalties"));
                    e.setEarly_penalties(rs.getInt("early_penalties"));
                    e.setExpected_profits(rs.getInt("expected_profits"));
                    e.setId_order_erp(id_order_number);
                    order_erp_info.add(e);
                    disconnect();
                    return 0;
                }
            }

        }
        catch (SQLException ex){
            System.out.println(ex.getMessage());
            disconnect();
            return -1;
        }
        return -1;

    }
/*
    //Updates
    //Update Machine
    static String updateId_order= null;

    static String updateTool= null;

    static String updateIn_use= null;

    static String updateBroken= null;

    static String updatePiece_detected= null;

    static String updateWork_time= null;

    public int updateNome(String tipo){
        int affect1, affect2;

        updateId_order = "UPDATE infi2023.machine SET id_order = ? WHERE id_machine = ?";
        updateTool = "UPDATE infi2023.machine SET tool = ? WHERE id_machine = ?";
        updateIn_use = "UPDATE infi2023.machine SET in_use = ? WHERE id_machine = ?";
        updateBroken = "UPDATE infi2023.machine SET broken = ? WHERE id_machine = ?";
        updatePiece_detected = "UPDATE infi2023.machine SET piece_detected = ? WHERE id_machine = ?";
        updateWork_time = "UPDATE infi2023.machine SET work_time = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt1 = con.prepareStatement(updateId_order);
                System.out.println("try 1 n");
                pstmt1.setInt(1, id_order);
                pstmt1.setInt(2, id_machine);
                System.out.println("try 2 n");
                affect1 = pstmt1.executeUpdate();
                System.out.println("try 3 n");

                PreparedStatement pstmt2  = con.prepareStatement(updateTool);
                System.out.println("try 1 n");
                pstmt2.setInt(1, tool);
                pstmt2.setInt(2, id_machine);
                System.out.println("try 2 n");
                affect2 = pstmt2.executeUpdate();
                System.out.println("try 3 n");


                if(affect1 < 0){
                    disconnect();
                    return affect1;
                }else{
                    disconnect();
                    return affect1;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }

        }
        return -1;



    }*/


}



