package com.example.javafx_test;

import java.util.ArrayList;

public class Order {

    //FIXME: in future stuff adicionar ordens com peças misturadas. até pq é mais facil ter sempre a mesma peça

    public int order_ID;        // Único de cada ordem, identificador da ordem

    ArrayList<Machine> Machines = new ArrayList<>();        // 2 maquinas ja existentes atribuidas

    public int piece_type;                          // Peça final desejada, um único valor
    public int raw_piece;       // Peça inicial pela qual começou. Um único valor
    public int raw_cost;        // Valor de compra de peça (Nao sei se é average ou total). Um único valor
    public int pieces_arrival;      // Dia de chegada da peça (Nao sei se é average ou total). Un único valor
    public int number_of_pieces;            //Number of pieces inside order. Um unico valor
    private int completed = 0;             // How many pieces have been already unloaded/dispatched. Um unico valor, <= number_of_pieces
    public int start_date = 0;          //Ana Rita ignore //TODO: how to implement this?       // Day scheduled by ERP to start things
    public int expected_delivery;            // When to deliver the goods, valor único
    public float expected_cost;               //Cost expected with no delays, um unico valor
    public float production_cost = 0;          // Calculado no fim, preço total de custo da ordem. um único valor. Usa machines para calcular
    public float total_cost = 0;                  // Total cost of the order, um unico valor
    public boolean dispatching = false;
    public int war_to_dispatch = 0;

    public boolean is_over = false;

    private ArrayList<Integer> pieces_dispatched_index = new ArrayList<>();           // To calculate cost later  //TODO: not yet used

    // Ana Rita: daqui para baixo doesn't matter
    private Piece_new piece;

    public int[][] paths={  // 3x5 Matrix
        {1,6,8,0,0},
        {2,3,6,8,0},
        {2,4,7,9,5}
    };


    /**
     * Creates a new order. I would put it 2 ways:
     *  --- It can create a new piece and keeps going from there
     *  --- Receives an already existing piece
     *
     *  We will have it both ways. The first to be implemented will be the second option. The ERP will probably order the piece first and only later give us the order,
     *  so is better for the MES to add already existing pieces to a new order
     */
    public Order(int order, int quantity,int delivery,int raw, int type, Piece_new p){
        order_ID=order;
        piece_type = type;      //FIXME: Sera que isto me vai eventualmente partir tudo?
        number_of_pieces=quantity;
        expected_delivery=delivery;
        piece = p;
        raw_piece = raw;
        System.out.println("ORDER: Raw piece: " + raw_piece);
        raw_cost = 15;      //FIXME: change this ducking value
        System.out.println(this.toString());
    }

    /**
     * Supposedly adds the order to schedule, FIXME: find how
     */
    public void place_to_schedule(){

    }

    public void dispatch_started(){
        dispatching = true;
    }

    // Receives from PLC signaling that the order has finished on the machines
    public void order_finished_in_machine(){
        for(int i=0; i<Machines.size(); i++){
            production_cost += Machines.get(i).work_time();
        }
    }

    public void calculate_expected_cost(){
        float production_cost;
        float deprecation_cost;

        production_cost = Piece_new.expected_prod_cost(this.raw_piece, this.piece_type) * number_of_pieces;
        deprecation_cost = raw_cost * (expected_delivery - pieces_arrival) * (float)0.01;

        expected_cost = raw_cost + production_cost + deprecation_cost;

    }

    public int which_raw_piece(int type){
        if(type<0 || type>9){
            System.out.println("ERROR, invalid type");
            return 0;
        }

        for(int i=0;i<2;i++){
            for(int j=0;j<5;j++){
                if(type == paths[i][j]){
                    raw_piece = i+1;
                    return raw_piece;
                }
            }
        }

        raw_piece = 2;
        return raw_piece;
    }


    public void start_manufacturing(ArrayList<Machine> m, int start){

        this.start_date = start;

        System.out.println("ORDER: O PLC disse-me que estava a usar a M "+m.get(0).ID+" e "+ m.get(1).ID);

        //Add existing thing to order
        for(int i=0;i<2;i++){
            Machines = m;
            if((Machines.get(i).change_order(this)) != 0){
                System.out.println("ERROR, should be 0");
            }

        }
    }

    /**
     * Called when an order is finished
     */
    public void finish(int delivery_day){

        if(completed != number_of_pieces){
            System.out.println("\\u001B[31m"+"ERROR, can't finish order cause completed smaller than number of pieces" + "\\u001B[0m");
            return;
        }

        total_cost = this.real_order_cost(delivery_day);

        System.out.println("Order total cost: " + total_cost);

        int plant_days;

        //Remove pieces from class
        for(int i=0; i<this.number_of_pieces ; i++){
            piece.remove(this.raw_piece, this.piece_type);
        }
        // Signal to machine order is finished
        for(int i=0; i<this.Machines.size() ; i++) {
            Machines.get(i).change_order(null);
        }
        System.out.println("Order has been finished!");
    }

    // We will still count the last delivery day
    public float real_order_cost(int delivery_day){

        float prod_cost=0;
        float deprecation_cost =0;

        if(raw_cost == 0 || delivery_day == pieces_arrival){
            System.out.println("\\u001B[31m"+"ERROR, Invalid values for cost"+ "\\u001B[0m");
        }

        for(int i=0; i < Machines.size(); i++){
            System.out.println("Machine " + i + " work time: " + Machines.get(i).work_time());
           prod_cost += (float)(Machines.get(i).work_time()/1000);
        }

        System.out.println("Production cost: " + prod_cost);

        if(prod_cost == 0){
            System.out.println("ERROR, production cost shouldn't be zero, not that efficient ;(");
            return 0;
        }

        int raw_total = 0;
        //FIXME: probably change this one later as all this variables might be different between pieces
        for(int i=0; i<this.number_of_pieces; i++){
            System.out.println("ORDER RAW COST: " +raw_piece+ " "+piece_type + " "+delivery_day + " "+ i+ " ");
            int aux = piece.raw_cost(raw_piece, piece_type, delivery_day, i);
            raw_total += aux;
            deprecation_cost += aux * (delivery_day - piece.arrive_date(raw_piece, piece_type, delivery_day, i));
        }
        deprecation_cost = deprecation_cost * 0.01f;

        System.out.println("Deprecation cost: " + deprecation_cost);
        System.out.println("Raw Material Cost: " + raw_total);

        return deprecation_cost + raw_total + prod_cost;
    }

    /**
     * Assuming the piece can be dispatched earlier as long as it is in the same day
     * Will only consider last piece delivery day
     */
    public void piece_dispatched(int dev_day){
        this.completed ++;  // Sums dispatched pieces
        this.pieces_dispatched_index.add(dev_day);      // Says the day they were delivered

        System.out.println("Piece dispatched on Order " + order_ID + ": Completed-> " + this.completed + " Total number: " + this.number_of_pieces);
        System.out.println("For above order, total_cost -> " + this.total_cost);

        // Is already only allowed in the first half of the day
        if(this.completed == this.number_of_pieces){
            this.finish(piece.last_dispatched(this.raw_piece, this.piece_type));
        }

    }

    /**
     * Changes when a order is supposed to be delivered based on the happening events
     */
    public void change_expected_delivery(int new_delivery) {
        expected_delivery = new_delivery;
    }

    /**
     * Where is Piece and what is happening with piece?
     */
    public void get_piece_info(){

    }









}
