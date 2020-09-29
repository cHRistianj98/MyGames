package com.thesis.mygames.formats;

import com.thesis.mygames.R;
import com.thesis.mygames.activities.MainActivity;
import com.thesis.mygames.gameutils.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNValidator {

    public boolean validate(String pgn) {
        StringBuilder moveSection = new StringBuilder();

        Scanner scanner = new Scanner(pgn);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();
            Pattern compiledTagPattern = Pattern.compile("\\[.*\\]");

            if(compiledTagPattern.matcher(line).matches()) {
                Pattern compiledSpecificTagPattern = Pattern.compile("\\[(Event|Site|Round|Date|White|Black) \".+\"\\]");
                Matcher tagMatcher = compiledSpecificTagPattern.matcher(line);
                if(tagMatcher.matches()) {
                    saveTagProperty(line);
                }
                continue;
            }

            Pattern compiledMoveSection = Pattern.compile("(.+ )+(1\\-0)?(1\\\\2\\-1\\\\2)?(0\\-1)?");
            if(compiledMoveSection.matcher(line + " ").matches()) {
                moveSection.append(line);
            }
        }
        scanner.close();

        String moves = moveSection.toString();
        moves = moves.trim();
        String []movesArray = moves.split(" ");

        for (int i = 0; i < movesArray.length; i++) {
            if(i%3 == 0)
                movesArray[i] = null;
        }

        List<String> moveList = new ArrayList<>();

        for (String move : movesArray) {
            if (move != null)
                moveList.add(move);
        }

        String[] results = Move.activity.getApplicationContext().getResources().getStringArray(R.array.pgn_results);
        if(Arrays.asList(results[0], results[1], results[2], results[3]).contains(moveList.get(moveList.size() - 1)))
            moveList.remove(moveList.size() - 1);

        final String SINGLE_HIT_STRING_PATTERN = "([a-hA-H]+[1-8]{1}[=][QRBN]{1}[\\+|#]?|" + // promotion
                "[a-hrnqkA-HRNQK]{1}[1-8]{1}[\\+]?|" + // one letter / one number pattern
                // : simple hit
                "[a-hA-H]{1}[1-8]?[a-hA-H]{1,3}?[1-8]{1}[\\+|#]?|" + // complex hit
                "[O]+[\\-][O]+[\\-][O]+[\\+|#]?|" + // queenside castling hit
                "[O]+[\\-][O]+[\\+|#]?|" + // kingside castling hit
                "[a-hpA-HP]{1}[\\-][a-hpA-HP]{1}[\\+|#]?|" + //
                "[RNBQK]?[a-hp]?[1-8]?[x]?[a-hp][1-8][\\+|#]?)";
        Pattern movePattern = Pattern.compile(SINGLE_HIT_STRING_PATTERN);
        Matcher moveMatcher;
        for (String move : moveList) {
            moveMatcher = movePattern.matcher(move);
            if(!moveMatcher.matches()) {
                return false;
            }
        }
        return true;
    }

    public void saveTagProperty(String line) {
        switch (line.charAt(1)) {
            case 'E': MainActivity.EVENT = getProperty(line); break;
            case 'S': MainActivity.SITE = getProperty(line); break;
            case 'R': MainActivity.ROUND = Integer.parseInt(getProperty(line)); break;
            case 'D': MainActivity.DATE = getProperty(line); break;
            case 'W': MainActivity.WHITE = getProperty(line); break;
            case 'B': MainActivity.BLACK = getProperty(line); break;
           // default: MainActivity.RESULT = getProperty(line); break;
        }
    }

    public String getProperty(String line) {
        line = line.substring(line.indexOf("\"") + 1);
        line = line.substring(0, line.indexOf("\""));
        return line;
    }
}
