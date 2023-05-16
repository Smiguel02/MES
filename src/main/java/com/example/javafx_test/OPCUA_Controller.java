package com.example.javafx_test;

import org.eclipse.milo.opcua.stack.core.UaException;

import java.util.concurrent.ExecutionException;

public class OPCUA_Controller extends Thread{

    Object lock;
    private boolean values_updated = false;
    public boolean piece_arrived = true;   // TODO: test implementation, change later
    public boolean piece_warehouse = true;   // TODO: test implementation, change later



    // Flag is 1 if is MES, 0 if is OPC_UA
    //! OPCUA only calls it when it wants to write changes
    public synchronized boolean update_values_verification() throws InterruptedException {

        // Verify if flag has been updated, if so, update_values
        // For MES:Updated main function and when it ends sends a notify
        //For OPC_UA: if true, enters in lock.wait(), if false just udpates values
        return values_updated;
    }

    // Flag is 1 if is MES, 0 if is OPC_UA
    public synchronized void i_have_updated_my_values(boolean FLAG){
        values_updated = !FLAG;
    }

    public OPCUA_Controller(Object l){
        this.lock = l;
    }

    @Override
    public void run(){
        System.out.println("Hey there baby");
        OpcUa n = null;
        try {
            n = OpcUa.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while(true){

            // Run always updating code

            // At the end of the cycle updates values
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

            try {
                n.mandarFazerPeca(1,6,1);
            } catch (UaException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }


    }
}
