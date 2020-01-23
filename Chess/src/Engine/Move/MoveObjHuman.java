package Engine.Move;

import Engine.Pieces.Color;
import Engine.Pieces.PieceType;

public class MoveObjHuman implements MoveObj {

    private PieceType chessPiece;
    private int column;
    private int row;

    private int file=0;
    private int rank=0;

    private Color color;
    private Capture capture = Capture.NOCAPTURE; //Important: Set to Engine.Move.Capture.CAPTURE if capture and Engine.Move.Capture.NOCAPTURE if not
    private PieceType promotedPiece;
    private Check check = Check.NONE;

    private boolean debug = false;


    private SpecialMove specialMove = SpecialMove.DISAMBGIGUOUS;

    /* moveObj
        check if there is a checkmate marked, if not then decode the input otherwise decode (string-1 or string -2 for check or checkmate)
     */
    public MoveObjHuman(String input, Color color){
        this.color = color;
        //CHECK and CHECKMATE have + or ++ at the end of the move string.
        if(input.length()>2 && input.charAt(input.length()-1) == '+'){
            if(input.charAt(input.length()-2) == '+'){
                moveDecoder(input.substring(0, input.length() - 2));
                if(debug){System.out.println("checkmate entered in Engine.Move.MoveObj");}
                this.check = check.CHECKMATE;
            }
            else{
                moveDecoder(input.substring(0, input.length() - 1));
                this.check = check.CHECK;
            }
        }
        else{
            moveDecoder(input);
        }
    }
    /* move decoder:
        Input length =
            2 -> simple pawn move : e6
            3 -> simple move : Be6, pawn promotion: e8Q, Castling king size: 0-0
            4 -> Engine.Move.Capture : Bxe6, ambiguous simple move : Bde6 or B4e6, enpassant : exd6
            5 -> Engine.Pieces.Queen side Castling : 0-0-0, Superambiguous move : Qh6e6, ambiguous simple capture: Bdxe6
            6 -> Superambiguous capture move: Qh6xe6
        sets all parameters
     */
    public void moveDecoder(String input){
        //Sets color
        this.color = color;
        // pawn move -- done
        if(input.length() == 2) {
            this.chessPiece = PieceType.PAWN;
            this.column = letterToNumber(input.charAt(0));
            this.row = Character.getNumericValue(input.charAt(1))-1;
        }
        // input len = 3 (kingCastling, promotion, normal move and ambiguos pawn move) -- done
        else if(input.length() == 3){
            //king side castling
            if(input.equals("0-0")){

                this.chessPiece = PieceType.ROOK;
                this.specialMove = SpecialMove.KINGSIDECASTLING;
            }
            //promotion
            else if(Character.isUpperCase(input.charAt(2))){
                this.chessPiece = PieceType.PAWN;
                this.specialMove = SpecialMove.PROMOTION;
                this.column = letterToNumber(input.charAt(0));
                this.row = Character.getNumericValue(input.charAt(1))-1;
                this.promotedPiece = translatorPiece(input.charAt(2));
            }
            //normal move with piece diff than pawn
            else if(Character.isUpperCase(input.charAt(0))){
                char in = input.charAt(0);
                this.chessPiece = translatorPiece(in);

                this.column = letterToNumber(input.charAt(1));
                this.row = Character.getNumericValue(input.charAt(2))-1;
            }

            else if(input.charAt(0) == 'x') {
                this.chessPiece = PieceType.PAWN;
                this.column = letterToNumber(input.charAt(1));
                this.row = Character.getNumericValue(input.charAt(2))-1;
                this.specialMove = specialMove.DISAMBGIGUOUS;
                this.capture = Capture.CAPTURE;
            }
            //ambiguos move with pawn and either file or rank
            else{
                this.chessPiece = PieceType.PAWN;
                if(Character.isLetter(input.charAt(0))){
                    this.file = letterToNumber(input.charAt(0));
                }
                else this.rank = input.charAt(0)-1;

                this.column = letterToNumber(input.charAt(1));
                this.row = Character.getNumericValue(input.charAt(2))-1;
                this.specialMove = SpecialMove.AMBIGUOUS;
            }
        }
        // input len = 4 (Capturing Bxe6, ambiguos Bae6 or B1e6, enpassant exd6) -- done
        else if(input.length() == 4){
            // Attack move
            if(input.charAt(1) == 'x'){
                this.capture = Capture.CAPTURE;
                //ENPASSANT
                if(Character.isLowerCase(input.charAt(0))){
                    this.chessPiece = PieceType.PAWN;
                }
                //Normal capturing
                else {
                    this.chessPiece = translatorPiece(input.charAt(0));
                }
                this.column = letterToNumber(input.charAt(2));
                this.row = Character.getNumericValue(input.charAt(3))-1;
            }
            // Bae6 B1e6 ambiguos move
            else {
                char in = input.charAt(0);
                this.chessPiece = translatorPiece(in);
                //BeE6
                if (Character.isLetter(input.charAt(1))) {
                    this.rank = letterToNumber(input.charAt(1))-1;
                } else {
                    //B6E6
                    this.file = Character.getNumericValue(input.charAt(1))-1;
                }
                this.column = letterToNumber(input.charAt(2));
                this.row = Character.getNumericValue(input.charAt(3))-1;
                this.specialMove = SpecialMove.AMBIGUOUS;
            }
        }
        // input len = 5 (QueenCastling, SUPERAMBIGUOS Qh4h6, ambiguous simple capture) -- done
        else if(input.length() == 5){
            //QUEENcaslting
            if(input.charAt(1) == '-'){
                this.chessPiece = PieceType.ROOK;
                this.specialMove = SpecialMove.QUEENSIDECASTLING;
            }
            //ambiguous capture
            else if(input.charAt(2) == 'x'){
                this.chessPiece = translatorPiece(input.charAt(0));
                if(Character.isLetter(input.charAt(1))) {
                    this.file = letterToNumber(input.charAt(1));
                }
                else this.rank = Character.getNumericValue(input.charAt(1))-1;

                this.column = letterToNumber(input.charAt(3));
                this.row = Character.getNumericValue(input.charAt(4))-1;

                this.capture = Capture.CAPTURE;
            }

            //Engine.Pieces.Pawn promotion capture
            else if (input.charAt(1) == 'x'){
                this.file =letterToNumber(input.charAt(0));
                this.capture = Capture.CAPTURE;
                this.column = letterToNumber(input.charAt(2));
                this.row = Character.getNumericValue(input.charAt(3))-1;
                this.promotedPiece = translatorPiece(input.charAt(4));
            }
            //superambiguos
            else {
                this.chessPiece = translatorPiece(input.charAt(0));
                this.file = letterToNumber(input.charAt(1));
                this.rank = Character.getNumericValue(input.charAt(2))-1;

                this.column = letterToNumber(input.charAt(3));
                this.row = Character.getNumericValue(input.charAt(4))-1;

                this.specialMove = SpecialMove.AMBIGUOUS;
            }

        }
        // input len = 6 Superambiguos capture
        else{
            this.chessPiece = translatorPiece(input.charAt(0));

            this.file = letterToNumber(input.charAt(1));
            this.rank = Character.getNumericValue(input.charAt(2))-1;

            this.column = letterToNumber(input.charAt(4));
            this.row = Character.getNumericValue(input.charAt(5))-1;

            this.capture = Capture.CAPTURE;
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

    public int getIntColumn(){
        int res = this.column-'a'+1;
        return res;
    }

    public PieceType translatorPiece(char input){
        switch (input){
            case 'N':
                return PieceType.KNIGHT;
            case 'K':
                return PieceType.KING;
            case 'Q':
                return PieceType.QUEEN;
            case 'R':
                return PieceType.ROOK;
            case 'B':
                return PieceType.BISHOP;
        }
        return PieceType.PAWN;
    }

    public int letterToNumber(char input) {
        switch (input){
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
        }
        throw new RuntimeException("Should not have gotten here");
    }

}
