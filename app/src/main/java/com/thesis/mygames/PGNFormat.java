package com.thesis.mygames;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void generatePgnMoves() {
        if(moveList.size() % 2 == 1) {
            PGNMoveGenerator.append(moveList.size()/2 + 1).
                    append(". ").append(moveList.get(moveList.size() - 1)).append(" ");
        } else {
           PGNMoveGenerator.append(moveList.get(moveList.size() - 1)).append(" ");
        }
    }

}
