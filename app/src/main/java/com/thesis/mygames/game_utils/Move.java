package com.thesis.mygames.game_utils;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.thesis.mygames.android_utils.MyApplication;
import com.thesis.mygames.formats.PGNFormat;
import com.thesis.mygames.android_utils.PromotionDialog;
import com.thesis.mygames.R;
import com.thesis.mygames.pieces.Bishop;
import com.thesis.mygames.pieces.King;
import com.thesis.mygames.pieces.Knight;
import com.thesis.mygames.pieces.Pawn;
import com.thesis.mygames.pieces.Queen;
import com.thesis.mygames.pieces.Rook;

import java.util.Arrays;
import java.util.List;

import static com.thesis.mygames.game_utils.Chessboard.*;

public class Move {
    public static AppCompatActivity activity;

    private Piece piece;
    private Piece newPieceAfterPawnPromotion = null;
    private Piece capturedPiece = null;
    private Piece castledRook = null;
    private Square endSquare = null;
    private Square startSquare = null;
    private String pieceAfterPromotion = "";
    private String selectedPiece;
    private boolean wasCapture = false;
    private boolean wasPawnPromotion = false;
    private boolean wasEnPassant = false;
    private boolean wasCastling = false;
    private String notation;

    public Move(Piece piece) {
        this.piece = piece;
    }

    public static void makeNextMove() {
        if(moveList.size() == moveIndicator + 1)
            return;

        Piece p = moveList.get(moveIndicator + 1).piece;
        String endSquareName = getSquareName(moveList.get(moveIndicator + 1).getEndSquare().getId());

        Square endSquare = getSquares(getSquareId(endSquareName));
        Move move = new Move(p);
        int startSquareId = p.getSquare().getId();
        move.nextMoveHandler(endSquare.getId(), endSquare, startSquareId);
    }

    public static void makeUndoMove() {
        if (moveIndicator == -1)
            return;

        Move lastMove = moveList.get(moveIndicator);
        String endSquareName = getSquareName(lastMove.startSquare.getId());
        Square endSquare = getSquares(getSquareId(endSquareName));
        Move move = new Move(lastMove.piece);
        int startSquareId = lastMove.piece.getSquare().getId();
        move.undoMoveHandler(endSquare.getId(), endSquare, startSquareId);

        if(lastMove.wasPawnPromotion) {
            lastMove.endSquare.setPiece(null);
            if(lastMove.piece.color) {
                whitePieces.set(lastMove.newPieceAfterPawnPromotion.getId(), lastMove.piece);
            } else {
                blackPieces.set(lastMove.newPieceAfterPawnPromotion.getId(), lastMove.piece);
            }
            b[lastMove.endSquare.getId()].setImageResource(0);

            if(lastMove.wasCapture) {
                lastMove.endSquare.setPiece(lastMove.capturedPiece);
                if(lastMove.piece.color) {
                    blackPieces.set(getSquares(lastMove.endSquare.getId()).getPiece().getId(), lastMove.capturedPiece);
                } else {
                    whitePieces.set(getSquares(lastMove.endSquare.getId()).getPiece().getId(), lastMove.capturedPiece);
                }
                lastMove.capturedPiece.setSquare(lastMove.endSquare);
                b[lastMove.endSquare.getId()].setImageResource(lastMove.capturedPiece.getIcon());
            }

            lastMove.startSquare.setPiece(lastMove.piece);
            lastMove.piece.setSquare(lastMove.startSquare);
            b[lastMove.startSquare.getId()].setImageResource(lastMove.piece.getIcon());
        }

        else if (lastMove.wasEnPassant) {
            if (lastMove.piece.getColor()) {
                blackPieces.set(lastMove.capturedPiece.getId(), lastMove.capturedPiece);
            } else {
                whitePieces.set(lastMove.capturedPiece.getId(), lastMove.capturedPiece);
            }
            getSquares(lastMove.capturedPiece.getSquare().getId()).setPiece(lastMove.capturedPiece);
            b[lastMove.capturedPiece.getSquare().getId()].setImageResource(lastMove.capturedPiece.getIcon());
        }

        else if(lastMove.wasCapture) {
            lastMove.endSquare.setPiece(lastMove.capturedPiece);
            if(lastMove.piece.color) {
                blackPieces.set(getSquares(lastMove.endSquare.getId()).getPiece().getId(), lastMove.capturedPiece);
            } else {
                whitePieces.set(getSquares(lastMove.endSquare.getId()).getPiece().getId(), lastMove.capturedPiece);
            }
            lastMove.capturedPiece.setSquare(lastMove.endSquare);
            b[lastMove.endSquare.getId()].setImageResource(lastMove.capturedPiece.getIcon());
        }

        else if(lastMove.wasCastling) {
            Square endRookSquare = lastMove.castledRook.getSquare();

            if(endRookSquare.getId() % 8 == 5) {
                endRookSquare.setPiece(null);
                b[endRookSquare.getId()].setImageResource(0);
                getSquares(endRookSquare.getId() + 2).setPiece(lastMove.castledRook);
                b[endRookSquare.getId() + 2].setImageResource(lastMove.castledRook.getIcon());
            } else if(endRookSquare.getId() % 8 == 3) {
                endRookSquare.setPiece(null);
                b[endRookSquare.getId()].setImageResource(0);
                getSquares(endRookSquare.getId() - 3).setPiece(lastMove.castledRook);
                b[endRookSquare.getId() - 3].setImageResource(lastMove.castledRook.getIcon());
            }
        }
    }

