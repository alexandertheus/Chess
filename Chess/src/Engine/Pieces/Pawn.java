package Engine.Pieces;

import java.util.ArrayList;
import Engine.Board.*;

public class Pawn implements Piece {
    private boolean debug = false;

    private PieceType type = PieceType.PAWN;
    private boolean hasMoved = false;
    private Square square;
    private Color color;
    private boolean justMoved2 = false;
    private boolean enpassantPossible = false;


    public Pawn(Square square, Color color) {
        this.square = square;
        this.color = color;
    }
    public boolean hasMoved() {return this.hasMoved;}
    public void setHasMoved(boolean hasMoved) {this.hasMoved = hasMoved;}
    public void setHasMoved2(boolean hasMoved2) {this.justMoved2 = hasMoved2;}
    public Square getSquare() {
        return this.square;
    }
    public Color getColor() {
        return this.color;
    }
    public PieceType getType() {return this.type;}
    public void setSquare(Square square) {
        this.hasMoved = true;
        this.square = square;}
    public boolean isEnpassantPossible() { return enpassantPossible; }
    public void setEnpassant(boolean enpassant) {this.enpassantPossible=enpassant;}

    public boolean getisJustMoved2() { return justMoved2; }
    public void setJustMoved2(boolean moved2){
        justMoved2 = moved2;
    }

    public ArrayList<Square> validMoves(ArrayList<ArrayList<Square>> squares) {
        if(debug){System.out.println(this.square.getRow() + " " + this.square.getCol() );}
        enpassantPossible = false;
        ArrayList<Square> possible_squares = new ArrayList<>();

        int n = 1;
        int row = this.getSquare().getRow();
        int col = this.getSquare().getCol();

        if (this.getColor() == Color.WHITE) {
            if (!this.hasMoved) {
                // = null is empty
                if (row +2 < 8 && squares.get(row+2).get(col).getPiece() == null) {
                    // System.out.println("two "+ (row+2) + " " + col);
                    possible_squares.add(squares.get(row+2).get(col));
                }
            }
            if (row+1 < 8 && squares.get(row+1).get(col).getPiece() == null) {
                // System.out.println("three " + (row+1) + " " + col);
                possible_squares.add(squares.get(row+1).get(col));
            }
            if (row+1 < 8 && col+1 < 8 && squares.get(row+1).get(col+1).getPiece() != null) {
                if (squares.get(row+1).get(col+1).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row+1).get(col+1));
                    //System.out.println("Four ");
                }
            }
            if (row+1 < 8 && col-1 >= 0 && squares.get(row+1).get(col-1).getPiece() != null) {
                if (squares.get(row+1).get(col-1).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row+1).get(col-1));
                    //System.out.println("five ");
                }
            }
            //enpassant
            if(row+1 < 8 && col-1 >= 0 && squares.get(row).get(col-1).getPiece() != null){
                if(debug){System.out.println("Reached first condition");}
                if(squares.get(row).get(col-1).getPiece().getType() == PieceType.PAWN && squares.get(row).get(col-1).getPiece().getColor() != Color.WHITE && squares.get(row+1).get(col-1).getPiece() == null) {
                    if(debug){System.out.println("Reached second condition");}
                    if(((Pawn) squares.get(row).get(col-1).getPiece()).getisJustMoved2()) {
                        enpassantPossible = true;
                        possible_squares.add(squares.get(row + 1).get(col - 1));
                        System.out.println("empas2 ");
                    }
                }
            }
            if( row+1 < 8 && col+1 < 8 && squares.get(row).get(col+1).getPiece() != null){
                if(debug){System.out.println("Reached first condition");}
                if(squares.get(row).get(col+1).getPiece().getType() == PieceType.PAWN && squares.get(row).get(col+1).getPiece().getColor() != Color.WHITE && squares.get(row+1).get(col+1).getPiece() == null ) {
                    if(debug){System.out.println("Reached second condition");}
                    if(((Pawn) squares.get(row).get(col+1).getPiece()).getisJustMoved2()) {
                        enpassantPossible = true;
                        possible_squares.add(squares.get(row+1).get(col + 1));
                        System.out.println("empas2 ");
                    }
                }
            }
            //System.out.println(possible_squares);

        }
        if (this.getColor() == Color.BLACK) {
            if (!this.hasMoved) {
                if (row-2 >= 0 && squares.get(row-2).get(col).getPiece() == null) {
                    possible_squares.add(squares.get(row-2).get(col));
                }
            }
            if (row-1 >= 0 && squares.get(row-1).get(col).getPiece() == null) {
                possible_squares.add(squares.get(row-1).get(col));
            }
            if (row-1 >= 0 && col+1 < 8 && squares.get(row-1).get(col+1).getPiece() != null) {
                if (squares.get(row-1).get(col+1).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row-1).get(col+1));
                }
            }
            if (row-1 >= 0 && col-1 >= 0 && squares.get(row-1).get(col-1).getPiece() != null) {
                if (squares.get(row-1).get(col-1).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row-1).get(col-1));
                }
            }
            //enpassant
            if(row-1 < 8 && col-1 >= 0 && squares.get(row).get(col-1).getPiece() != null){
                if(squares.get(row).get(col-1).getPiece().getType() == PieceType.PAWN && squares.get(row).get(col-1).getPiece().getColor() != Color.BLACK && squares.get(row-1).get(col-1).getPiece() == null) {
                    if(((Pawn) squares.get(row).get(col-1).getPiece()).getisJustMoved2()) {
                        enpassantPossible = true;
                        possible_squares.add(squares.get(row - 1).get(col - 1));
                        if(debug){System.out.println("empas");}
                    }
                }
            }
            if( row-1 < 8 && col+1 < 8 && squares.get(row).get(col+1).getPiece() != null){
                if(squares.get(row).get(col+1).getPiece().getType() == PieceType.PAWN && squares.get(row).get(col+1).getPiece().getColor() != Color.BLACK && squares.get(row-1).get(col+1).getPiece() == null ) {
                    if(((Pawn) squares.get(row).get(col+1).getPiece()).getisJustMoved2()) {
                        enpassantPossible = true;
                        possible_squares.add(squares.get(row - 1).get(col + 1));
                        if(debug){System.out.println("empas2 ");}
                    }
                }
            }

        }
        return possible_squares;
    }

    @Override
    public String toString() {
        return this.color+""+this.type;
    }
}
