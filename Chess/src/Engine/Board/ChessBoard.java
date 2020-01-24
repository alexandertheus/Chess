package Engine.Board;

import Engine.Move.*;
import Engine.Board.Pieces.*;
import Engine.Player.Player;
import Engine.Run.Game;


import java.util.ArrayList;
import java.lang.Math;

public class ChessBoard {
    private boolean isEnded = false;
    private ArrayList<ArrayList<Square>> board = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private boolean empasPossible = false;
    private boolean empasSince = false;
    private ArrayList<Piece> lastmoves = new ArrayList<>();
    private int depth;
    private Piece eaten;


    /*
        Creates two player instances and creates the chessboard arraylist.
     */
    public ChessBoard(int depth){
        players.add(new Player("White", Color.WHITE));
        players.add(new Player("Black", Color.BLACK));
        this.depth = depth;
        this.board = createChessBoard();
    }

    /*
        Method that creates the board arraylist
     */
    public ArrayList<ArrayList<Square>> createChessBoard() {
        ArrayList<ArrayList<Square>> board = new ArrayList<>();
        for(int row = 1; row <= 8; row++){
            board.add( new ArrayList<Square>() );
            for(int col = 1; col <= 8;col++) {
                Square thisPosition = new Square(row-1, col-1);
                //Setting first row
                if (row == 1) {
                    //Setting white rook
                    if (col == 1 || col == 8) {
                        thisPosition.setPiece(new Rook(thisPosition, Color.WHITE));
                    }
                    //Setting white Knight
                    if (col == 2 || col == 7) {
                        thisPosition.setPiece(new Knight(thisPosition, Color.WHITE));
                    }
                    //Setting white Bishop
                    if (col == 3 || col == 6) {
                        thisPosition.setPiece(new Bishop(thisPosition, Color.WHITE));
                    }
                    //Setting white Queen
                    if (col == 4) {
                        thisPosition.setPiece(new Queen(thisPosition, Color.WHITE));
                    }
                    //Setting white King
                    if (col == 5) {
                        thisPosition.setPiece(new King(thisPosition, Color.WHITE));
                        lastmoves.add( thisPosition.getPiece());
                    }

                }
                //setting second row
                else if (row == 2) {
                    thisPosition.setPiece(new Pawn(thisPosition, Color.WHITE));
                }
                //setting 7th row
                else if (row == 7) {
                    thisPosition.setPiece(new Pawn(thisPosition, Color.BLACK));
                }
                //setting 8th row
                else if (row == 8) {
                    //Setting BLACK rook
                    if (col == 1 || col == 8) {
                        thisPosition.setPiece(new Rook(thisPosition, Color.BLACK));
                    }
                    //Setting BLACK Knight
                    if (col == 2 || col == 7) {
                        thisPosition.setPiece(new Knight(thisPosition, Color.BLACK));
                    }
                    //Setting BLACK Bishop
                    if (col == 3 || col == 6) {
                        thisPosition.setPiece(new Bishop(thisPosition, Color.BLACK));
                    }
                    //Setting BLACK Queen
                    if (col == 4) {
                        thisPosition.setPiece(new Queen(thisPosition, Color.BLACK));
                    }
                    //Setting BLACK King
                    if (col == 5) {
                        thisPosition.setPiece(new King(thisPosition, Color.BLACK));
                        lastmoves.add( thisPosition.getPiece());
                    }

                }
                board.get(row-1).add(thisPosition);
            }
        }
        return board;
    }
    /*
        Takes MoveObj as input, checks for errors and calls the specific move-method depending on what the special move is.
     */
    public void move(MoveObj move) {
        if (((move.getSpecialMove() != SpecialMove.KINGSIDECASTLING) && (move.getSpecialMove() != SpecialMove.QUEENSIDECASTLING)) && (move.getRow() > 7 || move.getRow() < 0 || move.getColumn() > 7 || move.getColumn() < 0)) {
            move(error("Coordinates not within board", move.getColor()));
        }
        if (move.getFile() < 0 || move.getFile() > 7 || move.getRank() < 0 || move.getRank() > 7) {
            move(error("Moving piece coordinates not within board", move.getColor()));
        }

        if (move.getSpecialMove() == SpecialMove.DISAMBGIGUOUS) {
            moveDisambiguous(move);
        }

        if (move.getSpecialMove() == SpecialMove.AMBIGUOUS) {
            moveAmbiguous(move);
        }
        if (move.getSpecialMove() == SpecialMove.KINGSIDECASTLING) {
            moveKingSideCastling(move);
        }
        if (move.getSpecialMove() == SpecialMove.QUEENSIDECASTLING) {
            moveQueenSideCastling(move);
        }
        if(move.getSpecialMove() == SpecialMove.PROMOTION) {
            movePromotion(move);
        }
    }

