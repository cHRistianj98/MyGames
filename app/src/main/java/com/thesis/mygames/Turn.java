package com.thesis.mygames;

import android.view.View;
import android.widget.ImageButton;



import java.util.List;

public class Turn {
    public static boolean whiteTurn;

    public static void enableWhitePieces() {
        whiteTurn = true;
        for (final Piece p : Chessboard.whitePieces) {
            if(p == null)
                continue;

            Chessboard.b[p.getSquare().getId()].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Square> list = p.action(Chessboard.b);
                    Move move = new Move(p);
                    move.addListeners(list);
                }
            });
        }
    }

    public static void enableBlackPieces() {
        whiteTurn = false;
        for (final Piece p : Chessboard.blackPieces) {
            if(p == null)
                continue;

            Chessboard.b[p.getSquare().getId()].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Square> list = p.action(Chessboard.b);
                    Move move = new Move(p);
                    move.addListeners(list);
                }
            });
        }
    }

    public static void disableWhitePieces() {
        for (Piece p : Chessboard.whitePieces) {
            if(p == null)
                continue;

            ImageButton btn = Chessboard.b[p.getSquare().getId()];
            btn.setOnClickListener(null);
        }
    }

    public static void disableBlackPieces() {
        for (Piece p : Chessboard.blackPieces) {

            if(p == null)
                continue;

            ImageButton btn = Chessboard.b[p.getSquare().getId()];
            btn.setOnClickListener(null);
        }
    }
}
