package Engine.Move;
import Engine.Pieces.*;
import Engine.Board.*;

import java.util.ArrayList;
import java.util.Iterator;

public class TreeNode {
    private Color color;
    private int score;
    private ArrayList<ArrayList<Square>> board;
    private ArrayList<TreeNode> children;
    private Piece mover;
    private Square destination;
    private SpecialMove specialMove;
    private int depth;
    private final Color max = Color.BLACK;


    public TreeNode(Color color, ArrayList<ArrayList<Square>> board, int depth) {
        this.color = color;
        this.board = board;
        children = new ArrayList<>();
        this.depth = depth;
        createTree(depth);
        setScore();
    }

    private void createTree(int depth) {
        Color new_color;
        if (this.color == Color.WHITE) {
            new_color = Color.BLACK;
        } else {
            new_color = Color.WHITE;
        }
        if (depth>0 && checkIfKingAlive() && (checkIfEnded() == CheckStatus.Continue)) {

            ArrayList<ArrayList<Square>> board = MoveChecker.deepCopy(this.board);
            ArrayList<Piece> pieces = MoveChecker.getPiecesOf(this.color, board);
            Iterator pieceIterator = pieces.iterator();
            while (pieceIterator.hasNext()) {
                Piece piece = (Piece) pieceIterator.next();
                ArrayList<Square> squares = piece.validMoves(board);
                Iterator squaresIterator = squares.iterator();
                while (squaresIterator.hasNext()) {
                    ArrayList<ArrayList<Square>> board_1 = MoveChecker.deepCopy(board);
                    Square square = (Square) squaresIterator.next();
                    Square square_1 = board_1.get(square.getRow()).get(square.getCol());
                    Piece piece_1 = board_1.get(piece.getSquare().getRow()).get(piece.getSquare().getCol()).getPiece();

                    MoveChecker.movePieceCopy(piece_1, square_1, board_1);
                    if (piece_1.getType() == PieceType.PAWN && (square.getRow()==7 || square.getRow()==0)) {
                        piece_1 = null;
                        piece_1 = new Queen(square_1, this.color);
                        square.setPiece(piece_1);
                    }
                    addChild(new_color,board_1, depth-1);
                    board_1=null;
                }
            }
        }


    }



    private void addChild(Color color, ArrayList<ArrayList<Square>> board, int depth) {
        TreeNode child = new TreeNode(color, board, depth);

        children.add(child);
    }


