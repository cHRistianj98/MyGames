package com.thesis.mygames;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

        init();
        Move.activity = this;
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        Move.selectedPiece = list[position];

        Move move = moveList.get(moveList.size() - 1);
        move.setNewPieceAfterPawnPromotion();

        TextView textView = findViewById(R.id.promo);
        textView.setText(Move.selectedPiece);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

}
