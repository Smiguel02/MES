package com.example.javafx_test;

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

    CommsController opcua;

    public ArrayList<Machine> Machines = new ArrayList<>();

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


        System.out.println("Bonjour MES!");

        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        //ERP Simulation values
        int system_total_pieces=pieces.total_pieces(), system_order_ID=0;         // Order ID values are given by ERP. To test many
        int p_raw_material=2, p_arrive_day=4, p_amount=4, p_initial_price=30;   //FIXME: consigo saber sempre initial price se comprar de proposito para uma ordem. Se a ordem trocar, é easy to do the transfer
        int o_final_Piece = 7, o_number_of_pieces=4, o_delivery=9, o_ID = 0;
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
            if(opcua.piece_on_at2 != opcua.previous_piece_on_at2){
                if(opcua.previous_piece_on_at2){
                    while(opcua.number_of_pieces_on_warehouse() == war.occupation()){
                        // OPTIMIZE: IS BLOCKING, maybe think of otehr implementation
                    }
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
                System.out.println("Warehouse size -> " + war.occupation());
                    opcua.previous_piece_on_at2 = opcua.piece_on_at2;
            }

            //verify if piece out of warehouse
            if(opcua.piece_on_at1 != opcua.previous_piece_on_at1){
                if(!opcua.previous_piece_on_at1 ){
                    while(opcua.number_of_pieces_on_warehouse() == war.occupation()){
                        // OPTIMIZE: IS BLOCKING, maybe think of otehr implementation
                    }
                    for(int i= 0 ; i< 9; i++){
                        if(war.specific_pieces_stored(i + 1) > opcua.war_piece_counter.get(i).intValue()){
                            System.out.println("Piece of type " + (i+1) + " removed from Warehouse!");
                            war.piece_removed(i+1);
                            break;
                        }
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
//            for(int i= 0; i< Machines.size(); i++){
//                //New piece detected
//                if(opcua.machines_signal.get(i) && !opcua.previous_machines_signal.get(i)){
//                    opcua.previous_machines_signal.set(i, true);
//                    Orders.get(o_ID).Machines.get(i).info_piece_placed();
//                }else if(!opcua.machines_signal.get(i) && opcua.previous_machines_signal.get(i)){
//                    opcua.previous_machines_signal.set(i, false);
//                    if(Machines.get(i).work_time() < (opcua.machs_time.get(i)- opcua.initial_machs_time.get(i))){
//                        // Machine has been used and time has been updated
//                        Machines.get(i).info_transformation_over((opcua.machs_time.get(i)- opcua.initial_machs_time.get(i)));
//                        if(Machines.get(i).work_time()!= (opcua.machs_time.get(i)- opcua.initial_machs_time.get(i))){
//                            System.out.println("\\u001B[31m" +"ERROR, Machine Prod time not updated correctly"+ "\\u001B[0m");
//                        }
//                    }else{
//                        System.out.println("\\u001B[31m" +"ERROR, work time shoub be smalleeeeeer"+ "\\u001B[0m");
//                    }
//                 }
//
//            }




            // TODO: add JSON verification if new order



            /***********************
             * *********************
             * ALWAYS RUNNING CYCLE*
             * *********************
             ***********************/

            // Check ERP Comms
            // Receive Piece from ERP
            if(erp.new_pieces){
                for (int i = 0; i < p_amount; i++) {
                    pieces.new_piece(p_raw_material, p_initial_price, p_arrive_day);
                }
                erp.new_pieces = false;
                system_total_pieces += p_amount;
            }

            // Receive Order from ERP
            if(erp.new_order){
                Order o = new Order(o_ID, o_number_of_pieces, o_delivery, o_final_Piece, pieces);
                Orders.add(o_ID, o);
                system_order_ID++;
                erp.new_order = false;
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
                    if(opcua.available_machines[0] == 0 || opcua.available_machines[1] == 0){
                        ArrayList<Machine> m_aux = new ArrayList<>();
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
                    Orders.get(o_ID).war_to_dispatch += opcua.start_dispatch(Orders.get(o_ID));

                    //Piece on dispatch dock and inside allowed timeline. Start dispatch procedure
                    if( ((float)(this.day * 60000)/(this.time) < 0.5) && !opcua.piece_arrived_on_pm1 && opcua.previous_piece_arrived_on_pm1 && !opcua.piece_arrived_on_pm2 && opcua.previous_piece_arrived_on_pm2){
                        if(!pieces.dispatched(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, this.day, Orders.get(o_ID))){
                            System.out.println("\\u001B[31m" + "ERROR For somethingggg" + "\\u001B[0m");
                        }
                    }
                }else{
                    // Keep making pieces for this order
                    System.out.println("piece_on_at1: " + opcua.piece_on_at1 + "|| piece_on_at2: " + opcua.piece_on_at2 + " values: " + opcua.values());
                    if(!opcua.piece_on_at1 && !opcua.piece_on_at2 && opcua.values()){
                        System.out.println("AGAIN???????");
                        opcua.update_piece_values(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, ((Orders.get(o_ID).Machines.get(0).ID - 1) / 2) + 1);
                        while(opcua.aux_make_piece[0] != 0){
                            //OPTIMIZE: as it is blocking
                            try {
                                sleep(5);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        System.out.println("Make piece values (0,1,2): ("+opcua.aux_make_piece[0]+" ,"+opcua.aux_make_piece[1]+ ", "+opcua.aux_make_piece[2]+" )");
                    }


//                    while(!opcua.piece_on_at1 || !opcua.previous_piece_on_at1){
//                        //FIXME: this commented code doesnt work and Idk why
//                    }
//                    System.out.println("PROD: Outside of while wait loop!");
                }
//                opcua.update_piece_values(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, ((Orders.get(o_ID).Machines.get(0).ID - 1) / 2) + 1);

            }

            try {
                Thread.sleep(10);     //TODO: change this at is delaying our program
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            /**
             * Performs a whole Order!
             */
        }

    }

}
