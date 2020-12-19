package com.thesis.mygames.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MyGamesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_games";
    private static final int DB_VERSION = 7;

    public MyGamesDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //super(context, null, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String view = "CREATE VIEW my_games_view AS\n" +
                "SELECT GAMES._id AS _id, WHITE_PLAYERS.last_name ||'  '|| GAMES.result ||'  '|| BLACK_PLAYERS.last_name AS list_view\n" +
                "FROM GAMES\n" +
                "INNER JOIN WHITE_PLAYERS ON GAMES.white_player_id = WHITE_PLAYERS._id\n" +
                "INNER JOIN BLACK_PLAYERS ON GAMES.black_player_id = BLACK_PLAYERS._id;";

        db.execSQL("DROP VIEW IF EXISTS my_games_view;");
        db.execSQL(view);

        db.execSQL("CREATE TABLE GAMES (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT, "
                + "round INTEGER, "
                + "result TEXT, "
                + "moves TEXT, "
                + "white_player_id INTEGER, "
                + "black_player_id INTEGER, "
                + "event_id INTEGER, "
                + "FOREIGN KEY (white_player_id) REFERENCES WHITE_PLAYERS (_id), "
                + "FOREIGN KEY (black_player_id) REFERENCES BLACK_PLAYERS (_id), "
                + "FOREIGN KEY (event_id) REFERENCES EVENTS (_id));"
        );

        db.execSQL("CREATE TABLE EVENTS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "site TEXT); "
        );

        db.execSQL("CREATE TABLE WHITE_PLAYERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT, "
                + "last_name TEXT, "
                + "rating INTEGER);"
        );

        db.execSQL("CREATE TABLE BLACK_PLAYERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT, "
                + "last_name TEXT, "
                + "rating INTEGER);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(DB_VERSION > 6) {
            this.onCreate(db);
        }
    }

    public static void insertGame(SQLiteDatabase db, String event, String site, String date, Integer round, String whiteFirstName,
                           String whiteLastName, String blackFirstName, String blackLastName, String result, Integer whiteElo,
                           Integer blackElo, String moves) {
        int whitePlayerId = getPlayerId(db, whiteFirstName, whiteLastName, whiteElo, true);
        int blackPlayerId = getPlayerId(db, blackFirstName, blackLastName, blackElo, false);
        int eventId = getEventId(db, event, site);

        if(whitePlayerId == 0) {
            ContentValues whitePlayerValues = new ContentValues();
            whitePlayerValues.put("first_name", whiteFirstName);
            whitePlayerValues.put("last_name", whiteLastName);
            whitePlayerValues.put("rating", whiteElo);
            db.insert("WHITE_PLAYERS", null, whitePlayerValues);
            whitePlayerId = getPlayerId(db, whiteFirstName, whiteLastName, whiteElo, true);
        }


        if(blackPlayerId == 0) {
            ContentValues blackPlayerValues = new ContentValues();
            blackPlayerValues.put("first_name", blackFirstName);
            blackPlayerValues.put("last_name", blackLastName);
            blackPlayerValues.put("rating", blackElo);
            db.insert("BLACK_PLAYERS", null, blackPlayerValues);
            blackPlayerId = getPlayerId(db, blackFirstName, blackLastName, blackElo, false);
        }

        if(eventId == 0) {
            ContentValues eventValues = new ContentValues();
            eventValues.put("name", event);
            eventValues.put("site", site);
            db.insert("EVENTS", null, eventValues);
            eventId = getEventId(db, event, site);
        }

        ContentValues gameValues = new ContentValues();
        gameValues.put("date", date);
        gameValues.put("round", round);
        gameValues.put("result", result);
        gameValues.put("moves", moves);
        gameValues.put("white_player_id", whitePlayerId);
        gameValues.put("black_player_id", blackPlayerId);
        gameValues.put("event_id", eventId);
        db.insert("GAMES", null, gameValues);
    }

    private static int getPlayerId(SQLiteDatabase db, String firstName, String lastName, Integer elo, boolean color) {
        String tableName = color ? "WHITE_PLAYERS" : "BLACK_PLAYERS";
        Cursor playerCursor = db.query(tableName,
                new String[] {
                        "_id",
                        "first_name",
                        "last_name",
                        "rating"
                },
                "first_name = ? AND last_name = ? AND rating = ? ",
                new String[] { firstName, lastName, Integer.toString(elo) },
                null, null, null
        );

        if(playerCursor.getCount() < 1) {
            playerCursor.close();
            return 0;
        }

        if(playerCursor.moveToFirst()) {
            int playerId;
            playerId = playerCursor.getInt(0);
            playerCursor.close();
            return playerId;
        }

        playerCursor.close();
        return 0;
    }

    private static int getEventId(SQLiteDatabase db, String event, String site) {
        Cursor eventCursor = db.query("EVENTS",
                new String[] {
                        "_id",
                        "name",
                        "site"
                },
                "name = ? AND site = ?",
                new String[] { event, site },
                null, null, null
        );

        if(eventCursor.getCount() < 1) {
            eventCursor.close();
            return 0;
        }

        if(eventCursor.moveToFirst()) {
            int eventId;
            eventId = eventCursor.getInt(0);
            eventCursor.close();
            return eventId;
        }

        eventCursor.close();
        return 0;
    }
}
