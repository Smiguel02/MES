package model.order;

public class Order_json {
    public String firstPiece;
    public String lastPiece;
    public String time;

    public Order_json(String firstPiece, String lastPiece, String time) {
        this.firstPiece = firstPiece;
        this.lastPiece = lastPiece;
        this.time = time;
    }

    public String getFirstPiece() {
        return firstPiece;
    }
    public String getLastPiece() {
        return lastPiece;
    }
    public String getTime() {
        return time;
    }
}