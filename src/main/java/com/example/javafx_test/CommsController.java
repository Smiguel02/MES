package com.example.javafx_test;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

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

    public boolean piece_arrived_on_ct3 = false, previous_piece_arrived_on_ct3 = false; //TODO: needs to be Int. Verify if it is
    public short available_machines[] ={0,0};

    public boolean piece_leave_war = false;
    public UShort piece_ask_war =  UShort.valueOf(0);

    long time = 0, initial_time =0;
    boolean init_time=true;
    // Flag is 1 if is MES, 0 if is OPC_UA
    //! OPCUA only calls it when it wants to write changes
    public synchronized boolean update_values_verification() throws InterruptedException {

        // Verify if flag has been updated, if so, update_values
        // For MES:Updated main function and when it ends sends a notify
        //For OPC_UA: if true, enters in lock.wait(), if false just udpates values
        return opcua_values_updated;
    }


    public synchronized int which_piece_left(){
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
    public void run(){
        System.out.println("Comms controller is running");

        OpcUa n;
        try {
            n = OpcUa.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while(true){


            /**
             * Always updating OPC_UA code:
             * I would say here we check the values to check if any subscribed variable in the PLC has been updated. MES will get that value later.
             * If so, update the values_updated boolean, and if not we just go next
             */
            try {
                time = (long)n.ReadOneVar(n.PLCTime);
                piece_on_at2 = !(boolean)n.ReadOneVar(n.at2_Livre);  //FIXME: Como é que sei que peça saiu?
                piece_on_at1 = !(boolean)n.ReadOneVar(n.at1_Livre);  //FIXME: Como é que sei que peça entrou?
                piece_arrived_on_ct8 = !(boolean)n.ReadOneVar(n.ct8_Livre);
                piece_arrived_on_ct3 = !(boolean)n.ReadOneVar(n.ct3_Livre);
                available_machines[0] = (short)n.ReadOneVar(n.Gvlprod1);
                available_machines[1] = (short)n.ReadOneVar(n.Gvlprod2);
                piece_ask_war = (UShort)n.ReadOneVar(n.Gvl_pedir_peca_Ware);
                piece_leave_war = (boolean)n.ReadOneVar(n.Gvl_sai_peca_Ware);

                if(init_time){
                    initial_time = time;
                    init_time = false;
                }
                System.out.println("Read variables debugging -> ");
                System.out.println("at1: " + piece_on_at1);
                System.out.println("at2: " + piece_on_at2);
                System.out.println("PLCTime: " + (time - initial_time));
                System.out.println("ct8: " + piece_arrived_on_ct8);
                System.out.println("ct3: " + piece_arrived_on_ct3);
                System.out.println("Prod1: " + available_machines[0]);
                System.out.println("Prod2: " + available_machines[1]);
                System.out.println("Piece ask war: " + piece_ask_war);
                System.out.println("Piece leave war: " + piece_leave_war);

            } catch (UaException e) {
                System.out.println("DIED HERE");
                throw new RuntimeException(e);
            }

            // Ordering new piece
            if(aux_make_piece[0] != 0 && aux_make_piece[1] != 0 && !piece_on_at1 && available_machines[aux_make_piece[2]] == 0){
                try {
                    System.out.println("Ordered new piece");
                    if(n.mandarFazerPeca(aux_make_piece[0], aux_make_piece[1], aux_make_piece[2] ) == -1){
                        System.out.println("ERROR, sent peça without machine being available");
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
