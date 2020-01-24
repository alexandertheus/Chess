package Engine.Board.Pieces;

import java.util.ArrayList;
import Engine.Board.*;

public class Knight implements Piece {
    private PieceType type = PieceType.KNIGHT;

    private Square square;
    private Color color;


    public Knight(Square square, Color color) {
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

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if ((Math.abs(i) == 2 && Math.abs(j) == 1) || (Math.abs(i) == 1 && Math.abs(j) == 2)) {
                    if ((0 <= row + i && row + i < 8) && (0 <= col + j && col + j < 8)) {
                        if (squares.get(row + i).get(col + j).getPiece() == null || squares.get(row + i).get(col + j).getPiece().getColor() != this.getColor()) {
                            possible_squares.add(squares.get(row + i).get(col + j));
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