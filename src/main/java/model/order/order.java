package model.order;

public class order {
    private String name;
    private int orderNumber ;
    private String WorkPiece ;
    private int StartPiece;
    private int Quantity ;
    private int DueDate ;
    private int LatePen ;
    private int EarlyPen ;
    private double Profit;
    private int arrivedate;
    private int time;

    private order instance;
    order(){

    }



    public int getWorkPiece() {

        if (WorkPiece.equals("P3")){
            return 3;
        }
        if (WorkPiece.equals("P4")){
            return 4;
        }
        if (WorkPiece.equals("P5")){
            return 5;
        }
        if (WorkPiece.equals("P6")){
            return 6;
        }
        if (WorkPiece.equals("P7")){
            return 7;
        }
        if (WorkPiece.equals("P8")){
            return 8;
        }
        if (WorkPiece.equals("P9")){
            return 9;
        }
        return 0;
    }


    public void setWorkPiece(String id) {
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
    public double getProfit() {
        return Profit;
    }

    public void setProfit(double id) {
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
