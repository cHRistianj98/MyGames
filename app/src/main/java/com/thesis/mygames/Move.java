package com.thesis.mygames;


import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import static com.thesis.mygames.Chessboard.*;

public class Move {
    public static AppCompatActivity activity;
    private Piece piece;
    private Piece newPieceAfterPawnPromotion = null;
    private Square endSquare = null;
    private Square startSquare = null;
    private String pieceAfterPromotion = "";
    private boolean wasCapture = false;
    private boolean wasPawnPromotion = false;
    public static boolean isNavigationMove = false;
    private String notation;
    public static String selectedPiece;

    public Move(Piece piece) {
        this.piece = piece;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void makeNextMove() {
        if(moveList.size() == moveIndicator + 1)
            return;

        isNavigationMove = true;

        Piece p = moveList.get(moveIndicator + 1).piece;
        String endSquareName = getSquareName(moveList.get(moveIndicator + 1).getEndSquare().getId());
        makeQuickMove(p, endSquareName);

        moveList.remove(moveList.size() - 1);
        if(moveIndicator % 2 == 0) {
            Turn.enableBlackPieces();
            Turn.disableWhitePieces();
        } else {
            Turn.enableWhitePieces();
            Turn.disableBlackPieces();
        }

        isNavigationMove = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void makeUndoMove() {
        if (moveIndicator == -1)
            return;

        isNavigationMove = true;

        Piece p = moveList.get(moveIndicator).piece;
        int endSquareId = moveList.get(moveIndicator).startSquare.getId();
        String endSquareName = getSquareName(endSquareId);
        makeQuickMove(p, endSquareName);

        moveList.remove(moveList.size() - 1);
        moveIndicator -= 2;
        if(moveIndicator % 2 == 0) {
            Turn.enableBlackPieces();
            Turn.disableWhitePieces();
        } else {
            Turn.enableWhitePieces();
            Turn.disableBlackPieces();
        }
        isNavigationMove = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void makeQuickMove(Piece piece, String endSquareName) {
        Square endSquare = getSquares(getSquareId(endSquareName));

//        if(!piece.possibleFieldsToMoveCheck().contains(endSquare)) {
//            throw new IllegalArgumentException("This whiteSquare is illegal for this piece!");
//        }

        Move move = new Move(piece);
        int startSquareId = piece.getSquare().getId();
        move.makeMove(endSquare.getId(), endSquare, startSquareId);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addListeners(List<Square> possibleSquares) {
        resetActionListeners();
        showPossibleSquares(possibleSquares);
        setActionListenersOnPossibleSquares(possibleSquares);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void resetActionListeners() {
        Square f;
        for (int i = 0; i < 64; i++) {
            f = getSquares(i);
            b[i].setBackground(MyApplication.getAppContext().getResources().getDrawable( getSquareColor(i)));
            if (f.getPiece() != null && f.getPiece().getColor() == piece.getColor()) {
                continue;
            }

            b[i].setOnClickListener(null);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPossibleSquares(List<Square> possibleSquares) {
        for (Square fi : possibleSquares) {
            int id = fi.getId();
            b[id].setBackground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.yellow_square));;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setActionListenersOnPossibleSquares(List<Square> possibleSquares) {
        final int startSquareId = piece.getSquare().getId();

        for (final Square endSquare : possibleSquares) {
            final int endSquareId = endSquare.getId();
            b[endSquareId].setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    makeMove(endSquareId, endSquare, startSquareId);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void makeMove(int endSquareId, Square f, int startSquareId) {
        startSquare = piece.getSquare();
        endSquare = f;
        b[startSquareId].setImageResource(0);
        getSquares(startSquareId).setPiece(null);
         removeUnnecessaryActionListeners();

        if (Pawn.wasEnPassant)
            enPassantMoveHandler(endSquareId);

        else if (getSquares(endSquare.getId()).getPiece() != null && piece.getColor() && !getSquares(endSquare.getId()).getPiece().getColor())
            deleteCapturedBlackPiece(endSquareId);

        else if (getSquares(endSquare.getId()).getPiece() != null && !piece.getColor() && getSquares(endSquare.getId()).getPiece().getColor()) {
            deleteCapturedWhitePiece(endSquareId);
        }

        setPieceOnNewSquare(endSquareId);

        if (piece instanceof King && endSquare.getId() - startSquare.getId() == 2) {
            shortCastleHandler();
        } else if (piece instanceof King && endSquare.getId() - startSquare.getId() == -2) {
            longCastleHandler();
        }
         else if (this.piece instanceof Pawn && this.piece.getSquare().getId() >= 0 && this.piece.getSquare().getId() <= 7) {
            wasPawnPromotion = true;
            pawnPromotion(endSquareId, true);
        } else if (this.piece instanceof Pawn && this.piece.getSquare().getId() >= 56 && this.piece.getSquare().getId() <= 63) {
            wasPawnPromotion = true;
            pawnPromotion(endSquareId, false);
        }

        chessNotation(wasCapture, wasPawnPromotion, ((King) blackPieces.get(12)).isCheck() ||
                ((King) whitePieces.get(12)).isCheck());

        moveIndicator++;

        if (piece instanceof King)
            ((King) piece).setWasMoved(true);

        else if (piece instanceof Rook)
            ((Rook) piece).setWasMoved(true);

        enPassantPossible = getEnPassantPossibleSquare();
        setTurn();

        moveList.add(this);

//        PGNFormat.generatePGNFromPosition();
//        System.out.println(PGNGenerator);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void removeUnnecessaryActionListeners() {
        removeCurrentActionListener(piece);
        removePossibleActionListeners(piece.possibleFieldsToMove());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removeCurrentActionListener(Piece p) {
        int start = p.getSquare().getId();
        b[start].setOnClickListener(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removePossibleActionListeners(List<Square> possibleSquares) {
        for (Square f : possibleSquares) {
            b[f.getId()].setBackground(MyApplication.getAppContext().getResources().getDrawable( getSquareColor(f.getId())));
            b[f.getId()].setOnClickListener(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void enPassantMoveHandler(int endId) {
        if (piece.getColor()) {
            blackPieces.set(getSquares(endId + 8).getPiece().getId(), null);
            getSquares(endId + 8).setPiece(null);
            b[endId + 8].setImageResource(0);
        } else {
            whitePieces.set(getSquares(endId - 8).getPiece().getId(), null);
            getSquares(endId - 8).setPiece(null);
            b[endId - 8].setImageResource(0);
        }

        Pawn.wasEnPassant = false;
        wasCapture = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteCapturedBlackPiece(int endId) {
        blackPieces.set(getSquares(endSquare.getId()).getPiece().getId(), null);
        getSquares(endSquare.getId()).setPiece(null);
        b[endId].setImageResource(0);
        wasCapture = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteCapturedWhitePiece(int endId) {
        whitePieces.set(getSquares(endSquare.getId()).getPiece().getId(), null);
        getSquares(endSquare.getId()).setPiece(null);
        b[endId].setImageResource(0);
        wasCapture = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setPieceOnNewSquare(int endSquareId) {
        piece.setSquare(getSquares(endSquareId));
        b[endSquareId].setImageResource(piece.getIcon());
        getSquares(endSquareId).setPiece(piece);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void shortCastleHandler() {
        resetActionListeners();
        if (piece.getColor()) {
            b[63].setImageResource(0);
            getSquares(63).setPiece(null);
            removeCurrentActionListener(whitePieces.get(15));
            whitePieces.get(15).setSquare(getSquares(61));
            b[61].setImageResource(whitePieces.get(15).getIcon());
            getSquares(61).setPiece(whitePieces.get(15));
        } else {
            b[7].setImageResource(0);
            getSquares(7).setPiece(null);
            removeCurrentActionListener(blackPieces.get(15));
            blackPieces.get(15).setSquare(getSquares(5));
            b[5].setImageResource(blackPieces.get(15).getIcon());
            getSquares(5).setPiece(blackPieces.get(15));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void longCastleHandler() {
        resetActionListeners();
        if (piece.getColor()) {
            b[56].setImageResource(0);
            getSquares(56).setPiece(null);
            removeCurrentActionListener(whitePieces.get(8));
            whitePieces.get(8).setSquare(getSquares(59));
            b[59].setImageResource(whitePieces.get(8).getIcon());
            getSquares(59).setPiece(whitePieces.get(8));
        } else {
            b[0].setImageResource(0);
            getSquares(0).setPiece(null);
            removeCurrentActionListener(blackPieces.get(8));
            blackPieces.get(8).setSquare(getSquares(3));
            b[3].setImageResource(blackPieces.get(8).getIcon());
            getSquares(3).setPiece(blackPieces.get(8));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pawnPromotion(int endSquareId, boolean color) {
        DialogFragment singleChoiceDialog = new PromotionDialog();
        singleChoiceDialog.setCancelable(false);
        singleChoiceDialog.show(activity.getSupportFragmentManager(), "Single Choice Dialog");

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setNewPieceAfterPawnPromotion() {
        int pawnId = piece.getId();
        int endSquareId = endSquare.getId();
        switch (selectedPiece) {
            case "Hetman":
                newPieceAfterPawnPromotion = new Queen(piece.getSquare(), piece.color, pawnId, 0);
                pieceAfterPromotion = "Q";
                if (piece.getColor()) {
                    b[endSquareId].setImageResource(whiteIcons[11]);
                    newPieceAfterPawnPromotion.setIcon(whiteIcons[11]);
                    whitePieces.set(pawnId, null);
                    whitePieces.set(pawnId, newPieceAfterPawnPromotion);
                } else {
                    b[endSquareId].setImageResource(blackIcons[3]);
                    newPieceAfterPawnPromotion.setIcon(blackIcons[3]);
                    blackPieces.set(pawnId, null);
                    blackPieces.set(pawnId, newPieceAfterPawnPromotion);
                }
                break;
            case "Wieża":
                newPieceAfterPawnPromotion = new Rook(piece.getSquare(), piece.color, pawnId, 0);
                pieceAfterPromotion = "R";
                if (piece.getColor()) {
                    b[endSquareId].setImageResource(whiteIcons[8]);
                    newPieceAfterPawnPromotion.setIcon(whiteIcons[8]);
                    whitePieces.set(pawnId, null);
                    whitePieces.set(pawnId, newPieceAfterPawnPromotion);
                } else {
                    b[endSquareId].setImageResource(blackIcons[0]);
                    newPieceAfterPawnPromotion.setIcon(blackIcons[0]);
                    blackPieces.set(pawnId, null);
                    blackPieces.set(pawnId, newPieceAfterPawnPromotion);
                }
                break;
            case "Goniec":
                newPieceAfterPawnPromotion = new Bishop(piece.getSquare(), piece.color, pawnId, 0);
                pieceAfterPromotion = "B";
                if (piece.getColor()) {
                    b[endSquareId].setImageResource(whiteIcons[10]);
                    newPieceAfterPawnPromotion.setIcon(whiteIcons[10]);
                    whitePieces.set(pawnId, null);
                    whitePieces.set(pawnId, newPieceAfterPawnPromotion);
                } else {
                    b[endSquareId].setImageResource(blackIcons[2]);
                    newPieceAfterPawnPromotion.setIcon(blackIcons[2]);
                    blackPieces.set(pawnId, null);
                    blackPieces.set(pawnId, newPieceAfterPawnPromotion);
                }
                break;
            case "Skoczek":
                newPieceAfterPawnPromotion = new Knight(piece.getSquare(), piece.color, pawnId, 0);
                pieceAfterPromotion = "N";
                if (piece.getColor()) {
                    b[endSquareId].setImageResource(whiteIcons[9]);
                    newPieceAfterPawnPromotion.setIcon(whiteIcons[9]);
                    whitePieces.set(pawnId, null);
                    whitePieces.set(pawnId, newPieceAfterPawnPromotion);
                } else {
                    b[endSquareId].setImageResource(blackIcons[1]);
                    newPieceAfterPawnPromotion.setIcon(blackIcons[1]);
                    blackPieces.set(pawnId, null);
                    blackPieces.set(pawnId, newPieceAfterPawnPromotion);
                }
                break;
        }

        newPieceAfterPawnPromotion.setSquare(getSquares(endSquareId));
        getSquares(endSquareId).setPiece(newPieceAfterPawnPromotion);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void chessNotation(boolean wasCapture, boolean wasPawnPromotion, boolean wasCheck) {
        StringBuilder move = new StringBuilder();
        if (piece instanceof Pawn) {
            if (wasCapture) {
                move.append(getSquareLetter(startSquare.getId()));
                move.append("x");
            }
            move.append(getSquareName(endSquare.getId()));

            if (wasPawnPromotion) {
                move.append("=");
                move.append(pieceAfterPromotion);
            }

            if (wasCheck) {
                if (((King) blackPieces.get(12)).isCheckmate() || ((King) whitePieces.get(12)).isCheckmate()) {
                    move.append("#");
                } else {
                    move.append("+");
                }
            }
            notation = move.toString();
        } else if (piece instanceof Knight || piece instanceof Rook || piece instanceof Bishop || piece instanceof Queen) {
            move.append(getFirstLetterOfPiece());

            if (isOtherPieceWhichCanMoveOnTheSameSquare() == 1) {
                move.append(getSquareNumber(startSquare.getId()));
            } else if (isOtherPieceWhichCanMoveOnTheSameSquare() == 2) {
                move.append(getSquareLetter(startSquare.getId()));
            }

            if (wasCapture)
                move.append("x");
            move.append(getSquareName(endSquare.getId()));

            if (wasCheck) {
                if (((King) blackPieces.get(12)).isCheckmate() || ((King) whitePieces.get(12)).isCheckmate()) {
                    move.append("#");
                } else {
                    move.append("+");
                }
            }
            notation = move.toString();
        }
        //else if(piece instanceof King)
        else {
            if (endSquare.getId() - startSquare.getId() == 2 || endSquare.getId() - startSquare.getId() == -2) {
                if (endSquare.getId() - startSquare.getId() == 2) {
                    move.append("O-O");
                } else {
                    move.append("O-O-O");
                }

                if (wasCheck) {
                    if (((King) blackPieces.get(12)).isCheckmate() || ((King) whitePieces.get(12)).isCheckmate()) {
                        move.append("#");
                    } else {
                        move.append("+");
                    }
                }
                notation = move.toString();
                return;
            }
            move.append(getFirstLetterOfPiece());
            if (wasCapture)
                move.append("x");
            move.append(getSquareName(endSquare.getId()));
            if (wasCheck) {
                if (((King) blackPieces.get(12)).isCheckmate() || ((King) whitePieces.get(12)).isCheckmate()) {
                    move.append("#");
                } else {
                    move.append("+");
                }
            }
            notation = move.toString();
        }

    }

    private String getFirstLetterOfPiece() {
        if (piece instanceof Rook) return "R";
        else if (piece instanceof Knight) return "N";
        else if (piece instanceof Bishop) return "B";
        else if (piece instanceof Queen) return "Q";
        else if (piece instanceof King) return "K";
        else return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private int isOtherPieceWhichCanMoveOnTheSameSquare() {
        if (piece.getColor()) {
            for (Piece p : whitePieces) {
                if (p == null)
                    continue;
                if (piece.getId() == p.getId())
                    continue;
                if (piece.getClass().equals(p.getClass())) {
                    List<Integer> possibleSquaresId = p.getMyPiecesBlocked();
                    if (possibleSquaresId.contains(endSquare.getId())) {
                        if (getSquareLetter(startSquare.getId()).equals(getSquareLetter(p.getSquare().getId())))
                            return 1;
                        else
                            return 2;
                    }
                }
            }
        } else {
            for (Piece p : blackPieces) {
                if (p == null)
                    continue;
                if (piece.getId() == p.getId())
                    continue;
                if (piece.getClass().equals(p.getClass())) {
                    List<Integer> possibleSquaresId = p.getMyPiecesBlocked();
                    if (possibleSquaresId.contains(endSquare.getId())) {
                        if (getSquareLetter(startSquare.getId()).equals(getSquareLetter(p.getSquare().getId())))
                            return 1;
                        else
                            return 2;
                    }
                }
            }
        }
        return 0;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Square getEnPassantPossibleSquare() {
        if (piece instanceof Pawn) {
            if (endSquare.getId() - startSquare.getId() == 16)
                return getSquares(endSquare.getId() - 8);
            else if (endSquare.getId() - startSquare.getId() == -16)
                return getSquares(endSquare.getId() + 8);
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setTurn() {
        if (piece.getColor()) {
            Turn.disableWhitePieces();
            Turn.enableBlackPieces();
        } else {
            Turn.disableBlackPieces();
            Turn.enableWhitePieces();
        }
    }

    public Square getEndSquare() {
        return endSquare;
    }

    public void setEndSquare(Square endSquare) {
        this.endSquare = endSquare;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}