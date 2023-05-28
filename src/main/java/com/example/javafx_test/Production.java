package com.example.javafx_test;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.util.ArrayList;
import java.util.List;

public class Production extends Thread{

    int last_no_order_piece=0;          // refers to the index of the first piece that isn't in an order

    private float plant_time;

    private int new_warehouse_piece = 0;
    boolean stuck = false;

    List<Order> Orders = new ArrayList<>();
    private long time = 0;
    private int day=0;
    public Object lock;
    public boolean war_update_entering = false, war_update_leaving = false, mach_update_time = false;

    CommsController opcua;

    public ArrayList<Machine> Machines = new ArrayList<>();
    public ArrayList<Integer> Amount_of_pieces = new ArrayList<>();

    //Receives current time from PLS periodically
    float update_time(){
    return 0;
    }


    public Production(Object l, CommsController opc){
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
            System.out.println("\\u001B[31m"+"ERROR, not enough pieces to give to order"+"\\u001B[0m");
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

        Warehouse war = new Warehouse();
        ERP_API erp = new ERP_API();
        Piece_new pieces = new Piece_new();


        //Have 4 machines
        for(int i=0;i<4;i++){
            Machines.add(new Machine(i+1, i+1,pieces));
        }
        for(int i=0;i<9;i++){
            Amount_of_pieces.add(0);
        }

        System.out.println("Bonjour MES!");

        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        //ERP Simulation values
        int system_total_pieces=pieces.total_pieces(), system_order_ID=0;         // Order ID values are given by ERP. To test many
        int p_raw_material=2, p_arrive_day=4, p_amount=6, p_initial_price=30;   //FIXME: consigo saber sempre initial price se comprar de proposito para uma ordem. Se a ordem trocar, é easy to do the transfer
        int o_final_Piece = 9, o_number_of_pieces=6, o_delivery=9, o_ID = 0;
        int plc_raw_type = 2, plc_machine_2 = 4, plc_final_piece = 7;
        int order_real_cost;

        while(true) {

            this.time = opcua.time - opcua.initial_time;    //Updates time
            this.day = (int)(this.time / 60000) + 1;

            /***********************
             ***********************
             ******CHECK COMMS******
             ***********************
             ***********************/

            // verify if new piece inside warehouse
            if(opcua.piece_on_at2 != opcua.previous_piece_on_at2 || war_update_entering){
                if(opcua.previous_piece_on_at2 || war_update_entering){
                    if(opcua.number_of_pieces_on_warehouse() == war.occupation()){
                        war_update_entering = true;
                    }else{
                        war_update_entering = false;
                        for(int i= 0 ; i< 9; i++){
                            if(war.specific_pieces_stored(i + 1) < opcua.war_piece_counter.get(i).intValue()){
                                System.out.println("Piece of type " + (i+1) + " added to Warehouse!");
                                war.piece_added(i+1);
                                if(Orders.get(o_ID).piece_type == (i +1)){
                                    System.out.println("Piece transformation happened and notified order!");
                                    pieces.transform(Orders.get(o_ID).raw_piece, (i +1));
                                }
                                break;
                            }
                        }
                    }
                }
                System.out.println("Warehouse Entering size -> " + war.occupation());
                opcua.previous_piece_on_at2 = opcua.piece_on_at2;
            }

            //verify if piece out of warehouse
            if(opcua.piece_on_at1 != opcua.previous_piece_on_at1 || war_update_leaving){
                if(!opcua.previous_piece_on_at1 || war_update_leaving){
                    if(opcua.number_of_pieces_on_warehouse() == war.occupation()){
                        war_update_leaving = true;
                    }else {
                        war_update_leaving = false;
                        for(int i= 0 ; i< 9; i++){
                            if(war.specific_pieces_stored(i + 1) > opcua.war_piece_counter.get(i).intValue()){
                                System.out.println("Piece of type " + (i+1) + " removed from Warehouse!");
                                war.piece_removed(i+1);
                                if(i <=  1 && opcua.dispatched_piece){
                                    System.out.println("ERROR, why are you dispatching that piece??");
                                }
                                opcua.ordered_piece = false;
                                opcua.dispatched_piece = false;
                                break;
                            }
                        }
                        System.out.println("Warehouse Leaving size -> " + war.occupation());
                    }
                }
                opcua.previous_piece_on_at1 = opcua.piece_on_at1;
            }




            // Verify if new piece arrived on ct8 and if inside desired time
            if(opcua.piece_arrived_on_ct8 != opcua.previous_piece_arrived_on_ct8){
                if(!opcua.previous_piece_arrived_on_ct8 ){
                    int raw = pieces.expected_piece();
                    if(raw == 0 || raw == 1){
                        System.out.println("ERROR, Not expecting any piece, please remove");
                        break;
                    }
                    if(!pieces.arrived(2, this.day)){
                        System.out.println("PROD: ERROR, Why Bro?? That piece shouldn't be here!");
                    }
                    System.out.println("PROD: Expected raw: " + raw);
                    /**
                     * Chegam por ordem e simplesmente adicionamos. Looks like a good approach
                     */
                }
                opcua.previous_piece_arrived_on_ct8 = opcua.piece_arrived_on_ct8;
            }
            // Verify if new piece arrived on ct3 and if inside desired time. Only p1 pieces
            if(opcua.piece_arrived_on_ct3 != opcua.previous_piece_arrived_on_ct3){
                if(!opcua.previous_piece_arrived_on_ct3){
                    int raw = pieces.expected_piece();
                    if(raw == 0 || raw == 2){
                        System.out.println("ERROR, Not expecting any piece, please remove");
                        break;
                    }
                    if(!pieces.arrived(1, this.day)){
                        System.out.println("PROD: ERROR, Why Bro?? That piece shouldn't be here!");
                    }
                    System.out.println("PROD: Expected raw: " + raw);
                    /**
                     * Chegam por ordem e simplesmente adicionamos. Looks like a good approach
                     */
                }
                opcua.previous_piece_arrived_on_ct3 = opcua.piece_arrived_on_ct3;
            }



            // Update Machine values
            for(int i= 0; i< Machines.size(); i++){
                //New piece detected
                if(opcua.machines_signal.get(i) && !opcua.previous_machines_signal.get(i)){
                    opcua.previous_machines_signal.set(i, true);
                    Orders.get(o_ID).Machines.get(i%2).info_piece_placed();
                    System.out.println("Piece detected on machine "+ (i+1) +"!");
                }else if(!opcua.machines_signal.get(i) && opcua.previous_machines_signal.get(i) || mach_update_time){
                    opcua.previous_machines_signal.set(i, false);
                    if(Machines.get(i).work_time() < (opcua.machs_time.get(i)- opcua.initial_machs_time.get(i))){
                        mach_update_time = false;
                        System.out.println("Updating machine time!");
                        // Machine has been used and time has been updated
                        System.out.println("Machine "+ i + 1+" wait_time: "+ ((opcua.machs_time.get(i)- opcua.initial_machs_time.get(i)) - Machines.get(i).work_time()));
                        Machines.get(i).info_transformation_over((opcua.machs_time.get(i)- opcua.initial_machs_time.get(i)));
                        System.out.println("Machine "+ i + 1+" total_time: " + Machines.get(i).work_time());
                        if(Machines.get(i).work_time()!= (opcua.machs_time.get(i)- opcua.initial_machs_time.get(i))){
                            System.out.println("\\u001B[31m" +"ERROR, Machine Prod time not updated correctly"+ "\\u001B[0m");
                        }
                    }else{
                        mach_update_time = true;
                    }
                 }

            }


            // TODO: add JSON verification if new order



            /***********************
             * *********************
             * ALWAYS RUNNING CYCLE*
             * *********************
             ***********************/

            // Check ERP Comms
            // Receive Piece from ERP
            //TODO: remove this
            if(erp.new_pieces || opcua.got_pieces!=null){
//                for (int i = 0; i < p_amount; i++) {
//                    pieces.new_piece(p_raw_material, p_initial_price, p_arrive_day);
//                }
//                erp.new_pieces = false;
//                system_total_pieces += p_amount;

                for (int i = 0; i < opcua.got_pieces.getnumberOfPieces(); i++) {
                    pieces.new_piece(opcua.got_pieces.getpieceType(), opcua.got_pieces.getprice(), opcua.got_pieces.getdaysToArrive());
                }
                opcua.got_pieces = null;
                system_total_pieces += opcua.got_pieces.getnumberOfPieces();
            }


            // Receive Order from ERP
            if(erp.new_order){
                Order o = new Order(o_ID, o_number_of_pieces, o_delivery, o_final_Piece, pieces);
                Orders.add(o_ID, o);
                system_order_ID++;
                erp.new_order = false;
            }

            //TODO: quando ordem terminar tratar dos valores e fazer pedidos. Find out how
            //TODO: quando ordem terminar tratar dos valores e fazer pedidos. Find out how
            //TODO: quando ordem terminar tratar dos valores e fazer pedidos. Find out how

            if(opcua.received_order_1!=null){
                Order o = new Order(opcua.received_order_1.getorderNumber(), opcua.received_order_1.getQuantity(), opcua.received_order_1.getDueDate(), opcua.received_order_1.getWorkPiece(), pieces);
                Orders.add(0, o);
                system_order_ID++;      //FIXME: do I keep this?


                opcua.received_order_1 = null;
            }

              //FIXME: try implementation with 2 orders

            if(opcua.received_order_2!=null){
                Order o = new Order(opcua.received_order_2.getorderNumber(), opcua.received_order_2.getQuantity(), opcua.received_order_2.getDueDate(), opcua.received_order_2.getWorkPiece(), pieces);
                Orders.add(1, o);
                system_order_ID++;      //FIXME: do I keep this?
                opcua.received_order_2 = null;
            }


            //! very pretty so not eliminating :)
//        Pieces.forEach(Piece::info_piece);              // Prints where are all the pieces

            /********Start PLC Order*********/
            // Which raw piece works for a certain order
            // Verify availability
            //FIXME: maybe later put this on MES_SCHEDULE
            if(Orders.get(o_ID).start_date == 0){
                if (war.specific_pieces_stored(Orders.get(o_ID).raw_piece) < Orders.get(o_ID).number_of_pieces) {
                }else if((war.specific_pieces_stored(Orders.get(o_ID).raw_piece) >= (Orders.get(o_ID).number_of_pieces / 2) && (pieces.which_day_arrives(Orders.get(o_ID).raw_piece) == this.day)) || (war.specific_pieces_stored(Orders.get(o_ID).raw_piece) >= Orders.get(o_ID).number_of_pieces)){

                    //FIXME: available machines vai deixar de ser dependente do prod. Reiniciar manualmente
                    if(opcua.available_machines[0] == 0 || opcua.available_machines[1] == 0){
                        ArrayList<Machine> m_aux = new ArrayList<>();
                        Amount_of_pieces.set(Orders.get(o_ID).piece_type -1, Orders.get(o_ID).number_of_pieces);
                        System.out.print("Starting manufacturing on Machine ");
                        if(opcua.available_machines[0] == 0){
                            m_aux.add(Machines.get(0));
                            m_aux.add(Machines.get(1));
                            System.out.println("1!");
                        }else{
                            m_aux.add(Machines.get(2));
                            m_aux.add(Machines.get(3));
                            System.out.println("2!");
                        }
                        Orders.get(o_ID).start_manufacturing(m_aux, this.day);
                        System.out.println("PROD: order updated start manufacturing!");
                    }
                }
            }else{  // If order already started

                // Are there enough pieces on the warehouse to finish the order? Then can start dispatching
                //OPTIMIZE: Make it possible to start even though it is still finishing the order and the warehouse doesnt have enough pieces
                if((war.specific_pieces_stored(Orders.get(o_ID).piece_type) == Orders.get(o_ID).number_of_pieces) || Orders.get(o_ID).dispatching){
                    Orders.get(o_ID).dispatch_started();
//                    && opcua.piece_ask_war.equals(UShort.valueOf(0))
                    if(!opcua.pos_cam1_ocup && !opcua.pos_cam2_ocup  && !opcua.piece_leave_war && !opcua.dispatched_piece){
                        System.out.println("MES: Dispatch Started");
                        System.out.println(" "+ !opcua.pos_cam1_ocup +" " +!opcua.pos_cam2_ocup + " " + !opcua.dispatched_piece);
                        if(opcua.start_dispatch(Orders.get(o_ID), Orders.get(o_ID).piece_type)){
                            Orders.get(o_ID).war_to_dispatch++;
                            System.out.println("EHEEHEH");
                        }
                    }

                    //Piece on dispatch dock and inside allowed timeline. Start dispatch procedure
                    if( ((float)(this.day * 60000)/(this.time) < 0.5) && !opcua.piece_arrived_on_pm1 && opcua.previous_piece_arrived_on_pm1 && !opcua.piece_arrived_on_pm2 && opcua.previous_piece_arrived_on_pm2){
                        if(pieces.dispatched(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, this.day, Orders.get(o_ID))){
                        }
                    }
                }else{

                    // Keep making pieces for this order
                    if(!opcua.piece_on_at1 && !opcua.piece_arrived_on_st1 && !opcua.ordered_piece){
                        if(((((Orders.get(o_ID).Machines.get(0).ID - 1) / 2) + 1) == 1 ) && !opcua.pos_cam1_ocup){
                            if(Amount_of_pieces.get(Orders.get(o_ID).piece_type -1) != 0){
                                Amount_of_pieces.set(Orders.get(o_ID).piece_type -1, Amount_of_pieces.get(Orders.get(o_ID).piece_type -1) -1);
                                opcua.update_piece_values(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, ((Orders.get(o_ID).Machines.get(0).ID - 1) / 2) + 1);
                                System.out.println("MES1: Sent Order (0,1,2): ("+opcua.aux_make_piece[0]+" ,"+opcua.aux_make_piece[1]+ ", "+opcua.aux_make_piece[2]+" )");
                        }
                        }else if(((((Orders.get(o_ID).Machines.get(0).ID - 1) / 2) + 1) == 2 ) && !opcua.pos_cam1_ocup && !opcua.pos_cam2_ocup){
                            if(Amount_of_pieces.get(Orders.get(o_ID).piece_type -1) != 0) {
                                Amount_of_pieces.set(Orders.get(o_ID).piece_type - 1, Amount_of_pieces.get(Orders.get(o_ID).piece_type - 1) - 1);
                                opcua.update_piece_values(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, ((Orders.get(o_ID).Machines.get(0).ID - 1) / 2) + 1);
                                System.out.println("MES2: Sent Order (0,1,2): (" + opcua.aux_make_piece[0] + " ," + opcua.aux_make_piece[1] + ", " + opcua.aux_make_piece[2] + " )");
                            }
                            }
                    }


                }
            }


            /**************************************************
             *           Update previous piece values         *
             **************************************************/

            opcua.previous_piece_on_at2 = opcua.piece_on_at2;
            opcua.previous_piece_on_at1 = opcua.piece_on_at1;
            opcua.previous_piece_arrived_on_st1 = opcua.piece_arrived_on_st1;


            int iteraction = 0;
            while(opcua.can_I_read_values(0 , iteraction)){
                iteraction = 1;
                try {
                    Thread.sleep(10);     //TODO: change this at is delaying our program
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
//            System.out.println("Uno");



            /**
             * Performs a whole Order!
             */
        }

    }

}
