package com.example.javafx_test;

import java.util.ArrayList;
import java.util.List;

public class MES_Schedule {

    List<Order> Orders = new ArrayList();         // Piece object is started here




    // Initiates schedule, does it need any value??
    void MES_Schedule(){

        System.out.println("I exist");
    }


    void add_order(Order o){
        Orders.add(o);
        order_queue();      //TODO: Adds an order to schedule, and verifies if it needs priority
    }

    void order_queue(){

        // Comparar delivery days de cada ordem

        // Verificar quais estão quase prontas to prioritize

        // Calculate based on penalties and if fast order siga
    }




}
