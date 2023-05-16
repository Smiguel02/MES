package com.example.javafx_test;

import java.util.ArrayList;
import java.util.List;

public class Production extends Thread{

    int last_no_order_piece=0;          // refers to the index of the first piece that isn't in an order

    private float plant_time;

    public Object lock;

    CommsController opcua;

    public ArrayList<Machine> Machines = new ArrayList<>();

    //Receives current time from PLS periodically
    float update_time(){
    return 0;
    }


    public Production(Object l, CommsController opc){  //FIXME: acho que ficava melhor com a classe OpcUa
        this.lock = l;
        this.opcua = opc;
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

        OpcUa comms_opc = null;     //FIXME: iniciado assim mas nem sei bem se está correto ou nao
        try {
            comms_opc = OpcUa.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Warehouse war = new Warehouse();
        ERP_API erp = new ERP_API();
        Piece_new pieces = new Piece_new();
//        ArrayList<Machine> Machines = new ArrayList<>();

        //Have 4 machines
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

            /***********************
             ***********************
             ******CHECK COMMS******
             ***********************
             ***********************/

            // TODO: use this to also receive ERP stuff


            try {
                if(opcua.update_values_verification()){
                    // Call update values method that verifies and updates everything
                    opcua.i_have_updated_my_values(true);// ends by saying the values have already been updated
                    // In case OPC_UA is in lock waiting to udpate the values, this will allow it to continue
                    synchronized (lock){
                        lock.notify();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            /***********************
             * *********************
             * ALWAYS RUNNING CYCLE*
             * *********************
             ***********************/

            // Check ERP Comms
            // Receive Piece from ERP
            if(erp.new_pieces){
                for (int i = 0; i < p_amount; i++) {
                    pieces.new_piece(p_raw_material, p_initial_price);
                }
            }
            system_total_pieces += p_amount;        //FIXME: later remove


            // Receive Order from ERP
            List<Order> Orders = new ArrayList<>();
            if(erp.new_order){
                Order o = new Order(o_ID, o_number_of_pieces, o_delivery, o_final_Piece, pieces);
                Orders.add(o_ID, o);
                system_order_ID++;
            }

            //! very pretty so not eliminating :)
//        Pieces.forEach(Piece::info_piece);              // Prints where are all the pieces

            //Pieces arrived
            if(opcua.piece_arrived){
                for (int i = 0; i < p_amount; i++) {
                    pieces.arrived(p_raw_material, p_arrive_day);   //comes from PLC
                }
            }

            if(opcua.piece_warehouse){
                //Move pieces to warehouse
                for (int i = 0; i < p_amount; i++) {
                    war.piece_added(p_raw_material);
                }
                System.out.println("Warehouse occupation: " + war.occupation());
            }


            /********Start PLC Order*********/
            // Which raw piece works for a certain order
            // Verify availability
            if (war.specific_pieces_stored(Orders.get(o_ID).raw_piece) < Orders.get(o_ID).number_of_pieces) {
                System.out.println("ERROR, not enough pieces to complete order");
                return;
            }

            // Order PLC
            //! FIXME: not ready for this yet
//            try {
//                comms_opc.mandarFazerPeca(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, 2);    //FIXME: needs to return machines in use
//            } catch (UaException | ExecutionException | InterruptedException e) {
//                throw new RuntimeException(e);
//            }


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

            try {
                Thread.sleep(1000);     //TODO: change this at is delaying our program
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            /**
             * Performs a whole Order!
             */
        }

    }

}
