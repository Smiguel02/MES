package com.example.javafx_test;

import java.util.ArrayList;

/**
 * Handles all pieces
 */
public class Piece_new {

    /**
     * Attributes
     */

    private ArrayList<Integer>[] type = new ArrayList[2];      // Existing pieces

    private ArrayList<Integer>[] type_arrival_date= new ArrayList[2];      // Arrival date

    private ArrayList<Boolean>[] have_arrived= new ArrayList[2];      // Arrival date

    private ArrayList<Integer>[] type_delivery_date= new ArrayList[2];     // Which day they were dispatched

    private ArrayList<Integer>[] type_raw_price= new ArrayList[2];       //FIXME: NOT YET being used on other classes

    public static int[][] paths={  // 3x5 Matrix
            {1,6,8,0,0},
            {2,3,6,8,0},
            {2,4,7,9,5}
    };

    public static int[][] price_path = {
            {0,20,30,0,0},
            {0,10,20,30,0},
            {0,10,10,10,15}
    };



    /**
     * METHODS
     */

    /**
     * @return 0 if error
     */

    public Piece_new(){
        type[0] = new ArrayList<>();                // Initial raw 1
        type[1] = new ArrayList<>();                // Initial raw 2
        type_arrival_date[0] = new ArrayList<>();   // Arrival date raw 1
        type_arrival_date[1] = new ArrayList<>();   // Arrival date raw 2
        have_arrived[0] = new ArrayList<>();   // If have arrived on plant
        have_arrived[1] = new ArrayList<>();   // If have arrived on plant
        type_delivery_date[0] = new ArrayList<>();  // Delivery date raw 1
        type_delivery_date[1] = new ArrayList<>();  // Delivery date raw 2
        type_raw_price[0] = new ArrayList<>();      // Price raw 1
        type_raw_price[1] = new ArrayList<>();      // Price raw 2
    }

    public int expected_piece(){
    int size = 0, index = 2;

        if(this.have_arrived[0].size() < this.have_arrived[1].size()){
            size = this.have_arrived[0].size();
            index = 1;
        }else{
            size = this.have_arrived[1].size();
            index = 0;
        }
        int i= 0;
        for(i=0; i<size; i++){
            if(!have_arrived[0].get(i) || !have_arrived[1].get(i)){
                if(type_arrival_date[0].get(i) < type_arrival_date[1].get(i)){
                    return 1;
                }else{
                    return 2;
                }
            }
        };
        for(i=i; i < this.have_arrived[index].size(); i++){
            if(!have_arrived[index].get(i)){
                return index + 1;
            }
        }
        System.out.println("ERROR, Didn't find any piece");
        return 0;
    }

    int total_pieces(){
        return type[0].size() + type[1].size();
    }

    // Should already have final piece
    int expected_deprecation_cost(int expected_delivery_day, int index, int raw_piece){

        if((!(raw_piece == 1 || raw_piece ==2)) || index > type[raw_piece-1].size()){
            System.out.println("ERROR, invalid raw piece value");
            return 0;
        }

        return expected_delivery_day - type_arrival_date[raw_piece-1].get(index);
    }

    // Array should already have final piece
    static int expected_prod_cost(int initial_raw, int final_piece){

        //Verifies if variable is already changed
        int sum=0;

        if(final_piece == 1 || final_piece ==2){
            System.out.println("ERROR, change the piece before asking cost");
            return 0;
        }
        for(int j=initial_raw-1;j<3;j++){
            sum=0;
            for(int i=1;i<5;i++){
                sum += price_path[j][i];
                if(paths[j][i] == final_piece){
                    return sum;
                }
            }
        }

        return 0;   //Error message

    }

    public void new_piece(int raw_type, int price, int arrive_day){

        if(!(raw_type == 1 || raw_type == 2)){
            System.out.println("ERROR, give me raw type only dude");
        }

        type[raw_type -1].add(raw_type);
        type_raw_price[raw_type-1].add(price);
        type_delivery_date[raw_type-1].add(0);
        type_arrival_date[raw_type-1].add(arrive_day);
        have_arrived[raw_type-1].add(false);
        System.out.println("Waiting for " +raw_type+ " piece!");
        System.out.println("Piece " + raw_type+ " size : "+  type[raw_type -1].size());
    }

    //Info from PLC, when piece arrives to Plant
    public boolean arrived(int raw_type,int day){

        int i=0;
        for(i=0 ; i<have_arrived[raw_type-1].size() ; i++){
            if(!have_arrived[raw_type-1].get(i)){
                break;
            }
        }
        if(i == have_arrived[raw_type-1].size()){
            System.out.println("PIECE: ERROR, not expecting that piece, please remove!");
        }

        System.out.println("PIECE: Arrive index -> " + i);
        if(day != type_arrival_date[raw_type-1].get(i)){
            System.out.println("ERROR, Arrival day is " + type_arrival_date[raw_type-1].get(have_arrived[raw_type-1].size()-1) +"but arrived on " + day);
        }

        if(!have_arrived[raw_type-1].get(i)){
            System.out.println("I acknowledged the piece!");
            have_arrived[raw_type-1].set(i,true);
            return true;
        }
        return false;
    }

