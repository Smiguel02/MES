package com.example.javafx_test;

import org.eclipse.milo.opcua.stack.core.UaException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class main extends Thread{

    public void main(){}

    @Override
    public void run(){
        System.out.println("Hey there baby");
        OpcUa n = null;
        try {
            n = OpcUa.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            n.mandarFazerPeca(1,6,1);
        } catch (UaException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
