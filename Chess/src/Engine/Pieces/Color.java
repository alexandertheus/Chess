package Engine.Pieces;

public enum Color {
    BLACK,
    WHITE;

    @Override
    public String toString() {
        switch(this) {
            case BLACK: return "B";
            case WHITE: return "W";
        }
        throw new RuntimeException("Should not have gotten here");
    }
}
