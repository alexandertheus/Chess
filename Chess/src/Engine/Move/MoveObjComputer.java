package Engine.Move;

import Engine.Board.Square;
import Engine.Pieces.Color;
import Engine.Pieces.Pawn;
import Engine.Pieces.Piece;
import Engine.Pieces.PieceType;
import Engine.Board.ChessBoard;

import java.util.ArrayList;

public class MoveObjComputer implements MoveObj {
    private PieceType chessPiece;
    //Column of destination
    private int column;
    //Row of destination
    private int row;
    //Column of origin
    private int file;
    //Row of origin
    private int rank;

    private final Color color = Color.BLACK;
    private Capture capture = Capture.NOCAPTURE; //Important: Set to Engine.Move.Capture.CAPTURE if capture and Engine.Move.Capture.NOCAPTURE if not
    private PieceType promotedPiece;
    private Check check = Check.NONE;
    private SpecialMove specialMove;
    private ChessBoard board;
    private final int depth;

    public MoveObjComputer(ChessBoard board,int depth) {
        this.board = board;
        this.depth=depth;
        getMove();
    }

    public void getMove() {
        TreeNode root = new TreeNode(this.color, this.board.getSquares(), this.depth);
        ArrayList<ArrayList<Square>> board = this.board.getSquares();

        Piece mover = board.get(root.getBestChildPiece().getSquare().getRow()).get(root.getBestChildPiece().getSquare().getCol()).getPiece();
        Square destination = board.get(root.getBestChildSquare().getRow()).get(root.getBestChildSquare().getCol());


        this.chessPiece = mover.getType();
        this.column = destination.getCol();
        this.row = destination.getRow();
        this.file = mover.getSquare().getCol();
        this.rank = mover.getSquare().getRow();
        this.specialMove = SpecialMove.AMBIGUOUS;
        if (destination.getPiece() != null) {
            this.capture = Capture.CAPTURE;
        }
        else {
            this.capture = Capture.NOCAPTURE;
        }

        if (MoveChecker.checkIfCheckmating(mover, destination, this.color,board)) {
            this.check = Check.CHECKMATE;
        }
        else if (MoveChecker.checkIfChecking(mover, destination, this.color,board)) {
            this.check = Check.CHECK;
        }
        else {
            this.check = Check.NONE;
        }




        if (chessPiece == PieceType.PAWN && (row==7 || row==0)) {
            promotedPiece = PieceType.QUEEN;
            specialMove = SpecialMove.PROMOTION;
        }


    }

    public PieceType getChessPiece() {
        return this.chessPiece;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    public int getFile() {
        return this.file;
    }
    public int getRank() { return this.rank;}

    public SpecialMove getSpecialMove() {
        return this.specialMove;
    }
    public Color getColor() {return this.color;}
    public Capture getCapture() {return this.capture;}
    public PieceType getPromotedPiece() {return this.promotedPiece;}
    public Check getCheck() {return this.check;}

}