    // Will update to new piece
    //TODO: add verification step. If new piece is valid and if current piece in fact exists
    public void transform(int raw_piece, int new_piece) {

        for (int i = 0; i < type[raw_piece - 1].size(); i++) {
            if (type[raw_piece - 1].get(i) == raw_piece) {
                type[raw_piece - 1].set(i, new_piece);
                System.out.println("On transformation: raw_piece: " + raw_piece + " | new piece: " + new_piece);
                return;
            }
        }
    }

    /**
     *  Called by somewhere by order of the PLC
     * @param raw_piece
     * @param
     * @param o Updates order values
     * @return When it is dispatched. Saves on variable
     */
    public boolean dispatched(int raw_piece, int piece_type, int today, Order o){

        //Verifies with order values
        if(o.piece_type != piece_type){
            System.out.println("\\u001B[31m" + "ERROR, piece type error when delivering" + "\\u001B[0m");
            return false;
        }


        int index=0;
        for(int i=0;i<type_arrival_date[raw_piece-1].size();i++){
            // If corresponds to the type and hasn't yet been dispatched
            if(type[raw_piece-1].get(i) == piece_type && (type_delivery_date[raw_piece-1].get(i) == 0)){
                index = i;
                break;
            }
        }

        if(index == type_arrival_date[raw_piece-1].size()){
            System.out.println("\\u001B[31m"+"ERROR didn't find corresponding variable"+ "\\u001B[0m");
            return false;
        }

        type_delivery_date[raw_piece-1].add(index,today);
        o.piece_dispatched(today);

        return true;
    }

    public int raw_cost(int raw_type, int piece_type, int delivery_day, int index_offset){

        if(!type[raw_type-1].contains(piece_type)){
            System.out.println("ERROR when viewing cost");
        }
        int count = 0;
        for(int i=0; i<type[raw_type-1].size(); i++){
            if((type[raw_type-1].get(i) == piece_type) && (type_delivery_date[raw_type-1].get(i) == delivery_day) && (type_raw_price[raw_type-1].get(i) != 0)){
                if(index_offset == count){
//                    System.out.println("Piece " + index_offset + " cost: " + type_raw_price[raw_type-1].get(i));
                    return type_raw_price[raw_type-1].get(i);
                }else count++;
            }
        }

        return 0;   //Error
    }

    // When piece is dispatched

    /**
     *
     * @param raw_piece
     * @param piece_type
     * @return Days inside plant
     */
    public int remove(int raw_piece, int piece_type){

        int plant_days;
        int index=0;

        for(int i=0;i<type_arrival_date[raw_piece-1].size();i++){
            // If corresponds to the type and has been dispatched
            if(type[raw_piece-1].get(i)==piece_type && type_delivery_date[raw_piece-1].get(i) != 0){
                index = i;
                break;
            }
        }

        if(index == type_arrival_date[raw_piece-1].size()){
            System.out.println("ERROR, didnt find index when removing piece");
            return 0;
        }

        if(type_delivery_date[raw_piece-1].get(index) == 0){
            System.out.println("ERROR, delivery date is 0, should be dispatched already");
            return 0;
        }

        plant_days = type_delivery_date[raw_piece-1].get(index) - type_arrival_date[raw_piece-1].get(index);

        //Removes from structure
        type[raw_piece-1].remove(index);
        type_arrival_date[raw_piece-1].remove(index);
        type_delivery_date[raw_piece-1].remove(index);
        type_raw_price[raw_piece -1].remove(index);

        return plant_days;
    }

    /**
     *
     * @return -1 if error
     */
    public int last_dispatched(int raw_type,int piece_type){

        System.out.println("PIECE: raw -> "+raw_type+" and type-> " + piece_type);
        int last_index = type[raw_type-1].lastIndexOf(piece_type);
        if(last_index<0){
            System.out.println("ERROR, last piece doesn't exist");
            return -1;
        }
        int delivery_day = type_delivery_date[raw_type-1].get(last_index);
        if(delivery_day<=0){
            System.out.println("ERROR, piece not dispatched or not found");
            return -1;
        }

        System.out.println((last_index - type[raw_type-1].indexOf(piece_type)+1) + " pieces delivered on day: "+ delivery_day + "!");

        return delivery_day;
    }

    public int arrive_date(int raw_type, int piece_type, int delivery_day, int index_offset){

        if(!type[raw_type-1].contains(piece_type)){
            System.out.println("ERROR when viewing cost");
        }
        int count = 0;
        for(int i=0; i<type[raw_type-1].size(); i++){
            if((type[raw_type-1].get(i) == piece_type) && (type_delivery_date[raw_type-1].get(i) == delivery_day)){
                if(index_offset == count){
//                    System.out.println("Piece " + index_offset + " arrival_date: " + type_arrival_date[raw_type-1].get(i));
                    return type_arrival_date[raw_type-1].get(i);
                }else count++;
            }
        }

        return 0;   //Error
    }

    public int which_day_arrives(int raw){
        for(int i= 0; i<type_arrival_date[raw-1].size(); i++){
            if(!have_arrived[raw-1].get(i)){
                return type_arrival_date[raw -1].get(i);
            }
        }
    return 0;
    }

    /**
     * Gives info about piece location
     */
    public void info_piece(){
        System.out.println("Type 1: " + type[0].size()+ " Type 2: " + type[1].size());
    }

    public int how_many_of_certain_piece(int raw, int piece){
        int count = 0;
        for(int i=0; i<type[raw - 1].size(); i++){
            if(type[raw-1].get(i) == piece){
                count ++;
            }
        }
        return count;
    }


}
