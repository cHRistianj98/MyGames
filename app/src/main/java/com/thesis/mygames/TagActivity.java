package com.thesis.mygames;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

public class TagActivity extends AppCompatActivity {
    //public static final String TAG = "TagActivity";
    public static final String MOVES = "moves";

    private SQLiteDatabase db;

    private EditText event, site, round, whiteLastName, whiteFirstName, blackLastName, blackFirstName,
             whiteElo, blackElo;
    private TextInputLayout tilEvent, tilSite, tilRound, tilWhiteLastName, tilWhiteFirstName,
                    tilBlackLastName, tilBlackFirstName, tilWhiteElo, tilBlackELo;
    private TextView date;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Spinner result;
    private Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        initializeWidgets();

        date.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    TagActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListener,
                    year, month, day);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            dateSetListener = (view, year1, month1, dayOfMonth) -> {
                month1 = month1 + 1;
                String date = year1 + "." + month1 + "." + dayOfMonth;
                TagActivity.this.date.setText(date);
            };
        });
    }

    private void initializeWidgets() {
        event = findViewById(R.id.event);
        site = findViewById(R.id.site);
        round = findViewById(R.id.round);
        whiteLastName = findViewById(R.id.white_lastname);
        whiteFirstName = findViewById(R.id.white_firstname);
        blackLastName = findViewById(R.id.black_lastname);
        blackFirstName = findViewById(R.id.black_firstname);
        whiteElo = findViewById(R.id.white_elo);
        blackElo = findViewById(R.id.black_elo);

        tilEvent = findViewById(R.id.text_input_layout_event);
        tilSite = findViewById(R.id.text_input_layout_site);
        tilRound = findViewById(R.id.text_input_layout_round);
        tilWhiteLastName = findViewById(R.id.text_input_layout_white_lastname);
        tilWhiteFirstName = findViewById(R.id.text_input_layout_white_firstname);
        tilBlackLastName = findViewById(R.id.text_input_layout_black_lastname);
        tilBlackFirstName = findViewById(R.id.text_input_layout_black_firstname);
        tilWhiteElo = findViewById(R.id.text_input_layout_white_elo);
        tilBlackELo = findViewById(R.id.text_input_layout_black_elo);

        date = findViewById(R.id.date);
        result = findViewById(R.id.result);
        saveButton = findViewById(R.id.save_button);
    }

    public void addNewGameToDatabase(View view) {
        Validator validator = new Validator();
        if(validator.validate()) {
            new AddingGameAsyncTask().execute();
        }
    }

    private class Validator {
        public boolean validate() {
            return isEventValid() && isSiteValid() && isNameValid(whiteLastName, tilWhiteLastName)
                    && isNameValid(blackLastName, tilBlackLastName)
                    && isFirstNameValid(whiteFirstName, tilWhiteFirstName)
                    && isFirstNameValid(blackFirstName, tilBlackFirstName);
        }

        private boolean isEventValid() {
            Pattern p = Pattern.compile("^[a-zA-Z0-9śćźżółęąŚĆŻŹÓŁĘĄ]*$");
            boolean isValid = p.matcher(event.getText().toString()).find();
            if(!isValid) {
                tilEvent.setError("możliwe są tylko znaki alfanumeryczne!");
                Toast.makeText(TagActivity.this, R.string.event_error, Toast.LENGTH_SHORT).show();
            } else {
                tilEvent.setErrorEnabled(false);
            }

            return isValid;
        }

        private boolean isSiteValid() {
            Pattern p = Pattern.compile("^[a-zA-Z0-9śćźżółęąŚĆŻŹÓŁĘĄ]*$");
            boolean isValid = p.matcher(site.getText().toString()).find();

            if(!isValid) {
                tilSite.setError("możliwe są tylko znaki alfanumeryczne!");
                Toast.makeText(TagActivity.this, R.string.site_error, Toast.LENGTH_SHORT).show();
            } else {
                tilSite.setErrorEnabled(false);
            }

            return isValid;
        }

        private boolean isNameValid(EditText lastName, TextInputLayout inputLayout) {
            if(lastName.getText().toString().isEmpty()) {
                inputLayout.setError("nazwa/login zawodnika nie może być pusta");
                Toast.makeText(TagActivity.this, R.string.empty_lastname_error, Toast.LENGTH_SHORT).show();
                return false;
            }

            Pattern p = Pattern.compile("^[a-zA-Z0-9śćźżółęąŚĆŻŹÓŁĘĄ.]*$");
            boolean isValid = p.matcher(lastName.getText().toString()).find();

            if(!isValid) {
                inputLayout.setError("możliwe są tylko znaki alfanumeryczne!");
                Toast.makeText(TagActivity.this, R.string.lastname_error, Toast.LENGTH_SHORT).show();
            } else {
                inputLayout.setErrorEnabled(false);
            }

            return isValid;
        }

        private boolean isFirstNameValid(EditText firstName, TextInputLayout inputLayout) {
            Pattern p = Pattern.compile("^[a-zA-ZśćźżółęąŚĆŻŹÓŁĘĄ]*$");
            boolean isValid = p.matcher(firstName.getText().toString()).find();

            if(!isValid) {
                inputLayout.setError("możliwe są tylko znaki polskiego alfabetu!");
                Toast.makeText(TagActivity.this, R.string.firstname_error, Toast.LENGTH_SHORT).show();
            } else {
                inputLayout.setErrorEnabled(false);
            }

            return isValid;
        }
    }

    private class AddingGameAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            Intent intent = getIntent();
            SQLiteOpenHelper myGamesDatabaseHelper = new MyGamesDatabaseHelper(TagActivity.this);
            try {
                db = myGamesDatabaseHelper.getReadableDatabase();

                MyGamesDatabaseHelper.insertGame(db,
                        event.getText().toString().isEmpty() ? "?" : event.getText().toString(),
                        site.getText().toString().isEmpty() ? "?" : site.getText().toString(),
                        date.getText().toString().isEmpty() ? "????.??.??" : date.getText().toString(),
                        round.getText().toString().isEmpty() ? 0 : Integer.parseInt(round.getText().toString()),
                        whiteFirstName.getText().toString().isEmpty() ? "?" : whiteFirstName.getText().toString(),
                        whiteLastName.getText().toString().isEmpty() ? "?" : whiteLastName.getText().toString(),
                        blackFirstName.getText().toString().isEmpty() ? "?" : blackFirstName.getText().toString(),
                        blackLastName.getText().toString().isEmpty() ? "?" : blackLastName.getText().toString(),
                        getStringResult(result.getSelectedItem().toString()),
                        whiteElo.getText().toString().isEmpty() ? 0 : Integer.parseInt(whiteElo.getText().toString()),
                        blackElo.getText().toString().isEmpty() ? 0 : Integer.parseInt(blackElo.getText().toString()),
                        intent.getStringExtra(MOVES)
                );
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast toast = Toast.makeText(TagActivity.this, "Baza danych jest obecnie niedostępna",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            Intent intent = new Intent(TagActivity.this, MyGamesActivity.class);
            startActivity(intent);
        }
    }

    private String getStringResult(String selectedItem) {
        String[] resultArray = getApplicationContext().getResources().getStringArray(R.array.results);
        if(selectedItem.equals(resultArray[0]))
            return "1-0";
        else if(selectedItem.equals(resultArray[1]))
            return "1/2-1/2";
        else
            return "0-1";
    }
}