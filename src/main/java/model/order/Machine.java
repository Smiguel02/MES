package model.order;

public class Machine {

    private int id_machine;

    private int id_order;

    private int tool;

    private int piece_detected;

    private boolean in_use;

    private boolean broken;

    private float work_time;

    public int getId_machine() {
        return id_machine;
    }

    public void setId_machine(int id_machine) {
        this.id_machine = id_machine;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getTool() {
        return tool;
    }

    public void setTool(int tool) {
        this.tool = tool;
    }

    public int getPiece_detected() {
        return piece_detected;
    }

    public void setPiece_detected(int piece_detected) {
        this.piece_detected = piece_detected;
    }

    public boolean isIn_use() {
        return in_use;
    }

    public void setIn_use(boolean in_use) {
        this.in_use = in_use;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public float getWork_time() {
        return work_time;
    }

    public void setWork_time(float work_time) {
        this.work_time = work_time;
    }
}
