package com.thesis.mygames.activities;

import com.thesis.mygames.androidutils.MyGamesDatabaseHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MyGamesActivity extends ListActivity {
    //public static final String TAG = "MyGamesActivity";

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
        cursor.close();
        db.close();
    }

    @Override
    protected void onListItemClick(ListView listView, View itemView, int position, long id) {
        new ShowGameAsyncTask().execute((int)id);
    }


    private class ShowGameAsyncTask extends AsyncTask<Integer, Void, Boolean> {
        private String moves, event, site, date, whiteFirstName, whiteLastName, blackFirstName,
                        blackLastName, result;
        private int round;

        @Override
        protected Boolean doInBackground(Integer... ids) {
            int id = ids[0];
            SQLiteOpenHelper myGamesDatabaseHelper = new MyGamesDatabaseHelper(MyGamesActivity.this);
            try {
                db = myGamesDatabaseHelper.getReadableDatabase();
                Cursor singleCursor = db.query("GAME",
                        new String[] {
                                "_id",
                                "moves",
                                "event",
                                "site",
                                "date",
                                "round",
                                "white_firstname",
                                "white_lastname",
                                "black_firstname",
                                "black_lastname",
                                "result"
                        },
                        "_id = ?",
                        new String[] { Integer.toString(id) },
                        null, null, null
                        );

                if(singleCursor.moveToFirst()) {
                    moves = singleCursor.getString(1);
                    event = singleCursor.getString(2);
                    site = singleCursor.getString(3);
                    date = singleCursor.getString(4);
                    round = singleCursor.getInt(5);
                    whiteFirstName = singleCursor.getString(6);
                    whiteLastName = singleCursor.getString(7);
                    blackFirstName = singleCursor.getString(8);
                    blackLastName = singleCursor.getString(9);
                    result = singleCursor.getString(10);
                }
                singleCursor.close();

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
                Toast toast = Toast.makeText(MyGamesActivity.this, "Baza danych jest obecnie niedostępna",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
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