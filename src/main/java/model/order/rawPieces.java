package model.order;

public class rawPieces {
    private int pieceType;
    private int daysToArrive;
    private int numberOfPieces;
    private int price;
    public void setpieceType(int id) {
        this.pieceType = id;
    }
    public int getpieceType() {
        return pieceType;
    }
    public void setdaysToArrive(int id) {
        this.daysToArrive = id;
    }
    public int getdaysToArrive() {
        return daysToArrive;
    }
    public void setnumberOfPieces(int id) {
        this.numberOfPieces = id;
    }
    public int getnumberOfPieces() {
        return numberOfPieces;
    }
    public void setprice(int id) {
        this.price = id;
    }
    public int getprice() {
        return price;
    }

    public rawPieces(int type, int days, int quantity, int priceone) {
        this.pieceType= type;
        this.daysToArrive= days;
        this.numberOfPieces= quantity;
        this.price= priceone;
    }
}
