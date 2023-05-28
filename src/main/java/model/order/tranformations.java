package model.order;

public class tranformations {
    private static int transforms;
    private static int transforms_machine;
    private static int time;

    public static int get_part(String final_piece){

        if (final_piece=="P6"){
            transforms=1;
            transforms_machine=1;
            time=20;
        } else if (final_piece=="P3") {
            transforms=2;
            transforms_machine=2;
            time=10;
        } else if (final_piece=="P4") {
            transforms=2;
            transforms_machine=3;
            time=10;
        } else if (final_piece== "P5") {
            transforms=9;
            transforms_machine=4;
            time=15;
        } else if (final_piece== "P6") {
            transforms=3;
            transforms_machine=1;
            time=20;
        } else if (final_piece=="P7") {
            transforms=4;
            transforms_machine=4;
            time=10;
        } else if (final_piece=="P8") {
            transforms=6;
            transforms_machine=3;
            time=30;
        } else if (final_piece=="P9") {
            transforms=7;
            transforms_machine=3;
            time=10;
        }
        return transforms;
    }
    public int get_time(int final_piece ){
        time=0;
        int piece= final_piece;
        while(piece!= 1 && piece!= 2){
            if (piece == 9){
                time+=10;
                piece=7;
            }
            else if (piece == 8){
                time+=30;
                piece=6;
            }
            else if (piece == 7){
                time+=10;
                piece=4;
            }
            else if (piece == 6){
                time+=20;
                piece=1;
            }
            else if (piece == 5){
                time+=15;
                piece=9;
            }
            else if (piece == 4){
                time+=10;
                piece=2;
            }
            else if (piece == 3){
                time+=10;
                piece=2;
            }
        }
        return time;
    } public static int get_piece(int final_piece){
        int piece= final_piece;
        while((piece!= 1) && (piece!= 2) ){
            if (piece == 9 ){
                piece=7;
            }
            if (piece == 8){
                piece=6;
            }
            if (piece == 7){
                piece=4;
            }
            if (piece == 6){
                piece=1;
            }
            if (piece == 5){
                piece=9;
            }
            if (piece == 4){
                piece=2;
            }
            if (piece == 3){
                piece=2;
            }
        }
        return piece;
    }
}