    public static void makeQuickMove(Piece piece, String endSquareName) {
        Square endSquare = getSquares(getSquareId(endSquareName));
        Move move = new Move(piece);
        int startSquareId = piece.getSquare().getId();
        move.makeMove(endSquare.getId(), endSquare, startSquareId);
    }

    public void addListeners(List<Square> possibleSquares) {
        resetActionListeners();
        showPossibleSquares(possibleSquares);
        setActionListenersOnPossibleSquares(possibleSquares);
    }

    private void resetActionListeners() {
        Square f;
        for (int i = 0; i < 64; i++) {
            f = getSquares(i);
            b[i].setBackground(MyApplication.getAppContext().getResources().getDrawable( getSquareColor(i)));
            if (f.getPiece() != null && f.getPiece().getColor() == piece.getColor())
                continue;

            b[i].setOnClickListener(null);
        }
    }

    private void showPossibleSquares(List<Square> possibleSquares) {
        for (Square fi : possibleSquares) {
            int id = fi.getId();
            b[id].setBackground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.yellow_square));
        }
    }

    private void setActionListenersOnPossibleSquares(List<Square> possibleSquares) {
        final int startSquareId = piece.getSquare().getId();

        for (final Square endSquare : possibleSquares) {
            final int endSquareId = endSquare.getId();
            b[endSquareId].setOnClickListener(v -> makeMove(endSquareId, endSquare, startSquareId));
        }
    }

    public void nextMoveHandler(int endSquareId, Square f, int startSquareId) {
        startSquare = getSquares(startSquareId);
        endSquare = f;
        b[startSquareId].setImageResource(0);
        getSquares(startSquareId).setPiece(null);

        boolean wasBlackPieceCaptured = getSquares(endSquare.getId()).getPiece() != null
                && piece.getColor() && !getSquares(endSquare.getId()).getPiece().getColor();
        boolean wasWhitePieceCaptured = getSquares(endSquare.getId()).getPiece() != null
                && !piece.getColor() && getSquares(endSquare.getId()).getPiece().getColor();
        wasEnPassant = piece instanceof Pawn
                && Arrays.asList(-9, -7, 7, 9).contains(endSquareId - startSquareId)
                && endSquare.getPiece() == null;
        if (wasEnPassant)
            enPassantMoveHandler(endSquareId);
        else if (wasBlackPieceCaptured)
            deleteCapturedBlackPiece(endSquareId);
        else if (wasWhitePieceCaptured)
            deleteCapturedWhitePiece(endSquareId);

        setPieceOnNewSquare(endSquareId);

        boolean longCastleCondition = piece instanceof King && endSquare.getId() - startSquare.getId() == -2;
        boolean shortCastleCondition = piece instanceof King && endSquare.getId() - startSquare.getId() == 2;
        wasCastling = longCastleCondition || shortCastleCondition;
        if (shortCastleCondition)
            shortCastleHandler();

        if (longCastleCondition)
            longCastleHandler();

        wasPawnPromotion = piece.color ?
                piece instanceof Pawn && piece.getSquare().getId()>=0 && piece.getSquare().getId()<=7 :
                piece instanceof Pawn && piece.getSquare().getId()>=56 && piece.getSquare().getId()<=63;
        if (wasPawnPromotion)
            simulatePawnPromotion();

        moveIndicator++;
        setTurn();
    }

    public void undoMoveHandler(int endSquareId, Square f, int startSquareId) {
        startSquare = getSquares(startSquareId);
        endSquare = f;
        b[startSquareId].setImageResource(0);
        getSquares(startSquareId).setPiece(null);

        boolean wasBlackPieceCaptured = getSquares(endSquare.getId()).getPiece() != null && piece.getColor()
                && !getSquares(endSquare.getId()).getPiece().getColor();
        boolean wasWhitePieceCaptured = getSquares(endSquare.getId()).getPiece() != null && !piece.getColor()
                && getSquares(endSquare.getId()).getPiece().getColor();
        if (wasBlackPieceCaptured)
            deleteCapturedBlackPiece(endSquareId);
        else if (wasWhitePieceCaptured)
            deleteCapturedWhitePiece(endSquareId);

        setPieceOnNewSquare(endSquareId);

        wasPawnPromotion = piece.color ?
                piece instanceof Pawn && piece.getSquare().getId()>=0 && piece.getSquare().getId()<=7 :
                piece instanceof Pawn && piece.getSquare().getId()>=56 && piece.getSquare().getId()<=63;
        if (wasPawnPromotion) {
            Move move = moveList.get(moveList.size() - 1);
            move.setNewPieceAfterPawnPromotion();
        }

        moveIndicator--;
        Turn.disableBlackPieces();
        Turn.disableWhitePieces();
    }

    public void makeMove(int endSquareId, Square f, int startSquareId) {
        startSquare = piece.getSquare();
        endSquare = f;
        b[startSquareId].setImageResource(0);
        getSquares(startSquareId).setPiece(null);
        removeUnnecessaryActionListeners();

        boolean wasBlackPieceCaptured = getSquares(endSquare.getId()).getPiece() != null
                && piece.getColor() && !getSquares(endSquare.getId()).getPiece().getColor();
        boolean wasWhitePieceCaptured = getSquares(endSquare.getId()).getPiece() != null
                && !piece.getColor() && getSquares(endSquare.getId()).getPiece().getColor();
        wasEnPassant = piece instanceof Pawn
                && Arrays.asList(-9, -7, 7, 9).contains(endSquareId - startSquareId)
                && endSquare.getPiece() == null;
        if (wasEnPassant)
            enPassantMoveHandler(endSquareId);
        else if (wasBlackPieceCaptured)
            deleteCapturedBlackPiece(endSquareId);
        else if (wasWhitePieceCaptured)
            deleteCapturedWhitePiece(endSquareId);

        setPieceOnNewSquare(endSquareId);

        boolean longCastleCondition = piece instanceof King && endSquare.getId() - startSquare.getId() == -2;
        boolean shortCastleCondition = piece instanceof King && endSquare.getId() - startSquare.getId() == 2;

        wasCastling = longCastleCondition || shortCastleCondition;
        if (shortCastleCondition)
            shortCastleHandler();

        if (longCastleCondition)
            longCastleHandler();

        wasPawnPromotion = piece.color ?
                piece instanceof Pawn && piece.getSquare().getId()>=0 && piece.getSquare().getId()<=7 :
                piece instanceof Pawn && piece.getSquare().getId()>=56 && piece.getSquare().getId()<=63;
        if (wasPawnPromotion)
            pawnPromotion();

        chessNotation(wasCapture, wasPawnPromotion, ((King) blackPieces.get(12)).isCheck() ||
                ((King) whitePieces.get(12)).isCheck());

        moveIndicator++;

        if (piece instanceof King)
            ((King) piece).setWasMoved(true);

        else if (piece instanceof Rook)
            ((Rook) piece).setWasMoved(true);

        moveList.add(this);
        if(!wasPawnPromotion)
            PGNFormat.generatePgnMoves();

        TextView textView = activity.findViewById(R.id.moves);
        textView.setText(PGNMoveGenerator.toString());

        enPassantPossible = getEnPassantPossibleSquare();
        setTurn();
    }

    public void makeMoveFromPgn(int endSquareId, Square f, int startSquareId, String selectedPieceFromPgn) {
        startSquare = piece.getSquare();
        endSquare = f;
        b[startSquareId].setImageResource(0);
        getSquares(startSquareId).setPiece(null);

        boolean wasBlackPieceCaptured = getSquares(endSquare.getId()).getPiece() != null
                && piece.getColor() && !getSquares(endSquare.getId()).getPiece().getColor();
        boolean wasWhitePieceCaptured = getSquares(endSquare.getId()).getPiece() != null
                && !piece.getColor() && getSquares(endSquare.getId()).getPiece().getColor();
        wasEnPassant = piece instanceof Pawn
                && Arrays.asList(-9, -7, 7, 9).contains(endSquareId - startSquareId)
                && endSquare.getPiece() == null;
        if (wasEnPassant)
            enPassantMoveHandler(endSquareId);
        else if (wasBlackPieceCaptured)
            deleteCapturedBlackPiece(endSquareId);
        else if (wasWhitePieceCaptured)
            deleteCapturedWhitePiece(endSquareId);

        setPieceOnNewSquare(endSquareId);

        boolean longCastleCondition = piece instanceof King && endSquare.getId() - startSquare.getId() == -2;
        boolean shortCastleCondition = piece instanceof King && endSquare.getId() - startSquare.getId() == 2;

        wasCastling = longCastleCondition || shortCastleCondition;
        if (shortCastleCondition)
            shortCastleHandler();

        if (longCastleCondition)
            longCastleHandler();

        wasPawnPromotion = piece.color ?
                piece instanceof Pawn && piece.getSquare().getId()>=0 && piece.getSquare().getId()<=7 :
                piece instanceof Pawn && piece.getSquare().getId()>=56 && piece.getSquare().getId()<=63;
        if (wasPawnPromotion)
            simulatePawnPromotionFromPgn(selectedPieceFromPgn);

        chessNotation(wasCapture, wasPawnPromotion, ((King) blackPieces.get(12)).isCheck() ||
                ((King) whitePieces.get(12)).isCheck());

        moveIndicator++;

        if (piece instanceof King)
            ((King) piece).setWasMoved(true);

        else if (piece instanceof Rook)
            ((Rook) piece).setWasMoved(true);

        moveList.add(this);
        if(!wasPawnPromotion)
            PGNFormat.generatePgnMoves();

        TextView textView = activity.findViewById(R.id.moves);
        textView.setText(PGNMoveGenerator.toString());

        enPassantPossible = getEnPassantPossibleSquare();
        setTurn();
    }

    private void removeUnnecessaryActionListeners() {
        removeCurrentActionListener(piece);
        removePossibleActionListeners(piece.possibleFieldsToMove());
    }

    public void removeCurrentActionListener(Piece p) {
        int start = p.getSquare().getId();
        b[start].setOnClickListener(null);
    }

    public void removePossibleActionListeners(List<Square> possibleSquares) {
        for (Square f : possibleSquares) {
            b[f.getId()].setBackground(MyApplication.getAppContext().getResources().getDrawable( getSquareColor(f.getId())));
            b[f.getId()].setOnClickListener(null);
        }
    }

    private void enPassantMoveHandler(int endId) {
        if (piece.getColor()) {
            capturedPiece = blackPieces.get(getSquares(endId + 8).getPiece().getId());
            blackPieces.set(getSquares(endId + 8).getPiece().getId(), null);
            getSquares(endId + 8).setPiece(null);
            b[endId + 8].setImageResource(0);
        } else {
            capturedPiece = whitePieces.get(getSquares(endId - 8).getPiece().getId());
            whitePieces.set(getSquares(endId - 8).getPiece().getId(), null);
            getSquares(endId - 8).setPiece(null);
            b[endId - 8].setImageResource(0);
        }

        wasEnPassant = true;
        wasCapture = true;
    }

    private void deleteCapturedBlackPiece(int endId) {
        capturedPiece = blackPieces.get(getSquares(endSquare.getId()).getPiece().getId());
        blackPieces.set(getSquares(endSquare.getId()).getPiece().getId(), null);
        getSquares(endSquare.getId()).setPiece(null);
        b[endId].setImageResource(0);
        wasCapture = true;
    }

    private void deleteCapturedWhitePiece(int endId) {
        capturedPiece = whitePieces.get(getSquares(endSquare.getId()).getPiece().getId());
        whitePieces.set(getSquares(endSquare.getId()).getPiece().getId(), null);
        getSquares(endSquare.getId()).setPiece(null);
        b[endId].setImageResource(0);
        wasCapture = true;
    }

    private void setPieceOnNewSquare(int endSquareId) {
        piece.setSquare(getSquares(endSquareId));
        b[endSquareId].setImageResource(piece.getIcon());
        getSquares(endSquareId).setPiece(piece);
    }

    private void shortCastleHandler() {
        resetActionListeners();
        if (piece.getColor()) {
            castledRook = whitePieces.get(15);
            b[63].setImageResource(0);
            getSquares(63).setPiece(null);
            removeCurrentActionListener(whitePieces.get(15));
            whitePieces.get(15).setSquare(getSquares(61));
            b[61].setImageResource(whitePieces.get(15).getIcon());
            getSquares(61).setPiece(whitePieces.get(15));
        } else {
            castledRook = blackPieces.get(15);
            b[7].setImageResource(0);
            getSquares(7).setPiece(null);
            removeCurrentActionListener(blackPieces.get(15));
            blackPieces.get(15).setSquare(getSquares(5));
            b[5].setImageResource(blackPieces.get(15).getIcon());
            getSquares(5).setPiece(blackPieces.get(15));
        }
    }

    private void longCastleHandler() {
        resetActionListeners();
        if (piece.getColor()) {
            castledRook = whitePieces.get(8);
            b[56].setImageResource(0);
            getSquares(56).setPiece(null);
            removeCurrentActionListener(whitePieces.get(8));
            whitePieces.get(8).setSquare(getSquares(59));
            b[59].setImageResource(whitePieces.get(8).getIcon());
            getSquares(59).setPiece(whitePieces.get(8));
        } else {
            castledRook = blackPieces.get(8);
            b[0].setImageResource(0);
            getSquares(0).setPiece(null);
            removeCurrentActionListener(blackPieces.get(8));
            blackPieces.get(8).setSquare(getSquares(3));
            b[3].setImageResource(blackPieces.get(8).getIcon());
            getSquares(3).setPiece(blackPieces.get(8));
        }
    }

    public void pawnPromotion() {
        DialogFragment singleChoiceDialog = new PromotionDialog();
        singleChoiceDialog.setCancelable(false);
        singleChoiceDialog.show(activity.getSupportFragmentManager(), "Single Choice Dialog");
    }

    public void simulatePawnPromotion() {
        int pawnId = piece.getId();
        int endSquareId = endSquare.getId();

        String nextMoveNotation = moveList.get(moveIndicator + 1).notation;
        char lastSign = nextMoveNotation.charAt(nextMoveNotation.length() - 1);

        if(lastSign == '+' || lastSign == '#') {
            lastSign = nextMoveNotation.charAt(nextMoveNotation.length() - 2);
        }
        if(lastSign == 'Q' || lastSign == 'R' || lastSign == 'B' || lastSign == 'N') {
            setSelectedPiece(moveList.get(moveIndicator + 1).selectedPiece);
        }

        String[] piecesNames = Move.activity.getApplicationContext().getResources().getStringArray(R.array.pieces);

        if(selectedPiece.equals(piecesNames[0]))
            promotionHandler(new Queen(piece.getSquare(), piece.color, pawnId, 0), "Q", whiteIcons[11], blackIcons[3]);
        else if(selectedPiece.equals(piecesNames[1]))
            promotionHandler(new Rook(piece.getSquare(), piece.color, pawnId, 0), "R", whiteIcons[8], blackIcons[0]);
        else if(selectedPiece.equals(piecesNames[2]))
            promotionHandler(new Bishop(piece.getSquare(), piece.color, pawnId, 0), "B", whiteIcons[10], blackIcons[2]);
        else
            promotionHandler(new Knight(piece.getSquare(), piece.color, pawnId, 0), "N", whiteIcons[9], blackIcons[1]);

        newPieceAfterPawnPromotion.setSquare(getSquares(endSquareId));
        getSquares(endSquareId).setPiece(newPieceAfterPawnPromotion);
    }

    public void simulatePawnPromotionFromPgn(String selectedPieceFromPgn) {
        int pawnId = piece.getId();
        int endSquareId = endSquare.getId();

        setSelectedPiece(selectedPieceFromPgn);
        String[] piecesNames = Move.activity.getApplicationContext().getResources().getStringArray(R.array.pieces);

        if(selectedPiece.equals(piecesNames[0]))
            promotionHandler(new Queen(piece.getSquare(), piece.color, pawnId, 0), "Q", whiteIcons[11], blackIcons[3]);
        else if(selectedPiece.equals(piecesNames[1]))
            promotionHandler(new Rook(piece.getSquare(), piece.color, pawnId, 0), "R", whiteIcons[8], blackIcons[0]);
        else if(selectedPiece.equals(piecesNames[2]))
            promotionHandler(new Bishop(piece.getSquare(), piece.color, pawnId, 0), "B", whiteIcons[10], blackIcons[2]);
        else
            promotionHandler(new Knight(piece.getSquare(), piece.color, pawnId, 0), "N", whiteIcons[9], blackIcons[1]);

        newPieceAfterPawnPromotion.setSquare(getSquares(endSquareId));
        getSquares(endSquareId).setPiece(newPieceAfterPawnPromotion);
    }

    public void setNewPieceAfterPawnPromotion() {
        int pawnId = piece.getId();
        int endSquareId = endSquare.getId();

        String[] piecesNames = Move.activity.getApplicationContext().getResources().getStringArray(R.array.pieces);

        if(selectedPiece.equals(piecesNames[0]))
            promotionHandler(new Queen(piece.getSquare(), piece.color, pawnId, 0), "Q", whiteIcons[11], blackIcons[3]);
        else if(selectedPiece.equals(piecesNames[1]))
            promotionHandler(new Rook(piece.getSquare(), piece.color, pawnId, 0), "R", whiteIcons[8], blackIcons[0]);
        else if(selectedPiece.equals(piecesNames[2]))
            promotionHandler(new Bishop(piece.getSquare(), piece.color, pawnId, 0), "B", whiteIcons[10], blackIcons[2]);
        else
            promotionHandler(new Knight(piece.getSquare(), piece.color, pawnId, 0), "N", whiteIcons[9], blackIcons[1]);

        Move move = moveList.get(moveList.size() - 1);
        move.setNotation(move.getNotation() + pieceAfterPromotion);
        PGNFormat.generatePgnMoves();

        TextView textView = activity.findViewById(R.id.moves);
        textView.setText(PGNMoveGenerator.toString());

        newPieceAfterPawnPromotion.setSquare(getSquares(endSquareId));
        getSquares(endSquareId).setPiece(newPieceAfterPawnPromotion);
    }

    public void promotionHandler(Piece p, String letter, int whiteIcon, int blackIcon) {
        int pawnId = piece.getId();
        int endSquareId = endSquare.getId();
        newPieceAfterPawnPromotion = p;
        pieceAfterPromotion = letter;

        int icon = piece.color ? whiteIcon : blackIcon;
        b[endSquareId].setImageResource(icon);
        newPieceAfterPawnPromotion.setIcon(icon);

        List<Piece> pieces = piece.color ? whitePieces : blackPieces;
        pieces.set(pawnId, null);
        pieces.set(pawnId, newPieceAfterPawnPromotion);
    }

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

    public Square getEnPassantPossibleSquare() {
        if (piece instanceof Pawn && endSquare.getId() - startSquare.getId() == 16)
            return getSquares(endSquare.getId() - 8);

        if(piece instanceof Pawn && endSquare.getId() - startSquare.getId() == -16)
            return getSquares(endSquare.getId() + 8);

        return null;
    }

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

    public void setSelectedPiece(String selectedPiece) {
        this.selectedPiece = selectedPiece;
    }
}