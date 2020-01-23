package Engine.Board;

import java.util.ArrayList;
import java.util.Iterator;

public class ChessPieceIterator<Piece> implements Iterable<Piece> {

    ArrayList<Piece> pieces;

    public ChessPieceIterator() {
        pieces = new ArrayList<Piece>();
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public Iterator<Piece> iterator() {
        return (Iterator<Piece>) new PieceIterator();
    }

    class PieceIterator implements Iterator<Piece> {
        int currentIndex = 0;

        @Override
        public boolean hasNext() {
            if (currentIndex >= pieces.size()) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public Piece next() {
            return pieces.get(currentIndex++);
        }

        @Override
        public void remove() {
            pieces.remove(--currentIndex);
        }

    }
}
