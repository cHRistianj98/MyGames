package com.thesis.mygames.pieces;

import android.widget.ImageButton;

import com.thesis.mygames.gameutils.Chessboard;
import com.thesis.mygames.gameutils.Piece;
import com.thesis.mygames.gameutils.Square;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Square square, boolean color, int icon) {
        super(square, color, icon);
    }

    public Pawn(Square square, boolean color, int id, int icon) {
        super(square, color, id, icon);
    }

    @Override
    public List<Integer> getMyPiecesBlocked() {
        List<Integer> toRemove = new ArrayList<>();

        //////////////////////////////WHITE///////////////////////////////////////////
        if( this.getColor() ) {
            Square leftPieceToTake = (square.getId() - 9 < 0) ? null : Chessboard.getSquares(square.getId() - 9);
            Square rightPieceToTake = (square.getId() - 7 < 0) ? null : Chessboard.getSquares(square.getId() - 7);
            if (rightPieceToTake != null && rightPieceToTake.getPiece() != null && rightPieceToTake.getPiece().getColor()
                    && !Arrays.asList(15, 23, 31, 39, 47, 55).contains(square.getId()))
                toRemove.add(rightPieceToTake.getId());
            if (leftPieceToTake != null && leftPieceToTake.getPiece() != null && leftPieceToTake.getPiece().getColor()
                    && !Arrays.asList(8, 16, 24, 32, 40, 48).contains(square.getId()))
                toRemove.add(leftPieceToTake.getId());
        }

        //////////////////////////////BLACK///////////////////////////////////////////
        else {
            Square leftPieceToTakeBlack = square.getId() + 9 > 63 ? null : Chessboard.getSquares(square.getId() + 9);
            Square rightPieceToTakeBlack = square.getId() + 7 > 63 ? null : Chessboard.getSquares(square.getId() + 7);

            if (rightPieceToTakeBlack != null && rightPieceToTakeBlack.getPiece() != null && !rightPieceToTakeBlack.getPiece().getColor()
                    && !Arrays.asList(8, 16, 24, 32, 40, 48).contains(square.getId()))
                toRemove.add(rightPieceToTakeBlack.getId());
            if (leftPieceToTakeBlack != null && leftPieceToTakeBlack.getPiece() != null && !leftPieceToTakeBlack.getPiece().getColor() &&
                    !Arrays.asList(15, 23, 31, 39, 47, 55).contains(square.getId()))
                toRemove.add(leftPieceToTakeBlack.getId());
        }
        return toRemove;
    }

    public List<Square> possibleTakingToMove() {
        List<Square> possibleTaking = new ArrayList<>();

        if(this.getColor()) {
            Square leftPieceToTake = (square.getId() - 9 < 0) ? null : Chessboard.getSquares(square.getId() - 9);
            Square rightPieceToTake = (square.getId() - 7 < 0) ? null : Chessboard.getSquares(square.getId() - 7);
            if (leftPieceToTake != null && !Arrays.asList(8, 16, 24, 32, 40, 48).contains(square.getId()))
                possibleTaking.add(leftPieceToTake);
            if (rightPieceToTake != null && !Arrays.asList(15, 23, 31, 39, 47, 55).contains(square.getId()))
                possibleTaking.add(rightPieceToTake);
        } else {
            Square leftPieceToTakeBlack = square.getId() + 9 > 63 ? null : Chessboard.getSquares(square.getId() + 9);
            Square rightPieceToTakeBlack = square.getId() + 7 > 63 ? null : Chessboard.getSquares(square.getId() + 7);
            if (leftPieceToTakeBlack != null && !Arrays.asList(15, 23, 31, 39, 47, 55).contains(square.getId()))
                possibleTaking.add(leftPieceToTakeBlack);
            if (rightPieceToTakeBlack != null && !Arrays.asList(8, 16, 24, 32, 40, 48).contains(square.getId()))
                possibleTaking.add(rightPieceToTakeBlack);
        }

        return possibleTaking;
    }

    @Override
    public List<Square> possibleFieldsToMove() {

        List<Square> possibleSquares = new ArrayList<>();

        //////////////////////////////WHITE///////////////////////////////////////////
        if( this.getColor() ) {
            Square oneUpSquare = (square.getId() - 8 < 0) ? null : Chessboard.getSquares(square.getId() - 8);
            Square twoUpSquare = (square.getId() - 16 < 0) ? null : Chessboard.getSquares(square.getId() - 16);
            Square leftPieceToTake = (square.getId() - 9 < 0) ? null : Chessboard.getSquares(square.getId() - 9);
            Square rightPieceToTake = (square.getId() - 7 < 0) ? null : Chessboard.getSquares(square.getId() - 7);

            if (oneUpSquare != null && twoUpSquare != null && oneUpSquare.getPiece() == null && twoUpSquare.getPiece() == null && square.getId() >= 48 && square.getId() <= 55)
                possibleSquares.add(twoUpSquare);
            if (oneUpSquare != null && oneUpSquare.getPiece() == null)
                possibleSquares.add(oneUpSquare);
            if (rightPieceToTake != null && rightPieceToTake.getPiece() != null && !rightPieceToTake.getPiece().getColor()
                    && !Arrays.asList(15, 23, 31, 39, 47, 55).contains(square.getId()))
                possibleSquares.add(rightPieceToTake);
            else if(rightPieceToTake != null && rightPieceToTake.equals(Chessboard.enPassantPossible)) {
                possibleSquares.add(rightPieceToTake);
            }
            if (leftPieceToTake != null && leftPieceToTake.getPiece() != null && !leftPieceToTake.getPiece().getColor()
                    && !Arrays.asList(8, 16, 24, 32, 40, 48).contains(square.getId()))
                possibleSquares.add(leftPieceToTake);
            else if(leftPieceToTake != null && leftPieceToTake.equals(Chessboard.enPassantPossible)) {
                possibleSquares.add(leftPieceToTake);
            }
        }
        //////////////////////////////BLACK///////////////////////////////////////////
        else {
            Square oneUpSquareBlack = square.getId() + 8 > 63 ? null : Chessboard.getSquares(square.getId() + 8);
            Square twoUpSquareBlack = square.getId() + 16 > 63 ? null : Chessboard.getSquares(square.getId() + 16);
            Square leftPieceToTakeBlack = square.getId() + 9 > 63 ? null : Chessboard.getSquares(square.getId() + 9);
            Square rightPieceToTakeBlack = square.getId() + 7 > 63 ? null : Chessboard.getSquares(square.getId() + 7);

            if (oneUpSquareBlack != null && twoUpSquareBlack != null && oneUpSquareBlack.getPiece() == null && twoUpSquareBlack.getPiece() == null && square.getId() >= 8 && square.getId() <= 15)
                possibleSquares.add(twoUpSquareBlack);
            if (oneUpSquareBlack != null && oneUpSquareBlack.getPiece() == null)
                possibleSquares.add(oneUpSquareBlack);
            if (rightPieceToTakeBlack != null && rightPieceToTakeBlack.getPiece() != null && rightPieceToTakeBlack.getPiece().getColor()
                    && !Arrays.asList(8, 16, 24, 32, 40, 48).contains(square.getId()))
                possibleSquares.add(rightPieceToTakeBlack);
            else if(rightPieceToTakeBlack != null && rightPieceToTakeBlack.equals(Chessboard.enPassantPossible)) {
                possibleSquares.add(rightPieceToTakeBlack);
            }
            if (leftPieceToTakeBlack != null && leftPieceToTakeBlack.getPiece() != null && leftPieceToTakeBlack.getPiece().getColor()
                    && !Arrays.asList(15, 23, 31, 39, 47, 55).contains(square.getId()))
                possibleSquares.add(leftPieceToTakeBlack);
            else if(leftPieceToTakeBlack != null && leftPieceToTakeBlack.equals(Chessboard.enPassantPossible)) {
                possibleSquares.add(leftPieceToTakeBlack);
            }
        }
        Collections.sort(possibleSquares);
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