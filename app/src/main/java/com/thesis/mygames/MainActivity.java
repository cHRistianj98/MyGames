package com.thesis.mygames;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Move.makeNextMove();
            }
        });

        Button undoButton = findViewById(R.id.undo_move);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Move.makeUndoMove();
            }
        });

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

//    public void loadPositionFromFen(View view) throws Exception {
//        EditText editText = findViewById(R.id.fen);
//        String fen = editText.getText().toString();
//
//        FENFormat.loadPositionFromFen(fen);
//    }
}
