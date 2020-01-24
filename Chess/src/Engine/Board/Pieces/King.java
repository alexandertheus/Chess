package Engine.Board.Pieces;

import java.util.ArrayList;
import Engine.Board.*;

public class King implements Piece {
    private PieceType type = PieceType.KING;
    private boolean hasMoved = false;
    private Square square;
    private Color color;


    public King(Square square, Color color) {
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

        int row = this.getSquare().getRow();
        int col = this.getSquare().getCol();
        for(int row_i = row+1 ; row_i>=row-1; row_i--) {
            for (int col_i = col + 1; col_i >= col-1; col_i--) {
                if (row_i >= 0 && row_i < 8) {
                    if (col_i >= 0 && col_i < 8) {
                        if (squares.get(row_i).get(col_i).getPiece() == null) {
                            possible_squares.add(squares.get(row_i).get(col_i));
                        }
                        else if (squares.get(row_i).get(col_i).getPiece() != null && squares.get(row_i).get(col_i).getPiece().getColor() != this.color) {
                            possible_squares.add(squares.get(row_i).get(col_i));
                        }
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