    /*
        Makes a disambiguous move (Only chesspiece, color and destination coordinates given)
     */
    private void moveDisambiguous(MoveObj move) {
        ArrayList<Piece> pieces_move;
        Square destination = board.get(move.getRow()).get(move.getColumn());
        pieces_move = search(move.getChessPiece(), move.getColor(), move.getRow(), move.getColumn());
        if (pieces_move.size() != 1) {
            move(error("Ambiguous/impossible move", move.getColor()));
        }
        else if(move.getChessPiece() == PieceType.PAWN && (move.getRow()==7 || move.getRow()==0)){
            move(error("Invalid promotion", move.getColor()));
        }
        else if(move.getChessPiece() == PieceType.KING && MoveChecker.checkCheck(move.getColor(), destination, this.board)) {
            if (move instanceof MoveObjHuman) {
                move(error("Can't put king into checkmate", move.getColor()));
            }
            else {
                move(new MoveObjComputer(this,depth));
            }
        }
        else {
            Piece piece = pieces_move.get(0);

            if (piece.getType() == PieceType.PAWN && !MoveChecker.validEnPassant(piece, destination, move.getCapture(), this.board) && destination.getPiece() == null) {
                move(error("Invalid enpassant", move.getColor()));
            }
            else if (!MoveChecker.validCapture(move.getCapture(), destination, empasPossible) && !(piece.getType() == PieceType.PAWN && !MoveChecker.validEnPassant(piece, destination, move.getCapture(), this.board) && destination.getPiece() == null)) {
                move(error("Invalid attack", move.getColor()));

            }
            else if ((move.getCheck() == Check.CHECK && (!MoveChecker.checkIfChecking(piece, destination, move.getColor(), this.board) || MoveChecker.checkIfCheckmating(piece, destination, move.getColor(), this.board)) )|| move.getCheck() == Check.NONE && MoveChecker.checkIfChecking(piece, destination, move.getColor(), this.board)) {
                move((MoveObjHuman) error("Invalid check", move.getColor()));
            }
            else if (((move.getCheck() == Check.CHECKMATE && !MoveChecker.checkIfCheckmating(piece, destination, move.getColor(), this.board)) || (move.getCheck() == Check.NONE && MoveChecker.checkIfCheckmating(piece, destination, move.getColor(), this.board)))) {

                move((MoveObjHuman) error("Invalid checkmate", move.getColor()));
            }
            else if (piece.getType()==PieceType.KING && (MoveChecker.checkCheck(move.getColor(), destination, this.board))){
                if (move instanceof MoveObjHuman) {
                    move(error("Can't put king into checkmate", move.getColor()));
                }
                else {
                    move(new MoveObjComputer(this,depth));
                }
            }
            else if (piece.getType() != PieceType.KING && MoveChecker.checkSuicide(move.getColor(), piece, destination, this.board)) {
                if (move instanceof MoveObjHuman) {
                    move((MoveObjHuman) error("Piece movement would result in checkmate/check of own color", move.getColor()));
                }
                else {
                    move(new MoveObjComputer(this,depth));
                }
            }
            else {
                if(piece.getType() == PieceType.PAWN){

                    ((Pawn) piece).setJustMoved2( Math.abs(piece.getSquare().getRow()-destination.getRow()) == 2);
                }


                movePiece(piece, destination);
                updateLastMoves(piece);
                Color opponent;
                if (move.getColor() == Color.WHITE){opponent = Color.BLACK;}
                else {opponent = Color.WHITE;}
                if (MoveChecker.checkCheckmate(opponent, board)) {isEnded = true;}
                ArrayList<Piece> king_list = MoveChecker.getPiecesOf(PieceType.KING, move.getColor(), this.board);
                Piece king = king_list.get(0);
                if (MoveChecker.checkCheck(move.getColor(), king.getSquare(), this.board)) {isEnded = true;}
            }
        }
    }

