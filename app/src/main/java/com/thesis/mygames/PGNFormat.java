package com.thesis.mygames;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.List;

import static com.thesis.mygames.Chessboard.*;

public class PGNFormat {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void generatePgnTags() {
        PGNTagGenerator.append(generateEventTag("Memoral Anderssena")).append("\n");
        PGNTagGenerator.append(generateSiteTag("Warsaw", "POL")).append("\n");
        PGNTagGenerator.append(generateDateTag(LocalDate.of(2012, 3, 3))).append("\n");
        PGNTagGenerator.append(generateRoundTag(5)).append("\n");
        PGNTagGenerator.append(generateWhiteTag("Pazdzioch", "Marian")).append("\n");
        PGNTagGenerator.append(generateBlackTag("Papka", "Jan")).append("\n");
        PGNTagGenerator.append(generateResultTag("1-0")).append("\n");
    }

    public static String generateEventTag(String event) {
        if(event == null || event.equals(""))
            event = "?";

        return String.format("[Event \"%s\"]", event);
    }

    public static String generateSiteTag(String site, String country) {
        if(site == null || country == null)
            return "[Site \"?\"]";

        return String.format("[Site \"%s %s\"]", site, country);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String generateDateTag(LocalDate date) {
        if(date == null)
            return "[Date \"????.??.??\"]";

        int year = date.getYear();

        int month = date.getMonthValue();
        String monthStr = month < 10 ? "0" + month : Integer.toString(month);

        int day = date.getDayOfMonth();
        String dayStr = day < 10 ? "0" + day : Integer.toString(day);

        return String.format("[Date \"%d.%s.%s\"]", year, monthStr, dayStr);
    }

    public static String generateRoundTag(int number) {
        if(number == 0)
            return "[Round \"?\"]";

        return String.format("[Round \"%d\"]", number);
    }

    public static String generateWhiteTag(String lastName, String firstName, String... secondName) {
        if(lastName == null)
            return "[White \"?\"]";

        if(secondName.length != 0)
            return String.format("[White \"%s, %s %s\"]", lastName, firstName, secondName[0]);

        return String.format("[White \"%s, %s\"]", lastName, firstName);
    }

    public static String generateBlackTag(String lastName, String firstName, String... secondName) {
        if(lastName == null)
            return "[Black \"?\"]";

        if(secondName.length != 0)
            return String.format("[Black \"%s, %s %s\"]", lastName, firstName, secondName[0]);

        return String.format("[Black \"%s, %s\"]", lastName, firstName);
    }

    public static String generateResultTag(String result) {
        if(result.equals("1-0") || result.equals("1/2-1/2") || result.equals("0-1")) {
            return String.format("[Result \"%s\"]", result);
        } else {
            return "[Result \"*\"]";
        }
    }

    public static void generatePgnMoves() {
        if(moveList.size() % 2 == 1) {
            PGNMoveGenerator.append(moveList.size()/2 + 1).
                    append(". ").append(moveList.get(moveList.size() - 1).getNotation()).append(" ");
        } else {
           PGNMoveGenerator.append(moveList.get(moveList.size() - 1).getNotation()).append(" ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getMovedPiece(String move, int index) {

        String []classes = { "info.kania.Knight", "info.kania.Rook", "info.kania.Bishop", "info.kania.Queen", "info.kania.King", "info.kania.Pawn" };
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
        Integer []possiblePieces = pieces.stream()
                .filter(p -> {
                    try {
                        return isBelongToClass(p, myClass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .filter(p -> p.possibleFieldsToMoveCheck().contains(getSquares(getSquareId(getEndSquareName(move)))))
                .map(Piece::getId)
                .toArray(Integer[]::new);

        if(possiblePieces.length == 1)
            return possiblePieces[0];

        else if (possiblePieces.length > 1){
            List<Integer> listOfSquaresWherePieceCanBe = getRangeOfPossibleSquareId(move.charAt(1));
            for (Integer possiblePiece : possiblePieces) {
                if (listOfSquaresWherePieceCanBe.contains(pieces.get(possiblePiece).getId()))
                    return possiblePiece;
            }
        }

        return 0;
    }

    public static boolean isBelongToClass(Object obj, String c) throws ClassNotFoundException {
        return Class.forName(c).isInstance(obj);
    }

    public static String getEndSquareName(String move) {
        char lastSign = move.charAt(move.length() - 1);
        if(lastSign == '+' || lastSign == '#')
            return Character.toString(move.charAt(move.length() - 3)) + move.charAt(move.length() - 2);

        else if(lastSign == 'Q' || lastSign == 'N'|| lastSign == 'B' || lastSign == 'R')
            return Character.toString(move.charAt(move.length() - 4)) + move.charAt(move.length() - 3);

        else if(lastSign == 'O') {
            if(move.length() == 3) {
                if(Chessboard.moveList.size() % 2 == 0) {
                    return "g8";
                } else {
                    return "g1";
                }
            } else {
                if(Chessboard.moveList.size() % 2 == 0) {
                    return "c8";
                } else {
                    return "c1";
                }
            }
        } else {
            return Character.toString(move.charAt(move.length() - 2)) + move.charAt(move.length() - 1);
        }
    }

    public static List<Integer> getRangeOfPossibleSquareId(char c) {
        List<Integer> possibilities = new LinkedList<>();
        List<Character> coordinates = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
        if(coordinates.contains(c)) {
            for (int i = Character.getNumericValue(c) % 10; i < 64; i = i + 8)
                possibilities.add(i);
            return possibilities;
        }
        int param = Character.getNumericValue(c);
        for (int i = 64 - param*8; i < 64 - (param-1)*8; i++) {
            possibilities.add(i);
        }
        return possibilities;
    }
}
