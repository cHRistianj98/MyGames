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
    private static Context context;
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

    public static int[] whiteIcons = {
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_pawn,
            R.drawable.white_rook,
            R.drawable.white_knight,
            R.drawable.white_bishop,
            R.drawable.white_queen,
            R.drawable.white_king,
            R.drawable.white_bishop,
            R.drawable.white_knight,
            R.drawable.white_rook
    };

    public static int[] blackIcons = {
            R.drawable.black_rook,
            R.drawable.black_knight,
            R.drawable.black_bishop,
            R.drawable.black_queen,
            R.drawable.black_king,
            R.drawable.black_bishop,
            R.drawable.black_knight,
            R.drawable.black_rook,
            R.drawable.black_pawn,
            R.drawable.black_pawn,
            R.drawable.black_pawn,
            R.drawable.black_pawn,
            R.drawable.black_pawn,
            R.drawable.black_pawn,
            R.drawable.black_pawn,
            R.drawable.black_pawn
    };


    public Chessboard(Context mContext) {
        this.context = mContext;
        activity = (mContext instanceof Activity) ? (Activity) mContext : null;
    }

    public Context getContext() {
        return context;
    }

    public static void init() {
        for (int i = 0; i < 64; i++) {
            squares[i] = new Square(i, null);
            squares[i].setName(getSquareName(i));
        }

        ///////////////////WHITE PIECES/////////////////////

        for (int i = 48; i <= 55; i++)
            whitePieces.add(new Pawn(Chessboard.getSquares(i), true, i - 48, whiteIcons[i - 48]));

        whitePieces.add(new Rook(Chessboard.getSquares(56), true, 8, whiteIcons[8]));
        whitePieces.add(new Knight(Chessboard.getSquares(57), true, 9, whiteIcons[9]));
        whitePieces.add(new Bishop(Chessboard.getSquares(58), true, 10, whiteIcons[10]));
        whitePieces.add(new Queen(Chessboard.getSquares(59), true, 11, whiteIcons[11]));
        whitePieces.add(new King(Chessboard.getSquares(60), true, 12, whiteIcons[12]));
        whitePieces.add(new Bishop(Chessboard.getSquares(61), true, 13, whiteIcons[13]));
        whitePieces.add(new Knight(Chessboard.getSquares(62), true, 14, whiteIcons[14]));
        whitePieces.add(new Rook(Chessboard.getSquares(63), true, 15, whiteIcons[15]));

        for (int i = 0; i < 8; i++)
            addPiece(whitePieces.get(i), i + 48, whiteIcons[i]);

        addPiece(whitePieces.get(8), 56, whiteIcons[8]);
        addPiece(whitePieces.get(9), 57, whiteIcons[9]);
        addPiece(whitePieces.get(10), 58, whiteIcons[10]);
        addPiece(whitePieces.get(11), 59, whiteIcons[11]);
        addPiece(whitePieces.get(12), 60, whiteIcons[12]);
        addPiece(whitePieces.get(13), 61, whiteIcons[13]);
        addPiece(whitePieces.get(14), 62, whiteIcons[14]);
        addPiece(whitePieces.get(15), 63, whiteIcons[15]);

        ///////////////////BLACK PIECES/////////////////////

        for (int i = 8; i <= 15; i++)
            blackPieces.add(new Pawn(Chessboard.getSquares(i), false, i - 8, blackIcons[i]));

        blackPieces.add(new Rook(Chessboard.getSquares(0), false, 8, blackIcons[0]));
        blackPieces.add(new Knight(Chessboard.getSquares(1), false, 9, blackIcons[1]));
        blackPieces.add(new Bishop(Chessboard.getSquares(2), false, 10, blackIcons[2]));
        blackPieces.add(new Queen(Chessboard.getSquares(3), false, 11, blackIcons[3]));
        blackPieces.add(new King(Chessboard.getSquares(4), false, 12, blackIcons[4]));
        blackPieces.add(new Bishop(Chessboard.getSquares(5), false, 13, blackIcons[5]));
        blackPieces.add(new Knight(Chessboard.getSquares(6), false, 14, blackIcons[6]));
        blackPieces.add(new Rook(Chessboard.getSquares(7), false, 15, blackIcons[7]));

        for (int i = 0; i < 8; i++)
            addPiece(blackPieces.get(i), i + 8, blackIcons[i + 8]);

        addPiece(blackPieces.get(8), 0, blackIcons[0]);
        addPiece(blackPieces.get(9), 1, blackIcons[1]);
        addPiece(blackPieces.get(10), 2, blackIcons[2]);
        addPiece(blackPieces.get(11), 3, blackIcons[3]);
        addPiece(blackPieces.get(12), 4, blackIcons[4]);
        addPiece(blackPieces.get(13), 5, blackIcons[5]);
        addPiece(blackPieces.get(14), 6, blackIcons[6]);
        addPiece(blackPieces.get(15), 7, blackIcons[7]);

        Turn.enableWhitePieces(context);
//        Turn.disableBlackPieces();
    }

    public static void addPiece(Piece piece, int id, int icon) {
        MainActivity.b[id].setImageResource(icon);
        Chessboard.getSquares(id).setPiece(piece);
//        b[id].addActionListener(
//                e -> {
//                    List<Square> list = piece.action(Chessboard.b);
//                    Move move = new Move(piece);
//                    move.addListeners(list);
//                }
//        );
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

        return result ? R.drawable.black_square : R.drawable.white_square;
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
