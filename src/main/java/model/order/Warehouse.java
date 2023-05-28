package Model;

public class Warehouse {
    private int id_warehouse;
    private int max_capacity;
    private boolean is_full;

    public int getId_warehouse() {
        return id_warehouse;
    }

    public void setId_warehouse(int id_warehouse) {
        this.id_warehouse = id_warehouse;
    }

    public int getMax_capacity() {
        return max_capacity;
    }

    public void setMax_capacity(int max_capacity) {
        this.max_capacity = max_capacity;
    }

    public boolean isIs_full() {
        return is_full;
    }

    public void setIs_full(boolean is_full) {
        this.is_full = is_full;
    }
}
