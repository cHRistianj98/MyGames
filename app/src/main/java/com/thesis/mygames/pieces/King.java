package com.thesis.mygames.pieces;

import android.widget.ImageButton;

import com.thesis.mygames.gameutils.Chessboard;
import com.thesis.mygames.gameutils.Piece;
import com.thesis.mygames.gameutils.Square;

import java.util.*;

public class King extends Piece {

    private List<Integer> toRemove = new ArrayList<>();
    private Set<Square> attackedSquares = new HashSet<>();
    private List<Integer> validId = new ArrayList<>();

    private boolean wasMoved = false;

    public King(boolean color){
        super(color);
    }

    public King(Square square, boolean color, int icon) {
        super(square, color, icon);
    }

    public King(Square square, boolean color, int id, int icon) {
        super(square, color, id, icon);
    }

    public List<Integer> getEveryPossibilities() {
        List<Integer> validId = new ArrayList<>();
        int id = this.square.getId();
        Integer []toCheck = new Integer[]{ id - 9, id - 8, id - 7,
                                           id - 1,         id + 1,
                                           id + 7, id + 8, id + 9 };
        switch(id) {
            case 0:
                validId.addAll(Arrays.asList(1, 8, 9));
                break;
            case 7:
                validId.addAll(Arrays.asList(6, 14, 15));
                break;
            case 56:
                validId.addAll(Arrays.asList(48, 49, 57));
                break;
            case 63:
                validId.addAll(Arrays.asList(54, 55, 62));
                break;

            default:
                if (Arrays.asList( 15, 23, 31, 39, 47, 55 ).contains(id)) {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64)
                            validId.add(i);
                    }
                    validId.removeAll(Arrays.asList(id - 7, id + 1, id + 9));
                }
                else if(Arrays.asList( 8, 16, 24, 32, 40, 48 ).contains(id)) {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64)
                            validId.add(i);
                    }
                    validId.removeAll(Arrays.asList(id - 9, id - 1, id + 7));
                }

                else {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64)
                            validId.add(i);
                    }
                }
                break;
        }

        return validId;
    }

    public void checkValidFields(List<Square> possibleSquares) {

        validId.clear();
        validId = getEveryPossibilities();
        attackedFieldsFun();
        checkIfMyPieceOnValidFields();
        checkIfCheckOnPossibleFields();

        for (Integer i : validId)
            possibleSquares.add(Chessboard.getSquares(i));
        validId.clear();
        attackedSquares.clear();

    }

    @Override
    public List<Integer> getMyPiecesBlocked() {
        List<Integer> toRemove = new ArrayList<>();
        List<Integer> validId;

        validId = getEveryPossibilities();
        for (int i = 0; i < validId.size(); i++) {
            if(Chessboard.getSquares(validId.get(i)).getPiece() != null && Chessboard.getSquares(validId.get(i)).getPiece().getColor() == this.getColor())
                toRemove.add(validId.get(i));
        }

        Collections.sort(toRemove);

        return toRemove;
    }

    public void checkIfCheckOnPossibleFields() {
        for (int i = 0; i < validId.size(); i++) {
            if(attackedSquares.contains(Chessboard.getSquares(validId.get(i)))) {
                toRemove.add(validId.get(i));}

            if(Chessboard.getSquares(validId.get(i)).getPiece() != null && Chessboard.getSquares(validId.get(i)).getPiece().getColor() != this.getColor()) {
               // Piece p = Chessboard.getSquares(validId.get(i)).getPiece();
                if(this.getColor()) {
                    for(Piece pi : Chessboard.blackPieces) {
                        if(pi == null)
                            continue;
                        List<Integer> tmpPieces = pi.getMyPiecesBlocked();
                        if(tmpPieces.contains(validId.get(i))) {
                            toRemove.add(validId.get(i));
                        }
                    }
                } else {
                    for(Piece pi : Chessboard.whitePieces) {
                        if(pi == null)
                            continue;
                        List<Integer> tmpPieces = pi.getMyPiecesBlocked();
                        if(tmpPieces.contains(validId.get(i))) {
                            toRemove.add(validId.get(i));
                        }
                    }
                }
            }
        }
        validId.removeAll(toRemove);
        toRemove.clear();
    }

    public void checkIfMyPieceOnValidFields() {
        for (int i = 0; i < validId.size(); i++) {
            if(Chessboard.getSquares(validId.get(i)).getPiece() != null && Chessboard.getSquares(validId.get(i)).getPiece().getColor() == this.getColor())
                toRemove.add(validId.get(i));
        }
    }

    public boolean isCheck() {
        attackedFieldsFun();
        if(attackedSquares.contains(this.square)) {
            attackedSquares.clear();
            return true;
        } else {
            attackedSquares.clear();
            return false;
        }
    }

    public boolean isCheckmate() {
        List<Square> possibilities = new ArrayList<>();

        if(this.getColor()) {
            for (Piece p : Chessboard.whitePieces) {
                if(p == null)
                    continue;

                possibilities.addAll(p.possibleFieldsToMoveCheck());
                if(!possibilities.isEmpty())
                    return false;
            }
        } else {
            for (Piece p : Chessboard.blackPieces) {
                if(p == null)
                    continue;

                possibilities.addAll(p.possibleFieldsToMoveCheck());
                if(!possibilities.isEmpty())
                    return false;
            }
        }

        return true;
    }

