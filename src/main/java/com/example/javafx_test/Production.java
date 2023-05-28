package com.example.javafx_test;

import model.order.pedidos;
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
    MyDB mydb = MyDB.getInstance();

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
            if(((int)(time/60000) +1) > day){
                System.out.println("/************************************************\n" +
                                   "***** DAY " + ((int)(time/60000) + 1) + "*************************\n" +
                                    "******************************************************/");
                mydb.midDay=false;

            }
            this.day = (int)(this.time / 60000) + 1;

           if((int)(this.time % 60000)>30000){
               mydb.midDay=true;
           }
            mydb.updateTenpo(this.time);
            mydb.updateDia(this.day);


            /***********************
             ***********************
             ******CHECK COMMS******
             ***********************
             ***********************/


                // verify if new piece inside warehouse
                if (opcua.piece_on_at2 != opcua.previous_piece_on_at2 || war_update_entering) {
                    if (opcua.previous_piece_on_at2 || war_update_entering) {
                        if (opcua.number_of_pieces_on_warehouse() == war.occupation()) {
                            war_update_entering = true;
                        } else {
                            war_update_entering = false;
                            for (int i = 0; i < 9; i++) {
                                if (war.specific_pieces_stored(i + 1) < opcua.war_piece_counter.get(i).intValue()) {
                                    System.out.println("Piece of type " + (i + 1) + " added to Warehouse!");
                                    war.piece_added(i + 1);
                                    for(int j = 0; j < 2;j++) {
                                        if (Orders.get(j).piece_type == (i + 1)) {
                                            System.out.println("Piece transformation happened and notified order!");
                                            pieces.transform(Orders.get(j).raw_piece, (i + 1));
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println("Warehouse Entering size -> " + war.occupation());
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
                                opcua.piece_leaving_war = false;
                                break;
                            }
                        }
                        System.out.println("Warehouse Leaving size -> " + war.occupation());
                    }
                }
            }




            // Verify if new piece arrived on ct8 and if inside desired time
            if(opcua.piece_arrived_on_ct8 != opcua.previous_piece_arrived_on_ct8){
                if(!opcua.previous_piece_arrived_on_ct8 ){
                    int raw = pieces.expected_piece();
                    if(!pieces.arrived(2, this.day)){
                        System.out.println("PROD: ERROR, Why Bro?? That piece shouldn't be here!");
                    }
                    System.out.println("PROD: Expected raw: " + raw);
                    /**
                     * Chegam por ordem e simplesmente adicionamos. Looks like a good approach
                     */
                }
            }

            // Verify if new piece arrived on ct3 and if inside desired time. Only p1 pieces
            if(opcua.piece_arrived_on_ct3 != opcua.previous_piece_arrived_on_ct3){
                if(!opcua.previous_piece_arrived_on_ct3){
                    int raw = pieces.expected_piece();
                    if(!pieces.arrived(1, this.day)){
                        System.out.println("PROD: ERROR, Why Bro?? That piece shouldn't be here!");
                    }
                    System.out.println("PROD: Expected raw: " + raw);
                    /**
                     * Chegam por ordem e simplesmente adicionamos. Looks like a good approach
                     */
                }
            }

            // Update Machine values
            for(int i= 0; i< Machines.size(); i++){
                //New piece detected
                if(opcua.machines_signal.get(i) && !opcua.previous_machines_signal.get(i)){
                    opcua.previous_machines_signal.set(i, true);
                    Machines.get(i).info_piece_placed();
                    System.out.println("Piece detected on machine "+ (i+1) +"!");
                }else if(!opcua.machines_signal.get(i) && opcua.previous_machines_signal.get(i) || mach_update_time){
                    opcua.previous_machines_signal.set(i, false);
                    if(Machines.get(i).work_time() < (opcua.machs_time.get(i)- opcua.initial_machs_time.get(i))){
                        mach_update_time = false;
                        System.out.println("Updating machine time!");
                        // Machine has been used and time has been updated
                        System.out.println("Machine "+ (i + 1)+" wait_time: "+ ((opcua.machs_time.get(i)- opcua.initial_machs_time.get(i)) - Machines.get(i).work_time()));
                        Machines.get(i).info_transformation_over((opcua.machs_time.get(i)- opcua.initial_machs_time.get(i)));
                        System.out.println("Machine "+ (i + 1 )+" total_time: " + Machines.get(i).work_time());
                        if(Machines.get(i).work_time()!= (opcua.machs_time.get(i)- opcua.initial_machs_time.get(i))){
                            System.out.println("\\u001B[31m" +"ERROR, Machine Prod time not updated correctly"+ "\\u001B[0m");
                        }
                    }else{
                        mach_update_time = true;
                    }
                 }

            }



            /***********************
             * *********************
             * ALWAYS RUNNING CYCLE*
             * *********************
             ***********************/

            // Check ERP Comms
            // Receive Piece from ERP
            if(opcua.got_pieces!=null){
                for (int i = 0; i < opcua.got_pieces.getnumberOfPieces(); i++) {
                    pieces.new_piece(opcua.got_pieces.getpieceType(), opcua.got_pieces.getprice(), opcua.got_pieces.getdaysToArrive() + this.day-1);
                }
                System.out.println("I have " + pieces.how_many_of_certain_piece( opcua.got_pieces.getpieceType(), opcua.got_pieces.getpieceType()) + " Pieces of type " + opcua.got_pieces.getpieceType() + "in Stock!" );
                system_total_pieces += opcua.got_pieces.getnumberOfPieces();
                opcua.got_pieces = null;
        }


            //TODO: quando ordem terminar tratar dos valores e fazer pedidos. Find out how
            //TODO: quando ordem terminar tratar dos valores e fazer pedidos. Find out how
            //TODO: quando ordem terminar tratar dos valores e fazer pedidos. Find out how

            if(opcua.received_order_1!=null){
                Order o = new Order(opcua.received_order_1.getorderNumber(), opcua.received_order_1.getQuantity(), opcua.received_order_1.getDueDate(), opcua.received_order_1.getStartPiece(), opcua.received_order_1.getWorkPiece(), pieces);
                Orders.add(0, o);
                system_order_ID++;      //FIXME: do I keep this?

                opcua.received_order_1 = null;
            }

              //FIXME: try implementation with 2 orders

            if(opcua.received_order_2!=null){
                Order o = new Order(opcua.received_order_2.getorderNumber(), opcua.received_order_2.getQuantity(), opcua.received_order_2.getDueDate(),opcua.received_order_2.getStartPiece(), opcua.received_order_2.getWorkPiece(), pieces);
                Orders.add(1, o);
                system_order_ID++;      //FIXME: do I keep this?
                opcua.received_order_2 = null;
            }


            //! very pretty so not eliminating :)
//        Pieces.forEach(Piece::info_piece);              // Prints where are all the pieces

            if(!opcua.first_order) {
                /********Start PLC Order*********/
                // Which raw piece works for a certain order
                // Verify availability
                //FIXME: maybe later put this on MES_SCHEDULE
                int count = 0;
                boolean prod_order_sent = false;
                while(count < 2) {

                    int j = 0;
                    if(count == 0){
                        if(Orders.get(0).expected_delivery > Orders.get(1).expected_delivery){
                            j = 1;
                        }
                    }else{
                        if(Orders.get(0).expected_delivery > Orders.get(1).expected_delivery){
                            j = 0;
                        }else{
                            j = 1;
                        }
                    }
                    count ++;
                    if(Orders.get(j).is_over){
                        continue;
                    }
                    if (Orders.get(j).start_date == 0) {
                     if (((war.specific_pieces_stored(Orders.get(j).raw_piece) > (Orders.get(j).number_of_pieces / 2)) && (pieces.which_day_arrives(Orders.get(j).raw_piece) == this.day)) || (war.specific_pieces_stored(Orders.get(j).raw_piece) >= Orders.get(j).number_of_pieces)) {
                         System.out.println("War stored of type " + Orders.get(j).raw_piece+ " -> " + war.specific_pieces_stored(Orders.get(j).raw_piece));
                         System.out.println("Order "+ j +" has this many type " + Orders.get(j).raw_piece+ " Pieces -> " + Orders.get(j).number_of_pieces);
                            //FIXME: available machines vai deixar de ser dependente do prod. Reiniciar manualmente
                            if (opcua.available_machines[0] == 0 || opcua.available_machines[1] == 0) {
                                ArrayList<Machine> m_aux = new ArrayList<>();
                                Amount_of_pieces.set(Orders.get(j).piece_type - 1, Orders.get(j).number_of_pieces);
                                System.out.print("Starting manufacturing on Machine ");
                                if (opcua.available_machines[0] == 0) {
                                    m_aux.add(Machines.get(0));
                                    m_aux.add(Machines.get(1));
                                    opcua.available_machines[0] = Orders.get(j).piece_type;
                                    System.out.println("1!");
                                } else {
                                    m_aux.add(Machines.get(2));
                                    m_aux.add(Machines.get(3));
                                    opcua.available_machines[1] = Orders.get(j).piece_type;
                                    System.out.println("2!");
                                }
                                Orders.get(j).start_manufacturing(m_aux, this.day);
                                System.out.println("PROD: order updated start manufacturing!");
                            }
                        }
                    } else {  // If order already started
                        // Are there enough pieces on the warehouse to finish the order? Then can start dispatching
                        //OPTIMIZE: Make it possible to start even though it is still finishing the order and the warehouse doesnt have enough pieces
                        if ((war.specific_pieces_stored(Orders.get(j).piece_type) == Orders.get(j).number_of_pieces) || Orders.get(j).dispatching) {
                            if(!Orders.get(j).dispatching){
                                System.out.println("Dispatching of order "+ j + " has begun");
                                opcua.available_machines[(Orders.get(j).Machines.get(0).ID-1) / 2] = 0; //OPTIMIZE, é um desperdicio isto estar asisma  espera
                            }
                            Orders.get(j).dispatch_started();

                            if (!opcua.pos_cam1_ocup && !opcua.pos_cam2_ocup && !opcua.piece_leave_war && !opcua.piece_leaving_war && !(Orders.get(j).war_to_dispatch == Orders.get(j).number_of_pieces)) {
                                System.out.println("MES: Dispatch Started");
                                System.out.println(" " + !opcua.pos_cam1_ocup + " " + !opcua.pos_cam2_ocup + " " + !opcua.piece_leaving_war);
                                if (opcua.start_dispatch(Orders.get(j), Orders.get(j).piece_type)) {
                                    Orders.get(j).war_to_dispatch++;
                                    System.out.println("EHEEHEH");
                                    prod_order_sent = true;
                                }
                            }
                            //Piece on dispatch dock and inside allowed timeline. Start dispatch procedure
                            //OPTIMIZE: removed wait for delivery day logic
                            if (((!opcua.piece_arrived_on_pm1 && opcua.previous_piece_arrived_on_pm1) || (!opcua.piece_arrived_on_pm2 && opcua.previous_piece_arrived_on_pm2)) && !Orders.get(j).is_over) {
                                System.out.println("Order " +j);
                                if (pieces.dispatched(Orders.get(j).raw_piece, Orders.get(j).piece_type, this.day, Orders.get(j))) {
                                    System.out.println("Dispatched a Piece!");
                                    //Piece has been dispatched!
                                    System.out.println("Cost at this point in time: " + Orders.get(j).total_cost);
                                    if(Orders.get(j).total_cost != 0){
                                        System.out.println("\nGRANDEEEEE, ORDER IS OVERR\n");
                                        opcua.requests = new pedidos(0 , 1, Orders.get(j).total_cost , Orders.get(j).order_ID);
                                        opcua.finished_dispatch();
                                        Orders.get(j).is_over = true;
                                        opcua.initial_machs_time = opcua.machs_time;
                                    }
                                }
                            }
                        } else {

                            // Keep making pieces for this order
                            if (!opcua.piece_on_at1 && !opcua.piece_arrived_on_st1 && !opcua.piece_leaving_war && !prod_order_sent) {
                                //Se na Machine 2
                                if (((((Orders.get(j).Machines.get(0).ID - 1) / 2) + 1) == 1) && !opcua.pos_cam1_ocup) {
                                    if (Amount_of_pieces.get(Orders.get(j).piece_type - 1) != 0) {
                                        Amount_of_pieces.set(Orders.get(j).piece_type - 1, Amount_of_pieces.get(Orders.get(j).piece_type - 1) - 1);
                                        opcua.update_piece_values(Orders.get(j).raw_piece, Orders.get(j).piece_type, ((Orders.get(j).Machines.get(0).ID - 1) / 2) + 1);
                                        System.out.println("MES1: Sent Order (0,1,2): (" + opcua.aux_make_piece[0] + " ," + opcua.aux_make_piece[1] + ", " + opcua.aux_make_piece[2] + " )");
                                        prod_order_sent = true;
                                    }
                                    // Se na Machine 1
                                } else if (((((Orders.get(j).Machines.get(0).ID - 1) / 2) + 1) == 2) && !opcua.pos_cam1_ocup && !opcua.pos_cam2_ocup) {
                                    if (Amount_of_pieces.get(Orders.get(j).piece_type - 1) != 0) {
                                        Amount_of_pieces.set(Orders.get(j).piece_type - 1, Amount_of_pieces.get(Orders.get(j).piece_type - 1) - 1);
                                        opcua.update_piece_values(Orders.get(j).raw_piece, Orders.get(j).piece_type, ((Orders.get(j).Machines.get(0).ID - 1) / 2) + 1);
                                        System.out.println("MES2: Sent Order (0,1,2): (" + opcua.aux_make_piece[0] + " ," + opcua.aux_make_piece[1] + ", " + opcua.aux_make_piece[2] + " )");
                                        prod_order_sent = true;
                                    }
                                }
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
            opcua.previous_piece_arrived_on_ct8 = opcua.piece_arrived_on_ct8;
            opcua.previous_piece_arrived_on_ct3 = opcua.piece_arrived_on_ct3;
            opcua.previous_piece_arrived_on_pm1 = opcua.piece_arrived_on_pm1;
            opcua.previous_piece_arrived_on_pm2 = opcua.piece_arrived_on_pm2;

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
