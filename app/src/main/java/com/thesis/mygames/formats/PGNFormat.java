package com.thesis.mygames.formats;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.thesis.mygames.R;
import com.thesis.mygames.game.Chessboard;
import com.thesis.mygames.game.Move;
import com.thesis.mygames.game.Piece;
import com.thesis.mygames.game.Square;
import com.thesis.mygames.pieces.Pawn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.thesis.mygames.game.Chessboard.*;

public class PGNFormat {
    public static void generatePgnTags(String event,
                                       String site,
                                       String date,
                                       int round,
                                       String whiteLastName,
                                       String whiteFirstName,
                                       String blackLastName,
                                       String blackFirstName,
                                       String result) {
        PGNTagGenerator.append(generateEventTag(event)).append("\n");
        PGNTagGenerator.append(generateSiteTag(site, "POL")).append("\n");
        PGNTagGenerator.append(generateDateTag(date)).append("\n");
        PGNTagGenerator.append(generateRoundTag(round)).append("\n");
        PGNTagGenerator.append(generateWhiteTag(whiteLastName, whiteFirstName)).append("\n");
        PGNTagGenerator.append(generateBlackTag(blackLastName, blackFirstName)).append("\n");
        PGNTagGenerator.append(generateResultTag(result)).append("\n");
    }

    public static String generateEventTag(String event) {
        if(event == null || event.equals(""))
            event = "?";

        return String.format("[Event \"%s\"]", event);
    }

    public static String generateSiteTag(String site, String country) {
        if(site == null || country == null || site.equals("?"))
            return "[Site \"?\"]";

        return String.format("[Site \"%s %s\"]", site, country);
    }

    public static String generateDateTag(String date) {
        if(date == null || date.equals("????.??.??"))
            return "[Date \"????.??.??\"]";

        return date;
    }

    public static String generateRoundTag(int number) {
        if(number == 0)
            return "[Round \"?\"]";

        return "[Round \"" + number + "\"]";
    }

    public static String generateWhiteTag(String lastName, String firstName) {
        if(lastName == null || lastName.equals("?"))
            return "[White \"?\"]";

        else if(firstName == null || firstName.equals("?"))
            return String.format("[White \"%s\"]", lastName);

        return String.format("[White \"%s, %s\"]", lastName, firstName);
    }

    public static String generateBlackTag(String lastName, String firstName) {
        if(lastName == null || lastName.equals("?"))
            return "[Black \"?\"]";

        else if(firstName == null || firstName.equals("?"))
            return String.format("[Black \"%s\"]", lastName);

        return String.format("[Black \"%s, %s\"]", lastName, firstName);
    }

    public static String generateResultTag(String result) {
        if (result == null)
            return "[Result \"*\"]";

        if (result.equals("1-0") || result.equals("1/2-1/2") || result.equals("0-1")) {
            return String.format("[Result \"%s\"]", result);
        } else {
            return "[Result \"*\"]";
        }
    }

