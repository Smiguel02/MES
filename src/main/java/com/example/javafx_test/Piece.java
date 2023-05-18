package com.example.javafx_test;

/**
 * Piece is created as soon as it is ordered
 */

//TODO: Can I put transformation and update_pos('M') together?
// Check wether public or private variables
//TODO: Wonder if possible paths are needed to be added.
//      - If so, where do I get the info? File or hard code? Probably file as it gets cleaner inside code


public class Piece {

    /**
     * Attributes
     */

    public int raw;     //Raw material

    public int piece_number;    //Current Piece NUmber

    public int final_piece;

    public int initial_price;      // Order price

    public int ID;      // Piece ID, not related to Order

    public int order_ID;

    public char pos;    //Where it is, 'W': Warehouse, 'C': Conveyors, 'M': Machine, 'U': Unloading Dock, 'O': Ordered, 'D': Dispatched

    public int predicted_arrive_day, predicted_dispatch_day;    //Predicted days
    // The arrive days can be different because pieces will have to wait for a piece to be removed from the conveyor
    public int arrive_day, dispatch_day;    //True days

    public boolean in_production_line, is_dispatched;   // if is in production line or has been dispatched

    public boolean in_order;            //Has been given to an order

    public float production_time; // How long on a machine sensor

    public int production_cost; // How much did it cost in production

    MyDB n = MyDB.getInstance();

    /**
     * Constructor
     */
    //TODO: might need to create a new constructor if pieces can be created posteriory to being ordered
    
    // A piece can be created as soon as an order for new pieces has been placed
    public Piece(int raw_material, int day, int price, int piece_ID){

        this.raw=raw_material;
        this.piece_number=raw_material;
        this.predicted_arrive_day=day;
        this.initial_price = price;
        this.ID=piece_ID;                              // If ID=0, then not in any order

        this.in_production_line=false;
        this.is_dispatched=false;
        this.in_order=false;
        this.pos='O';
        this.production_time=0;
        this.production_cost=0;
    }


    /**
     * METHODS
     */

    //Info from PLC, when piece arrives to Plant
    public void arrived(int day){
        arrive_day=day;
        in_production_line=true;
        pos_update('C');
    }

    //Order has been created and piece is being attributed to it
    public void add_to_order(int order_number, int predicted_dispatch, int last_piece){
        this.in_order=true;
        this.order_ID=order_number;
        this.predicted_dispatch_day=predicted_dispatch;
        this.final_piece=last_piece;
    }

    // In case we want to change piece from order/final product
    public void change_order_info(int identification, int last_piece){
        this.ID=identification;
        this.final_piece=last_piece;
    }


    //TODO: could also call this function from machine update position

    // Will update to new piece
    public void transform(int new_piece_number){

        if(pos!='W'){
            //Still do it anyway lol
            System.out.println("ERROR, piece can't/shouldn't be transformed without being inside a machine!");
        }

        this.piece_number= new_piece_number;
    }

    // Update pos with verifications when not in Machine
    public void pos_update(char new_pos){

        //Verifies if new position is valid, allowing everything though
        switch(pos){
            case 'M':
            case 'U':
                System.out.println("ERROR POS, Machine can't be in this method, specify duration");
                return;

            case 'C':
                if(new_pos == 'D' || new_pos=='O'){
                    System.out.println("ERROR, should first be on Unloading Dock!");
                    break;
                }

                pos=new_pos;
                break;

            case 'W':
            case 'O':
                //Goes mandatorily to conveyors
                if(new_pos!='C'){
                    System.out.println("ERROR, after Machine/Warehouse should go to conveyor, but it is going to " + new_pos + "!");
                }
                pos=new_pos;
                break;

            case 'D':
                System.out.println("ERROR, After being dispatched, piece should never be back!!");
                pos=new_pos;
                break;

            default:
                System.out.println("ERROR POS, Bro wtf are you sending, that doesn't even exist!");
                break;
        }


        //updates stuff
    }


    // Only available for machine
    public void pos_update(char new_pos, float value){

        if(pos!='M' && pos!= 'U'){
            System.out.println("ERROR POS, wrong method bro!");
            return;
        }

        switch(pos){
            case 'M':

                if(new_pos!='C'){
                    System.out.println("ERROR POS, After Machine should go to conveyor, going to "+new_pos+"!");
                    return;
                }

                pos=new_pos;
                production_time+=value;

                break;

            case 'U':
                if(new_pos!='D'){
                    System.out.println("ERROR POS, After Unloading should go to Dispatch, going to "+new_pos+"!");
                    return;
                }

                dispatched((int) value);
        }

    }

    // When piece is dispatched
    public void dispatched(int dispatch){
        dispatch_day=(dispatch);
        in_production_line=false;
        is_dispatched=true;
        pos='D';
        //small change
        //TODO: Add more stuff
        //  - Maybe Deconstructor
        //  - Any other info
    }

    /**
     * Gives info about piece location
     */
    public void info_piece(){
        System.out.println("Piece:");
        System.out.println("pos - " + pos);
        System.out.println("ID - " + ID);
        System.out.println("Type - " + piece_number);
        System.out.println("Initial cost - " + initial_price);
        System.out.println("Production cost - " + production_cost);
        System.out.println("Production time - " + production_time);
        System.out.println();
        System.out.println();

    }


}
