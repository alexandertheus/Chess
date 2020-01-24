package Engine.Board.Pieces;

public enum PieceType {
    PAWN,
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT;

    @Override
    public String toString() {
        switch(this) {
            case PAWN: return "P";
            case KING: return "K";
            case QUEEN: return "Q";
            case ROOK: return "R";
            case BISHOP: return "B";
            case KNIGHT: return "N";
        }
        throw new RuntimeException("Should not have gotten here");
    }
}
