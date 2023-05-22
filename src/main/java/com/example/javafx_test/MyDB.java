package com.example.javafx_test;

//import model.*;
//import org.apache.commons.lang3.StringUtils;

import Model.Machine;
import Model.Order;
import Model.Piece;

import java.sql.*;
import java.util.*;
import java.util.jar.JarOutputStream;


public class MyDB {

    /* syntax for DB_URL:         jdbc:postgresql://<database_host>:<port>/<database_name> */
    static final String db_url = "jdbc:postgresql://10.227.240.130:5432/pswa0201";

    static final String user = "pswa0201";

    static final String passwd = "studybud12345";

    static String getInfoMachine = "SELECT * FROM infi2023.machine";

    static String getInfoPiece = "SELECT * FROM infi2023.piece";

    static String getInfoOrder = "SELECT * FROM infi2023.order";

    private static MyDB single_instance = null;


    //Machine
    int id_machine, id_m_order, tool, piece_detected;
    boolean in_use, broken;
    float work_time;
    //Order
    int id_order, piece_type, raw_piece, raw_cost, pieces_arrival, number_pieces, order_completed, expected_delivery;
    float expected_cost, production_cost, total_cost;
    //Piece
    int raw_1, raw_2, raw_1_arrival, raw_2_arrival, raw_1_dispatch, raw_2_dispatch, raw_1_price, raw_2_price, total_system_pieces;


    public MyDB(){}

    // Static method to create instance of Singleton class
    public static MyDB getInstance() {
        if (single_instance == null) single_instance = new MyDB();
        return single_instance;
    }

    // Declaring a variable of type String
    public Connection conn = null;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself

    public Connection connect() throws SQLException {
        conn = DriverManager.getConnection(db_url, user, passwd);
        System.out.println(conn);
        return conn;
    }
    public void disconnect() throws SQLException {
        if (conn != null)
            conn.close();
    }

    //Falta update da informação.

    //Get da informação
    public void getInfo(ResultSet rs, String type) throws SQLException {
        if(Objects.equals(type, "machine")){
            id_machine = rs.getInt("id_machine");
            id_m_order = rs.getInt("id_order");
            tool = rs.getInt("tool");
            piece_detected = rs.getInt("piece_detected");
            in_use = rs.getBoolean("in_use");
            broken = rs.getBoolean("broken");
            work_time = rs.getFloat("work_time");
        }
        else if (Objects.equals(type, "order")){
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
        else if (Objects.equals(type, "piece")){
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
    public int getInfoPiece() throws SQLException{
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



}



