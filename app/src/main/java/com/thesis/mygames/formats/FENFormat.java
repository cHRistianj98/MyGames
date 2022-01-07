package com.thesis.mygames.formats;

import android.widget.TextView;

import com.thesis.mygames.R;
import com.thesis.mygames.game.Move;
import com.thesis.mygames.game.Piece;
import com.thesis.mygames.game.Square;
import com.thesis.mygames.game.Turn;
import com.thesis.mygames.pieces.Bishop;
import com.thesis.mygames.pieces.King;
import com.thesis.mygames.pieces.Knight;
import com.thesis.mygames.pieces.Pawn;
import com.thesis.mygames.pieces.Queen;
import com.thesis.mygames.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thesis.mygames.game.Chessboard.*;

public class FENFormat {
    public static String generateFenFromPosition() {
        return getFenPosition() + " " +
                whoseMove() + " " +
                getCastlingInformation() + " " +
                getEnPassantPossibility() + " " +
                getNumberOfHalfMoves() + " " +
                getFullMoveNumber();
    }

    private static String getFenPosition() {
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

    private static String generateLetterBasedOnPiece(Piece p) {
        if (p.getColor()) {
            if (p instanceof Pawn) return "P";
            else if (p instanceof Rook) return "R";
            else if (p instanceof Knight) return "N";
            else if (p instanceof Bishop) return "B";
            else if (p instanceof Queen) return "Q";
            else if (p instanceof King) return "K";
        } else {
            if (p instanceof Pawn) return "p";
            else if (p instanceof Rook) return "r";
            else if (p instanceof Knight) return "n";
            else if (p instanceof Bishop) return "b";
            else if (p instanceof Queen) return "q";
            else if (p instanceof King) return "k";
        }
        return "";
    }

    private static String whoseMove() {
        if (Turn.whiteTurn) return "w";
        else return "b";
    }

    private static String getCastlingInformation() {
        StringBuilder fen = new StringBuilder();

        if (!((King) whitePieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(15)).isWasMoved())
            fen.append("K");
        if (!((King) whitePieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(8)).isWasMoved())
            fen.append("Q");
        if (!((King) blackPieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(15)).isWasMoved())
            fen.append("k");
        if (!((King) blackPieces.get(12)).isWasMoved() && !((Rook) whitePieces.get(8)).isWasMoved())
            fen.append("q");

        if (fen.toString().equals(""))
            return "-";
        else
            return fen.toString();
    }

    private static String getEnPassantPossibility() {
        if (enPassantPossible == null)
            return "-";
        else
            return getSquareName(enPassantPossible.getId());
    }

    private static String getNumberOfHalfMoves() {
        int counter = 0;
        for (Move move : moveList) {
            if (move.getNotation().startsWith("a") || move.getNotation().startsWith("b") || move.getNotation().startsWith("c") ||
                    move.getNotation().startsWith("d") || move.getNotation().startsWith("e") || move.getNotation().startsWith("f")
                    || move.getNotation().startsWith("g") || move.getNotation().startsWith("h") || move.getNotation().contains("x")) {
                counter = 0;
            } else {
                counter++;
            }
        }
        return Integer.toString(counter);
    }

    private static String getFullMoveNumber() {
        int listSize = moveList.size();

        return listSize % 2 == 0 ? Integer.toString((listSize + 2) / 2) : Integer.toString((listSize + 1) / 2);
    }

    public static void loadPositionFromFen(String fen) throws IllegalArgumentException {
        if (!isFenValid(fen))
            throw new IllegalArgumentException("Code in FEN format is wrong!");

        clearMoveList();
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

    private static boolean isFenValid(String fen) {
        Pattern fenPattern = Pattern.compile("((([prnbqkPRNBQK12345678]*/){7})([prnbqkPRNBQK12345678]*)) (w|b) ((K?Q?k?q?)|\\-) (([abcdefgh][36])|\\-) (\\d*) (\\d*)");
        Matcher fenMatcher = fenPattern.matcher(fen);
        if (!fenMatcher.matches()) {
            return false;
        }

        String[] ranks = Objects.requireNonNull(fenMatcher.group(2)).split("/");
        for (String rank : ranks) {
            if (!verifyRank(rank))
                return false;
        }
        if (!verifyRank(Objects.requireNonNull(fenMatcher.group(4)))) {
            return false;
        }

        return Objects.requireNonNull(fenMatcher.group(1)).contains("k") && Objects.requireNonNull(fenMatcher.group(1)).contains("K");
    }

    private static void clearMoveList() {
        moveList = new ArrayList<>();
        moveIndicator = -1;

        enPassantPossible = null;
        numberOfHalfMoves = 0;
        fullMoveNumber = 0;
        PGNTagGenerator = new StringBuilder();
        PGNMoveGenerator = new StringBuilder();

        TextView textView = Move.activity.findViewById(R.id.moves);
        textView.setText(PGNMoveGenerator.toString());
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

    private static void clearPieces() {
        for (int i = 0; i < 64; i++) {
            getSquares(i).setPiece(null);
            b[i].setImageResource(0);
        }
    }

    private static void removeActionListeners() {
        for (int i = 0; i < 64; i++) {
            b[i].setOnClickListener(null);
        }
    }

    private static void setObjectsOnNull() {
        for (int i = 0; i < 16; i++) {
            whitePieces.set(i, null);
            blackPieces.set(i, null);
        }
    }

    private static void setNewObjects(String fen) {
        char sign;
        for (int i = 0; i < fen.length() ; i++) {
            sign = fen.charAt(i);
            if (sign == ' ') {
                break;
            }
            if (isPiece(sign)) {
                objectFactory(sign, fen, i);
            }
        }
    }

    private static boolean isPiece(char s) {
        return s == 'p' || s == 'P' || s == 'r' || s == 'R' || s == 'n' || s == 'N' ||
                s == 'b' || s == 'B' || s == 'q' || s == 'Q' || s == 'k' || s == 'K';
    }

    private static void objectFactory(char s, String fen, int i) {
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

    private static void createObject(Piece p) {
        p.setId(assignId(p));
        if (p.getColor()) {
            whitePieces.set(p.getId(), p);
        } else {
            blackPieces.set(p.getId(), p);
        }
        getSquares(p.getSquare().getId()).setPiece(p);
    }

    private static int assignId(Piece p) {
        List<Piece> pieces = p.getColor() ? whitePieces : blackPieces;

        switch(p.getClass().getSimpleName()) {
            case "Pawn":
                for (int i = 0; i < 8; i++) {
                    if(pieces.get(i) == null) {
                        return i;
                    }
                }
                break;

            case "Rook":
                if(pieces.get(8) == null) return 8;
                if(pieces.get(15) == null) return 15;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(pieces.get(i) == null) {
                            return i;
                        }
                    }
                }
                break;

            case "Knight":
                if(pieces.get(9) == null) return 9;
                if(pieces.get(14) == null) return 14;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(pieces.get(i) == null) {
                            return i;
                        }
                    }
                }
                break;
            case "Bishop":
                if(pieces.get(10) == null) return 10;
                if(pieces.get(13) == null) return 13;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(pieces.get(i) == null) {
                            return i;
                        }
                    }
                }
                break;
            case "Queen":
                if(pieces.get(11) == null) return 11;
                else {
                    for (int i = 0; i < 8; i++) {
                        if(pieces.get(i) == null) {
                            return i;
                        }
                    }
                }
                break;
            case "King":
                return 12;
        }
        return -1;
    }

    private static int getSquareIdFromFen(String fen, int id) {
        char sign;
        int counter = 0;
        for (int i = 0; i < id ; i++) {
            sign = fen.charAt(i);
            if (sign == ' ')
                break;
            if (sign == '/')
                continue;
            if (isPiece(sign)) {
                counter++;
            } else {
                counter += Character.getNumericValue(sign);
            }
        }
        return counter;
    }

    private static void setIcons() {
        for (Piece p : whitePieces) {
            if (p == null)
                continue;
            b[p.getSquare().getId()].setImageResource(p.getIcon());
        }
        for (Piece p : blackPieces) {
            if (p == null)
                continue;
            b[p.getSquare().getId()].setImageResource(p.getIcon());
        }
    }

    private static void setTurn(String fen) {
        int index = fen.indexOf(" ");
        if (fen.charAt(index + 1) == 'w') {
            Turn.enableWhitePieces();
            Turn.disableBlackPieces();
        } else {
            Turn.disableWhitePieces();
            Turn.enableBlackPieces();
        }
    }

    private static void setCastlingInformation(String fen) {
        if ((whitePieces.get(12)) != null)((King) whitePieces.get(12)).setWasMoved(true);
        if ((whitePieces.get(15)) != null)((Rook) whitePieces.get(15)).setWasMoved(true);
        if ((whitePieces.get(8)) != null)((Rook) whitePieces.get(8)).setWasMoved(true);
        if ((blackPieces.get(12)) != null)((King) blackPieces.get(12)).setWasMoved(true);
        if ((blackPieces.get(15)) != null)((Rook) blackPieces.get(15)).setWasMoved(true);
        if ((blackPieces.get(8)) != null)((Rook) blackPieces.get(8)).setWasMoved(true);
        char sign;
        for (int i = fen.indexOf(" ") + 3; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if (sign == ' ')
                break;
            if (sign == 'K') {
                ((King) whitePieces.get(12)).setWasMoved(false);
                ((Rook) whitePieces.get(15)).setWasMoved(false);
            }
            else if (sign == 'Q') {
                ((King) whitePieces.get(12)).setWasMoved(false);
                ((Rook) whitePieces.get(8)).setWasMoved(false);
            }
            else if (sign == 'k') {
                ((King) blackPieces.get(12)).setWasMoved(false);
                ((Rook) blackPieces.get(15)).setWasMoved(false);
            }
            else if (sign == 'q') {
                ((King) blackPieces.get(12)).setWasMoved(false);
                ((Rook) blackPieces.get(8)).setWasMoved(false);
            }
            else if (sign == '-') {
                if ((whitePieces.get(12)) != null)((King) whitePieces.get(12)).setWasMoved(true);
                if ((whitePieces.get(15)) != null)((Rook) whitePieces.get(15)).setWasMoved(true);
                if ((whitePieces.get(8)) != null)((Rook) whitePieces.get(8)).setWasMoved(true);
                if ((blackPieces.get(12)) != null)((King) blackPieces.get(12)).setWasMoved(true);
                if ((blackPieces.get(15)) != null)((Rook) blackPieces.get(15)).setWasMoved(true);
                if ((blackPieces.get(8)) != null)((Rook) blackPieces.get(8)).setWasMoved(true);
            }
        }
    }

    private static void setEnPassantPossible(String fen) {
        int lastIndex = fen.lastIndexOf(" ");
        lastIndex = fen.substring(0, lastIndex).lastIndexOf(" ");
        lastIndex = fen.substring(0, lastIndex).lastIndexOf(" ");
        char sign;
        StringBuilder squareName = new StringBuilder();
        for (int i = lastIndex + 1; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if (sign == ' ')
                break;
            if (sign == '-') {
                enPassantPossible = null;
                return;
            }
            squareName.append(sign);
        }
        enPassantPossible = getSquares(getSquareId(squareName.toString()));
    }

    private static void setNumberOfHalfMoves(String fen) {
        int lastIndex = fen.lastIndexOf(" ");
        lastIndex = fen.substring(0, lastIndex).lastIndexOf(" ");
        StringBuilder toConvert = new StringBuilder();
        char sign;
        for (int i = lastIndex + 1; i < fen.length(); i++) {
            sign = fen.charAt(i);
            if (sign == ' ')
                break;
            toConvert.append(sign);
        }
        numberOfHalfMoves = Integer.parseInt(toConvert.toString());
    }

    private static void setFullMoveNumber(String fen) {
        String []fenSplitted = fen.split(" ");
        moveList.clear();
        fullMoveNumber = Integer.parseInt(fenSplitted[5]);
    }
}
