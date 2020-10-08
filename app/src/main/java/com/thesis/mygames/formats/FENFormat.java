package com.thesis.mygames.formats;

import com.thesis.mygames.game_utils.Move;
import com.thesis.mygames.game_utils.Piece;
import com.thesis.mygames.game_utils.Square;
import com.thesis.mygames.game_utils.Turn;
import com.thesis.mygames.pieces.Bishop;
import com.thesis.mygames.pieces.King;
import com.thesis.mygames.pieces.Knight;
import com.thesis.mygames.pieces.Pawn;
import com.thesis.mygames.pieces.Queen;
import com.thesis.mygames.pieces.Rook;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thesis.mygames.game_utils.Chessboard.*;

public class FENFormat {
    public static String generateFENFromPosition() {
        return getFenPosition() + " " +
                whoseMove() + " " +
                getCastlingInformation() + " " +
                getEnPassantPossibility() + " " +
                getNumberOfHalfMoves() + " " +
                getFullMoveNumber();
    }

    public static String getFenPosition() {
        StringBuilder fen = new StringBuilder();
        Square s;
        int counter = 0;
        for (int i = 0 ; i < 64;  i++) {
            s = getSquares(i);
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

        if(!((King) whitePieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(15)).isWasMoved())
            fen.append("K");
        if(!((King) whitePieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(8)).isWasMoved())
            fen.append("Q");
        if(!((King) blackPieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(15)).isWasMoved())
            fen.append("k");
        if(!((King) blackPieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(8)).isWasMoved())
            fen.append("q");

        if(fen.toString().equals(""))
            return "-";
        else
            return fen.toString();
    }

    public static String getEnPassantPossibility() {
        if(enPassantPossible == null)
            return "-";
        else
            return getSquareName(enPassantPossible.getId());
    }

    public static String getNumberOfHalfMoves() {
        int counter = 0;
        for (Move move : moveList) {
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
        int listSize = moveList.size();

        return listSize % 2 == 0 ? Integer.toString((listSize + 2) / 2) : Integer.toString((listSize + 1) / 2);
    }

    public static void loadPositionFromFen(String fen) throws IllegalArgumentException {
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
        Pattern pattern = Pattern.compile("((([prnbqkPRNBQK12345678]*/){7})([prnbqkPRNBQK12345678]*)) (w|b) ((K?Q?k?q?)|\\-) (([abcdefgh][36])|\\-) (\\d*) (\\d*)");
        Matcher matcher = pattern.matcher(fen);
        if (!matcher.matches()) {
            return false;
        }

        String[] ranks = Objects.requireNonNull(matcher.group(2)).split("/");
        for (String rank : ranks) {
            if (!verifyRank(rank))
                return false;
        }
        if (!verifyRank(Objects.requireNonNull(matcher.group(4)))) {
            return false;
        }

        if (!Objects.requireNonNull(matcher.group(1)).contains("k") || !Objects.requireNonNull(matcher.group(1)).contains("K")) {
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

    public static void clearPieces() {
        for (int i = 0; i < 64; i++) {
            getSquares(i).setPiece(null);
            b[i].setImageResource(0);
        }
    }

    public static void removeActionListeners() {
        for (int i = 0; i < 64; i++) {
            b[i].setOnClickListener(null);
        }
    }

    public static void setObjectsOnNull() {
        for (int i = 0; i < 16; i++) {
            whitePieces.set(i, null);
            blackPieces.set(i, null);
        }
    }

    public static void setNewObjects(String fen) {
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
        return s == 'p' || s == 'P' || s == 'r' || s == 'R' || s == 'n' || s == 'N' ||
                s == 'b' || s == 'B' || s == 'q' || s == 'Q' || s == 'k' || s == 'K';
    }

    public static void objectFactory(char s, String fen, int i) {
        switch (s) {
            case 'p': createObject(new Pawn(getSquares(getSquareIdFromFen(fen, i)),false, blackIcons[8])); break;
            case 'P': createObject(new Pawn(getSquares(getSquareIdFromFen(fen, i)),true, whiteIcons[0])); break;
            case 'r': createObject(new Rook(getSquares(getSquareIdFromFen(fen, i)),false, blackIcons[0])); break;
            case 'R': createObject(new Rook(getSquares(getSquareIdFromFen(fen, i)),true, whiteIcons[8])); break;
            case 'n': createObject(new Knight(getSquares(getSquareIdFromFen(fen, i)),false, blackIcons[1])); break;
            case 'N': createObject(new Knight(getSquares(getSquareIdFromFen(fen, i)),true, whiteIcons[9])); break;
            case 'b': createObject(new Bishop(getSquares(getSquareIdFromFen(fen, i)),false, blackIcons[2])); break;
            case 'B': createObject(new Bishop(getSquares(getSquareIdFromFen(fen, i)),true, whiteIcons[10])); break;
            case 'q': createObject(new Queen(getSquares(getSquareIdFromFen(fen, i)),false, blackIcons[3])); break;
            case 'Q': createObject(new Queen(getSquares(getSquareIdFromFen(fen, i)),true, whiteIcons[11])); break;
            case 'k': createObject(new King(getSquares(getSquareIdFromFen(fen, i)),false, blackIcons[4])); break;
            case 'K': createObject(new King(getSquares(getSquareIdFromFen(fen, i)),true, whiteIcons[12])); break;
        }
    }

    public static void createObject(Piece p) {
        p.setId(assignId(p));
        if (p.getColor()) {
            whitePieces.set(p.getId(), p);
        } else {
            blackPieces.set(p.getId(), p);
        }
        getSquares(p.getSquare().getId()).setPiece(p);
    }

    public static int assignId(Piece p) {

        if (p.getColor()) {
            if(p instanceof Pawn) {
                for (int i = 0; i < 8; i++) {
                    if(whitePieces.get(i) == null) {
                        return i;
                    }
                }
            }

            else if(p instanceof Rook) {
                if(whitePieces.get(8) == null) return 8;
                if(whitePieces.get(15) == null) return 15;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Knight) {
                if(whitePieces.get(9) == null) return 9;
                if(whitePieces.get(14) == null) return 14;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Bishop) {
                if(whitePieces.get(10) == null) return 10;
                if(whitePieces.get(13) == null) return 13;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(whitePieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Queen) {
                if(whitePieces.get(11) == null) return 11;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(whitePieces.get(i) == null) {
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
                    if(blackPieces.get(i) == null) {
                        return i;
                    }
                }
            }

            else if(p instanceof Rook) {
                if(blackPieces.get(8) == null) return 8;
                if(blackPieces.get(15) == null) return 15;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Knight) {
                if(blackPieces.get(9) == null) return 9;
                if(blackPieces.get(14) == null) return 14;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Bishop) {
                if(blackPieces.get(10) == null) return 10;
                if(blackPieces.get(13) == null) return 13;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(blackPieces.get(i) == null) {
                            return i;
                        }
                    }
                }
            }

            else if(p instanceof Queen) {
                if(blackPieces.get(11) == null) return 11;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(blackPieces.get(i) == null) {
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
        for (Piece p : whitePieces) {
            if(p == null)
                continue;
            b[p.getSquare().getId()].setImageResource(p.getIcon());
        }
        for (Piece p : blackPieces) {
            if(p == null)
                continue;
            b[p.getSquare().getId()].setImageResource(p.getIcon());
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
        if((whitePieces.get(12)) != null)((King) whitePieces.get(12)).setWasMoved(true);
        if((whitePieces.get(15)) != null)((Rook) whitePieces.get(15)).setWasMoved(true);
        if((whitePieces.get(8)) != null)((Rook) whitePieces.get(8)).setWasMoved(true);
        if((blackPieces.get(12)) != null)((King) blackPieces.get(12)).setWasMoved(true);
        if((blackPieces.get(15)) != null)((Rook) blackPieces.get(15)).setWasMoved(true);
        if((blackPieces.get(8)) != null)((Rook) blackPieces.get(8)).setWasMoved(true);
        char sign;
        for(int i = fen.indexOf(" ") + 3; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if(sign == ' ')
                break;
            if(sign == 'K') {
                ((King) whitePieces.get(12)).setWasMoved(false);
                ((Rook) whitePieces.get(15)).setWasMoved(false);
            }
            else if(sign == 'Q') {
                ((King) whitePieces.get(12)).setWasMoved(false);
                ((Rook) whitePieces.get(8)).setWasMoved(false);
            }
            else if(sign == 'k') {
                ((King) blackPieces.get(12)).setWasMoved(false);
                ((Rook) blackPieces.get(15)).setWasMoved(false);
            }
            else if(sign == 'q') {
                ((King) blackPieces.get(12)).setWasMoved(false);
                ((Rook) blackPieces.get(8)).setWasMoved(false);
            }
            else if(sign == '-') {
                if((whitePieces.get(12)) != null)((King) whitePieces.get(12)).setWasMoved(true);
                if((whitePieces.get(15)) != null)((Rook) whitePieces.get(15)).setWasMoved(true);
                if((whitePieces.get(8)) != null)((Rook) whitePieces.get(8)).setWasMoved(true);
                if((blackPieces.get(12)) != null)((King) blackPieces.get(12)).setWasMoved(true);
                if((blackPieces.get(15)) != null)((Rook) blackPieces.get(15)).setWasMoved(true);
                if((blackPieces.get(8)) != null)((Rook) blackPieces.get(8)).setWasMoved(true);
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
                enPassantPossible = null;
                return;
            }
            squareName.append(sign);
        }
        enPassantPossible = getSquares(getSquareId(squareName.toString()));
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
        numberOfHalfMoves = Integer.parseInt(toConvert.toString());
    }

    public static void setFullMoveNumber(String fen) {
        String []fenSplitted = fen.split(" ");
        moveList.clear();
        fullMoveNumber = Integer.parseInt(fenSplitted[5]);

    }
}