package Engine.Move;

import Engine.Pieces.Color;
import Engine.Pieces.PieceType;

public interface MoveObj {

    public PieceType getChessPiece();

    public int getColumn();

    public int getRow();

    public int getFile();
    public int getRank();

    public SpecialMove getSpecialMove();
    public Color getColor();
    public Capture getCapture();
    public PieceType getPromotedPiece();
    public Check getCheck();
}
