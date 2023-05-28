package model.order;

public class order {
    private String name;
    private int orderNumber ;
    private int WorkPiece ;
    private int StartPiece;
    private int Quantity ;
    private int DueDate ;
    private int LatePen ;
    private int EarlyPen ;
    private float Profit;
    private int arrivedate;
    private int time;

    public int getWorkPiecenumber(String piece) {

        if (piece.equals("P3")){
            return 3;
        }
        if (piece.equals("P4")){
            return 4;
        }
        if (piece.equals("P5")){
            return 5;
        }
        if (piece.equals("P6")){
            return 6;
        }
        if (piece.equals("P7")){
            return 7;
        }
        if (piece.equals("P8")){
            return 8;
        }
        if (piece.equals("P9")){
            return 9;
        }
        return 0;
    }
    public int getWorkPiece() {
        return WorkPiece;
    }

    public void setWorkPiece(String id) {
        WorkPiece = getWorkPiecenumber(id);
    }
    public void setWorkPiece1(int id) {
        WorkPiece = id;
    }
    public int getStartPiece() {
        return StartPiece;
    }

    public void setStartPiece(int id) {

        if (id!=1 || id !=2 ){
            id= tranformations.get_piece(id);
            StartPiece = id;
        }
        else {
            StartPiece = id;
        }
    }

    public String getnameLabel() {
        return name;
    }

    public void setnameLabel(String imagSrc) {
        name = imagSrc;
    }
    public int getorderNumber() {
        return orderNumber;
    }

    public void setorderNumber(int id) {
        this.orderNumber = id;
    }
    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int id) {
        this.Quantity = id;
    }
    public int getDueDate() {
        return DueDate;
    }

    public void setDueDate(int id) {
        this.DueDate = id;
    }
    public int getLatePen() {
        return LatePen;
    }

    public void setLatePen(int id) {
        this.LatePen = id;
    }
    public int getEarlyPen() {
        return EarlyPen;
    }

    public void setEarlyPen(int id) {
        this.EarlyPen = id;
    }
    public float getProfit() {
        return Profit;
    }

    public void setProfit(float id) {
        this.Profit = id;
    }
    public int getArrivedate() {
        return arrivedate;
    }

    public void setArrivedate(int id) {
        this.arrivedate = id;
    }
    public int getTime() {
        return time;
    }

    public void setTime(int id) {
        this.time = id;
    }
}