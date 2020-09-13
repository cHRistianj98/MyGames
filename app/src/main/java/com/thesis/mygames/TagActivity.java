package com.thesis.mygames;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

public class TagActivity extends AppCompatActivity {

    public static final String TAG = "TagActivity";

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
        event = (EditText) findViewById(R.id.event);
        site = (EditText) findViewById(R.id.site);
        round = (EditText) findViewById(R.id.round);
        whiteLastName = (EditText) findViewById(R.id.white_lastname);
        whiteFirstName = (EditText) findViewById(R.id.white_firstname);
        blackLastName = (EditText) findViewById(R.id.black_lastname);
        blackFirstName = (EditText) findViewById(R.id.black_firstname);
        whiteElo = (EditText) findViewById(R.id.white_elo);
        blackElo = (EditText) findViewById(R.id.black_elo);

        tilEvent = (TextInputLayout) findViewById(R.id.text_input_layout_event);
        tilSite = (TextInputLayout) findViewById(R.id.text_input_layout_site);
        tilRound = (TextInputLayout) findViewById(R.id.text_input_layout_round);
        tilWhiteLastName = (TextInputLayout) findViewById(R.id.text_input_layout_white_lastname);
        tilWhiteFirstName = (TextInputLayout) findViewById(R.id.text_input_layout_white_firstname);
        tilBlackLastName = (TextInputLayout) findViewById(R.id.text_input_layout_black_lastname);
        tilBlackFirstName = (TextInputLayout) findViewById(R.id.text_input_layout_black_firstname);
        tilWhiteElo = (TextInputLayout) findViewById(R.id.text_input_layout_white_elo);
        tilBlackELo = (TextInputLayout) findViewById(R.id.text_input_layout_black_elo);

        date = (TextView) findViewById(R.id.date);
        result = (Spinner) findViewById(R.id.result);
        saveButton = (Button) findViewById(R.id.save_button);
    }

    public void addNewGameToDatabase(View view) {
        Validator validator = new Validator();
        if(validator.validate()) {
            ;
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
}