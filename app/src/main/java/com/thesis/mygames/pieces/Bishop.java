package com.thesis.mygames.pieces;

import android.widget.ImageButton;

import com.thesis.mygames.game_utils.Chessboard;
import com.thesis.mygames.game_utils.Piece;
import com.thesis.mygames.game_utils.Square;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(Square square, boolean color, int icon) {
        super(square, color, icon);
    }
    public Bishop(Square square, boolean color, int id, int icon) {
        super(square, color, id, icon);
    }

    @Override
    public List<Integer> getMyPiecesBlocked() {
        List<Integer> toRemove = new ArrayList<>();

        List<Integer> squaresOnBorders = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7,
                15, 23, 31, 39, 47, 55,
                56, 57, 58, 59, 60, 61, 62, 63,
                8, 16, 24, 32, 40, 48);

        int squareId;
        int []diffBetweenSquares = new int[]{-9, -7, 7, 9};

        for(int d : diffBetweenSquares) {
            squareId = this.square.getId();
            do {
                if ((d == -9 || d == 7) && squareId % 8 == 0) break;
                if ((d == -7 || d == 9) && squareId % 8 == 7) break;
                squareId += d;
                if(squareId < 0 || squareId > 63) break;
                if(Chessboard.getSquares(squareId).getPiece() != null && this.getColor() != Chessboard.getSquares(squareId).getPiece().getColor())
                    break;
                if(Chessboard.getSquares(squareId).getPiece() != null && this.getColor() == Chessboard.getSquares(squareId).getPiece().getColor()) {
                    toRemove.add(squareId);
                    break;
                }
                if(squaresOnBorders.contains(squareId)) break;
            } while(true);
        }
        return toRemove;
    }

    @Override
    public List<Square> possibleFieldsToMove(){
        List<Square> possibleSquares = new ArrayList<>();
        List<Integer> squaresOnBorders = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7,
                15, 23, 31, 39, 47, 55,
                56, 57, 58, 59, 60, 61, 62, 63,
                8, 16, 24, 32, 40, 48);
        int squareId;
        int []diffBetweenSquares = new int[]{-9, -7, 7, 9};

        for(int d : diffBetweenSquares) {
            squareId = this.square.getId();
            do {
                if ((d == -9 || d == 7) && squareId % 8 == 0) break;
                if ((d == -7 || d == 9) && squareId % 8 == 7) break;
                squareId += d;
                if (squareId < 0 || squareId > 63) break;
                if (Chessboard.getSquares(squareId).getPiece() != null && this.getColor() != Chessboard.getSquares(squareId).getPiece().getColor()) {
                    possibleSquares.add(Chessboard.getSquares(squareId));
                    break;
                }
                if (Chessboard.getSquares(squareId).getPiece() != null && this.getColor() == Chessboard.getSquares(squareId).getPiece().getColor()) break;
                if (squaresOnBorders.contains(squareId)) {
                    possibleSquares.add(Chessboard.getSquares(squareId));
                    break;
                }
                possibleSquares.add(Chessboard.getSquares(squareId));
            } while (true);
        }
        return possibleSquares;
    }

    @Override
    public List<Square> possibleFieldsToMoveCheck() {
        List<Square> possibleSquares = this.possibleFieldsToMove();
        List<Square> possibleSquareDuringCheck = new ArrayList<>();

        Piece piece = null;
        Square square = this.square;
        boolean wasPiece;
        int pieceId = 0;

        for(Square s : possibleSquares) {
            wasPiece = false;
            Chessboard.getSquares(square.getId()).setPiece(null);
            this.setSquare(s);

            if(Chessboard.getSquares(s.getId()).getPiece() != null) {
                piece = Chessboard.getSquares(s.getId()).getPiece();
                pieceId = Chessboard.getSquares(s.getId()).getPiece().getId();
                wasPiece = true;
                if(this.getColor()) {
                    Chessboard.blackPieces.set(pieceId, null);
                } else {
                    Chessboard.whitePieces.set(pieceId, null);
                }
                Chessboard.getSquares(s.getId()).setPiece(null);
            }
            Chessboard.getSquares(s.getId()).setPiece(this);
            if(this.getColor()) {
                if(!(((King)Chessboard.whitePieces.get(12)).isCheck()))
                    possibleSquareDuringCheck.add(s);
            } else {
                if(!(((King)Chessboard.blackPieces.get(12)).isCheck()))
                    possibleSquareDuringCheck.add(s);
            }

            if(wasPiece) {
                if(this.getColor()) {
                    Chessboard.blackPieces.set(pieceId, piece);
                } else {
                    Chessboard.whitePieces.set(pieceId, piece);
                }
                Chessboard.getSquares(s.getId()).setPiece(piece);
            } else {
                Chessboard.getSquares(s.getId()).setPiece(null);
            }
            this.setSquare(square);
            Chessboard.getSquares(square.getId()).setPiece(this);
        }
        return possibleSquareDuringCheck;
    }

    @Override
    public List<Square> action(ImageButton[]b) {
        List<Square> list = null;

        try {
            list = this.possibleFieldsToMoveCheck();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}