    /*
        Makes a ambiguous move (Only chesspiece, color, one coordinate of origin and destination coordinates given)
     */
    private void moveAmbiguous(MoveObj move) {
        ArrayList<Piece> pieces_move;
        Square destination = board.get(move.getRow()).get(move.getColumn());
        if (move.getFile() == 0 && move instanceof MoveObjHuman) {
            pieces_move = search(move.getChessPiece(), move.getRank(), move.getColor(), move.getRow(), move.getColumn(), move);
        }
        else if (move.getRank() == 0 && move instanceof MoveObjHuman) {
            pieces_move = search(move.getChessPiece(), move.getFile(), move.getColor(), move.getRow(), move.getColumn(), move);
        }
        else {
            pieces_move = search(move.getChessPiece(), move.getRank(),move.getFile(), move.getColor(), move.getRow(), move.getColumn());
        }
        if (pieces_move.size() != 1) {
            move(error("Ambiguous impossible", move.getColor()));
            ////IMPORTANT: Need method to ask for coordinates. Then call this.move(new Engine.Move.MoveObj(String input, color)
        } else {
            Piece piece = pieces_move.get(0);
            if (piece.getType() == PieceType.PAWN && !MoveChecker.validEnPassant(piece, destination, move.getCapture(), this.board) && destination.getPiece() == null) {
                move(error("Invalid enpassant", move.getColor()));
            }

            else if (!MoveChecker.validCapture(move.getCapture(), destination, empasPossible)) {
                move(error("Ambiguous capture invalid", move.getColor()));
                ////IMPORTANT: Need method to ask for coordinates. Then call this.move(new Engine.Move.MoveObj(String input, color)
            }
            else if (piece.getType() == PieceType.PAWN && (destination.getRow() == 7 || destination.getRow() == 0)) {
                move(error("Ambiguous promotion invalid", move.getColor()));
            }
            else if((move.getCheck() == Check.CHECK && !MoveChecker.checkIfChecking(piece, destination, move.getColor(), this.board)) || move.getCheck() == Check.NONE && MoveChecker.checkIfChecking(piece, destination, move.getColor(), this.board)) {
                move(error("Invalid check", move.getColor()));
            }
            else if (((move.getCheck() == Check.CHECKMATE && !MoveChecker.checkIfCheckmating(piece, destination, move.getColor(), this.board)) || (move.getCheck() == Check.NONE && MoveChecker.checkIfCheckmating(piece, destination, move.getColor(), this.board)))){

                move(error("Invalid checkmate", move.getColor()));
            }
            else if (piece.getType()==PieceType.KING){
                if (MoveChecker.checkCheck(move.getColor(), destination, this.board)) {
                    if (move instanceof MoveObjHuman) {
                        move(error("Can't put king into checkmate", move.getColor()));
                    } else {
                        move(new MoveObjComputer(this, depth));
                    }
                }
            }
            else if (MoveChecker.checkSuicide(move.getColor(), piece, destination, this.board)) {
                if (move instanceof MoveObjHuman) {
                    move(error("Piece movement would result in checkmate", move.getColor()));
                }
                else {
                    move(new MoveObjComputer(this, depth));
                }
            }
            else {
                if(piece.getType() == PieceType.PAWN){

                    ((Pawn) piece).setJustMoved2(Math.abs(piece.getSquare().getRow()-destination.getRow()) == 2);;
                }
                movePiece(piece, destination);
                updateLastMoves(piece);
                Color opponent;
                if (move.getColor() == Color.WHITE){opponent = Color.BLACK;}
                else {opponent = Color.WHITE;}
                if (MoveChecker.checkCheckmate(opponent, board)) {isEnded = true;}
                ArrayList<Piece> king_list = MoveChecker.getPiecesOf(PieceType.KING, move.getColor(), this.board);
                Piece king = king_list.get(0);
                if (MoveChecker.checkCheck(move.getColor(), king.getSquare(), this.board)) {isEnded = true;}
            }
        }
    }

