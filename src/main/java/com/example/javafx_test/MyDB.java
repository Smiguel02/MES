package com.example.javafx_test;

//import model.*;
//import org.apache.commons.lang3.StringUtils;

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

    //Obter a informação da máquina
    int id_machine, id_order, tool, piece_detected;
    boolean in_use, broken;
    float work_time;

    //ArrayList<Machine> machine_info = new ArrayList<>();
    /*public void getInfoMachine(int id_machine, int id_order) throws SQLException {
        machine_info.clear();
        getInfoMachine = "SELECT * FROM infi2023.machine";
        try(Connection con = connect()){
            System.out.println("Deu connect");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(getInfoMachine);
            while(rs.next()){
                if((rs.getInt("id_machine")==id_machine) && (rs.getInt("id_order") == id_order))
                {
                    Machine m = new Machine();


                }

            }
        }*/


    }



