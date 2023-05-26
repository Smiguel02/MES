package com.example.javafx_test;

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
    private int aux_make_piece[] = {0,0,0};
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

    public short available_machines[] ={0,0};

    public boolean piece_leave_war = false;
    public UShort piece_ask_war =  UShort.valueOf(0);

    long time = 0, initial_time =0;
    boolean init_time=true;
    public List<Long> machs_time = new ArrayList<>();
    public List<Long> initial_machs_time = new ArrayList<>();

    public List<Short> war_piece_counter = new ArrayList<>();
    public UShort p1, p2,p3,p4,p5,p6,p7,p8,p9;

    private Order Ord_dispatch;


    // Flag is 1 if is MES, 0 if is OPC_UA
    //! OPCUA only calls it when it wants to write changes
    public synchronized boolean update_values_verification() throws InterruptedException {

        // Verify if flag has been updated, if so, update_values
        // For MES:Updated main function and when it ends sends a notify
        //For OPC_UA: if true, enters in lock.wait(), if false just udpates values
        return opcua_values_updated;
    }

    public int start_dispatch(Order disp){

        //TODO: how will this be implemented?
        if(Ord_dispatch != null){
            System.out.println("\u001B[31m" + "Order already being dispatched" + "\\u001B[0m");
        }


        return 1;

//        return 0;   // if no new piece dispatched
    }

    public synchronized int which_piece_left(){
        System.out.println("Piece " + war_leaving+ " left the Warehouse");
        return war_leaving;   // if error
    }

    // Flag is 1 if is MES, 0 if is OPC_UA
    public synchronized void i_have_updated_my_values(boolean FLAG){
        opcua_values_updated = !FLAG;
    }

    public CommsController(Object l){
        this.lock = l;
    }

    public synchronized void do_piece(int pecaWarehouse, int pecaFabricar, int MachineToUse){
        aux_make_piece[0] = pecaWarehouse;
        aux_make_piece[1] = pecaFabricar;
        aux_make_piece[2] = MachineToUse;
    }

    @Override
    public void run() throws RuntimeException {
        System.out.println("Comms controller is running");

        OpcUa n;
        try {
            n = OpcUa.getInstance();
            sleep(100);
        } catch (Exception e) {
            System.out.println("WHATTTT");
            throw new RuntimeException(e);
        }

        for(int i= 0; i<4 ; i++){
            //Initialize machine time
            machs_time.add(0L);
            initial_machs_time.add(0L);
        }
        for(int i= 0; i<9 ; i++){
            //Pieces on warehouse told by PLC
            war_piece_counter.add((short) 0);
        }



        while(true){


            /**
             * Always updating OPC_UA code:
             * I would say here we check the values to check if any subscribed variable in the PLC has been updated. MES will get that value later.
             * If so, update the values_updated boolean, and if not we just go next
             */

            /****************************
             *          Readings        *
             ****************************/

            try {
                time = (long)n.ReadOneVar(n.PLCTime);
                piece_on_at2 = !(boolean)n.ReadOneVar(n.at2_Livre);  //FIXME: Como é que sei que peça saiu?
                piece_on_at1 = !(boolean)n.ReadOneVar(n.at1_Livre);  //FIXME: Como é que sei que peça entrou?
                piece_arrived_on_ct8 = !(boolean)n.ReadOneVar(n.ct8_Livre);
                piece_arrived_on_ct3 = !(boolean)n.ReadOneVar(n.ct3_Livre);
                piece_arrived_on_st1 = !(boolean)n.ReadOneVar(n.st1_Livre);
                piece_arrived_on_pt1 = !(boolean)n.ReadOneVar(n.pt1_Livre);
                available_machines[0] = (short)n.ReadOneVar(n.Gvlprod1);
                available_machines[1] = (short)n.ReadOneVar(n.Gvlprod2);
                piece_ask_war = (UShort)n.ReadOneVar(n.Gvl_pedir_peca_Ware);
                piece_leave_war = (boolean)n.ReadOneVar(n.Gvl_sai_peca_Ware);
                machs_time.set(0, (long)n.ReadOneVar(n.time_maq1));
                machs_time.set(1, (long)n.ReadOneVar(n.time_maq2));
                machs_time.set(2, (long)n.ReadOneVar(n.time_maq3));
                machs_time.set(3, (long)n.ReadOneVar(n.time_maq4));
                piece_arrived_on_pm1 = (boolean)n.ReadOneVar(n.PM1c_Sensor);
                piece_arrived_on_pm2 = (boolean)n.ReadOneVar(n.PM2c_Sensor);

                if(init_time){
                    initial_time = time;
                    initial_machs_time.set(0, machs_time.get(0));
                    initial_machs_time.set(1, machs_time.get(1));
                    initial_machs_time.set(2, machs_time.get(2));
                    initial_machs_time.set(3, machs_time.get(3));
                    init_time = false;
                }

                System.out.println("Read variables debugging -> ");
                System.out.println("at1: " + piece_on_at1);
                System.out.println("at2: " + piece_on_at2);
                System.out.println("PLCTime: " + (time - initial_time));
                System.out.println("ct8: " + piece_arrived_on_ct8);
                System.out.println("ct3: " + piece_arrived_on_ct3);
                System.out.println("st1: " + piece_arrived_on_st1);
                System.out.println("pt1: " + piece_arrived_on_pt1);
                System.out.println("Prod1: " + available_machines[0]);
                System.out.println("Prod2: " + available_machines[1]);
                System.out.println("Piece ask war: " + piece_ask_war);
                System.out.println("Piece leave war: " + piece_leave_war);
                System.out.println("Mach 1 Time: " + machs_time.get(0));
                System.out.println("Mach 2 Time: " + machs_time.get(1));
                System.out.println("Mach 3 Time: " + machs_time.get(2));
                System.out.println("Mach 4 Time: " + machs_time.get(3));
                System.out.println("Dispatch 1: " + piece_arrived_on_pm1);
                System.out.println("Dispatch 2: " + piece_arrived_on_pm2);

            } catch (UaException e) {
                throw new RuntimeException(e);
            }

            try {
                List<DataValue> aux_counter= n.ReadMultiVars(n.PieceCounter);
                for(int i=0;i<9;i ++){
                    war_piece_counter.set(i, (short)aux_counter.get(i).getValue().getValue());
                }
        } catch ( ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            /****************************************
             *              Orders from MES         *
             ****************************************/

            //Dispatch Order from Warehouse, with this condition can dispatch
            if(Ord_dispatch!= null && !piece_on_at1 && !piece_arrived_on_st1 && !piece_arrived_on_pt1){
                try {
                    n.mandarSairPeca(Ord_dispatch.piece_type);
                    Ord_dispatch.war_to_dispatch ++;
                } catch (UaException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Ordering new piece
            if(aux_make_piece[0] != 0 && aux_make_piece[1] != 0 && !piece_on_at1 && available_machines[aux_make_piece[2]] == 0){
                try {
                    System.out.println("Ordered new piece");
                    if(n.mandarFazerPeca(aux_make_piece[0], aux_make_piece[1], aux_make_piece[2] ) == -1){
                        System.out.println( "\\u001B[31m"+"ERROR, sent peça without machine being available" + "\\u001B[0m");
                    }else{
                        if(war_leaving != 0){
                        System.out.println("OHHHH SHIT");
                    }
                        war_leaving = aux_make_piece[1];
                        aux_make_piece[0] = 0;
                        aux_make_piece[1] = 0;
                        aux_make_piece[2] = 0;

                    }
                } catch (UaException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(!piece_on_at1 && piece_leave_war && Objects.equals(piece_ask_war, UShort.valueOf(0)) && aux_leave_piece != 0){
                try {
                    System.out.println("Dispatched a piece");
                    if(n.mandarSairPeca(aux_leave_piece) == -1){
                        System.out.println("ERROR, sent peça without machine being available");
                    }else{
                        if(war_leaving != 0){
                            System.out.println("OHHHH SHIT");
                        }
                        war_leaving = aux_leave_piece;
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

            //! At the end of the cycle updates values
            if(false){   //FIXME: change this as it depends on the values of OPC_UA
                try {
                    if(update_values_verification()){
                        synchronized (lock){
                            System.out.println("Waiting for MES to read me 0_0");
                            lock.wait();
                            System.out.println("Goshhh MES finally");
                        }
                    }
                    //Update values
                    i_have_updated_my_values(false);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * Orders from MES to both the PLC and ERP and sent from the main MES code. It has access to the
             * OPCUA and JSON classes (or will have), and can send the commands whenever. Maybe might need to make some functions synchronized (let Miguel worry with this lol)
             */

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
