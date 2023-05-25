package com.example.javafx_test;

import java.util.ArrayList;

public class Warehouse {

    public final int  WAR_MAX_CAPACITY = 32;

    private boolean is_full=false;

    private ArrayList<Integer> pieces_list = new ArrayList<>();




    /**
     * Constructor
     * Program Initiates with 20 P1 pieces
     */
    public Warehouse(){
        for(int i=0;i<20;i++){
            this.piece_added(1);
        }
        is_full = false;
    }

    /**
     * Receives info from PLC
     */
    public void piece_added(int type)
    {
        pieces_list.add(type);

        if(pieces_list.size() == WAR_MAX_CAPACITY){
            is_full =true;
        }
    }

    /**
     * Receives info from PLC
     */
    //FIXME: sould return some error message or something
    public void piece_removed(int type){
        is_full = false;
    }

    /**
     * Sends info to management or something
     * FIXME: nem sei bem o que isto é suposto fazer
     */
    public boolean at_full_capacity(){
        return false;
    }

    public int occupation(){return pieces_list.size();}

    /**
     * How many of a type stored
     */
    public int specific_pieces_stored(int piece_type){

        int num =0;
        for(int i=0; i<this.pieces_list.size();i++){
            if(this.pieces_list.get(i)== piece_type){
                num++;
            }
        }

        return num;
    }






}