    /*
        Makes a kingsidecastling move.
     */
    private void moveKingSideCastling(MoveObj move) {
        if (!MoveChecker.checkKingSideCastling(move.getColor(), this.board)) {
            move(error("Kingsidecastling not valid", move.getColor()));
        }
        else {
            int row;
            if (move.getColor() == Color.WHITE) {row=0;}
            else {row=7;}
            Piece king = board.get(row).get(4).getPiece();
            Piece rook = board.get(row).get(7).getPiece();
            if(MoveChecker.checkCheck(move.getColor(), board.get(row).get(6), this.board)) {
                if (move instanceof MoveObjHuman) {
                    move(error("Can't put king into checkmate", move.getColor()));
                } else {
                    move(new MoveObjComputer(this, depth));
                }
            }
            else {
                movePiece(king, board.get(row).get(6));
                movePiece(rook, board.get(row).get(5));
                Color opponent;
                if (move.getColor() == Color.WHITE) {
                    opponent = Color.BLACK;
                } else {
                    opponent = Color.WHITE;
                }
                if (MoveChecker.checkCheckmate(opponent, board)) {
                    isEnded = true;
                }
                if (MoveChecker.checkCheck(move.getColor(), king.getSquare(), this.board)) {
                    isEnded = true;
                }
                updateLastMoves(king);
            }
        }
    }

    /*
        Makes a Queensidecastling move
     */
    private void moveQueenSideCastling(MoveObj move) {
        if (!MoveChecker.checkQueenSideCastling(move.getColor(), this.board)) {
            move(error("Queensidecastling not valid", move.getColor()));
        }
        else {
            int row;
            if (move.getColor() == Color.WHITE) {
                row = 0;
            } else {
                row = 7;
            }
            Piece king = board.get(row).get(4).getPiece();
            Piece rook = board.get(row).get(0).getPiece();
            if (MoveChecker.checkCheck(move.getColor(), board.get(row).get(2), this.board)) {
                if (move instanceof MoveObjHuman) {
                    move(error("Can't put king into checkmate", move.getColor()));
                } else {
                    move(new MoveObjComputer(this, depth));
                }
            } else {
                movePiece(king, board.get(row).get(2));
                movePiece(rook, board.get(row).get(3));
                Color opponent;
                if (move.getColor() == Color.WHITE) {
                    opponent = Color.BLACK;
                } else {
                    opponent = Color.WHITE;
                }
                if (MoveChecker.checkCheckmate(opponent, board)) {
                    isEnded = true;
                }
                ArrayList<Piece> king_list = MoveChecker.getPiecesOf(PieceType.KING, move.getColor(), this.board);
                if (MoveChecker.checkCheck(move.getColor(), king.getSquare(), this.board)) {
                    isEnded = true;
                }
                updateLastMoves(king);
            }
        }
    }

