package Engine.Board;

import Engine.Board.Pieces.Piece;

public class Square {
    private int row;
    private int col;
    private Piece piece;

    public Square(int row, int column) {
        this.row = row;
        this.col = column;
        this.piece = null;
    }

    public Piece getPiece() {
        return this.piece;
    }


    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public int getRow() {return this.row;}

    public int getCol() {
        return col;
    }
    public void removePiece() { this.piece = null;}

    @Override
    public String toString() {
        if (this.piece == null){
            return "[  ]";
        }
        else {
            return "["+this.piece+"]";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Square) {
            Square toCompare = (Square) o;
            return (this.row == toCompare.getRow() && this.col == toCompare.getCol());
        }
        return false;
    }
}
