package com.thesis.mygames;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Move {
    private Piece piece;
    private Piece newPieceAfterPawnPromotion = null;
    private static Context context;
    private Square endSquare = null;
    private Square startSquare = null;
    private String pieceAfterPromotion = "";
    private boolean wasCapture = false;
    private boolean wasPawnPromotion = false;

    public Move(Context context, Piece piece) {
        Move.context = context;
        this.piece = piece;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void makeQuickMove(Piece piece, String endSquareName) {
        Square endSquare = Chessboard.getSquares(Chessboard.getSquareId(endSquareName));

        if(!piece.possibleFieldsToMoveCheck().contains(endSquare)) {
            throw new IllegalArgumentException("This whiteSquare is illegal for this piece!");
        }

        Move move = new Move(context, piece);
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
            f = Chessboard.getSquares(i);
            MainActivity.b[i].setBackground(context.getResources().getDrawable( Chessboard.getSquareColor(i)));
            if (f.getPiece() != null && f.getPiece().getColor() == piece.getColor()) {
                continue;
            }

            MainActivity.b[i].setOnClickListener(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showPossibleSquares(List<Square> possibleSquares) {
        for (Square fi : possibleSquares) {
            int id = fi.getId();
            MainActivity.b[id].setBackground(context.getResources().getDrawable(R.drawable.yellow_square));;
        }
    }

    private void setActionListenersOnPossibleSquares(List<Square> possibleSquares) {
        final int startSquareId = piece.getSquare().getId();

        for (final Square endSquare : possibleSquares) {
            final int endSquareId = endSquare.getId();
            MainActivity.b[endSquareId].setOnClickListener(new View.OnClickListener() {
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
        MainActivity.b[startSquareId].setImageResource(0);
        Chessboard.getSquares(startSquareId).setPiece(null);
        removeUnnecessaryActionListeners();

        if (Pawn.wasEnPassant)
            enPassantMoveHandler(endSquareId);

        else if (Chessboard.getSquares(endSquare.getId()).getPiece() != null && piece.getColor() && !Chessboard.getSquares(endSquare.getId()).getPiece().getColor())
            deleteCapturedBlackPiece(endSquareId);

        else if (Chessboard.getSquares(endSquare.getId()).getPiece() != null && !piece.getColor() && Chessboard.getSquares(endSquare.getId()).getPiece().getColor()) {
            deleteCapturedWhitePiece(endSquareId);
        }

        setPieceOnNewSquare(endSquareId);

        if (piece instanceof King && endSquare.getId() - startSquare.getId() == 2) {
            shortCastleHandler();
        } else if (piece instanceof King && endSquare.getId() - startSquare.getId() == -2) {
            longCastleHandler();
        }
//        } else if (this.piece instanceof Pawn && this.piece.getSquare().getId() >= 0 && this.piece.getSquare().getId() <= 7) {
//            wasPawnPromotion = true;
//            pawnPromotion(endSquareId, true);
//        } else if (this.piece instanceof Pawn && this.piece.getSquare().getId() >= 56 && this.piece.getSquare().getId() <= 63) {
//            wasPawnPromotion = true;
//            pawnPromotion(endSquareId, false);
//        }

        chessNotation(wasCapture, wasPawnPromotion, ((King) Chessboard.blackPieces.get(12)).isCheck() ||
                ((King) Chessboard.whitePieces.get(12)).isCheck());

//        PGNFormat.generatePgnTags();
//        System.out.println(Chessboard.PGNTagGenerator);
//
//        PGNFormat.generatePgnMoves();
//        System.out.println(Chessboard.PGNMoveGenerator);

        if (piece instanceof King)
            ((King) piece).setWasMoved(true);

        else if (piece instanceof Rook)
            ((Rook) piece).setWasMoved(true);

        Chessboard.enPassantPossible = getEnPassantPossibleSquare();
        setTurn();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void removeUnnecessaryActionListeners() {
        removeCurrentActionListener(piece);
        removePossibleActionListeners(piece.possibleFieldsToMove());
    }

    public void removeCurrentActionListener(Piece p) {

        int start = p.getSquare().getId();
        MainActivity.b[start].setOnClickListener(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void removePossibleActionListeners(List<Square> possibleSquares) {
        for (Square f : possibleSquares) {
            MainActivity.b[f.getId()].setBackground(context.getResources().getDrawable( Chessboard.getSquareColor(f.getId())));
            MainActivity.b[f.getId()].setOnClickListener(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void enPassantMoveHandler(int endId) {
        if (piece.getColor()) {
            Chessboard.blackPieces.set(Chessboard.getSquares(endId + 8).getPiece().getId(), null);
            Chessboard.getSquares(endId + 8).setPiece(null);
            MainActivity.b[endId + 8].setImageResource(0);
        } else {
            Chessboard.whitePieces.set(Chessboard.getSquares(endId - 8).getPiece().getId(), null);
            Chessboard.getSquares(endId - 8).setPiece(null);
            MainActivity.b[endId - 8].setImageResource(0);
        }

        Pawn.wasEnPassant = false;
        wasCapture = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteCapturedBlackPiece(int endId) {
        Chessboard.blackPieces.set(Chessboard.getSquares(endSquare.getId()).getPiece().getId(), null);
        Chessboard.getSquares(endSquare.getId()).setPiece(null);
        MainActivity.b[endId].setImageResource(0);
        wasCapture = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteCapturedWhitePiece(int endId) {
        Chessboard.whitePieces.set(Chessboard.getSquares(endSquare.getId()).getPiece().getId(), null);
        Chessboard.getSquares(endSquare.getId()).setPiece(null);
        MainActivity.b[endId].setImageResource(0);
        wasCapture = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setPieceOnNewSquare(int endSquareId) {
        piece.setSquare(Chessboard.getSquares(endSquareId));
        MainActivity.b[endSquareId].setImageResource(piece.getIcon());
        Chessboard.getSquares(endSquareId).setPiece(piece);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void shortCastleHandler() {
        resetActionListeners();
        if (piece.getColor()) {
            MainActivity.b[63].setImageResource(0);
            Chessboard.getSquares(63).setPiece(null);
            removeCurrentActionListener(Chessboard.whitePieces.get(15));
            Chessboard.whitePieces.get(15).setSquare(Chessboard.getSquares(61));
            MainActivity.b[61].setImageResource(Chessboard.whitePieces.get(15).getIcon());
            Chessboard.getSquares(61).setPiece(Chessboard.whitePieces.get(15));
        } else {
            MainActivity.b[7].setImageResource(0);
            Chessboard.getSquares(7).setPiece(null);
            removeCurrentActionListener(Chessboard.blackPieces.get(15));
            Chessboard.blackPieces.get(15).setSquare(Chessboard.getSquares(5));
            MainActivity.b[5].setImageResource(Chessboard.blackPieces.get(15).getIcon());
            Chessboard.getSquares(5).setPiece(Chessboard.blackPieces.get(15));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void longCastleHandler() {
        resetActionListeners();
        if (piece.getColor()) {
            MainActivity.b[56].setImageResource(0);
            Chessboard.getSquares(56).setPiece(null);
            removeCurrentActionListener(Chessboard.whitePieces.get(8));
            Chessboard.whitePieces.get(8).setSquare(Chessboard.getSquares(59));
            MainActivity.b[59].setImageResource(Chessboard.whitePieces.get(8).getIcon());
            Chessboard.getSquares(59).setPiece(Chessboard.whitePieces.get(8));
        } else {
            MainActivity.b[0].setImageResource(0);
            Chessboard.getSquares(0).setPiece(null);
            removeCurrentActionListener(Chessboard.blackPieces.get(8));
            Chessboard.blackPieces.get(8).setSquare(Chessboard.getSquares(3));
            MainActivity.b[3].setImageResource(Chessboard.blackPieces.get(8).getIcon());
            Chessboard.getSquares(3).setPiece(Chessboard.blackPieces.get(8));
        }
    }

//    public void pawnPromotion(int endSquareId, boolean color) {
//        String[] values = {"Hetman", "Wieża", "Goniec", "Skoczek"};
//
//        Object selected = JOptionPane.showInputDialog(null, "Which figure you want to choose",
//                "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
//        if (selected != null) {
//            String selectedString = selected.toString();
//            int pawnId = piece.getId();
//            switch (selectedString) {
//                case "Hetman":
//                    newPieceAfterPawnPromotion = new Queen(piece.getSquare(), color, pawnId, null);
//                    pieceAfterPromotion = "Q";
//                    if (piece.getColor()) {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.whiteIcons[11]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.whiteIcons[11]);
//                        Chessboard.whitePieces.set(pawnId, null);
//                        Chessboard.whitePieces.set(pawnId, newPieceAfterPawnPromotion);
//                    } else {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.blackIcons[3]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.blackIcons[3]);
//                        Chessboard.blackPieces.set(pawnId, null);
//                        Chessboard.blackPieces.set(pawnId, newPieceAfterPawnPromotion);
//                    }
//                    break;
//                case "Wieża":
//                    newPieceAfterPawnPromotion = new Rook(piece.getSquare(), color, pawnId, null);
//                    pieceAfterPromotion = "R";
//                    if (piece.getColor()) {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.whiteIcons[8]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.whiteIcons[8]);
//                        Chessboard.whitePieces.set(pawnId, null);
//                        Chessboard.whitePieces.set(pawnId, newPieceAfterPawnPromotion);
//                    } else {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.blackIcons[0]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.blackIcons[0]);
//                        Chessboard.blackPieces.set(pawnId, null);
//                        Chessboard.blackPieces.set(pawnId, newPieceAfterPawnPromotion);
//                    }
//                    break;
//                case "Goniec":
//                    newPieceAfterPawnPromotion = new Bishop(piece.getSquare(), color, pawnId, null);
//                    pieceAfterPromotion = "B";
//                    if (piece.getColor()) {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.whiteIcons[10]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.whiteIcons[10]);
//                        Chessboard.whitePieces.set(pawnId, null);
//                        Chessboard.whitePieces.set(pawnId, newPieceAfterPawnPromotion);
//                    } else {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.blackIcons[2]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.blackIcons[2]);
//                        Chessboard.blackPieces.set(pawnId, null);
//                        Chessboard.blackPieces.set(pawnId, newPieceAfterPawnPromotion);
//                    }
//                    break;
//                case "Skoczek":
//                    newPieceAfterPawnPromotion = new Knight(piece.getSquare(), color, pawnId, null);
//                    pieceAfterPromotion = "N";
//                    if (piece.getColor()) {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.whiteIcons[9]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.whiteIcons[9]);
//                        Chessboard.whitePieces.set(pawnId, null);
//                        Chessboard.whitePieces.set(pawnId, newPieceAfterPawnPromotion);
//                    } else {
//                        Chessboard.b[endSquareId].setIcon(Chessboard.blackIcons[1]);
//                        newPieceAfterPawnPromotion.setIcon(Chessboard.blackIcons[1]);
//                        Chessboard.blackPieces.set(pawnId, null);
//                        Chessboard.blackPieces.set(pawnId, newPieceAfterPawnPromotion);
//                    }
//                    break;
//            }
//
//            newPieceAfterPawnPromotion.setSquare(Chessboard.getSquares(endSquareId));
//            Chessboard.getSquares(endSquareId).setPiece(newPieceAfterPawnPromotion);
//
//        } else {
//            System.out.println("User cancelled");
//        }
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void chessNotation(boolean wasCapture, boolean wasPawnPromotion, boolean wasCheck) {
        StringBuilder move = new StringBuilder();
        if (piece instanceof Pawn) {
            if (wasCapture) {
                move.append(Chessboard.getSquareLetter(startSquare.getId()));
                move.append("x");
            }
            move.append(Chessboard.getSquareName(endSquare.getId()));

            if (wasPawnPromotion) {
                move.append("=");
                move.append(pieceAfterPromotion);
            }

            if (wasCheck) {
                if (((King) Chessboard.blackPieces.get(12)).isCheckmate() || ((King) Chessboard.whitePieces.get(12)).isCheckmate()) {
                    move.append("#");
                } else {
                    move.append("+");
                }
            }
            Chessboard.moveList.add(move.toString());
        } else if (piece instanceof Knight || piece instanceof Rook || piece instanceof Bishop || piece instanceof Queen) {
            move.append(getFirstLetterOfPiece());

            if (isOtherPieceWhichCanMoveOnTheSameSquare() == 1) {
                move.append(Chessboard.getSquareNumber(startSquare.getId()));
            } else if (isOtherPieceWhichCanMoveOnTheSameSquare() == 2) {
                move.append(Chessboard.getSquareLetter(startSquare.getId()));
            }

            if (wasCapture)
                move.append("x");
            move.append(Chessboard.getSquareName(endSquare.getId()));

            if (wasCheck) {
                if (((King) Chessboard.blackPieces.get(12)).isCheckmate() || ((King) Chessboard.whitePieces.get(12)).isCheckmate()) {
                    move.append("#");
                } else {
                    move.append("+");
                }
            }
            Chessboard.moveList.add(move.toString());
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
                    if (((King) Chessboard.blackPieces.get(12)).isCheckmate() || ((King) Chessboard.whitePieces.get(12)).isCheckmate()) {
                        move.append("#");
                    } else {
                        move.append("+");
                    }
                }
                Chessboard.moveList.add(move.toString());
                return;
            }
            move.append(getFirstLetterOfPiece());
            if (wasCapture)
                move.append("x");
            move.append(Chessboard.getSquareName(endSquare.getId()));
            if (wasCheck) {
                if (((King) Chessboard.blackPieces.get(12)).isCheckmate() || ((King) Chessboard.whitePieces.get(12)).isCheckmate()) {
                    move.append("#");
                } else {
                    move.append("+");
                }
            }
            Chessboard.moveList.add(move.toString());
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
            for (Piece p : Chessboard.whitePieces) {
                if (p == null)
                    continue;
                if (piece.getId() == p.getId())
                    continue;
                if (piece.getClass().equals(p.getClass())) {
                    List<Integer> possibleSquaresId = p.getMyPiecesBlocked();
                    if (possibleSquaresId.contains(endSquare.getId())) {
                        if (Chessboard.getSquareLetter(startSquare.getId()).equals(Chessboard.getSquareLetter(p.getSquare().getId())))
                            return 1;
                        else
                            return 2;
                    }
                }
            }
        } else {
            for (Piece p : Chessboard.blackPieces) {
                if (p == null)
                    continue;
                if (piece.getId() == p.getId())
                    continue;
                if (piece.getClass().equals(p.getClass())) {
                    List<Integer> possibleSquaresId = p.getMyPiecesBlocked();
                    if (possibleSquaresId.contains(endSquare.getId())) {
                        if (Chessboard.getSquareLetter(startSquare.getId()).equals(Chessboard.getSquareLetter(p.getSquare().getId())))
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
                return Chessboard.getSquares(endSquare.getId() - 8);
            else if (endSquare.getId() - startSquare.getId() == -16)
                return Chessboard.getSquares(endSquare.getId() + 8);
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setTurn() {
        if (piece.getColor()) {
            Turn.disableWhitePieces();
            Turn.enableBlackPieces(context);
        } else {
            Turn.disableBlackPieces();
            Turn.enableWhitePieces(context);
        }
    }

    public void undoMove() {

    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}