    /*
        Makes a move with promotion.
     */
    private void movePromotion(MoveObj move) {
        ArrayList<Piece> pieces_move;
        Square destination = board.get(move.getRow()).get(move.getColumn());
        if (move.getFile() == 0) {
            pieces_move = search(move.getChessPiece(), move.getColor(), move.getRow(), move.getColumn());}
        else {
            pieces_move = search(move.getChessPiece(), move.getFile(),move.getColor(), move.getRow(), move.getColumn(), move);
        }
        if (pieces_move.size() != 1) {
            move(error("It's Ambiguous", move.getColor()));
        }
        else {
            Piece piece = pieces_move.get(0);
            Square piece_square = board.get(piece.getSquare().getRow()).get(piece.getSquare().getCol());
            piece_square.removePiece();

            piece = null;
            if (move.getPromotedPiece() == PieceType.KING) {
                move(error("Can't promote to king", move.getColor()));
            }
            else {
                if (!MoveChecker.validCapture(move.getCapture(), destination, empasPossible)) {
                    move(error("Capture invalid", move.getColor()));
                } else {
                    Piece promoted_piece = directPieceTranslation(move.getPromotedPiece(), destination, move.getColor());

                    movePiece(promoted_piece, destination);
                    Color opponent;
                    if (move.getColor() == Color.WHITE) {
                        opponent = Color.BLACK;
                    } else {
                        opponent = Color.WHITE;
                    }
                    ArrayList<Piece> king_list = MoveChecker.getPiecesOf(PieceType.KING, move.getColor(), this.board);
                    Piece king = king_list.get(0);
                    if (MoveChecker.checkCheckmate(opponent, board)) {
                        isEnded = true;
                    }
                    if (MoveChecker.checkCheck(move.getColor(), king.getSquare(), this.board)) {
                        isEnded = true;
                    }
                    updateLastMoves(promoted_piece);
                }
            }
        }
    }


    private void updateLastMoves(Piece piece) {
        if (piece.getColor()== Color.WHITE) {
            Piece opponentPiece = lastmoves.get(1);
            if (opponentPiece.getType() == PieceType.PAWN) {
                ((Pawn) opponentPiece).setJustMoved2(false);
            }
            this.lastmoves.set(0, piece);
        }
        else {
            Piece opponentPiece = lastmoves.get(0);
            if (opponentPiece.getType() == PieceType.PAWN) {
                ((Pawn) opponentPiece).setJustMoved2(false);
            }
            this.lastmoves.set(1, piece);
        }
    }


    private ArrayList<Piece> search (PieceType type, Color color,int row, int col){
        ArrayList<Piece> pieces_type = MoveChecker.getPiecesOf(type, color, this.board);
        ArrayList<Piece> possible_pieces = new ArrayList<>();
        for (int alpha = 0; alpha < pieces_type.size(); alpha++) {
            ArrayList<Square> possible_squares = new ArrayList<>();

            possible_squares = pieces_type.get(alpha).validMoves(board);
            int valid_pieces = 0;
            for (int j = 0; j < possible_squares.size(); j++) {
                if (possible_squares.get(j) == board.get(row).get(col)) {

                    valid_pieces++;
                }
            }
            if (valid_pieces != 0) {
                possible_pieces.add(pieces_type.get(alpha));
            }
        }
        return possible_pieces;
    }

