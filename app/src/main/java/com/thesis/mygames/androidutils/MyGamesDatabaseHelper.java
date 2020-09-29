package com.thesis.mygames.androidutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyGamesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_games";
    private static final int DB_VERSION = 1;

    public MyGamesDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE GAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "event TEXT, "
                + "site TEXT, "
                + "date TEXT, "
                + "round INTEGER, "
                + "white_firstname TEXT, "
                + "white_lastname TEXT, "
                + "black_firstname TEXT, "
                + "black_lastname TEXT, "
                + "result TEXT, "
                + "white_elo INTEGER, "
                + "black_elo INTEGER, "
                + "moves TEXT);");

//       insertGame(db,
//               "",
//               "",
//               "",
//               1,
//               "Jan",
//               "Wojtyla",
//               "Marcin",
//               "Najman",
//               "1-0",
//               1000,
//               1000,
//               "1. e2 e7");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void insertGame(SQLiteDatabase db, String event, String site, String date, Integer round, String whiteFirstName,
                           String whiteLastName, String blackFirstName, String blackLastName, String result, Integer whiteElo,
                           Integer blackElo, String moves) {
        ContentValues gameValues = new ContentValues();
        gameValues.put("event", event);
        gameValues.put("site", site);
        gameValues.put("date", date);
        gameValues.put("round", round);
        gameValues.put("white_firstname", whiteFirstName);
        gameValues.put("white_lastname", whiteLastName);
        gameValues.put("black_firstname", blackFirstName);
        gameValues.put("black_lastname", blackLastName);
        gameValues.put("result", result);
        gameValues.put("white_elo", whiteElo);
        gameValues.put("black_elo", blackElo);
        gameValues.put("moves", moves);
        db.insert("GAME", null, gameValues);
    }
}
