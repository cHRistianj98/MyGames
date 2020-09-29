package com.thesis.mygames.pieces;

import android.widget.ImageButton;

import com.thesis.mygames.gameutils.Chessboard;
import com.thesis.mygames.gameutils.Piece;
import com.thesis.mygames.gameutils.Square;

import java.util.ArrayList;
import java.util.List;
import static com.thesis.mygames.gameutils.Chessboard.getSquares;

public class Rook extends Piece {

    private boolean wasMoved = false;

    public Rook(Square square, boolean color, int icon) {
        super(square, color, icon);
    }

    public Rook(Square square, boolean color, int id, int icon) {
        super(square, color, id, icon);
    }

    @Override
    public List<Integer> getMyPiecesBlocked() {
        List<Integer> toRemove = new ArrayList<>();

        getBlockingPieceOnRightSide(toRemove);
        getBlockingPieceOnLeftSide(toRemove);
        getBlockingPieceOnTopSide(toRemove);
        getBlockingPieceOnBottomSide(toRemove);

        return toRemove;
    }

    private void getBlockingPieceOnRightSide(List<Integer> toRemove) {
        int squareId = this.square.getId();
        if(squareId % 8 == 7) return;
        for (int i = squareId + 1; i < 64; i++) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) return;
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) {
                toRemove.add(i);
                return;
            }
            if(i % 8 == 7) return;
        }
    }

    private void getBlockingPieceOnLeftSide(List<Integer> toRemove) {
        int squareId = this.square.getId();
        if(squareId % 8 == 0) return;
        for (int i = squareId - 1; i >= 0; i--) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) return;
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) {
                toRemove.add(i);
                return;
            }
            if(i % 8 == 0) return;
        }
    }

    private void getBlockingPieceOnTopSide(List<Integer> toRemove) {
        int squareId = this.square.getId();
        for (int i = squareId - 8; i >= 0 ; i = i - 8) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) break;
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) {
                toRemove.add(i);
                break;
            }
        }
    }

    private void getBlockingPieceOnBottomSide(List<Integer> toRemove) {
        int squareId = this.square.getId();
        for (int i = squareId + 8; i < 64 ; i = i + 8) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) break;
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) {
                toRemove.add(getSquares(i).getId());
                break;
            }
        }
    }

    @Override
    public List<Square> possibleFieldsToMove(){
        List<Square> possibleSquares = new ArrayList<>();

        getPossibleHorizontalSquaresToRight(possibleSquares);
        getPossibleHorizontalSquaresToLeft(possibleSquares);
        getPossibleVerticalSquaresToUp(possibleSquares);
        getPossibleVerticalSquaresToDown(possibleSquares);

        return possibleSquares;
    }

    private void getPossibleHorizontalSquaresToRight(List<Square> possibleSquares) {
        int squareId = this.square.getId();
        if(squareId % 8 == 7) return;
        for (int i = squareId + 1; i < 64; i++) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) {
                possibleSquares.add(getSquares(i));
                return;
            }
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) return;
            possibleSquares.add(getSquares(i));
            if(i % 8 == 7) {
                possibleSquares.add(getSquares(i));
                return;
            }
        }
    }

    private void getPossibleHorizontalSquaresToLeft(List<Square> possibleSquares) {
        int squareId = this.square.getId();
        if(squareId % 8 == 0) return;
        for (int i = squareId - 1; i >= 0; i--) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) {
                possibleSquares.add(getSquares(i));
                return;
            }
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) return;
            possibleSquares.add(getSquares(i));
            if(i % 8 == 0) {
                possibleSquares.add(getSquares(i));
                return;
            }
        }
    }

    private void getPossibleVerticalSquaresToUp(List<Square> possibleSquares) {
        int squareId = this.square.getId();
        for (int i = squareId - 8; i >= 0 ; i = i - 8) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) {
                possibleSquares.add(getSquares(i));
                break;
            }
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) break;
            possibleSquares.add(getSquares(i));
        }
    }

    private void getPossibleVerticalSquaresToDown(List<Square> possibleSquares) {
        int squareId = this.square.getId();
        for (int i = squareId + 8; i < 64 ; i = i + 8) {
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() != this.getColor()) {
                possibleSquares.add(getSquares(i));
                break;
            }
            if(getSquares(i).getPiece() != null && getSquares(i).getPiece().getColor() == this.getColor()) break;
            possibleSquares.add(getSquares(i));
        }
    }

    @Override
    public List<Square> possibleFieldsToMoveCheck() {
        List<Square> everyPossibleSquares = this.possibleFieldsToMove();
        List<Square> possibleSquares = new ArrayList<>();

        Piece piece = null;
        Square square = this.square;
        boolean wasPiece;
        int pieceId = 0;

        for(Square s : everyPossibleSquares) {
            wasPiece = false;
            Chessboard.getSquares(square.getId()).setPiece(null);
            this.setSquare(s);
            if(Chessboard.getSquares(s.getId()).getPiece() != null) {
                piece = Chessboard.getSquares(s.getId()).getPiece();
                pieceId = Chessboard.getSquares(s.getId()).getPiece().getId();
                wasPiece = true;
                simulateObjectDeletion(s.getId(), pieceId);
            }
            simulateMoveOnSquare(s, possibleSquares);
            if(wasPiece)
                simulateReturnCapturedPieceOnSquare(pieceId, piece, s);
            else
                Chessboard.getSquares(s.getId()).setPiece(null);

            this.setSquare(square);
            Chessboard.getSquares(square.getId()).setPiece(this);
        }
        return possibleSquares;
    }

    private void simulateObjectDeletion(int squareId, int pieceId) {
        if (this.getColor()) {
            Chessboard.blackPieces.set(pieceId, null);
        } else {
            Chessboard.whitePieces.set(pieceId, null);
        }
        Chessboard.getSquares(squareId).setPiece(null);
    }

    private void simulateMoveOnSquare(Square s, List<Square> possibleSquares) {
        Chessboard.getSquares(s.getId()).setPiece(this);
        if(this.getColor()) {
            if(!(((King)Chessboard.whitePieces.get(12)).isCheck()))
                possibleSquares.add(s);
        } else {
            if(!(((King)Chessboard.blackPieces.get(12)).isCheck()))
                possibleSquares.add(s);
        }
    }

    private void simulateReturnCapturedPieceOnSquare(int pieceId, Piece piece, Square s) {
        if(this.getColor()) {
            Chessboard.blackPieces.set(pieceId, piece);
        } else {
            Chessboard.whitePieces.set(pieceId, piece);
        }
        Chessboard.getSquares(s.getId()).setPiece(piece);
    }

    public boolean isWasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    @Override
    public List<Square> action(ImageButton []b) {
        List<Square> list = null;

        try {
            list = this.possibleFieldsToMoveCheck();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}