package com.thesis.mygames;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.thesis.mygames.Chessboard.*;

public class MainActivity extends AppCompatActivity implements PromotionDialog.SingleChoiceListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout layout = (GridLayout) findViewById(R.id.buttonContainerGridLayout);
        layout.setColumnCount(8);
        layout.setRowCount(8);

        for (int i = 0; i < 64 ; i++) {
            b[i] = new ImageButton(this);
            b[i].setId(i);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 125;
            params.height = 125;
            b[i].setLayoutParams(params);
            b[i].setBackground( getResources().getDrawable( getSquareColor(i)));
            layout.addView(b[i]);
        }

        Button nextButton = findViewById(R.id.next_move);
        nextButton.setOnClickListener(v -> Move.makeNextMove());

        Button undoButton = findViewById(R.id.undo_move);
        undoButton.setOnClickListener(v -> Move.makeUndoMove());

        Button save = findViewById(R.id.save);
        save.setOnClickListener(v -> {
            Intent intent = new Intent(this, TagActivity.class);
            intent.putExtra(TagActivity.MOVES, PGNMoveGenerator.toString());
            startActivity(intent);
        });

        Button fen = findViewById(R.id.fen_button);
        fen.setOnClickListener(v -> loadPositionFromFen());

        TextView moves = (TextView) findViewById(R.id.moves);
        moves.setMovementMethod(new ScrollingMovementMethod());

        init();
        Move.activity = this;
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
            } catch (Exception e) {
                fen.setError("Nieprawidłowy format kodu FEN");
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
