package com.example.javafx_test;

import jsoncomms.Client;
import jsoncomms.Server;
import model.order.order;
import model.order.pedidos;
import model.order.rawPieces;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/*************************************************************
 * Handles the loop to receive OPCUA and JSON communications *
 *************************************************************/

public class CommsController extends Thread{

    Object lock;
    public int aux_make_piece[] = {0,0,0};
    private int aux_leave_piece = 0;
    public int war_leaving = 0;
    private boolean opcua_values_updated = false;
    private boolean json_new_order = false;
    public boolean piece_warehouse = true;   // TODO: test implementation, change later
    public boolean piece_on_at1 = false, previous_piece_on_at1 = false; //TODO: needs to be Int. Verify if it is
    public boolean piece_on_at2 = false, previous_piece_on_at2 = false; //TODO: needs to be Int. Verify if it is
    public boolean piece_arrived_on_ct8 = false, previous_piece_arrived_on_ct8 = false; //TODO: needs to be Int. Verify if it is
    public boolean piece_arrived_on_st1 = false, previous_piece_arrived_on_st1 = false; //TODO: needs to be Int. Verify if it is
    public boolean piece_arrived_on_pt1 = false, previous_piece_arrived_on_pt1 = false; //TODO: needs to be Int. Verify if it is

    public boolean piece_arrived_on_ct3 = false, previous_piece_arrived_on_ct3 = false; //TODO: needs to be Int. Verify if it is
    public boolean piece_arrived_on_pm1 = false, previous_piece_arrived_on_pm1 = false;
    public boolean piece_arrived_on_pm2 = false, previous_piece_arrived_on_pm2 = false;

    public boolean pos_cam1_ocup, pos_cam2_ocup;

    public short available_machines[] ={0,0};

    public boolean piece_leave_war = false;
    public UShort piece_ask_war =  UShort.valueOf(0);

    private boolean can_MES_read_values = true, can_OPCUA_read_values = false;  // Synchronization variables
    long time = 0, initial_time =0;
    boolean init_time=true;
    public List<Long> machs_time = new ArrayList<>();
    public List<Long> initial_machs_time = new ArrayList<>();

    public List<Short> war_piece_counter = new ArrayList<>();
    public List<Boolean> machines_signal = new ArrayList<>();
    public List<Boolean> previous_machines_signal = new ArrayList<>();
    private Order Ord_dispatch;

    /**
     * JSON variables
     */
    public Server server;
    public rawPieces got_pieces;

    public pedidos requests;    //TODO: alterar isto no Production
    private boolean values_updated = true;
    public boolean ordered_piece = false, dispatched_piece = false;

    public order received_order_1, received_order_2;

    public boolean first_order = true;
    public boolean order_1_finished = false, order_2_finished = false;
    public float cost = 0;
    public int order_identification = 0;


    // Flag is 1 if is MES, 0 if is OPC_UA
    //! OPCUA only calls it when it wants to write changes
    public synchronized boolean update_values_verification() throws InterruptedException {

        // Verify if flag has been updated, if so, update_values
        // For MES:Updated main function and when it ends sends a notify
        //For OPC_UA: if true, enters in lock.wait(), if false just udpates values
        return opcua_values_updated;
    }

    public synchronized boolean can_I_read_values(int who, int iter){
        //who = 0, MES
        //who = 1, OPCUA

        // iter = 0, primeira iteração
        // iter = 1, segunda iteração
        boolean aux ;
        switch(iter){
            case 0:
                aux = can_OPCUA_read_values;
                can_OPCUA_read_values = can_MES_read_values;
                can_MES_read_values = aux;
                return true;

            case 1:
                switch(who){
                    case 0:
                        return !can_MES_read_values;

                    case 1:
                        return !can_OPCUA_read_values;
                }
                break;
        }


        System.out.println("ERROR, flag values badly sent");
        return false;
    }


    public boolean start_dispatch(Order disp, int type) {

        if (Ord_dispatch == null) {
            Ord_dispatch = disp;
            aux_leave_piece = type;
            System.out.println("Will dispatch piece " + type);
            return true;
        } else if (Ord_dispatch == disp) {
            aux_leave_piece = type;
            System.out.println("Will dispatch piece " + type);
            return true;
        } else {
            System.out.println("Dispatch already assigned to otehr Order!");
            return false;
        }

    }
    public int number_of_pieces_on_warehouse(){
        int sum = 0;
        for(int i=0; i<war_piece_counter.size();i++){
            sum += (int)war_piece_counter.get(i);
        }
        return sum;
    }

