package Engine.Move;

import Engine.Board.Pieces.*;

import java.util.ArrayList;
import Engine.Board.*;

public class MoveChecker {

    public static Piece directPieceTranslation(PieceType type, Square square, Color color) {
        switch (type){
            case BISHOP:
                return new Bishop(square, color);
            case QUEEN:
                return new Queen(square, color);
            case ROOK:
                return new Rook(square, color);
            case PAWN:
                return new Pawn(square, color);
            case KNIGHT:
                return new Knight(square, color);
            case KING:
                return new King(square, color);
        }
        throw new RuntimeException("Should not have gotten here");
    }

    public static void movePieceCopy(Piece piece, Square destination, ArrayList<ArrayList<Square>> copy) {
        piece.getSquare().removePiece();
        piece.setSquare(copy.get(destination.getRow()).get(destination.getCol()));
        destination.setPiece(piece);
    }

    public static ArrayList<ArrayList<Square>> deepCopy(ArrayList<ArrayList<Square>> board) {
        ArrayList<ArrayList<Square>> copy = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            copy.add(new ArrayList<Square>());
            for (int col = 0; col < 8; col++) {
                copy.get(row).add(new Square(row, col));
                if (board.get(row).get(col).getPiece() != null) {
                    PieceType type = board.get(row).get(col).getPiece().getType();
                    Color color = board.get(row).get(col).getPiece().getColor();
                    Piece piece = directPieceTranslation(type, copy.get(row).get(col), color);
                    if (piece instanceof Pawn && board.get(row).get(col).getPiece() instanceof Pawn) {
                        ((Pawn) piece).setHasMoved(((Pawn)board.get(row).get(col).getPiece()).hasMoved());
                        ((Pawn) piece).setHasMoved2(((Pawn)board.get(row).get(col).getPiece()).getisJustMoved2());
                        ((Pawn) piece).setEnpassant(((Pawn)board.get(row).get(col).getPiece()).isEnpassantPossible());
                    }
                    copy.get(row).get(col).setPiece(piece);
                }
            }
        }
        return copy;
    }

    public static ArrayList<Piece> getPiecesOf (PieceType type, Color color, ArrayList<ArrayList<Square>> copy){
        ArrayList<Piece> pieces_type = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(copy.get(i).get(j).getPiece() != null) {
                    if (copy.get(i).get(j).getPiece().getType() == type && copy.get(i).get(j).getPiece().getColor() == color) {
                        pieces_type.add(copy.get(i).get(j).getPiece());
                    }
                }
            }
        }
        return pieces_type;
    }

    public static ArrayList<Piece> getPiecesOf (Color color, ArrayList<ArrayList<Square>> copy) {
        ArrayList<Piece> pieces_type = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(copy.get(i).get(j).getPiece() != null) {
                    if (copy.get(i).get(j).getPiece().getColor() == color) {
                        pieces_type.add(copy.get(i).get(j).getPiece());
                    }
                }
            }
        }
        return pieces_type;
    }



    public static boolean checkCheckmate(Color color, ArrayList<ArrayList<Square>> board) {
        ArrayList<Piece> king_list = getPiecesOf(PieceType.KING, color, board);
        Piece king = king_list.get(0);

        if (!checkCheck(color, king.getSquare(), board)) {
            return false;
        }

        ArrayList<ArrayList<Square>> copy = deepCopy(board);
        ArrayList<Piece> pieces = getPiecesOf(color, copy);
        for (int i=0; i<pieces.size(); i++) {
            ArrayList<Square> moves = pieces.get(i).validMoves(copy);
            for (int j=0; j<moves.size(); j++) {
                ArrayList<ArrayList<Square>> copy_2 = deepCopy(board);
                Piece piece_copy = copy_2.get(pieces.get(i).getSquare().getRow()).get(pieces.get(i).getSquare().getCol()).getPiece();
                Square destination_copy = copy_2.get(moves.get(j).getRow()).get(moves.get(j).getCol());
                if (piece_copy.getType() == PieceType.KING) {

                    if (!checkCheck(color, destination_copy, copy_2)) {
                        copy_2.clear();
                        return false;
                    }

                }
                else {
                    movePieceCopy(piece_copy, destination_copy, copy_2);

                    king_list = getPiecesOf(PieceType.KING, color, copy_2);
                    king = king_list.get(0);
                    if (!checkCheck(color, king.getSquare(), copy_2)) {
                        copy_2.clear();
                        return false;
                    }
                }
                copy_2.clear();
            }
        }

        copy.clear();
        return true;
    }

    public static boolean checkCheck(Color color, Square destination, ArrayList<ArrayList<Square>> board) {

        ArrayList<Piece> king_list = getPiecesOf(PieceType.KING, color, board);
        Piece king_2 = king_list.get(0);
        ArrayList<ArrayList<Square>> copy = deepCopy(board);
        Square king_square = copy.get(king_2.getSquare().getRow()).get(king_2.getSquare().getCol());
        Square destination_king = copy.get(destination.getRow()).get(destination.getCol());
        Piece king = copy.get(king_2.getSquare().getRow()).get(king_2.getSquare().getCol()).getPiece();
        king_square.removePiece();
        destination_king.setPiece(king);
        king.setSquare(destination_king);

        Color opponent;
        if (color == Color.WHITE) {
            opponent = Color.BLACK;
        } else {
            opponent = Color.WHITE;
        }
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (copy.get(row).get(col).getPiece() != null && copy.get(row).get(col).getPiece().getColor() == opponent) {
                    if (copy.get(row).get(col).getPiece().validMoves(copy)!= null) {
                        if (copy.get(row).get(col).getPiece().validMoves(copy).contains(destination_king)) {
                            // added an edge case for check with pawn (suicide with pawn and pawn check blocked by king's movement towards pawn)
                            if (copy.get(row).get(col).getPiece().getType() == PieceType.PAWN && (Math.abs(row - king.getSquare().getRow()) == 1)) {
                                if ((Math.abs(col - king.getSquare().getCol()) == 1)) {
                                    return true;
                                }
                            } else {
                                //System.out.println("Checkmate with: " + copy.get(row).get(col).getPiece());
                                copy.clear();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        copy.clear();
        return false;
    }

    public static boolean checkSuicide(Color color, Piece piece, Square destination, ArrayList<ArrayList<Square>> board) {
        if (piece.getType() == PieceType.KING) {return false;}
        ArrayList<ArrayList<Square>> copy = deepCopy(board);
        Piece piece_copy;
        Square destination_copy;

        piece_copy = copy.get(piece.getSquare().getRow()).get(piece.getSquare().getCol()).getPiece();
        destination_copy = copy.get(destination.getRow()).get(destination.getCol());
        movePieceCopy(piece_copy, destination_copy, copy);
        ArrayList<Piece> king_list = getPiecesOf(PieceType.KING, color, copy);
        Piece king = king_list.get(0);
        return checkCheck(color, king.getSquare(), copy);
    }


    public static boolean checkIfCheckmating(Piece piece, Square dest, Color color, ArrayList<ArrayList<Square>> board) {
        Square temp = piece.getSquare();
        ArrayList<ArrayList<Square>> copy = deepCopy(board);
        Piece prev = copy.get(temp.getRow()).get(temp.getCol()).getPiece();
        Square destination = copy.get(dest.getRow()).get(dest.getCol());
        movePieceCopy(prev, destination, copy);
        ArrayList<Square> possible_squares = prev.validMoves(copy);
        Color opponent;
        if (color == Color.WHITE) {opponent = Color.BLACK;}
        else {opponent = Color.WHITE;}
        boolean checkmate = checkCheckmate(opponent, copy);

        return checkmate;
    }


    public static boolean checkIfChecking(Piece piece, Square dest, Color color, ArrayList<ArrayList<Square>> board) {
        Square temp = piece.getSquare();
        ArrayList<ArrayList<Square>> copy = deepCopy(board);
        Piece prev = copy.get(temp.getRow()).get(temp.getCol()).getPiece();
        Square destination = copy.get(dest.getRow()).get(dest.getCol());
        movePieceCopy(prev, destination, copy);
        ArrayList<Square> possible_squares = prev.validMoves(copy);
        Color opponent;
        if (color == Color.WHITE) {opponent = Color.BLACK;}
        else {opponent = Color.WHITE;}
        ArrayList<Piece> king_list = getPiecesOf(PieceType.KING, opponent, copy);
        if (king_list.size() ==0) {
            return false;
        }
        else {
            Piece king = king_list.get(0);
            return possible_squares.contains(king.getSquare());
        }
    }

    public static boolean checkQueenSideCastling(Color color, ArrayList<ArrayList<Square>> board) {
        int row;
        if (color == Color.WHITE) {
            row=0;
        }
        else {row=7;}

        Piece king = board.get(row).get(4).getPiece();
        Piece rook = board.get(row).get(0).getPiece();

        if (king == null || rook == null) {return false;}
        if (king.getType() != PieceType.KING || rook.getType() != PieceType.ROOK) {return false;}
        if (((King) king).getHasMoved() || ((Rook) rook).getHasMoved()) { return false; }
        return (board.get(row).get(1).getPiece() == null && board.get(row).get(2).getPiece() == null && board.get(row).get(3).getPiece() == null);
    }

    public static boolean checkKingSideCastling(Color color, ArrayList<ArrayList<Square>> board) {
        int row;
        if (color == Color.WHITE) {
            row=0;
        }
        else {row=7;}

        Piece king = board.get(row).get(4).getPiece();
        Piece rook = board.get(row).get(7).getPiece();

        if (king == null || rook == null) {return false;}
        if (king.getType() != PieceType.KING || rook.getType() != PieceType.ROOK) {return false;}
        if (((King) king).getHasMoved() || ((Rook) rook).getHasMoved()) { return false; }
        return (board.get(row).get(5).getPiece() == null && board.get(row).get(6).getPiece() == null);
    }

    public static boolean validEnPassant(Piece piece, Square destination, Capture capture, ArrayList<ArrayList<Square>> board) {
        if (((Pawn) piece).isEnpassantPossible() && capture == Capture.CAPTURE) {
            if (piece.getColor() == Color.WHITE) {
                Square square = board.get(destination.getRow()-1).get(destination.getCol());
                return (((Pawn) square.getPiece()).getisJustMoved2());
            }
            if (piece.getColor() == Color.BLACK) {
                Square square = board.get(destination.getRow()+1).get(destination.getCol());
                return (((Pawn) square.getPiece()).getisJustMoved2());
            }
        }
        if(capture == Capture.NOCAPTURE && destination.getCol() == piece.getSquare().getCol()){
            return true;
        }
        return false;
    }

    public static boolean validCapture (Capture capture, Square destination, boolean empasPossible){
        if(empasPossible){
            if(capture == Capture.CAPTURE){
                return true;
            }
            //-------------------------------------------
            return true;
        }
        if (capture == Capture.CAPTURE && destination.getPiece() == null) {
            return false;
        }
        else if(capture == Capture.NOCAPTURE && destination.getPiece() != null){
            return false;
        }return true;
    }

    public static boolean checkPromotion(PieceType type, int row) {
        return ((type == PieceType.PAWN) && (row==7 || row==0));
    }
}
