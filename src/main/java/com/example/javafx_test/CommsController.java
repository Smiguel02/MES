package com.example.javafx_test;

import org.eclipse.milo.opcua.stack.core.UaException;

import java.util.concurrent.ExecutionException;

/*************************************************************
 * Handles the loop to receive OPCUA and JSON communications *
 *************************************************************/

public class CommsController extends Thread{

    Object lock;
    private boolean opcua_values_updated = false;
    private boolean json_new_order = false;
    public boolean piece_arrived = true;   // TODO: test implementation, change later
    public boolean piece_warehouse = true;   // TODO: test implementation, change later



    // Flag is 1 if is MES, 0 if is OPC_UA
    //! OPCUA only calls it when it wants to write changes
    public synchronized boolean update_values_verification() throws InterruptedException {

        // Verify if flag has been updated, if so, update_values
        // For MES:Updated main function and when it ends sends a notify
        //For OPC_UA: if true, enters in lock.wait(), if false just udpates values
        return opcua_values_updated;
    }

    // Flag is 1 if is MES, 0 if is OPC_UA
    public synchronized void i_have_updated_my_values(boolean FLAG){
        opcua_values_updated = !FLAG;
    }

    public CommsController(Object l){
        this.lock = l;
    }

    @Override
    public void run(){
        System.out.println("Comms controller is running");

        OpcUa n = null;
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
        }


    }
}
