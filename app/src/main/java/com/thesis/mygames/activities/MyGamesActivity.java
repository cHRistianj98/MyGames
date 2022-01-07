package com.thesis.mygames.activities;

import com.thesis.mygames.android.MyGamesDatabaseHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class MyGamesActivity extends ListActivity {
    public static final String TAG = "MyGamesActivity";
    private SQLiteDatabase db;
    private Cursor cursor;
    ListView listGames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listGames = getListView();
        new GamesAsyncTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cursor != null) cursor.close();
        if(db != null) db.close();
    }

    @Override
    protected void onListItemClick(ListView listView, View itemView, int position, long id) {
        new ShowGameAsyncTask().execute((int)id);
    }

    //po kliknięciu do wczytania partii
    private class ShowGameAsyncTask extends AsyncTask<Integer, Void, Boolean> {
        private String moves, event, site, date, whiteFirstName, whiteLastName, blackFirstName,
                        blackLastName, result;
        private int round;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Integer... ids) {
            int id = ids[0];
            SQLiteOpenHelper myGamesDatabaseHelper = new MyGamesDatabaseHelper(MyGamesActivity.this);
            try {
                db = myGamesDatabaseHelper.getReadableDatabase();

                Cursor gameCursor = db.query("GAMES",
                        new String[] {
                                "_id",
                                "date",
                                "round",
                                "result",
                                "moves",
                                "white_player_id",
                                "black_player_id",
                                "event_id",
                        },
                        "_id = ?",
                        new String[] { Integer.toString(id) },
                        null, null, null
                );

                int whitePlayerId = 0, blackPlayerId = 0, eventId = 0;
                if(gameCursor.moveToFirst()) {
                    date = gameCursor.getString(1);
                    round = gameCursor.getInt(2);
                    result = gameCursor.getString(3);
                    moves = gameCursor.getString(4);
                    whitePlayerId = gameCursor.getInt(5);
                    blackPlayerId = gameCursor.getInt(6);
                    eventId = gameCursor.getInt(7);
                }
                gameCursor.close();

                Cursor whiteCursor = db.query("WHITE_PLAYERS",
                        new String[] {
                                "_id",
                                "first_name",
                                "last_name",
                        },
                        "_id = ?",
                        new String[] { Integer.toString(whitePlayerId) },
                        null, null, null
                );

                if(whiteCursor.moveToFirst()) {
                    whiteFirstName = whiteCursor.getString(1);
                    whiteLastName = whiteCursor.getString(2);
                }
                whiteCursor.close();

                Cursor blackCursor = db.query("BLACK_PLAYERS",
                        new String[] {
                                "_id",
                                "first_name",
                                "last_name",
                        },
                        "_id = ?",
                        new String[] { Integer.toString(blackPlayerId) },
                        null, null, null
                );

                if(blackCursor.moveToFirst()) {
                    blackFirstName = blackCursor.getString(1);
                    blackLastName = blackCursor.getString(2);
                }
                blackCursor.close();

                Cursor eventCursor = db.query("EVENTS",
                        new String[] {
                                "_id",
                                "name",
                                "site"
                        },
                        "_id = ?",
                        new String[] { Integer.toString(eventId) },
                        null, null, null
                );

                if(eventCursor.moveToFirst()) {
                    event = eventCursor.getString(1);
                    site = eventCursor.getString(2);
                }
                eventCursor.close();

                Intent intent = new Intent(MyGamesActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GAME_ID, id);
                intent.putExtra(MainActivity.EXTRA_MOVES, moves);
                intent.putExtra("event", event);
                intent.putExtra("site", site);
                intent.putExtra("date", date);
                intent.putExtra("round", round);
                intent.putExtra("white_firstname", whiteFirstName);
                intent.putExtra("white_lastname", whiteLastName);
                intent.putExtra("black_firstname", blackFirstName);
                intent.putExtra("black_lastname", blackLastName);
                intent.putExtra("result", result);

                startActivity(intent);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                String errorMessage = "Baza danych jest obecnie niedostępna";
                Toast toast = Toast.makeText(MyGamesActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //do pokazania listy partii
    private class GamesAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            SQLiteOpenHelper myGamesDatabaseHelper = new MyGamesDatabaseHelper(MyGamesActivity.this);
            try {
                db = myGamesDatabaseHelper.getReadableDatabase();
                String columns = "list_view";

                cursor = db.rawQuery(
                     "SELECT * FROM my_games_view",
                     null
                );

                CursorAdapter listAdapter = new SimpleCursorAdapter(MyGamesActivity.this,
                        android.R.layout.simple_list_item_activated_1,
                        cursor,
                        new String[] { columns },
                        new int[] { android.R.id.text1 },
                        0);

                listGames.setAdapter(listAdapter);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast toast = Toast.makeText(MyGamesActivity.this, "Baza danych jest obecnie niedostępna",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}