//    public boolean isStalemate() {
//        List<Square> possibilities = new ArrayList<>();
//
//        if(Chessboard.gameState == GameState.CHECK || Chessboard.gameState == GameState.CHECKMATE) {
//            System.out.println(possibilities);
//            return false;
//        }
//
//        if(this.getColor() && Chessboard.gameState == GameState.NORMAL) {
//            for (Piece p : Chessboard.whitePieces) {
//                if(p == null)
//                    continue;
//                possibilities.addAll(p.possibleFieldsToMove());
//                if(possibilities.size() > 0) {
//                    return false;
//                }
//            }
//        }
//
//        if(!this.getColor() && Chessboard.gameState == GameState.NORMAL){
//            for (Piece p : Chessboard.blackPieces) {
//                if(p == null)
//                    continue;
//                possibilities.addAll(p.possibleFieldsToMove());
//                if(possibilities.size() > 0)
//                    return false;
//            }
//        }
//        return true;
//    }

    public void attackedFieldsFun() {
        if(this.getColor()) {
            for(Piece p : Chessboard.blackPieces) {
                if(p == null)
                    continue;
                if(p instanceof Pawn) {
                    attackedSquares.addAll(((Pawn)p).possibleTakingToMove());
                    continue;
                }
                if(p instanceof King) {
                    List<Integer> possibilities = ((King)p).getEveryPossibilities();
                    List<Square> possibleSquares = new ArrayList<>();
                    for(Integer i : possibilities) {
                        possibleSquares.add(Chessboard.getSquares(i));
                    }
                    attackedSquares.addAll(possibleSquares);
                    continue;
                }
                attackedSquares.addAll(p.possibleFieldsToMove());
            }

        } else {
            for(Piece p : Chessboard.whitePieces) {
                if(p == null)
                    continue;
                if(p instanceof Pawn) {
                    attackedSquares.addAll(((Pawn)p).possibleTakingToMove());
                    continue;
                }
                if(p instanceof King) {
                    List<Integer> possibilities = ((King)p).getEveryPossibilities();
                    List<Square> possibleSquares = new ArrayList<>();
                    for(Integer i : possibilities) {
                        possibleSquares.add(Chessboard.getSquares(i));
                    }
                    attackedSquares.addAll(possibleSquares);
                    continue;
                }
                attackedSquares.addAll(p.possibleFieldsToMove());
            }

        }

    }

    @Override
    public List<Square> possibleFieldsToMove() {
        List<Square> possibleSquares = new ArrayList<>();
        checkValidFields(possibleSquares);
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

        if(isShortCastlePossible()) {
            possibleSquareDuringCheck.add(Chessboard.getSquares(square.getId() + 2));
        }

        if(isLongCastlePossible()) {
            possibleSquareDuringCheck.add(Chessboard.getSquares(square.getId() - 2));
        }

        return possibleSquareDuringCheck;
    }

    public boolean isShortCastlePossible() {
        if(wasMoved ||
                isCheck() ||
                isSquareAttacked(Chessboard.getSquares(getSquare().getId() + 1)) ||
                Chessboard.getSquares(getSquare().getId() + 1).getPiece() != null ||
                isSquareAttacked(Chessboard.getSquares(getSquare().getId() + 2)) ||
                Chessboard.getSquares(getSquare().getId() + 2).getPiece() != null
                )
            return false;

        if(this.getColor()) {
            if(((Rook)Chessboard.whitePieces.get(15)).isWasMoved()) {
                return false;
            }
        }
        if(!this.getColor()) {
            return !((Rook) Chessboard.blackPieces.get(15)).isWasMoved();
        }

        return true;
    }

    public boolean isLongCastlePossible() {
        if (
                wasMoved ||
                isCheck() ||
                isSquareAttacked(Chessboard.getSquares(getSquare().getId() - 1)) ||
                Chessboard.getSquares(getSquare().getId() - 1).getPiece() != null ||
                isSquareAttacked(Chessboard.getSquares(getSquare().getId() - 2)) ||
                Chessboard.getSquares(getSquare().getId() - 2).getPiece() != null
        )
            return false;

        if(this.getColor()) {
            if(((Rook)Chessboard.whitePieces.get(8)).isWasMoved()) {
                return false;
            }
        }
        if(!this.getColor()) {
            return !((Rook) Chessboard.blackPieces.get(8)).isWasMoved();
        }

        return true;
    }

    public boolean isSquareAttacked(Square s) {
        attackedFieldsFun();
        return attackedSquares.contains(s);
    }

    public boolean isWasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
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