package com.example.javafx_test;

import java.util.ArrayList;
import java.util.List;

public class Production extends Thread{

    int last_no_order_piece=0;          // refers to the index of the first piece that isn't in an order

    private float plant_time;

    private int new_warehouse_piece = 0;

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
                System.out.println("Values changed!!");
                System.out.println("PROD: war.occupation - " + war.occupation() + "read values from PLC: " + opcua.number_of_pieces_on_warehouse());
             if(war.occupation() < opcua.number_of_pieces_on_warehouse()){
                System.out.println("Bro am I here??");
                if(opcua.previous_piece_on_at2){
                    System.out.println("Oh yeah piece entered");
                    for(int i= 0 ; i< 9; i++){
                        if(war.specific_pieces_stored(i + 1) < opcua.war_piece_counter.get(i).intValue()){
                            war.piece_added(i+1);
                            if(Orders.get(o_ID).piece_type == (i +1)){
                                pieces.transform(Orders.get(o_ID).raw_piece, (i +1));
                            }else{
                                System.out.println("\\u001B[31m"+ "ERROR, Piece not updated by the correct order!"+"\\u001B[0m");
                            }

                            System.out.println("PROD: Piece added to Warehouse!");
                            break;
                        }
                    }
                }
                System.out.println("PROD: Piece entered the warehouse");
                }
                    opcua.previous_piece_on_at2 = opcua.piece_on_at2;
            }
            //verify if piece out of warehouse
//            if(opcua.piece_on_at1 != opcua.previous_piece_on_at1){
//                if(!opcua.previous_piece_on_at1 ){
//                    int aux = opcua.which_piece_left();
//                    if(aux!=0) {
//                        war.piece_removed(aux);
//                        System.out.println("Piece that entered: " + aux);
//                    }
//                }
//                opcua.previous_piece_on_at1 = opcua.piece_on_at1;
//            }




            // Verify if new piece arrived on ct8 and if inside desired time
            if(opcua.piece_arrived_on_ct8 != opcua.previous_piece_arrived_on_ct8){
                if(!opcua.previous_piece_arrived_on_ct8 ){
                    int raw = pieces.expected_piece();
                    if(raw == 0){
                        System.out.println("ERROR, Not expecting any piece, please remove");
                        break;
                    }
                    if(!pieces.arrived(raw, this.day)){
                        System.out.println("PROD: ERROR, Why Bro?? That piece shouldn't be here!");
                    }
                    System.out.println("PROD: Expected raw: " + raw);
                    /**
                     * Chegam por ordem e simplesmente adicionamos. Looks like a good approach
                     */
                }
                opcua.previous_piece_arrived_on_ct8 = opcua.piece_arrived_on_ct8;
            }
            // Verify if new piece arrived on ct3 and if inside desired time
            if(opcua.piece_arrived_on_ct3 != opcua.previous_piece_arrived_on_ct3){
                if(!opcua.previous_piece_arrived_on_ct3){
                    int raw = pieces.expected_piece();
                    if(raw == 0){
                        System.out.println("ERROR, Not expecting any piece, please remove");
                        break;
                    }
                    if(!pieces.arrived(raw, this.day)){
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

//            try {
//                if(opcua.update_values_verification()){
//                    // Call update values method that verifies and updates everything
//                    opcua.i_have_updated_my_values(true);// ends by saying the values have already been updated
//                    // In case OPC_UA is in lock waiting to udpate the values, this will allow it to continue
//                    synchronized (lock){
//                        lock.notify();
//                    }
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }


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
//            if(Orders.get(o_ID).start_date == 0){
//                if (war.specific_pieces_stored(Orders.get(o_ID).raw_piece) < Orders.get(o_ID).number_of_pieces) {
//                    System.out.println("\\u001B[31m" +"ERROR, not enough pieces to complete order" +"\\u001B[0m");
//                }else{
//                    if(opcua.available_machines[0] == 0 || opcua.available_machines[1] == 0){
//                        ArrayList<Machine> m_aux = new ArrayList<>();
//                        if(opcua.available_machines[0] == 0){
//                            m_aux.add(Machines.get(0));
//                            m_aux.add(Machines.get(1));
//                        }else{
//                            m_aux.add(Machines.get(2));
//                            m_aux.add(Machines.get(3));
//                        }
//                        Orders.get(o_ID).start_manufacturing(m_aux, this.day);
//                    }
//                }
//
//            }else{  // If order already started
//                //OPTIMIZE: Make it possible to start even though it is still finishing the order and the warehouse doesnt have enough pieces
//                if((war.specific_pieces_stored(Orders.get(o_ID).piece_type) == Orders.get(o_ID).number_of_pieces) || Orders.get(o_ID).dispatching){
//                    // Are there enough pieces on the warehouse to finish the order? Then can start dispatching
//                    Orders.get(o_ID).dispatch_started();
//                    Orders.get(o_ID).war_to_dispatch += opcua.start_dispatch(Orders.get(o_ID));
//
//                    //Piece on dispatch dock and inside allowed timeline. Start dispatch procedure
//                    if( ((float)(this.day * 60000)/(this.time) < 0.5) && !opcua.piece_arrived_on_pm1 && opcua.previous_piece_arrived_on_pm1 && !opcua.piece_arrived_on_pm2 && opcua.previous_piece_arrived_on_pm2){
//                        if(!pieces.dispatched(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, this.day, Orders.get(o_ID))){
//                            System.out.println("\\u001B[31m" + "ERROR For somethingggg" + "\\u001B[0m");
//                        }
//                    }
//                }else{
//                    // Keep making pieces for this order
//                    opcua.do_piece(Orders.get(o_ID).raw_piece, Orders.get(o_ID).piece_type, ((Orders.get(o_ID).Machines.get(0).ID - 1) / 2));
//                }
//
//            }


            // Order PLC
            // Ir processando peças along with Warehouse and machine stuff. One by one in this case
//            for (int i = 0; i < Orders.get(o_ID).number_of_pieces; i++) {
//                war.piece_removed(Orders.get(o_ID).raw_piece);
//                //TODO: Add this part to MES
//                //FIXME: vou chamar piece transform no fim da cena quando entrar no Warehouse!
//                Orders.get(o_ID).Machines.get(0).info_piece_placed(plc_raw_type);
//                Orders.get(o_ID).Machines.get(0).info_transformation_begun();
//                Orders.get(o_ID).Machines.get(0).info_transformation_over(plc_machine_2);
//                Orders.get(o_ID).Machines.get(0).piece_out(15.6f);
//
//                Orders.get(o_ID).Machines.get(1).info_piece_placed(plc_machine_2);
//                Orders.get(o_ID).Machines.get(1).info_transformation_begun();
//                Orders.get(o_ID).Machines.get(1).info_transformation_over(plc_final_piece);
//                Orders.get(o_ID).Machines.get(1).piece_out(15.6f);
//            }

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
