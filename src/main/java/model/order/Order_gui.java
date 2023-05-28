package model.order;

public class Order_gui {

    private int id_order;

    private int piece_type;

    private int raw_piece;

    private int raw_cost;

    private int pieces_arrival;

    private int number_pieces;

    private int order_completed;

    private int expected_delivery;

    private float expected_cost;

    private float production_cost;

    private float total_cost;

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getPiece_type() {
        return piece_type;
    }

    public void setPiece_type(int piece_type) {
        this.piece_type = piece_type;
    }

    public int getRaw_piece() {
        return raw_piece;
    }

    public void setRaw_piece(int raw_piece) {
        this.raw_piece = raw_piece;
    }

    public int getRaw_cost() {
        return raw_cost;
    }

    public void setRaw_cost(int raw_cost) {
        this.raw_cost = raw_cost;
    }

    public int getPieces_arrival() {
        return pieces_arrival;
    }

    public void setPieces_arrival(int pieces_arrival) {
        this.pieces_arrival = pieces_arrival;
    }

    public int getNumber_pieces() {
        return number_pieces;
    }

    public void setNumber_pieces(int number_pieces) {
        this.number_pieces = number_pieces;
    }

    public int getOrder_completed() {
        return order_completed;
    }

    public void setOrder_completed(int order_completed) {
        this.order_completed = order_completed;
    }

    public int getExpected_delivery() {
        return expected_delivery;
    }

    public void setExpected_delivery(int expected_delivery) {
        this.expected_delivery = expected_delivery;
    }

    public float getExpected_cost() {
        return expected_cost;
    }

    public void setExpected_cost(float expected_cost) {
        this.expected_cost = expected_cost;
    }

    public float getProduction_cost() {
        return production_cost;
    }

    public void setProduction_cost(float production_cost) {
        this.production_cost = production_cost;
    }

    public float getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(float total_cost) {
        this.total_cost = total_cost;
    }
}
