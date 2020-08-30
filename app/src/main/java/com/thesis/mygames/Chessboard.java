package com.thesis.mygames;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.graphics.Color.argb;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Chessboard {
    private Context context;
    private Activity activity;
    private static final Square[] squares = new Square[64];
    // public static final Button[] b = new Button[64];

    public static List<Piece> whitePieces = new ArrayList<>();
    public static List<Piece> blackPieces = new ArrayList<>();
    public static List<String> moveList = new ArrayList<>();
    public static Square enPassantPossible = null;
    public static int numberOfHalfMoves = 0;
    public static int fullMoveNumber = 0;
    //public static GameState gameState = GameState.NORMAL;
    public static StringBuilder PGNTagGenerator = new StringBuilder();
    public static StringBuilder PGNMoveGenerator = new StringBuilder();


    public Chessboard(Context mContext) {
        this.context = mContext;
        activity = (mContext instanceof Activity) ? (Activity) mContext : null;
    }

    public static void init() {
        for (int i = 0; i < 64; i++) {
            squares[i] = new Square(i, null);
            squares[i].setName(getSquareName(i));
        }
    }


    public static int getSquareColor(int i) {
        boolean result = true;
        if (i < 8) result = i % 2 == 0;
        else if (i < 16) result = i % 2 != 0;
        else if (i < 24) result = i % 2 == 0;
        else if (i < 32) result = i % 2 != 0;
        else if (i < 40) result = i % 2 == 0;
        else if (i < 48) result = i % 2 != 0;
        else if (i < 56) result = i % 2 == 0;
        else if (i < 64) result = i % 2 != 0;

        return result ? R.color.black : R.color.white;
    }

    public static String getSquareName(int i) {
        String[] firstLetters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        if (i < 0 || i > 63)
            throw new IllegalArgumentException();

        return firstLetters[i % 8] + (8 - (i / 8));
    }

    public static String getSquareLetter(int i) {
        String[] firstLetters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

        if (i < 0 || i > 63)
            throw new IllegalArgumentException();

        return firstLetters[i % 8];
    }

    public static String getSquareNumber(int i) {
        if (i < 0 || i > 63)
            throw new IllegalArgumentException();

        return "" + (8 - (i / 8));
    }

    public static Square getSquares(int id) {
        return squares[id];
    }

    public static int getSquareId(String squareName) {
        int asciiRepresentationOfFirstSign = squareName.charAt(0);
        int numericValueOfSecondSign = Character.getNumericValue(squareName.charAt(1));

        // Explanation: 'a' in Ascii table equals 97 and letters represent horizontal of chessboard
        return asciiRepresentationOfFirstSign - 97 + 64 - numericValueOfSecondSign * 8;
    }



}