    public ArrayList<Piece> search (PieceType type,int start, Color color,int row, int col, MoveObj move){
        ArrayList<Piece> pieces_type = new ArrayList<>();
        ArrayList<Square> possible_squares = new ArrayList<>();

        if (move.getFile() == 0) {
            for (int i=0; i<8;i++) {
                Piece piece = board.get(start).get(i).getPiece();
                if (piece.getType() == type && piece.getColor() == color) {
                    pieces_type.add(piece);
                }
            }
        }
        else {
            for (int i=0; i<8;i++) {
                Piece piece = board.get(i).get(start).getPiece();
                if (piece != null && piece.getType() == type && piece.getColor() == color) {
                    pieces_type.add(piece);
                }
            }
        }
        for (int i = 0; i < pieces_type.size(); i++) {
            possible_squares = pieces_type.get(i).validMoves(board);
            int valid_pieces = 0;
            for (int j = 0; j < possible_squares.size(); j++) {
                if (possible_squares.get(j) == board.get(row).get(col)) {
                    valid_pieces++;
                }
            }
            if (valid_pieces == 0) {
                pieces_type.remove(i);
            }
        }
        return pieces_type;
    }

    public ArrayList<Piece> search (PieceType type, int rank, int file, Color color,int row, int col){
        ArrayList<Piece> pieces_type = new ArrayList<>();
        Piece piece = board.get(rank).get(file).getPiece();
        Square destination = board.get(row).get(col);
        ArrayList<Square> possible_squares = piece.validMoves(board);

        for (int i=0; i<possible_squares.size(); i++) {
            if (possible_squares.get(i) == destination) {
                pieces_type.add(piece);
            }
        }
        return pieces_type;
    }



    public void movePiece (Piece piece, Square destination){
        if(empasPossible && empasSince){
            empasPossible = false;
        }
        else if(empasPossible){
            empasSince = true;
        }
        if(piece.getType() == PieceType.PAWN &&  Math.abs(piece.getSquare().getRow()-destination.getRow()) == 2){
            empasPossible = true;
        }
        if (piece.getType() == PieceType.PAWN && Math.abs(piece.getSquare().getCol()-destination.getCol()) == 1 && destination.getPiece()==null) {
            eaten = board.get(piece.getSquare().getRow()).get(destination.getCol()).getPiece();

            if (players.get(0).getColor() == piece.getColor()) {
                players.get(0).addEaten(board.get(piece.getSquare().getRow()).get(destination.getCol()).getPiece());
            } else {
                players.get(1).addEaten(board.get(piece.getSquare().getRow()).get(destination.getCol()).getPiece());
            }
            board.get(piece.getSquare().getRow()).get(destination.getCol()).removePiece();
        }
        else if (destination.getPiece() != null) {
            eaten = destination.getPiece();
            if (players.get(0).getColor() == piece.getColor()) {
                players.get(0).addEaten(destination.getPiece());
            } else {
                players.get(1).addEaten(destination.getPiece());
            }
        }

        piece.getSquare().removePiece();
        piece.setSquare(destination);
        destination.setPiece(piece);

        if (piece.getType() == PieceType.KING) {((King) piece).hasMoved();}
        if (piece.getType() == PieceType.ROOK) {((Rook) piece).hasMoved();}
        if (piece.getType() == PieceType.PAWN) {((Pawn) piece).hasMoved();}
        eaten = null;
    }


    public Piece directPieceTranslation(PieceType type, Square square, Color color) {
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

    public ArrayList<ArrayList<Square>> getSquares() {return this.board;}
    public boolean isEnded() {return this.isEnded;}

    public CheckStatus checkStatus() {
        if (MoveChecker.checkCheckmate(Color.WHITE, board)) {return CheckStatus.WhiteIsCheckmated;}
        else if (MoveChecker.checkCheckmate(Color.BLACK, board)) {return CheckStatus.BlackIsCheckmated;}
        return CheckStatus.Continue;
    }


    public void Print () {
        System.out.print("\n");
        for (int row = 7; row >=0; row--) {
            for (int col = 0; col < 8; col++) {
                System.out.print(board.get(row).get(col).toString());

            }
            System.out.print("\n");
        }
        System.out.println("---------------------------------------------------------------------");
    }


    public MoveObj error(String message, Color color){
        System.out.println(message +" enter new move");
        String inMove;
        inMove = Game.input.next();
        return new MoveObjHuman(inMove, color);
    }

}

