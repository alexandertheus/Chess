package Engine.Board.Pieces;

import java.util.ArrayList;
import Engine.Board.*;

public class Bishop implements Piece {
    private PieceType type = PieceType.BISHOP;
    private Square square;
    private Color color;


    public Bishop(Square square, Color color) {
        this.square = square;
        this.color = color;
    }
    public Square getSquare() {
        return this.square;
    }
    public Color getColor() {
        return this.color;
    }
    public PieceType getType() {return this.type;}
    public void setSquare(Square square) {this.square = square;}

    public ArrayList<Square> validMoves(ArrayList<ArrayList<Square>> squares) {
        ArrayList<Square> possible_squares = new ArrayList<>();

        int n = 1;
        int row = this.getSquare().getRow();
        int col = this.getSquare().getCol();

        //Adds squares to possible squares along ++ diagonal line
        while (row + n < 8 && col + n < 8) {
            if (squares.get(row+n).get(col+n).getPiece() == null) {
                possible_squares.add(squares.get(row+n).get(col+n));
            }
            else {
                if (squares.get(row+n).get(col+n).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row+n).get(col+n));
                    break;
                }
                else {break;}
            }
            n++;
        }
        n=1;
        //Adds squares to possible squares along -+ diagonal line
        while (row - n >= 0 && col + n < 8) {
            if(squares.get(row-n).get(col+n).getPiece() == null) {
                possible_squares.add(squares.get(row-n).get(col+n));
            }
            else {
                if (squares.get(row-n).get(col+n).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row-n).get(col+n));
                    break;
                }
                else {break;}
            }
            n++;
        }
        n=1;
        //Adds squares to possible squares along -- diagonal line
        while (row-n >= 0 && col-n >= 0) {
            if (squares.get(row-n).get(col-n).getPiece() == null) {
                possible_squares.add(squares.get(row-n).get(col-n));
            }
            else {
                if (squares.get(row-n).get(col-n).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row-n).get(col-n));
                    break;
                }
                else {break;}
            }
            n++;
        }
        n=1;
        //Adds squares to possible squares along +- diagonal line
        while (row + n < 8 && col-n >= 0) {
            if (squares.get(row+n).get(col-n).getPiece() == null) {
                possible_squares.add(squares.get(row+n).get(col-n));
            }
            else {
                if (squares.get(row+n).get(col-n).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row+n).get(col-n));
                    break;
                }
                else {break;}
            }
            n++;
        }
        return possible_squares;


    }
    @Override
    public String toString() {
        return this.color+""+this.type;
    }
}
