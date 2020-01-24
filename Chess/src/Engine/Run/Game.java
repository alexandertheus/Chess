package Engine.Run;

import Engine.Board.CheckStatus;
import Engine.Board.ChessBoard;
import Engine.Move.MoveObjComputer;
import Engine.Move.MoveObjHuman;
import Engine.Board.Pieces.Color;
import Engine.Player.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Game{
    private Color turn = Color.WHITE;
    private ArrayList<Player> players = new ArrayList<>();

    private ChessBoard chessBoard;
    public static Scanner input = new Scanner(System.in);

    private CheckStatus checkStatus;
    private int depth;

    /*  Game Initializer
        asks for two player names
        initialises all parameters
     */
    public Game(){
        // ask user for two names for players

        players.add(new Player("White", Color.WHITE));
        players.add(new Player("Black", Color.BLACK));
        entrance();
        // create new board and set status to none
        chessBoard = new ChessBoard(depth);
        checkStatus = CheckStatus.Continue;
        chessBoard.Print();
    }

    /* Play function:
    Contains a while loop that continues until the game is ended
    asks users for move input
    when ended it prints out the winner
     */
    public void play(){
        String nameW = players.get(0).getName();
        String nameB = players.get(1).getName();

        String inMove;

        while(!chessBoard.isEnded()){
            if(this.turn == Color.WHITE) {
                System.out.println("Enter your move (White) : ");
                inMove = input.next();
                if(inMove.length() < 2 ||  inMove.length() > 6 ) {
                    System.out.println("ERROR: entered move invalid string");
                    System.out.println("Enter move for " + nameW + " Player : ");
                    inMove = input.next();
                }
                chessBoard.move(new MoveObjHuman(inMove, this.turn));
            }
            else {
                System.out.println("Computer (Black) enters move...");
                chessBoard.move(new MoveObjComputer(this.chessBoard, depth));
            }
            if(this.turn == Color.WHITE){
                this.turn = Color.BLACK;
            }
            else {this.turn = Color.WHITE;}
            chessBoard.Print();
        }

        System.out.println(winner() + " won the game!");

    }


    /*
        Depending on who won the winner is given as ouput (String)
     */
    public String winner(){
        if(chessBoard.checkStatus() == CheckStatus.BlackIsCheckmated){
            return "You (White)";
        }
        return "Computer (Black)";
    }

    /*
        Prints intro text.
        Asks for level of difficulty and sets the variable "depth" accordingly.
     */
    public void entrance() {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Notation for chess pieces:");
        System.out.println("WP = White Pawn \nWR = White Rook \nWN = White Knight \nWB = White Bishop \nWQ = White Queen \nWK = White King\nBP = Black Pawn\nBR = Black Rook\netc.\n");
        System.out.println("This version uses the algebraic notation for chess moves.");
        System.out.println("For information about how to make a move please have a look at:\nhttps://en.wikipedia.org/wiki/Algebraic_notation_(chess)");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Enter level of difficulty: ");
        System.out.println("Enter 1 for easy\nEnter 2 for medium\nEnter 3 for hard");

        String level = input.next();
        while (level.length()!=1 ||Character.getNumericValue(level.charAt(0))<1 || Character.getNumericValue(level.charAt(0)) >3) {
            System.out.println("Illegal input. Please try again.");
            System.out.println("Enter level of difficulty: ");
            System.out.println("Enter 1 for easy\nEnter 2 for medium\nEnter 3 for hard");
            level = input.next();
        }
        this.depth = Character.getNumericValue(level.charAt(0));
        System.out.println("Level chosen: "+depth);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Game starts now!");
        System.out.println("---------------------------------------------------------------------");
    }





}