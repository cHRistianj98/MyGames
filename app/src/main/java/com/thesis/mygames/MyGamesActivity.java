package com.thesis.mygames;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MyGamesActivity extends ListActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    ListView listGames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_games);
        listGames = getListView();
        new GamesAsyncTask().execute();
    }

    //            Cursor cursor = db.query("GAME",
//                    new String[] {"event", "site", "date", "round", "white_firstname",
//                    "white_lastname", "black_firstname", "black_lastname", "result",
//                    "white_elo", "black_elo", "moves"},
//                    "_id = ?",
//                    new String[] {"1"},
//                    null, null, null);


//            if(cursor.moveToFirst()) {
//                String date = cursor.getString(2);
//                String whiteFirstName = cursor.getString(4);
//                String whiteLastName = cursor.getString(5);
//                String blackFirstName = cursor.getString(6);
//                String blackLastName = cursor.getString(7);
//                String result = cursor.getString(8);
//                Integer whiteElo = cursor.getInt(9);
//                Integer blackElo = cursor.getInt(10);
//                String moves = cursor.getString(11);
//                String[] tab = {whiteLastName + " " + result + " " + blackLastName, "0"};
//
//            }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    private class GamesAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            SQLiteOpenHelper myGamesDatabaseHelper = new MyGamesDatabaseHelper(MyGamesActivity.this);
            try {
                db = myGamesDatabaseHelper.getReadableDatabase();
                String concatenatedColumns = "white_lastname ||'  '|| result ||'  '|| black_lastname";

                cursor = db.query("GAME", new String[]{"_id", concatenatedColumns},
                        null, null, null, null, null);

                CursorAdapter listAdapter = new SimpleCursorAdapter(MyGamesActivity.this,
                        android.R.layout.simple_list_item_activated_1,
                        cursor,
                        new String[] { concatenatedColumns },
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