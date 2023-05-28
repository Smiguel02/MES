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

    static String getInfoWarehouse = "SELECT * FROM infi2023.warehouse";

    //Update
    static String update_machine = null;

    static String update_order = null;

    static String update_piece = null;

    static String update_order_erp = null;

    static String update_warehouse = null;

    //Nova Peça
    static String newPieceWar = null;
    static String newPiece = null;
    static String newOrder = null;
    static String newMachine = null;

    //Apagar Peça
    static String deletePiece = "DELETE FROM infi2023.piece WHERE id = ?";

    //updateEdit = "UPDATE studybud.favs SET tipo= ? , idaluno= ?  WHERE idaluno= ?  AND tipo= ?  ";


    //Machine
    int id_machine, id_m_order, tool, piece_detected;
    boolean in_use, broken;
    float work_time;
    //Order
    int id_order, piece_type, raw_piece, raw_cost, pieces_arrival, number_pieces, order_completed, expected_delivery;
    float expected_cost, production_cost, total_cost;
    //Piece
    int id_piece, raw_1, raw_2, raw_1_arrival, raw_2_arrival, raw_1_dispatch, raw_2_dispatch, raw_1_price, raw_2_price, total_system_pieces;
    int count_war;
    //Order ERP
    int id_order_number, work_piece, start_piece, quantity, due_date, late_penalties, early_penalties, expected_profits;
    String client_name;
    //Warehouse
    int id_warehouse, war_max_capacity;
    boolean is_full;

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

    public int queryTestMachine() throws SQLException {

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

    public int queryTestOrder() throws SQLException {
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

    public int queryTestPiece() throws SQLException {
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

    public int queryTestOrderERP() throws SQLException {
        if (conn == null)
            throw new SQLException("Call connect before querying...");

        Statement stmt = conn.createStatement();
        ResultSet rs_order_erp = stmt.executeQuery(getInfoOrderERP);
        System.out.println("passou query 4");
        while(rs_order_erp.next()){
            getInfo(rs_order_erp, "ordererp");
        }

        disconnect();
        return id_order_number;
    }

    public int queryTestWarehouse() throws SQLException{
        if (conn == null)
            throw new SQLException("Call connect before querying...");

        Statement stmt = conn.createStatement();
        ResultSet rs_warehouse = stmt.executeQuery(getInfoWarehouse);
        System.out.println("passou query 4");
        while(rs_warehouse.next()){
            getInfo(rs_warehouse, "warehouse");
        }

        disconnect();
        return id_warehouse;
    }

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
            count_war = rs.getInt("count_war");
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

            id_order_number = rs.getInt("id_order_number");
            client_name = rs.getString("client_name");
            work_piece = rs.getInt("work_piece");
            start_piece = rs.getInt("start_piece");
            quantity = rs.getInt("quantity");
            due_date = rs.getInt("due_date");
            late_penalties = rs.getInt("late_penalties");
            early_penalties = rs.getInt("early_penalties");
            expected_profits = rs.getInt("expected_profits");

        }
        else if(Objects.equals(type, "warehouse")){

            id_warehouse = rs.getInt("id_warehouse");
            war_max_capacity = rs.getInt("WAR_MAX_CAPACITY");
            is_full = rs.getBoolean("is_full");

        }

    }

    //Escrever na base de dados
    //Nota o count conta o numero de peças que estão na warehouse e o total_system conta o numero de peças do sistema
    public int newPiece (Piece new_piece) throws SQLException{
        int affect = 0;
        System.out.println("Nova peça");

        newPiece = "INSERT INTO infi2023.piece (raw_1, raw_2, raw_1_arrival, raw_2_arrival, raw_1_dispatch, raw_2_dispatch, raw_1_price, raw_2_price, total_system_pieces, count_war) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("Nova peça");
        try(Connection con = connect()) {
            System.out.println("deu connect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getInfoPiece);
            while (rs.next()) {
                if (Objects.equals(rs.getString("id_piece"), new_piece.getId_piece())) {
                    disconnect();
                    return 0;
                }
            }

            PreparedStatement pstmt = con.prepareStatement(newPiece);
            System.out.println("try 1 t");
            pstmt.setInt(1, new_piece.getId_piece());
            pstmt.setInt(2, new_piece.getRaw_1());
            pstmt.setInt(3, new_piece.getRaw_2());
            pstmt.setInt(4, new_piece.getRaw_1_arrival());
            pstmt.setInt(5, new_piece.getRaw_2_arrival());
            pstmt.setInt(6, new_piece.getRaw_1_dispatch());
            pstmt.setInt(7, new_piece.getRaw_2_dispatch());
            pstmt.setInt(8, new_piece.getRaw_1_price());
            pstmt.setInt(9, new_piece.getRaw_2_price());
            pstmt.setInt(10, new_piece.getTotal_system_pieces());
            pstmt.setInt(11, new_piece.getCount_war());

            if(affect < 0){
                disconnect();
                return affect;
            }else{
                disconnect();
                return affect;
            }


        }
        catch (SQLException ex){
            System.out.println(ex.getMessage());
            return -1;
        }




    }


    //Informação get and set
    //Machine ///////////////////////////////////////////////////////////////////////////
    // Escrever máquina apatir da que fui buscar a base de dados

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
    //Escrever order apatir da que fui buscar a base de dados

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
    //Escrever piece apatir da que fui buscar a base de dados

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
                p.setCount_war(rs.getInt("count_war"));
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
    //Escrever orderERP apatir da que fui buscar a base de dados

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

    //Updates
    //Update Machine
    static String updateId_order= null;

    static String updateTool= null;

    static String updateIn_use= null;

    static String updateBroken= null;

    static String updatePiece_detected= null;

    static String updateWork_time= null;


    public int updateIdOrder(String tipo){
        int affect;

        updateId_order = "UPDATE infi2023.machine SET id_order = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateId_order);
                System.out.println("try 11 n");
                pstmt.setInt(1, id_m_order);
                pstmt.setInt(2, id_machine);
                System.out.println("try 21 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 31 n");
                
                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateTool(String tipo){
        int affect;

        updateTool = "UPDATE infi2023.machine SET tool = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateTool);
                System.out.println("try 12 n");
                pstmt.setInt(1, tool);
                pstmt.setInt(2, id_machine);
                System.out.println("try 22 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 32 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateInUse(String tipo){
        int affect;

        updateIn_use = "UPDATE infi2023.machine SET in_use = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateIn_use);
                System.out.println("try 13 n");
                pstmt.setBoolean(1, in_use);
                pstmt.setInt(2, id_machine);
                System.out.println("try 23 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 33 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateBroken(String tipo){
        int affect;

        updateBroken = "UPDATE infi2023.machine SET broken = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateBroken);
                System.out.println("try 14 n");
                pstmt.setBoolean(1, broken);
                pstmt.setInt(2, id_machine);
                System.out.println("try 24 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 34 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updatePieceDetected(String tipo){
        int affect;

        updatePiece_detected = "UPDATE infi2023.machine SET piece_detected = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updatePiece_detected);
                System.out.println("try 15 n");
                pstmt.setInt(1, piece_detected);
                pstmt.setInt(2, id_machine);
                System.out.println("try 25 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 35 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateWorkTime(String tipo){
        int affect;

        updateWork_time = "UPDATE infi2023.machine SET work_time = ? WHERE id_machine = ?";

        if(Objects.equals(tipo, "machine")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateWork_time);
                System.out.println("try 16 n");
                pstmt.setFloat(1, work_time);
                pstmt.setInt(2, id_machine);
                System.out.println("try 26 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 36 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    //Update Order
    static String updatePiece_type = null;
    static String updateRaw_piece = null;
    static String updateRaw_cost = null;
    static String updatePieces_arrival = null;
    static String updateNumber_pieces = null;
    static String updateOrder_completed = null;
    static String updateExpected_delivery = null;
    static String updateExpected_cost = null;
    static String updateProduction_cost = null;
    static String updateTotal_cost = null;

    public int updatePieceType(String tipo){
        int affect;

        updatePiece_type = "UPDATE infi2023.order SET piece_type = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updatePiece_type);
                System.out.println("try 17 n");
                pstmt.setInt(1, piece_type);
                pstmt.setInt(2, id_order);
                System.out.println("try 27 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 37 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRawPiece(String tipo){
        int affect;

        updateRaw_piece = "UPDATE infi2023.order SET raw_piece = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_piece);
                System.out.println("try 18 n");
                pstmt.setInt(1, raw_piece);
                pstmt.setInt(2, id_order);
                System.out.println("try 28 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 38 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRawCost(String tipo){
        int affect;

        updateRaw_cost = "UPDATE infi2023.order SET raw_cost = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_cost);
                System.out.println("try 19 n");
                pstmt.setInt(1, raw_cost);
                pstmt.setInt(2, id_order);
                System.out.println("try 29 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 39 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updatePiecesArrival(String tipo){
        int affect;

        updatePieces_arrival = "UPDATE infi2023.order SET pieces_arrival = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updatePieces_arrival);
                System.out.println("try 10 n");
                pstmt.setInt(1, pieces_arrival);
                pstmt.setInt(2, id_order);
                System.out.println("try 20 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 30 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateNumberPieces(String tipo){
        int affect;

        updateNumber_pieces = "UPDATE infi2023.order SET number_pieces = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateNumber_pieces);
                System.out.println("try 112 n");
                pstmt.setInt(1, number_pieces);
                pstmt.setInt(2, id_order);
                System.out.println("try 212 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 312 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateOrderCompleted(String tipo){
        int affect;

        updateOrder_completed = "UPDATE infi2023.order SET order_completed = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateOrder_completed);
                System.out.println("try 113 n");
                pstmt.setInt(1, order_completed);
                pstmt.setInt(2, id_order);
                System.out.println("try 213 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 313 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateExpectedDelivery(String tipo){
        int affect;

        updateExpected_delivery = "UPDATE infi2023.order SET expected_delivery = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateExpected_delivery);
                System.out.println("try 114 n");
                pstmt.setInt(1, expected_delivery);
                pstmt.setInt(2, id_order);
                System.out.println("try 214 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 314 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateExpectedCost(String tipo){
        int affect;

        updateExpected_cost = "UPDATE infi2023.order SET expected_cost = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateExpected_cost);
                System.out.println("try 115 n");
                pstmt.setFloat(1, expected_cost);
                pstmt.setInt(2, id_order);
                System.out.println("try 215 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 315 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateProductionCost(String tipo){
        int affect;

        updateProduction_cost = "UPDATE infi2023.order SET production_cost = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateProduction_cost);
                System.out.println("try 116 n");
                pstmt.setFloat(1, production_cost);
                pstmt.setInt(2, id_order);
                System.out.println("try 216 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 316 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateTotalCost(String tipo){
        int affect;

        updateTotal_cost = "UPDATE infi2023.order SET total_cost = ? WHERE id_order = ?";

        if(Objects.equals(tipo, "order")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateTotal_cost);
                System.out.println("try 117 n");
                pstmt.setFloat(1, total_cost);
                pstmt.setInt(2, id_order);
                System.out.println("try 217 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 317 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    //Update Order ERP
    static String updateClient_name = null;
    static String updateWork_piece = null;
    static String updateStart_piece = null;
    static String updateQuantity = null;
    static String updateDue_date = null;
    static String updateLate_penalties = null;
    static String updateEarly_penalties = null;
    static String updateExpeceted_profits = null;

    public int updateClientName(String tipo){
        int affect;

        updateClient_name = "UPDATE infi2023.ordererp SET client_name = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateClient_name);
                System.out.println("try 118 n");
                pstmt.setString(1, client_name);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 218 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 318 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateWorkPiece(String tipo){
        int affect;

        updateWork_piece = "UPDATE infi2023.ordererp SET work_piece = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateWork_piece);
                System.out.println("try 119 n");
                pstmt.setInt(1, work_piece);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 219 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 319 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateStartPiece(String tipo){
        int affect;

        updateStart_piece = "UPDATE infi2023.ordererp SET start_piece = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateStart_piece);
                System.out.println("try 121 n");
                pstmt.setInt(1, start_piece);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 221 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 321 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateQuantity(String tipo){
        int affect;

        updateQuantity = "UPDATE infi2023.ordererp SET quantity = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateQuantity);
                System.out.println("try 122 n");
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 222 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 322 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateDueDate(String tipo){
        int affect;

        updateDue_date = "UPDATE infi2023.ordererp SET due_date = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateDue_date);
                System.out.println("try 123 n");
                pstmt.setInt(1, due_date);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 223 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 323 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateLatePenalties(String tipo){
        int affect;

        updateLate_penalties = "UPDATE infi2023.ordererp SET late_penalties = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateLate_penalties);
                System.out.println("try 124 n");
                pstmt.setInt(1, late_penalties);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 224 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 324 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateEarlyPenalties(String tipo){
        int affect;

        updateEarly_penalties = "UPDATE infi2023.ordererp SET early_penalties = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateEarly_penalties);
                System.out.println("try 125 n");
                pstmt.setInt(1, early_penalties);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 225 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 325 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateExpectedProfits(String tipo){
        int affect;

        updateExpeceted_profits = "UPDATE infi2023.ordererp SET expected_profits = ? WHERE id_order_number = ?";

        if(Objects.equals(tipo, "ordererp")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateExpeceted_profits);
                System.out.println("try 126 n");
                pstmt.setInt(1, expected_profits);
                pstmt.setInt(2, id_order_number);
                System.out.println("try 226 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 326 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    //Update Piece
    static String updateRaw_1 = null;
    static String updateRaw_2 = null;
    static String updateRaw_1_arrival = null;
    static String updateRaw_2_arrival = null;
    static String updateRaw_1_dispatch = null;
    static String updateRaw_2_dispatch = null;
    static String updateRaw_1_price = null;
    static String updateRaw_2_price = null;
    static String updateTotal_system_pieces = null;

    public int updateRaw1(String tipo){
        int affect;

        updateRaw_1 = "UPDATE infi2023.piece SET raw_1 = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_1);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_1);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw2(String tipo){
        int affect;

        updateRaw_2 = "UPDATE infi2023.piece SET raw_2 = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_2);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_2);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw1Arrival(String tipo){
        int affect;

        updateRaw_1_arrival = "UPDATE infi2023.piece SET raw_1_arrival = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_1_arrival);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_1_arrival);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw2Arrival(String tipo){
        int affect;

        updateRaw_2_arrival = "UPDATE infi2023.piece SET raw_2_arrival = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_2_arrival);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_2_arrival);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw1Dispatch(String tipo){
        int affect;

        updateRaw_1_dispatch = "UPDATE infi2023.piece SET raw_1_dispatch = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_1_dispatch);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_1_dispatch);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw2Dispatch(String tipo){
        int affect;

        updateRaw_2_dispatch = "UPDATE infi2023.piece SET raw_2_dispatch = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_2_dispatch);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_2_dispatch);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw1Price(String tipo){
        int affect;

        updateRaw_1_price = "UPDATE infi2023.piece SET raw_1_price = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_1_price);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_1_price);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateRaw2Price(String tipo){
        int affect;

        updateRaw_2_price = "UPDATE infi2023.piece SET raw_2_price = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateRaw_2_price);
                System.out.println("try 1 n");
                pstmt.setInt(1, raw_2_price);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public int updateTotalSystem(String tipo){
        int affect;

        updateTotal_system_pieces = "UPDATE infi2023.piece SET total_system_pieces = ? WHERE id_piece = ?";

        if(Objects.equals(tipo, "piece")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateTotal_system_pieces);
                System.out.println("try 1 n");
                pstmt.setInt(1,total_system_pieces);
                pstmt.setInt(2, id_piece);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    //Warehouse
    static String updateIs_full = null;
    public int updateIsFull(String tipo){
        int affect;

        updateIs_full = "UPDATE infi2023.warehouse SET is_full = ? WHERE id_warehouse = ?";

        if(Objects.equals(tipo, "warehouse")){
            System.out.println("AQUIII");
            try(Connection con = connect()){
                System.out.println("deu connect");
                PreparedStatement pstmt = con.prepareStatement(updateIs_full);
                System.out.println("try 1 n");
                pstmt.setBoolean(1,is_full);
                pstmt.setInt(2, id_warehouse);
                System.out.println("try 2 n");
                affect = pstmt.executeUpdate();
                System.out.println("try 3 n");

                if(affect < 0){
                    disconnect();
                    return affect;
                }else{
                    disconnect();
                    return affect;
                }
                // Analyse the resulting data

            }catch (SQLException ex){
                System.out.println(ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    //Adicionar peças  sempre que entra uma peça na warehouse
    public int AddPiece(String raw_1, String raw_2, String raw_1_arrival, String raw_2_arrival, String raw_1_dispatch, String raw_2_dispatch, String raw_1_price, String raw_2_price ) throws SQLException {
        int affect = 0;
        newPieceWar = "INSERT INTO infi2023.piece (raw_1, raw_2, raw_1_arrival, raw_2_arrival, raw_1_dispatch, raw_2_dispatch, raw_1_price, raw_2_price, total_system_pieces) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection con = connect()){
            System.out.println("deu connect");
            PreparedStatement pstmt = con.prepareStatement(newPieceWar);
            System.out.println("WEEEEEE vamos inserir peça na warehouse");
            pstmt.setInt(1, Integer.parseInt(raw_1));
            pstmt.setInt(2, Integer.parseInt(raw_2));
            pstmt.setInt(3, Integer.parseInt(raw_1_arrival));
            pstmt.setInt(4, Integer.parseInt(raw_2_arrival));
            pstmt.setInt(5, Integer.parseInt(raw_1_dispatch));
            pstmt.setInt(6, Integer.parseInt(raw_2_dispatch));
            pstmt.setInt(7, Integer.parseInt(raw_1_price));
            pstmt.setInt(8, Integer.parseInt(raw_2_price));
            System.out.println("WEEEEEE vamos inserir peça na warehouse phase 2");
            affect = pstmt.executeUpdate();
            System.out.println("WEEEEEE vamos inserir peça na warehouse phase 2");
            if(affect < 0){
                disconnect();
                return affect;
            }
            else{
                System.out.println("Nova Peça ui");
                disconnect();
                return affect;
            }
        }
        catch (SQLException ex){
            System.out.println(ex.getMessage());
            return -1;
        }
    }


    //Deletar peças sempre que sai uma peça do warehouse
    public int DeletePiece(int id_piece_war){
        int affect;

        try(Connection con = connect()){
            System.out.println("Vamo retirar uma peça do warehouse");
            PreparedStatement pstmt = con.prepareStatement(deletePiece);
            System.out.println("Vamo retirar uma peça do warehouse ok");
            pstmt.setInt(1, id_piece_war);
            System.out.println("Vamo retirar uma peça do warehouse ok 2");
            affect = pstmt.executeUpdate();
            System.out.println("Vamo retirar uma peça do warehouse ok");
            if(affect < 0){
                disconnect();
                return affect;
            }
            else{
                System.out.println("Number of affects: "+affect);
                disconnect();
                return affect;
            }
        }
        catch (SQLException ex){
            System.out.println(ex.getMessage());
            return -1;
        }
    }

    //Contador de peças no warehouse compara o tipo e conta++ tenho de dar update tb ao numero total de peças

    //Update Contador caso adicione ou tire peças de um tipo

}



