package Model;

public class OrderERP {

    private int id_order_erp;

    private int work_piece;

    private int start_piece;

    private int quantity;

    private int due_date;

    private int late_penalties;

    private int early_penalties;

    private int expected_profits;

    private String client_name;

    public int getId_order_erp() {
        return id_order_erp;
    }

    public void setId_order_erp(int id_order_erp) {
        this.id_order_erp = id_order_erp;
    }

    public int getWork_piece() {
        return work_piece;
    }

    public void setWork_piece(int work_piece) {
        this.work_piece = work_piece;
    }

    public int getStart_piece() {
        return start_piece;
    }

    public void setStart_piece(int start_piece) {
        this.start_piece = start_piece;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDue_date() {
        return due_date;
    }

    public void setDue_date(int due_date) {
        this.due_date = due_date;
    }

    public int getLate_penalties() {
        return late_penalties;
    }

    public void setLate_penalties(int late_penalties) {
        this.late_penalties = late_penalties;
    }

    public int getEarly_penalties() {
        return early_penalties;
    }

    public void setEarly_penalties(int early_penalties) {
        this.early_penalties = early_penalties;
    }

    public int getExpected_profits() {
        return expected_profits;
    }

    public void setExpected_profits(int expected_profits) {
        this.expected_profits = expected_profits;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }
}
