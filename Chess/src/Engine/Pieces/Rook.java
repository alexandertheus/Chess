package Engine.Pieces;

import java.util.ArrayList;
import Engine.Board.Square;

public class Rook implements Piece {
    private PieceType type = PieceType.ROOK;
    private Square square;
    private Color color;
    private boolean hasMoved = false;
    public ArrayList<Square> validMoves;

    public Rook(Square square, Color color) {
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
    public boolean getHasMoved() {return this.hasMoved;}
    public void hasMoved() {this.hasMoved = true;}

    public ArrayList<Square> validMoves(ArrayList<ArrayList<Square>> squares) {
        ArrayList<Square> possible_squares = new ArrayList<>();

        int n = 1;
        int row = this.getSquare().getRow();
        int col = this.getSquare().getCol();

        //Adds squares to possible squares along positive vertical line until it meets a piece
        while (row + n < 8) {
            if (squares.get(row + n).get(col).getPiece() == null) {
                possible_squares.add(squares.get(row + n).get(col));
            }
            else {
                if (squares.get(row+n).get(col).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row + n).get(col));
                    break;
                }
                else {break;}
            }
            n++;
        }

        n = 1;
        //Adds squares to possible squares along negative vertical line until it meets a piece
        while (row-n >= 0) {
            if(squares.get(row-n).get(col).getPiece() == null) {
                possible_squares.add(squares.get(row-n).get(col));
            }
            else {
                if (squares.get(row-n).get(col).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row-n).get(col));
                    break;
                }
                else {break;}
            }
            n++;
        }
        n=1;
        //Adds squares to possible squares along positive horizontal line until it meets a piece
        while (col+n < 8 ) {
            if (squares.get(row).get(col+n).getPiece() == null) {
                possible_squares.add(squares.get(row).get(col+n));
            }
            else {
                if (squares.get(row).get(col+n).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row).get(col+n));
                    break;
                }
                else {break;}
            }
            n++;
        }
        n=1;
        //Adds squares to possible squares along negative horizontal line until it meets a piece
        while (col-n >= 0) {
            if (squares.get(row).get(col-n).getPiece() == null) {
                possible_squares.add(squares.get(row).get(col-n));
            }
            else {
                if (squares.get(row).get(col-n).getPiece().getColor() != this.getColor()) {
                    possible_squares.add(squares.get(row).get(col-n));
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