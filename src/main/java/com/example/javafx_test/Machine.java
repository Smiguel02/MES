package com.example.javafx_test;

public class Machine {

    //In seconds
    private long work_time;           // Total time piece has been on the machine sensor. In seconds.

    public int  current_tool;       // current tool on the machine

    public boolean in_use;          // if machine is in use

    public int piece_detected;       //which piece number has been detected in the machine. 0 for no Piece

    public boolean broken;          // Hope not cause later we can't fix it

    Order order;               // Which order the machine is currently dealing with
    Piece_new piece;
    int ID;

    /**
     * Constructor
     * Will initiate when operation begins
     */


    public long work_time(){
        return work_time;
    }


    // Returns previous order total_cost
    //TODO: add verification steps
    public int change_order(Order new_order){

        float cost = work_time;

        order = new_order;
        work_time = 0;

        return (int)cost;   // If no cost or error
    }

    public Machine(int tool, int identification, Piece_new p){
        work_time=0;
        current_tool =tool;
        in_use=false;
        piece_detected = 0;
        broken = false;
        this.ID = identification;
        this.piece = p;
    }

    /**
     * Allows the main program to order a change of tool
     * Sends info to the PLC
     *
     * @return true if successful, false if not
     */
    public boolean change_tool(int tool){

        //Sends to PLC which tool we want
        //if good updates, if not sends false
        current_tool = tool;

        return true;
    }

    /**
     * Receives a change of tool from the PLC. Just updates variable
     */
    public void tool_changed(int tool){
        current_tool = tool;
    }

    /**
     * Receives info from the PLC that a transformation has begun.
     */
//    public void info_transformation_begun(){
//        in_use = true;
//    }

    /**
     * Updates when piece has been detected and starts counting machine use time
     * FIXME: receive timeline or start_time here??
     */
    public void info_piece_placed() {
        in_use = true;
    }



    /**
     * Status check, to PLC
     */
    public void info_operational(){
        // Hey bro how is this machine doing??
        // Eiah mano esperar nao é a minha cena
    }

    /**
     * PLC signals MES that the transformation is over and what is the new piece
     */
    public void info_transformation_over(long total_prod_time){
//        this.piece.transform(order.raw_piece, new_piece);
        work_time+=(total_prod_time-work_time);
        in_use=false;       //FIXME: only updated when machine prod whatever is 0
    }

    /**
     * Final iteration of the machine for a piece
     */
    public void piece_out(float time){
        work_time+=time;
//        System.out.println("Piece out, MACHINE " + ID + " TOTAL TIME: " + work_time);
        piece_detected = 0;
    }


//    //TODO: think about, is supposed to kill process. Not sure if needed
//    public void stop(){
//        piece_detected = 0;
//    }

}
