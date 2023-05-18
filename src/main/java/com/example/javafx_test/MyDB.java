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
    // Hey bro
    public void disconnect() throws SQLException {
        if (conn != null)
            conn.close();
    }


}
