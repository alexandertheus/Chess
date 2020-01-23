package Engine.Pieces;

import java.util.ArrayList;
import Engine.Board.*;

public interface Piece {

    public Square getSquare();

    public Color getColor();

    public PieceType getType();

    public ArrayList<Square> validMoves(ArrayList<ArrayList<Square>> squares);

    public void setSquare(Square square);

    @Override
    public String toString();
}
