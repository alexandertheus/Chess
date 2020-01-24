package Engine.Player;

import Engine.Board.Square;
import Engine.Board.Pieces.Color;
import Engine.Board.Pieces.Piece;

import java.util.ArrayList;

public class Player {
    private final String name;
    private final Color color;
    private ArrayList<Piece> eaten = new ArrayList<>();

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Piece> getEaten() {return this.eaten;}

    public void addEaten(Piece piece) {this.eaten.add(piece);}

    public boolean isValidEnd(Square end) {

        return true;

    }
}