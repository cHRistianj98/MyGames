package com.thesis.mygames;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class TagActivity extends AppCompatActivity {

    EditText event, site, date, round, whiteLastName, whiteFirstName, blackLastName, blackFirstName,
             whiteElo, blackElo;
    TextInputLayout tilEvent, tilSite, tilDate, tilRound, tilWhiteLastName, tilWhiteFirstName,
                    tilBlackLastName, tilBlackFirstName, tilWhiteElo, tilBlackELo;
    Spinner result;
    Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        initializeWidgets();
    }

    private void initializeWidgets() {
        event = (EditText) findViewById(R.id.event);
        site = (EditText) findViewById(R.id.site);
        date = (EditText) findViewById(R.id.date);
        round = (EditText) findViewById(R.id.round);
        whiteLastName = (EditText) findViewById(R.id.white_lastname);
        whiteFirstName = (EditText) findViewById(R.id.white_firstname);
        blackLastName = (EditText) findViewById(R.id.black_lastname);
        blackFirstName = (EditText) findViewById(R.id.black_firstname);
        whiteElo = (EditText) findViewById(R.id.white_elo);
        blackElo = (EditText) findViewById(R.id.black_elo);

        tilEvent = (TextInputLayout) findViewById(R.id.text_input_layout_event);
        tilSite = (TextInputLayout) findViewById(R.id.text_input_layout_site);
        tilDate = (TextInputLayout) findViewById(R.id.text_input_layout_date);
        tilRound = (TextInputLayout) findViewById(R.id.text_input_layout_round);
        tilWhiteLastName = (TextInputLayout) findViewById(R.id.text_input_layout_white_lastname);
        tilWhiteFirstName = (TextInputLayout) findViewById(R.id.text_input_layout_white_firstname);
        tilBlackLastName = (TextInputLayout) findViewById(R.id.text_input_layout_black_lastname);
        tilBlackFirstName = (TextInputLayout) findViewById(R.id.text_input_layout_black_firstname);
        tilWhiteElo = (TextInputLayout) findViewById(R.id.text_input_layout_white_elo);
        tilBlackELo = (TextInputLayout) findViewById(R.id.text_input_layout_black_elo);

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