    private void setScore() {
        int score=0;
        if (!checkIfKingAlive()) {
            ArrayList<Piece> pieces_black = MoveChecker.getPiecesOf(PieceType.KING, Color.BLACK, board);
            if (pieces_black.size()==0) {
                score = Integer.MIN_VALUE;
            }
            else {
                score = Integer.MAX_VALUE;
            }
        }
        else if (checkIfEnded()!=CheckStatus.Continue) {
            if (checkIfEnded() == CheckStatus.BlackIsCheckmated) {
                score = Integer.MIN_VALUE;
            }
            else {
                score = Integer.MAX_VALUE;
            }
        }

        else if (depth==0) {
            ArrayList<Piece> pieces_our = MoveChecker.getPiecesOf(this.color, board);
            ArrayList<Piece> pieces_other = MoveChecker.getPiecesOf(otherColor(), board);



            Iterator pieceIterator = pieces_our.iterator();
            int d;
            if (this.color == Color.BLACK) {
                d = 1;
            } else {
                d = -1;
            }
            while (pieceIterator.hasNext()) {
                Piece piece = (Piece) pieceIterator.next();
                if (piece.getType() == PieceType.PAWN) {
                    score = score + d;
                } else if (piece.getType() == PieceType.QUEEN) {
                    score = score + d * 5;
                } else if (piece.getType() == PieceType.KING) {
                    score = score + d;
                } else {
                    score = score + d * 2;
                }
            }

            Iterator piece2Iterator = pieces_other.iterator();
            while (piece2Iterator.hasNext()) {
                Piece piece = (Piece) piece2Iterator.next();
                if (piece.getType() == PieceType.PAWN) {
                    score = score - d;
                } else if (piece.getType() == PieceType.QUEEN) {
                    score = score - d * 5;
                } else if (piece.getType() == PieceType.KING) {
                    score = score - d;
                } else {
                    score = score - d * 2;
                }
            }

        }
        else {
            if (this.color == Color.BLACK) {
                int max = Integer.MIN_VALUE;
                Iterator childIterator = children.iterator();
                while (childIterator.hasNext()) {
                    TreeNode child = (TreeNode) childIterator.next();
                    max = Math.max(max, child.getScore());
                }
                score = max;
            }

            else {
                int min = Integer.MAX_VALUE;
                Iterator childIterator = children.iterator();
                while (childIterator.hasNext()) {
                    TreeNode child = (TreeNode) childIterator.next();
                    min = Math.min(min, child.getScore());
                }
                score = min;
            }
        }
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    private CheckStatus checkIfEnded() {
        if (MoveChecker.checkCheckmate(Color.WHITE, board)) {return CheckStatus.WhiteIsCheckmated;}
        else if (MoveChecker.checkCheckmate(Color.BLACK, board)) {return CheckStatus.BlackIsCheckmated;}
        return CheckStatus.Continue;
    }

    private boolean checkIfKingAlive() {
        ArrayList<Piece> pieces_black = MoveChecker.getPiecesOf(PieceType.KING, Color.BLACK, board);
        ArrayList<Piece> pieces_white = MoveChecker.getPiecesOf(PieceType.KING, Color.WHITE, board);
        return pieces_black.size() != 0 && pieces_white.size() != 0;
    }

    private Color otherColor() {
        if (this.color==Color.WHITE) {
            return Color.BLACK;
        }
        else {
            return Color.WHITE;
        }
    }

    public ArrayList<ArrayList<Square>> getBoard() {
        return this.board;
    }

    public ArrayList<Piece> getBestChildCombo() {
        int max = Integer.MIN_VALUE;
        Iterator childIterator = children.iterator();
        while (childIterator.hasNext()) {
            TreeNode child = (TreeNode) childIterator.next();
            if (child.getScore()>max) {
                max = child.getScore();
            }
        }

        Iterator childIterator2 = children.iterator();
        ArrayList<TreeNode> children2 = new ArrayList<>();
        while (childIterator2.hasNext()) {
            TreeNode child = (TreeNode) childIterator2.next();
            if (child.getScore()==max) {
                children2.add(child);
            }
        }
        double prob = Math.random();
        int index = (int)(prob*children2.size()-1);
        return findDifference(children2.get(index).getBoard());
    }


    public ArrayList<Piece> findDifference(ArrayList<ArrayList<Square>> board_1) {
        if (wasCapture(this.board,board_1)) {
            return findPiecesWhenCapture(board_1);
        }
        else {
            return findPiecesWhenNoCapture(board_1);
        }
    }



    private boolean wasCapture(ArrayList<ArrayList<Square>> board_1, ArrayList<ArrayList<Square>> board_2) {
        int pieces_amount_1 = 0;
        int pieces_amount_2 = 0;
        for (int i = 0; i < board_1.size(); i++) {
            for (int j = 0; j < board_1.get(0).size(); j++) {
                if (board_1.get(i).get(j).getPiece()!=null) {
                    pieces_amount_1 = pieces_amount_1+1;
                }
                if (board_2.get(i).get(j).getPiece()!=null) {
                    pieces_amount_2 = pieces_amount_2+1;
                }
            }
        }
        return !(pieces_amount_1==pieces_amount_2);
    }

    private ArrayList<Piece> findPiecesWhenNoCapture(ArrayList<ArrayList<Square>> board_1) {
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.add(null);
        pieces.add(null);
        for (int i = 0; i < board_1.size(); i++) {
            for (int j = 0; j < board_1.get(0).size(); j++) {
                if (this.board.get(i).get(j).getPiece()!=null && board_1.get(i).get(j).getPiece()==null) {
                    pieces.set(0,this.board.get(i).get(j).getPiece());
                }
                if (this.board.get(i).get(j).getPiece()==null && board_1.get(i).get(j).getPiece()!=null) {
                    pieces.set(1,board_1.get(i).get(j).getPiece());
                }
            }
        }
        return pieces;
    }

    private ArrayList<Piece> findPiecesWhenCapture(ArrayList<ArrayList<Square>> board_1) {
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.add(null);
        pieces.add(null);
        for (int i = 0; i < board_1.size(); i++) {
            for (int j = 0; j < board_1.get(0).size(); j++) {
                if (this.board.get(i).get(j).getPiece()!=null && board_1.get(i).get(j).getPiece()!=null) {
                    if ((this.board.get(i).get(j).getPiece().getColor()==Color.WHITE && board_1.get(i).get(j).getPiece().getColor()==Color.BLACK) || (this.board.get(i).get(j).getPiece().getColor()==Color.BLACK && board_1.get(i).get(j).getPiece().getColor()==Color.WHITE)) {
                        pieces.set(1, board_1.get(i).get(j).getPiece());
                    }
                }
                if (this.board.get(i).get(j).getPiece()!=null && board_1.get(i).get(j).getPiece()==null) {
                    pieces.set(0, this.board.get(i).get(j).getPiece());
                }
            }
        }
        return pieces;
    }

    public void Print (ArrayList<ArrayList<Square>> board) {
        System.out.print("\n");
        for (int row = 7; row >=0; row--) {
            for (int col = 0; col < 8; col++) {
                System.out.print(board.get(row).get(col).toString());

            }
            System.out.print("\n");
        }
    }

}
