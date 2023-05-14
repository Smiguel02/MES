package com.example.javafx_test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Production extends Thread{

    //FIXME: bro start using UnitTeST Pleaseeee
    //FIXME: Add the ability to send piece to machine for preprocessing and then do things faster when orders arrive

    int last_no_order_piece=0;          // refers to the index of the first piece that isn't in an order

    private float plant_time;

    //Receives current time from PLS periodically
    float update_time(){
    return 0;
    }


    /**
     * Iterates through piece list to find pieces that fit the order.
     * @return in p_quantity position, -1 if not enough pieces. Returns array of indexes
     */

    //Verifies which pieces are available for use
    public int[] pieces_good_to_order(int system_total_pieces, List<Piece> p, int p_quantity, int last_no_order_piece) {

        //ERROR case
        if(p_quantity>system_total_pieces-last_no_order_piece){
            System.out.println("ERROR, not enough pieces to give to order");
            int[] pieces_index = new int[system_total_pieces - last_no_order_piece];
            pieces_index[p_quantity - 1] = -1;
            return pieces_index;
        }

        int num = 0;
        int[] pieces_index = new int[system_total_pieces - last_no_order_piece];

        for (int i = last_no_order_piece; i < system_total_pieces; i++) {
            if (!p.get(i).in_order) {
                pieces_index[num] = i;
                num++;
            }
        }

        // If no pieces, the last value is -1
        System.out.println("Num: " + num + "| Quant: "+ p_quantity);
        if (num < p_quantity) {
            pieces_index[p_quantity - 1] = -1;
            System.out.println("This weird error");
        }

        return pieces_index;
    }

    @Override
    public void run(){

        Production prod =new Production();
        Warehouse war = new Warehouse();
        Piece_new pieces = new Piece_new();
        ArrayList<Machine> Machines = new ArrayList<>();

        //Have 4 msachines
        for(int i=0;i<4;i++){
            Machines.add(new Machine(i+1, i+1,pieces));
        }

        System.out.println("Bonjour MES!");



        //ERP Simulation values
        int system_total_pieces=pieces.total_pieces(), system_order_ID=0;         // Order ID values are given by ERP. To test many
        int p_raw_material=2, p_arrive_day=4, p_amount=4, p_initial_price=30;   //FIXME: consigo saber sempre initial price se comrpar de proposito par auma ordem. Se a ordem trocar, é easy to do the transfer
        int o_final_Piece = 7, o_number_of_pieces=4, o_delivery=9, o_ID = 0;
        int plc_raw_type = 2, plc_machine_2 = 4, plc_final_piece = 7;
        int order_real_cost;

        while(true) {


            // Receive Piece from ERP
            for (int i = 0; i < p_amount; i++) {
                pieces.new_piece(p_raw_material, p_initial_price);
            }

            system_total_pieces += p_amount;

            //! very pretty so not eliminating :)
//        Pieces.forEach(Piece::info_piece);              // Prints where are all the pieces

            //Pieces arrived
            for (int i = 0; i < p_amount; i++) {
                pieces.arrived(p_raw_material, p_arrive_day);   //comes from PLC
            }

            //Move pieces to warehouse
            for (int i = 0; i < p_amount; i++) {
                war.piece_added(p_raw_material);
            }
            System.out.println("Warehouse occupation: " + war.occupation());


            // Receive Order from ERP. Testing for only one order
            List<Order> Orders = new ArrayList<>();
            Order o = new Order(o_ID, o_number_of_pieces, o_delivery, o_final_Piece, pieces);
            Orders.add(o_ID, o);
            system_order_ID++;

            /********Start PLC Order*********/
            // Which raw piece works for a certain order
            // Verify availability
            if (war.specific_pieces_stored(Orders.get(o_ID).raw_piece) < Orders.get(o_ID).number_of_pieces) {
                System.out.println("ERROR, not enough pieces to complete order");
                return;
            }

            // Order PLC
            Orders.get(o_ID).start_manufacturing(Machines);

            // Ir processando peças along with Warehouse and machine stuff. One by one in this case
            for (int i = 0; i < Orders.get(o_ID).number_of_pieces; i++) {
                war.piece_removed(Orders.get(o_ID).raw_piece);
                Orders.get(o_ID).Machines.get(0).info_piece_placed(plc_raw_type);
                Orders.get(o_ID).Machines.get(0).info_transformation_begun();
                Orders.get(o_ID).Machines.get(0).info_transformation_over(plc_machine_2);
                Orders.get(o_ID).Machines.get(0).piece_out(15.6f);

                Orders.get(o_ID).Machines.get(1).info_piece_placed(plc_machine_2);
                Orders.get(o_ID).Machines.get(1).info_transformation_begun();
                Orders.get(o_ID).Machines.get(1).info_transformation_over(plc_final_piece);
                Orders.get(o_ID).Machines.get(1).piece_out(15.6f);

                if (!pieces.dispatched(plc_raw_type, plc_final_piece, Orders.get(o_ID).expected_delivery, Orders.get(o_ID))) {
                    System.out.println("ERROR For somethingggg");
                }

            }

            pieces.info_piece();
            //Threading stuff, waits for call
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        /**
         * Performs a whole Order!
         */

        //If all pieces have been dispatched, should end



//        int[] good_pieces= prod.pieces_good_to_order(system_total_pieces, Pieces, o_number_of_pieces, prod.last_no_order_piece);
//
//        if(good_pieces[Orders.get(o_ID).number_of_pieces-1] == -1){
//            System.out.println("ERROR, Not enough pieces. KILLING PROGRAM");
//            // Do something with ERP in case of too little pieces
//        }
//
//        int aux=Orders.get(o_ID).add_pieces(Pieces.subList(prod.last_no_order_piece, system_total_pieces), good_pieces, system_total_pieces- prod.last_no_order_piece);
//
//        if(aux<0){
//            System.out.println("ERROR, when giving pieces to order");
//            // Do something with ERP in case of too little pieces
//        }else{
//            prod.last_no_order_piece=aux;
//        }
//
//        Pieces.forEach(Piece::info_piece);
//        Orders.forEach(Order::info_order);
//
//        /**
//         * Successfully adds Piece to order
//         */
//
//
//        // Process Order
//
//        // Give Order to PLC
//        // Monitor Piece/Order path
//        // Receive Piece transformation and update
//        // Finish Order

    }


}
