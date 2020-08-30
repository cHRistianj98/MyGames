package com.thesis.mygames;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Turn {
    public static boolean whiteTurn;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void enableWhitePieces(final Context context) {
        whiteTurn = true;
        for (final Piece p : Chessboard.whitePieces) {
            if(p == null)
                continue;

            MainActivity.b[p.getSquare().getId()].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Square> list = p.action(MainActivity.b);
                    Move move = new Move(context, p);
                    move.addListeners(list);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void enableBlackPieces(final Context context) {
        whiteTurn = false;
        for (final Piece p : Chessboard.blackPieces) {
            if(p == null)
                continue;

            MainActivity.b[p.getSquare().getId()].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Square> list = p.action(MainActivity.b);
                    Move move = new Move(context, p);
                    move.addListeners(list);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void disableWhitePieces() {
        for (Piece p : Chessboard.whitePieces) {
            if(p == null)
                continue;

            ImageButton btn = MainActivity.b[p.getSquare().getId()];
            btn.setOnClickListener(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void disableBlackPieces() {
        for (Piece p : Chessboard.blackPieces) {

            if(p == null)
                continue;

            ImageButton btn = MainActivity.b[p.getSquare().getId()];
            btn.setOnClickListener(null);
        }
    }
}