    // Flag is 1 if is MES, 0 if is OPC_UA
    public synchronized void i_have_updated_my_values(boolean FLAG){
        opcua_values_updated = !FLAG;
    }

    public CommsController(Object l){
        this.lock = l;
    }

    public boolean update_piece_values(int pecaWarehouse, int pecaFabricar, int MachineToUse){
//        boolean aux = values_updated;

        aux_make_piece[0] = pecaWarehouse;
        aux_make_piece[1] = pecaFabricar;
        aux_make_piece[2] = MachineToUse;

        return true;
    }
    public boolean values(){
        return values_updated;
    }

    @Override
    public void run() throws RuntimeException {
        System.out.println("Comms controller is running");

        for(int i= 0; i<4 ; i++){
            //Initialize machine time
            machs_time.add(0L);
            initial_machs_time.add(0L);
            machines_signal.add(false);
            previous_machines_signal.add(false);
        }
        for(int i= 0; i<9 ; i++){
            //Pieces on warehouse told by PLC
            war_piece_counter.add((short) 0);
        }

        OpcUa n;
        try {
            n = OpcUa.getInstance();
            sleep(100);
        } catch (Exception e) {
            System.out.println("WHATTTT");
            throw new RuntimeException(e);
        }

        server = Server.getInstance();


        while(true) {

            /**
             * Always updating OPC_UA code:
             * I would say here we check the values to check if any subscribed variable in the PLC has been updated. MES will get that value later.
             * If so, update the values_updated boolean, and if not we just go next
             */

            /****************************
             *          Readings        *
             ****************************/

            try {
                time = (long) n.ReadOneVar(n.PLCTime);
                piece_on_at2 = !(boolean) n.ReadOneVar(n.at2_Livre);
                piece_on_at1 = !(boolean) n.ReadOneVar(n.at1_Livre);
                piece_arrived_on_ct8 = !(boolean) n.ReadOneVar(n.ct8_Livre);
                piece_arrived_on_ct3 = !(boolean) n.ReadOneVar(n.ct3_Livre);
                piece_arrived_on_st1 = !(boolean) n.ReadOneVar(n.st1_Livre);
                piece_arrived_on_pt1 = !(boolean) n.ReadOneVar(n.pt1_Livre);
                available_machines[0] = (short) n.ReadOneVar(n.Gvlprod1);
                available_machines[1] = (short) n.ReadOneVar(n.Gvlprod2);
                piece_ask_war = (UShort) n.ReadOneVar(n.Gvl_pedir_peca_Ware);
                piece_leave_war = (boolean) n.ReadOneVar(n.Gvl_sai_peca_Ware);
                machs_time.set(0, (long) n.ReadOneVar(n.time_maq1));
                machs_time.set(1, (long) n.ReadOneVar(n.time_maq2));
                machs_time.set(2, (long) n.ReadOneVar(n.time_maq3));
                machs_time.set(3, (long) n.ReadOneVar(n.time_maq4));
                piece_arrived_on_pm1 = (boolean) n.ReadOneVar(n.PM1c_Sensor);
                piece_arrived_on_pm2 = (boolean) n.ReadOneVar(n.PM2c_Sensor);
                pos_cam1_ocup = (boolean)n.ReadOneVar(n.poscaminho1_ocupado);
                pos_cam2_ocup = (boolean)n.ReadOneVar(n.poscaminho2_ocupado);

                if (init_time) {
                    initial_time = time;
                    for (int i = 0; i < initial_machs_time.size(); i++) {
                        initial_machs_time.set(i, machs_time.get(i));
                    }
                    init_time = false;
                }

//                System.out.println("Read variables debugging -> ");
//                System.out.println("at1: " + piece_on_at1);
//                System.out.println("at2: " + piece_on_at2);
//                System.out.println("PLCTime: " + (time - initial_time));
//                System.out.println("ct8: " + piece_arrived_on_ct8);
//                System.out.println("ct3: " + piece_arrived_on_ct3);
//                System.out.println("st1: " + piece_arrived_on_st1);
//                System.out.println("pt1: " + piece_arrived_on_pt1);
//                System.out.println("Prod1: " + available_machines[0]);
//                System.out.println("Prod2: " + available_machines[1]);
//                System.out.println("Piece ask war: " + piece_ask_war);
//                System.out.println("Piece leave war: " + piece_leave_war);
//                System.out.println("Mach 1 Time: " + machs_time.get(0));
//                System.out.println("Mach 2 Time: " + machs_time.get(1));
//                System.out.println("Mach 3 Time: " + machs_time.get(2));
//                System.out.println("Mach 4 Time: " + machs_time.get(3));
//                System.out.println("Dispatch 1: " + piece_arrived_on_pm1);
//                System.out.println("Dispatch 2: " + piece_arrived_on_pm2);

            } catch (UaException e) {
                throw new RuntimeException(e);
            }

            try {
                war_piece_counter.set(0, (short) n.ReadOneVar(n.p1_counter));
                war_piece_counter.set(1, (short) n.ReadOneVar(n.p2_counter));
                war_piece_counter.set(2, (short) n.ReadOneVar(n.p3_counter));
                war_piece_counter.set(3, (short) n.ReadOneVar(n.p4_counter));
                war_piece_counter.set(4, (short) n.ReadOneVar(n.p5_counter));
                war_piece_counter.set(5, (short) n.ReadOneVar(n.p6_counter));
                war_piece_counter.set(6, (short) n.ReadOneVar(n.p7_counter));
                war_piece_counter.set(7, (short) n.ReadOneVar(n.p8_counter));
                war_piece_counter.set(8, (short) n.ReadOneVar(n.p9_counter));

                machines_signal.set(0, (boolean) n.ReadOneVar(n.Mach1_signal));
                machines_signal.set(1, (boolean) n.ReadOneVar(n.Mach2_signal));
                machines_signal.set(2, (boolean) n.ReadOneVar(n.Mach3_signal));
                machines_signal.set(3, (boolean) n.ReadOneVar(n.Mach4_signal));

            } catch (UaException e) {
                throw new RuntimeException(e);
            }

            /****************************************
             *              Orders from MES         *
             ****************************************/


            // Ordering new piece
            if ((aux_make_piece[0] != 0 && aux_make_piece[1] != 0  && !ordered_piece)) {
                    try {
                        System.out.println("Ordered new piece execution");
                        if (n.mandarFazerPeca(aux_make_piece[0], aux_make_piece[1], aux_make_piece[2]) == -1) {
                            System.out.println("\\u001B[31m" + "ERROR, sent peça without machine being available" + "\\u001B[0m");
                        } else {
                            ordered_piece = true;
                            aux_make_piece[0] = 0;
                            aux_make_piece[1] = 0;
                            aux_make_piece[2] = 0;
                            System.out.println("COMMS: Pieces sent to PLC!");
                        }
                    } catch (UaException | ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

        }
            if(aux_leave_piece != 0 && !dispatched_piece){
                try {
                    System.out.println("Dispatched a piece " + aux_leave_piece);
                    if(n.mandarSairPeca(aux_leave_piece) == -1){
                        System.out.println("ERROR, sent peça without machine being available");
                    }else{
                        dispatched_piece = true;
                        aux_leave_piece = 0;
                    }
                } catch (UaException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * Always updating JSON code:
             * Always running checking if received anything from JSON.
             * if so, update the json_new_order boolean, if not, just keep going
             * Might be needed to create other boolean if we think other stuff is needed
             */

            if(server.client_has_new_pieces()){
                got_pieces = server.getPieces_read();
                server.set_client_values_read();
            }

            if(first_order){
                requests = new pedidos(1, 0 , 0);   //FIXME: not sure verify
                Client client = new Client(this.requests);
                Thread client_thread = new Thread(client);
                client_thread.start();
                try {
                    client_thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                received_order_1 = client.order1;
                received_order_2 = client.order2;
                if(received_order_1 == null){
                    System.out.println("ERROR, couldn't read new order");
                    while(true){

                    }
                }
                requests.setFlag_start(0);
                first_order = false;
            }else{
                if(order_1_finished){
                    requests.setloss(cost);
                    requests.setFlag_done(order_identification);

                    Client client = new Client(this.requests);
                    Thread client_thread = new Thread(client);
                    client_thread.start();
                    try {
                        client_thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    received_order_1 = client.order1;

                    cost = 0;
                    order_identification = 0;
                    order_1_finished = false;
                }

                if(order_2_finished){
                    requests.setloss(cost);
                    requests.setFlag_done(order_identification);

                    Client client = new Client(this.requests);
                    Thread client_thread = new Thread(client);
                    client_thread.start();
                    try {
                        client_thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    received_order_2 = client.order2;

                    cost = 0;
                    order_identification = 0;
                    order_2_finished = false;
                }
            }


            /**
             * Orders from MES to both the PLC and ERP and sent from the main MES code. It has access to the
             * OPCUA and JSON classes (or will have), and can send the commands whenever. Maybe might need to make some functions synchronized (let Miguel worry with this lol)
             */

            int iteraction = 0;
            while(can_I_read_values(1, iteraction)){
                iteraction = 1;
                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
//            System.out.println("DOS");

        }


    }
}
