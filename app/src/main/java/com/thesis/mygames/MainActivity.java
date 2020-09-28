package com.thesis.mygames;

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

import java.util.ArrayList;
import java.util.List;

import static com.thesis.mygames.Chessboard.*;

public class MainActivity extends AppCompatActivity implements PromotionDialog.SingleChoiceListener{
    public static final String EXTRA_GAME_ID = "id";
    public static final String EXTRA_MOVES = "moves";

    public static String EVENT = null;
    public static String SITE = null;
    public static int ROUND = 0;
    public static String WHITE = null;
    public static String BLACK = null;
    public static String DATE = null;
    public static String RESULT = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout layout = (GridLayout) findViewById(R.id.buttonContainerGridLayout);

        layout.setColumnCount(8);
        layout.setRowCount(8);

        resetChessboard();
        for (int i = 0; i < 64 ; i++) {
            b[i] = new ImageButton(this);
            b[i].setId(i);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params.width = (height - 290) / 8;
                params.height = (height - 290) / 8;
            } else {
                params.width = (width - 40) / 8;
                params.height = (width - 40) / 8;
            }

            b[i].setLayoutParams(params);
            b[i].setBackground( getResources().getDrawable( getSquareColor(i)));
            layout.addView(b[i]);
        }

        Button nextButton = (Button) findViewById(R.id.next_move);
        nextButton.setOnClickListener(v -> Move.makeNextMove());

        Button undoButton = (Button) findViewById(R.id.undo_move);
        undoButton.setOnClickListener(v -> Move.makeUndoMove());

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(v -> {
            Intent intent = new Intent(this, TagActivity.class);
            intent.putExtra(TagActivity.MOVES, PGNMoveGenerator.toString());
            startActivity(intent);
        });

        Button fen = (Button) findViewById(R.id.fen_button);
        fen.setOnClickListener(v -> loadPositionFromFen());

        TextView moves = (TextView) findViewById(R.id.moves);
        moves.setMovementMethod(new ScrollingMovementMethod());

        init();
        Move.activity = this;

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

            for (int i = 0; i < movesArray.length; i++) {
                if(movesArray[i] != null)
                    moveList.add(movesArray[i]);
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

            for (int i = 0; i < movesArray.length; i++) {
                if(movesArray[i] != null)
                    moveList.add(movesArray[i]);
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

    public void loadPositionFromFen() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fen_dialog, null);

        final EditText fen = (EditText) dialogView.findViewById(R.id.fen);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.button_submit);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.button_cancel);

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

    public void shareFen(View view) {
        String fen = FENFormat.generateFENFromPosition();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, fen);
        startActivity(intent);
    }

    public void sharePgn(View view) {
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
    public void loadGameFromPgn(View view) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pgn_dialog, null);

        final EditText pgn = (EditText) dialogView.findViewById(R.id.pgn);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.pgn_button_submit);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.pgn_button_cancel);

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
