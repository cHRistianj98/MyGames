package com.thesis.mygames;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FENFormat {

    public static String generateFENFromPosition() {
        StringBuilder fen = new StringBuilder();
        fen.append(getFenPosition()).append(" ").
                append(whoseMove()).append(" ").
                append(getCastlingInformation()).append(" "). //może zwracać głupoty
                append(getEnPassantPossibility()).append(" ").
                append(getNumberOfHalfMoves()).append(" ").
                append(getFullMoveNumber());

        return fen.toString();
    }

    public static String getFenPosition() {
        StringBuilder fen = new StringBuilder();
        Square s;
        int counter = 0;
        for (int i = 0 ; i < 64;  i++) {
            s = Chessboard.getSquares(i);
            if(s.getPiece() != null) {
                if(counter == 0) {
                    fen.append(generateLetterBasedOnPiece(s.getPiece()));
                } else {
                    fen.append(counter);
                    fen.append(generateLetterBasedOnPiece(s.getPiece()));
                    counter = 0;
                }
                if(i % 8 == 7 && i != 0 && i!=63) {
                    fen.append("/");
                }
            } else {
                counter++;
                if(i % 8 == 7 && i != 0) {
                    fen.append(counter);
                    if(i!=63)
                        fen.append("/");
                    counter = 0;
                }
            }
        }

        return fen.toString();
    }

    public static String generateLetterBasedOnPiece(Piece p) {
        if(p.getColor()) {
            if(p instanceof Pawn) return "P";
            else if(p instanceof Rook) return "R";
            else if(p instanceof Knight) return "N";
            else if(p instanceof Bishop) return "B";
            else if(p instanceof Queen) return "Q";
            else if(p instanceof King) return "K";
        } else {
            if(p instanceof Pawn) return "p";
            else if(p instanceof Rook) return "r";
            else if(p instanceof Knight) return "n";
            else if(p instanceof Bishop) return "b";
            else if(p instanceof Queen) return "q";
            else if(p instanceof King) return "k";
        }
        return "";
    }

    public static String whoseMove() {
        if(Turn.whiteTurn) return "w";
        else return "b";
    }

    public static String getCastlingInformation() {
        StringBuilder fen = new StringBuilder();

        if(!((King)Chessboard.whitePieces.get(12)).isWasMoved() && !((Rook)Chessboard.whitePieces.get(15)).isWasMoved())
            fen.append("K");
        if(!((King)Chessboard.whitePieces.get(12)).isWasMoved() && !((Rook)Chessboard.whitePieces.get(8)).isWasMoved())
            fen.append("Q");
        if(!((King)Chessboard.blackPieces.get(12)).isWasMoved() && !((Rook)Chessboard.whitePieces.get(15)).isWasMoved())
            fen.append("k");
        if(!((King)Chessboard.blackPieces.get(12)).isWasMoved() && !((Rook)Chessboard.whitePieces.get(8)).isWasMoved())
            fen.append("q");

        if(fen.toString().equals(""))
            return "-";
        else
            return fen.toString();
    }

    public static String getEnPassantPossibility() {
        if(Chessboard.enPassantPossible == null)
            return "-";
        else
            return Chessboard.getSquareName(Chessboard.enPassantPossible.getId());
    }

    public static String getNumberOfHalfMoves() {
        int counter = 0;
        for (Move move : Chessboard.moveList) {
            if(move.getNotation().startsWith("a") || move.getNotation().startsWith("b") || move.getNotation().startsWith("c") ||
                    move.getNotation().startsWith("d") || move.getNotation().startsWith("e") || move.getNotation().startsWith("f")
                    || move.getNotation().startsWith("g") || move.getNotation().startsWith("h") || move.getNotation().contains("x")) {
                counter = 0;
            } else {
                counter++;
            }
        }
        return Integer.toString(counter);
    }

    public static String getFullMoveNumber() {
        int listSize = Chessboard.moveList.size();

        return listSize % 2 == 0 ? Integer.toString((listSize + 2) / 2) : Integer.toString((listSize + 1) / 2);
    }

    public static void loadPositionFromFen(String fen) throws Exception {
        if(!isFenValid(fen))
            throw new IllegalArgumentException("Code in FEN format is wrong!");
        clearPieces();
        removeActionListeners();
        setObjectsOnNull();
        setNewObjects(fen);
        setIcons();
        setTurn(fen);
        setCastlingInformation(fen);
        setEnPassantPossible(fen);
        setNumberOfHalfMoves(fen);
        setFullMoveNumber(fen);
    }

    public static boolean isFenValid(String fen) {
        //this regular expression search if Fen have correct form
        // but it doesn't include e.g only 2 king on chessboard or max 8 squares in rank
        Pattern pattern = Pattern.compile("((([prnbqkPRNBQK12345678]*/){7})([prnbqkPRNBQK12345678]*)) (w|b) ((K?Q?k?q?)|\\-) (([abcdefgh][36])|\\-) (\\d*) (\\d*)");
        Matcher matcher = pattern.matcher(fen);
        if (!matcher.matches()) {
            return false;
        }

        // Check each rank.
        String[] ranks = matcher.group(2).split("/");
        for (String rank : ranks) {
            if (!verifyRank(rank)) {
                return false;
            }
        }
        if (!verifyRank(matcher.group(4))) {
            return false;
        }

        // Check two kings.
        if (!matcher.group(1).contains("k") || !matcher.group(1).contains("K")) {
            return false;
        }

        return true;
    }

    private static boolean verifyRank(String rank) {
        int count = 0;
        for (int i = 0; i < rank.length(); i++) {
            if (rank.charAt(i) >= '1' && rank.charAt(i) <= '8') {
                count += (rank.charAt(i) - '0');
            } else {
                count++;
            }
        }
        return count == 8;
    }

    public static String getShortFen(String fen) {
        int index = fen.indexOf(" ");

        return fen.substring(0, index);
    }

    public static void clearPieces() {
        for (int i = 0; i < 64; i++) {
            Chessboard.getSquares(i).setPiece(null);
            Chessboard.b[i].setImageResource(0);
        }
    }

    public static void removeActionListeners() {
        for (int i = 0; i < 64; i++) {
            Chessboard.b[i].setOnClickListener(null);
        }
    }

    public static void setObjectsOnNull() {
        for (int i = 0; i < 16; i++) {
            Chessboard.whitePieces.set(i, null);
            Chessboard.blackPieces.set(i, null);
        }
    }

    public static void setNewObjects(String fen) throws Exception {
        char sign;
        for (int i = 0; i < fen.length() ; i++) {
            sign = fen.charAt(i);

            if(sign == ' ')
                break;

            if(isPiece(sign)) {
                objectFactory(sign, fen, i);
            }

        }
    }

    public static boolean isPiece(char s) {
        if ( s == 'p' || s == 'P' || s == 'r' || s == 'R' ||  s == 'n' || s == 'N' ||
                s == 'b' || s == 'B' || s == 'q' || s == 'Q' || s == 'k' || s == 'K') {
            return true;
        }
       else
           return false;
    }

    public static void objectFactory(char s, String fen, int i) throws Exception {
        switch (s) {
            case 'p': createObject(new Pawn(Chessboard.getSquares(getSquareIdFromFen(fen, i)),false, Chessboard.blackIcons[8])); break;
            case 'P': createObject(new Pawn(Chessboard.getSquares(getSquareIdFromFen(fen, i)),true, Chessboard.whiteIcons[0])); break;
            case 'r': createObject(new Rook(Chessboard.getSquares(getSquareIdFromFen(fen, i)),false, Chessboard.blackIcons[0])); break;
            case 'R': createObject(new Rook(Chessboard.getSquares(getSquareIdFromFen(fen, i)),true, Chessboard.whiteIcons[8])); break;
            case 'n': createObject(new Knight(Chessboard.getSquares(getSquareIdFromFen(fen, i)),false, Chessboard.blackIcons[1])); break;
            case 'N': createObject(new Knight(Chessboard.getSquares(getSquareIdFromFen(fen, i)),true, Chessboard.whiteIcons[9])); break;
            case 'b': createObject(new Bishop(Chessboard.getSquares(getSquareIdFromFen(fen, i)),false, Chessboard.blackIcons[2])); break;
            case 'B': createObject(new Bishop(Chessboard.getSquares(getSquareIdFromFen(fen, i)),true, Chessboard.whiteIcons[10])); break;
            case 'q': createObject(new Queen(Chessboard.getSquares(getSquareIdFromFen(fen, i)),false, Chessboard.blackIcons[3])); break;
            case 'Q': createObject(new Queen(Chessboard.getSquares(getSquareIdFromFen(fen, i)),true, Chessboard.whiteIcons[11])); break;
            case 'k': createObject(new King(Chessboard.getSquares(getSquareIdFromFen(fen, i)),false, Chessboard.blackIcons[4])); break;
            case 'K': createObject(new King(Chessboard.getSquares(getSquareIdFromFen(fen, i)),true, Chessboard.whiteIcons[12])); break;
        }
    }

    public static void createObject(Piece p) throws Exception {
        p.setId(assignId(p));
        if (p.getColor()) {

            Chessboard.whitePieces.set(p.getId(), p);
        } else {
            Chessboard.blackPieces.set(p.getId(), p);
        }
        Chessboard.getSquares(p.getSquare().getId()).setPiece(p);
        //System.out.println(p.getIcon().toString());

    }

    public static int assignId(Piece p) {

        if (p.getColor()) {
            if(p instanceof Pawn) {
                for (int i = 0; i < 8; i++) {
                    if(Chessboard.whitePieces.get(i) == null) {
                        return i;
                    }
                }
            }

            else if(p instanceof Rook) {
                if(Chessboard.whitePieces.get(8) == null) return 8;
                if(Chessboard.whitePieces.get(15) == null) return 15;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Knight) {
                if(Chessboard.whitePieces.get(9) == null) return 9;
                if(Chessboard.whitePieces.get(14) == null) return 14;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Bishop) {
                if(Chessboard.whitePieces.get(10) == null) return 10;
                if(Chessboard.whitePieces.get(13) == null) return 13;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Queen) {
                if(Chessboard.whitePieces.get(11) == null) return 11;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof King) {
                return 12;
            }

        } else {
            if(p instanceof Pawn) {
                for (int i = 0; i < 8; i++) {
                    if(Chessboard.blackPieces.get(i) == null) {
                        return i;
                    }
                }
            }

            else if(p instanceof Rook) {
                if(Chessboard.blackPieces.get(8) == null) return 8;
                if(Chessboard.blackPieces.get(15) == null) return 15;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Knight) {
                if(Chessboard.blackPieces.get(9) == null) return 9;
                if(Chessboard.blackPieces.get(14) == null) return 14;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Bishop) {
                if(Chessboard.blackPieces.get(10) == null) return 10;
                if(Chessboard.blackPieces.get(13) == null) return 13;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Queen) {
                if(Chessboard.blackPieces.get(11) == null) return 11;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(Chessboard.blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof King) {
                return 12;
            }
        }
        System.out.println(p.getClass());

       //throw new Exception("There is no valid Id for this piece!");
        return 1000;
    }

    public static int getSquareIdFromFen(String fen, int id) {
        char sign;
        int counter = 0;
        for (int i = 0; i < id ; i++) {
            sign = fen.charAt(i);
            if(sign == ' ')
                break;
            if(sign == '/')
                continue;
            if(isPiece(sign)) {
                counter++;
            } else {
                counter += Character.getNumericValue(sign);
            }
        }
        return counter;
    }

    public static void setIcons() {
        for (Piece p : Chessboard.whitePieces) {
            if(p == null)
                continue;
            Chessboard.b[p.getSquare().getId()].setImageResource(p.getIcon());
        }
        for (Piece p : Chessboard.blackPieces) {
            if(p == null)
                continue;
            Chessboard.b[p.getSquare().getId()].setImageResource(p.getIcon());
        }
    }

    public static void setTurn(String fen) {
        int index = fen.indexOf(" ");
        if(fen.charAt(index + 1) == 'w') {
            Turn.enableWhitePieces();
            Turn.disableBlackPieces();
        } else {
            Turn.disableWhitePieces();
            Turn.enableBlackPieces();
        }
    }

    public static void setCastlingInformation(String fen) {

        if((Chessboard.whitePieces.get(12)) != null)((King) Chessboard.whitePieces.get(12)).setWasMoved(true);
        if((Chessboard.whitePieces.get(15)) != null)((Rook) Chessboard.whitePieces.get(15)).setWasMoved(true);
        if((Chessboard.whitePieces.get(8)) != null)((Rook) Chessboard.whitePieces.get(8)).setWasMoved(true);
        if((Chessboard.blackPieces.get(12)) != null)((King) Chessboard.blackPieces.get(12)).setWasMoved(true);
        if((Chessboard.blackPieces.get(15)) != null)((Rook) Chessboard.blackPieces.get(15)).setWasMoved(true);
        if((Chessboard.blackPieces.get(8)) != null)((Rook) Chessboard.blackPieces.get(8)).setWasMoved(true);
        char sign;
        for(int i = fen.indexOf(" ") + 3; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if(sign == ' ')
                break;
            if(sign == 'K') {
                ((King) Chessboard.whitePieces.get(12)).setWasMoved(false);
                ((Rook) Chessboard.whitePieces.get(15)).setWasMoved(false);
            }
            else if(sign == 'Q') {
                ((King) Chessboard.whitePieces.get(12)).setWasMoved(false);
                ((Rook) Chessboard.whitePieces.get(8)).setWasMoved(false);
            }
            else if(sign == 'k') {
                ((King) Chessboard.blackPieces.get(12)).setWasMoved(false);
                ((Rook) Chessboard.blackPieces.get(15)).setWasMoved(false);
            }
            else if(sign == 'q') {
                ((King) Chessboard.blackPieces.get(12)).setWasMoved(false);
                ((Rook) Chessboard.blackPieces.get(8)).setWasMoved(false);
            }
            else if(sign == '-') {
                if((Chessboard.whitePieces.get(12)) != null)((King) Chessboard.whitePieces.get(12)).setWasMoved(true);
                if((Chessboard.whitePieces.get(15)) != null)((Rook) Chessboard.whitePieces.get(15)).setWasMoved(true);
                if((Chessboard.whitePieces.get(8)) != null)((Rook) Chessboard.whitePieces.get(8)).setWasMoved(true);
                if((Chessboard.blackPieces.get(12)) != null)((King) Chessboard.blackPieces.get(12)).setWasMoved(true);
                if((Chessboard.blackPieces.get(15)) != null)((Rook) Chessboard.blackPieces.get(15)).setWasMoved(true);
                if((Chessboard.blackPieces.get(8)) != null)((Rook) Chessboard.blackPieces.get(8)).setWasMoved(true);
            }
        }
    }

    public static void setEnPassantPossible(String fen) {
        int lastIndex = fen.lastIndexOf(" ");
        lastIndex = fen.substring(0, lastIndex).lastIndexOf(" ");
        lastIndex = fen.substring(0, lastIndex).lastIndexOf(" ");
        char sign;
        StringBuilder squareName = new StringBuilder();
        for (int i = lastIndex + 1; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if(sign == ' ')
                break;
            if(sign == '-') {
                Chessboard.enPassantPossible = null;
                return;
            }
            squareName.append(sign);
        }
        Chessboard.enPassantPossible = Chessboard.getSquares(Chessboard.getSquareId(squareName.toString()));
    }

    public static void setNumberOfHalfMoves(String fen) {
        int lastIndex = fen.lastIndexOf(" ");
        lastIndex = fen.substring(0, lastIndex).lastIndexOf(" ");
        StringBuilder toConvert = new StringBuilder();
        char sign;
        for (int i = lastIndex + 1; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if(sign == ' ')
                break;
            toConvert.append(sign);
        }
        Chessboard.numberOfHalfMoves = Integer.parseInt(toConvert.toString());
    }

    public static void setFullMoveNumber(String fen) {
        String []fenSplitted = fen.split(" ");
        Chessboard.moveList.clear();
        Chessboard.fullMoveNumber = Integer.parseInt(fenSplitted[5]);

    }
}