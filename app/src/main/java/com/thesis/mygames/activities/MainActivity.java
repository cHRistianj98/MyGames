package com.thesis.mygames.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thesis.mygames.formats.FENFormat;
import com.thesis.mygames.game.Move;
import com.thesis.mygames.formats.PGNFormat;
import com.thesis.mygames.game.Piece;
import com.thesis.mygames.android.PromotionDialog;
import com.thesis.mygames.R;

import java.util.ArrayList;
import java.util.List;

import static com.thesis.mygames.game.Chessboard.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements PromotionDialog.SingleChoiceListener {
    public static final String EXTRA_GAME_ID = "id";
    public static final String EXTRA_MOVES = "moves";

    public static String EVENT = null;
    public static String SITE = null;
    public static int ROUND = 0;
    public static String DATE = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout layout = findViewById(R.id.buttonContainerGridLayout);
        layout.setColumnCount(8);
        layout.setRowCount(8);
        resetChessboard();
        for (int i = 0; i < 64 ; i++) {
            b[i] = new ImageButton(this);
            b[i].setId(i);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params.width = (height - 290) / 8;
                params.height = (height - 290) / 8;
            } else {
                params.width = (width - 2)  / 8;
                params.height = (width - 2) / 8;
            }
            b[i].setLayoutParams(params);
            b[i].setBackground( getResources().getDrawable( getSquareColor(i)));
            layout.addView(b[i]);
        }
        init();
        Move.activity = this;

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        TextView moves = findViewById(R.id.moves);
        moves.setMovementMethod(new ScrollingMovementMethod());

        String movesFromDatabase = getIntent().getStringExtra(EXTRA_MOVES);
        int idFromDatabase = getIntent().getIntExtra(EXTRA_GAME_ID, 0);

        if (movesFromDatabase != null && idFromDatabase != 0 && savedInstanceState == null) {
            movesFromDatabase = movesFromDatabase.trim();
            String []movesArray = movesFromDatabase.split(" ");

            for (int i = 0; i < movesArray.length; i++) {
                if(i%3 == 0)
                    movesArray[i] = null;
            }

            List<String> moveList = new ArrayList<>();

            for (String move : movesArray) {
                if (move != null)
                    moveList.add(move);
            }

            List<Piece> pieces;
            for (int i = 0; i < moveList.size(); i++) {
                pieces = i % 2 == 0 ? whitePieces : blackPieces;
                try {
                    Move.makeQuickMove(pieces.get(PGNFormat.getMovedPiece(moveList.get(i), i)), PGNFormat.getEndSquareName(moveList.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (savedInstanceState != null) {
            String madeMoves = savedInstanceState.getString("moves");
            madeMoves = madeMoves.trim();
            String []movesArray = madeMoves.split(" ");

            for (int i = 0; i < movesArray.length; i++) {
                if(i%3 == 0)
                    movesArray[i] = null;
            }

            List<String> moveList = new ArrayList<>();

            for (String move : movesArray) {
                if (move != null)
                    moveList.add(move);
            }

            List<Piece> pieces;
            for (int i = 0; i < moveList.size(); i++) {
                pieces = i % 2 == 0 ? whitePieces : blackPieces;
                int id = 0;
                try {
                    id = PGNFormat.getMovedPiece(moveList.get(i), i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Move.makeQuickMove(pieces.get(id), PGNFormat.getEndSquareName(moveList.get(i)));
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.next_menu:
                        Move.nextMove();
                        return true;
                    case R.id.undo_menu:
                        Move.undoMove();
                        return true;
                    case R.id.format_menu:
                        showFormatOptions(null);
                        return true;
                    case R.id.save_menu:
                        Intent intent = new Intent(this, TagActivity.class);
                        intent.putExtra(TagActivity.MOVES, PGNMoveGenerator.toString());
                        intent.putExtra("event", EVENT);
                        intent.putExtra("site", SITE);
                        intent.putExtra("round", ROUND);
                        intent.putExtra("date", DATE);
                        startActivity(intent);
                        return true;
                }
                return false;
            };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("moves", PGNMoveGenerator.toString());
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        Move move = moveList.get(moveList.size() - 1);
        move.setSelectedPiece(list[position]);
        move.setNewPieceAfterPawnPromotion();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showFormatOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] labels = {"Udostępnij PGN", "Wczytaj PGN", "Udostępnij FEN", "Wczytaj FEN"};
        builder.setItems(labels, (dialog, position) -> {
            switch (position) {
                case 0: sharePgn(); break;
                case 1: loadGameFromPgn(); break;
                case 2: shareFen(); break;
                case 3: loadPositionFromFen(); break;
            }
        });

        AlertDialog formatAlertDialog = builder.create();
        formatAlertDialog.show();
    }

    public void loadPositionFromFen() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fen_dialog, null);

        final EditText fen = dialogView.findViewById(R.id.fen);
        fen.setClickable(true);
        Button buttonSubmit = dialogView.findViewById(R.id.button_submit);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        buttonCancel.setOnClickListener(view12 -> dialogBuilder.dismiss());
        buttonSubmit.setOnClickListener(view1 -> {
            try {
                FENFormat.loadPositionFromFen(fen.getText().toString());
                dialogBuilder.dismiss();
            } catch (IllegalArgumentException e) {
                fen.setError("Nieprawidłowy format kodu FEN");
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    public void shareFen() {
        String fen = FENFormat.generateFenFromPosition();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, fen);
        startActivity(intent);
    }

    public void sharePgn() {
        PGNFormat.generatePgnTags(
                getIntent().getStringExtra("event"),
                getIntent().getStringExtra("site"),
                getIntent().getStringExtra("date"),
                getIntent().getIntExtra("round", 0),
                getIntent().getStringExtra("white_lastname"),
                getIntent().getStringExtra("white_firstname"),
                getIntent().getStringExtra("black_lastname"),
                getIntent().getStringExtra("black_firstname"),
                getIntent().getStringExtra("result")
        );

        String pgn = PGNTagGenerator.toString() + "\n" +
                PGNMoveGenerator.toString() +
                getIntent().getStringExtra("result") + "\n";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, pgn);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadGameFromPgn() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pgn_dialog, null);

        final EditText pgn = dialogView.findViewById(R.id.pgn);
        Button buttonSubmit = dialogView.findViewById(R.id.pgn_button_submit);
        Button buttonCancel = dialogView.findViewById(R.id.pgn_button_cancel);

        buttonCancel.setOnClickListener(view12 -> dialogBuilder.dismiss());
        buttonSubmit.setOnClickListener(view1 -> {
            try {
                PGNFormat.loadGameFromPgn(pgn.getText().toString());
                dialogBuilder.dismiss();
            } catch (IllegalArgumentException e) {
                pgn.setError("Nieprawidłowy format kodu PGN");
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
