package model.order;

public class pedidos {
    private int flag_start;
    private int flag_done;
    private float loss;
    private int id;
    public void setFlag_start(int id) {
        this.flag_start = id;
    }
    public int getFlag_start() {
        return flag_start;
    }
    public void setFlag_done(int id) {
        this.flag_done = id;
    }
    public int getFlag_done() {
        return flag_done;
    }
    public void setloss(float id) {
        this.loss = id;
    }
    public float getloss() {
        return loss;
    }
    public void setid(int id) {
        this.id = id;
    }
    public int getid() {
        return id;
    }

    public pedidos(int start, int done, int money, int order) {
        this.flag_start= start;
        this.flag_done= done;
        this.loss= money;
        this.id=order;
    }

}