    public static void generatePgnMoves() {
        if (moveList.size() % 2 == 1) {
            PGNMoveGenerator.append(moveList.size()/2 + 1).
                    append(". ").append(moveList.get(moveList.size() - 1).getNotation()).append(" ");
        } else {
            PGNMoveGenerator.append(moveList.get(moveList.size() - 1).getNotation()).append(" ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void loadGameFromPgn(String pgn) {
        clearChessboardForPgn();
        init();
        if (!isPgnValid(pgn)) {
            throw new IllegalArgumentException("Code in PGN format is wrong!");
        }

        StringBuilder moveSection = new StringBuilder();

        Scanner scanner = new Scanner(pgn);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();
            Pattern compiledTagPattern = Pattern.compile("\\[.*\\]");

            if (compiledTagPattern.matcher(line).matches()) {
                continue;
            }

            Pattern compiledMoveSection = Pattern.compile("(.+ )+(1\\-0)?(1\\\\2\\-1\\\\2)?(0\\-1)?");

            if (compiledMoveSection.matcher(line + " ").matches()) {
                moveSection.append(line);
            }
        }
        scanner.close();

        String moves = moveSection.toString();
        moves = moves.trim();
        String []movesArray = moves.split(" ");
        Pattern moveNumberPattern = Pattern.compile("[1-9][0-9]?[0-9]?[0-9]?\\.");
        for (int i = 0; i < movesArray.length; i++) {
            if (moveNumberPattern.matcher(movesArray[i]).matches()) {
                movesArray[i] = null;
            }
        }
        List<String> moveList = new ArrayList<>();
        for (String move : movesArray) {
            if (move != null)
                moveList.add(move);
        }

        String[] results = Move.activity.getApplicationContext().getResources().getStringArray(R.array.pgn_results);
        if (Arrays.asList(results[0], results[1], results[2], results[3])
                .contains(moveList.get(moveList.size() - 1)))
            moveList.remove(moveList.size() - 1);

        List<Piece> pieces;
        for (int i = 0; i < moveList.size(); i++) {
            pieces = i % 2 == 0 ? whitePieces : blackPieces;
            int id = 0;
            try {
                id = PGNFormat.getMovedPiece(moveList.get(i), i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String selectedPieceFromPgn = "";
            if (pieces.get(id) instanceof Pawn &&
                    Arrays.asList("a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", "a1",
                            "b1", "c1", "d1", "e1", "f1", "g1", "h1")
                            .contains(PGNFormat.getEndSquareName(moveList.get(i)))) {
                selectedPieceFromPgn = getPieceName(moveList.get(i));
            }
            Square endSquare = getSquares(getSquareId(PGNFormat.getEndSquareName(moveList.get(i))));
            Move move = new Move(pieces.get(id));
            int startSquareId = pieces.get(id).getSquare().getId();
            move.makeMoveFromPgn(endSquare.getId(), endSquare, startSquareId, selectedPieceFromPgn);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getMovedPiece(String move, int index) throws Exception {

        String []classes = {
                "com.thesis.mygames.pieces.Knight",
                "com.thesis.mygames.pieces.Rook",
                "com.thesis.mygames.pieces.Bishop",
                "com.thesis.mygames.pieces.Queen",
                "com.thesis.mygames.pieces.King",
                "com.thesis.mygames.pieces.Pawn" };
        String myClass;
        switch(move.charAt(0)) {
            case 'O': return 12;
            case 'N': myClass = classes[0]; break;
            case 'B': myClass = classes[2]; break;
            case 'R': myClass = classes[1]; break;
            case 'Q': myClass = classes[3]; break;
            case 'K': myClass = classes[4]; break;
            default: myClass = classes[5]; break;
        }

        List<Piece> pieces = index % 2 == 0 ? whitePieces : blackPieces;
        List<Integer> possPieces = new ArrayList<>();
        for (Piece p : pieces) {
            if (!isBelongToClass(p,myClass))
                continue;
            if (!p.possibleSquaresToMoveIncludingCheck().contains(getSquares(getSquareId(getEndSquareName(move)))))
                continue;
            possPieces.add(p.getId());
        }

        if (possPieces.size() == 1)
            return possPieces.get(0);

        else if (possPieces.size() > 1){
            List<Integer> listOfSquaresWherePieceCanBe = getRangeOfPossibleSquareId(move);
            for (Integer possiblePiece : possPieces) {
                if (listOfSquaresWherePieceCanBe.contains(pieces.get(possiblePiece).getSquare().getId()))
                    return possiblePiece;
            }
        }

        throw new Exception("Ruch nie będący pionem zwraca 0: " + move);
    }

    public static boolean isBelongToClass(Object obj, String c) throws ClassNotFoundException {
        return Class.forName(c).isInstance(obj);
    }

    public static String getEndSquareName(String move) {
        char lastSign = move.charAt(move.length() - 1);
        if (lastSign == '+' || lastSign == '#') {
            char sign = move.charAt(move.length() - 2);
            if (sign == 'Q' || sign == 'N'|| sign == 'B' || sign == 'R') {
                return Character.toString(move.charAt(move.length() - 5))
                        + move.charAt(move.length() - 4);
            }
            else if (sign == 'O') {
                if (move.length() == 4) {
                    if (Chessboard.moveList.size() % 2 == 1) {
                        return "g8";
                    } else {
                        return "g1";
                    }
                } else {
                    if (Chessboard.moveList.size() % 2 == 1) {
                        return "c8";
                    } else {
                        return "c1";
                    }
                }
            }
            return Character.toString(move.charAt(move.length() - 3))
                    + move.charAt(move.length() - 2);
        }

        if (lastSign == 'Q' || lastSign == 'N'|| lastSign == 'B' || lastSign == 'R')
            return Character.toString(move.charAt(move.length() - 4))
                    + move.charAt(move.length() - 3);

        else if (lastSign == 'O') {
            if (move.length() == 3) {
                if(Chessboard.moveList.size() % 2 == 1) {
                    return "g8";
                } else {
                    return "g1";
                }
            } else {
                if(Chessboard.moveList.size() % 2 == 1) {
                    return "c8";
                } else {
                    return "c1";
                }
            }
        } else {
            return Character.toString(move.charAt(move.length() - 2)) + move.charAt(move.length() - 1);
        }
    }

    public static List<Integer> getRangeOfPossibleSquareId(String move) {
        List<Integer> possibilities = new LinkedList<>();

        char keyCoordinate = move.charAt(1);

        if (keyCoordinate == 'x') {
            keyCoordinate = move.charAt(0);
        }

        List<Character> coordinates = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
        if (coordinates.contains(keyCoordinate)) {
            for (int i = Character.getNumericValue(keyCoordinate) % 10; i < 64; i = i + 8)
                possibilities.add(i);
            return possibilities;
        }
        int param = Character.getNumericValue(keyCoordinate);
        for (int i = 64 - param*8; i < 64 - (param-1)*8; i++) {
            possibilities.add(i);
        }
        return possibilities;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isPgnValid(String pgn) {
        PGNValidator validator = new PGNValidator();
        return validator.validate(pgn);
    }

    public static String getPieceName(String moveNotation) {
        char lastSign = moveNotation.charAt(moveNotation.length() - 1);
        String[] piecesNames = Move.activity.getApplicationContext().getResources().getStringArray(R.array.pieces);

        if (lastSign == '+' || lastSign =='#') {
            lastSign = moveNotation.charAt(moveNotation.length() - 2);
        }
        switch (lastSign) {
            case 'Q':
                return piecesNames[0];
            case 'R':
                return piecesNames[1];
            case 'B':
                return piecesNames[2];
            case 'N':
                return piecesNames[3];
        }
        return "nic";
    }
}
