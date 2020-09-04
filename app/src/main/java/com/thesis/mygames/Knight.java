package com.thesis.mygames;

import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Knight extends Piece {

    public Knight(boolean color) {
        super(color);
    }

    public Knight(Square square, boolean color, int icon) {
        super(square, color, icon);
    }

    public Knight(Square square, boolean color, int id, int icon) {
        super(square, color, id, icon);
    }

    @Override
    public List<Integer> getMyPiecesBlocked() {
        List<Integer> toRemove = new ArrayList<>();
        List<Integer> validId = getValidId();

        for (Integer i : validId) {
            if(Chessboard.getSquares(i).getPiece() != null && Chessboard.getSquares(i).getPiece().getColor() == this.getColor())
                toRemove.add(i);
        }

        return toRemove;
    }

    public List<Integer> getValidId() {

        int id = this.square.getId();
        Integer []toCheck = new Integer[]{     id - 17,   id - 15,
                id - 10,           id - 6,
                id + 6,            id + 10,
                id + 15,   id + 17 };

        List<Integer> validId = new ArrayList<>();

        switch(id) {
            case 0:
                validId.addAll(Arrays.asList(10, 17));
                break;
            case 1:
                validId.addAll(Arrays.asList(11, 18, 16));
                break;
            case 6:
                validId.addAll(Arrays.asList(12, 21, 23));
                break;
            case 7:
                validId.addAll(Arrays.asList(13, 22));
                break;
            case 8:
                validId.addAll(Arrays.asList(2, 18, 25));
                break;
            case 9:
                validId.addAll(Arrays.asList(3, 19, 26, 24));
                break;
            case 14:
                validId.addAll(Arrays.asList(4, 20, 29, 30));
                break;
            case 15:
                validId.addAll(Arrays.asList(5, 21, 30));
                break;
            case 48:
                validId.addAll(Arrays.asList(33, 42, 58));
                break;
            case 49:
                validId.addAll(Arrays.asList(32, 34, 43, 59));
                break;
            case 54:
                validId.addAll(Arrays.asList(37, 39, 44, 60));
                break;
            case 55:
                validId.addAll(Arrays.asList(38, 45, 61));
                break;
            case 56:
                validId.addAll(Arrays.asList(41, 50));
                break;
            case 57:
                validId.addAll(Arrays.asList(40, 42, 51));
                break;
            case 62:
                validId.addAll(Arrays.asList(45, 47, 52));
                break;
            case 63:
                validId.addAll(Arrays.asList(46, 53));
                break;
            default:
                if (Arrays.asList(16, 24, 32, 40).contains(id)) {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64) {
                            validId.add(i);
                        }
                    }
                    validId.removeAll(Arrays.asList(id - 17, id - 10, id + 6, id + 15));
                }

                else if (Arrays.asList(23, 31, 39, 47).contains(id)) {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64) {
                            validId.add(i);
                        }
                    }
                    validId.removeAll(Arrays.asList(id - 15, id - 6, id + 10, id + 17));
                }

                else if (Arrays.asList(17, 25, 33, 41).contains(id)) {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64) {
                            validId.add(i);
                        }
                    }
                    validId.removeAll(Arrays.asList(id - 10, id + 6));
                }

                else if (Arrays.asList(22, 30, 38, 46).contains(id)) {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64) {
                            validId.add(i);
                        }
                    }
                    validId.removeAll(Arrays.asList(id - 6, id + 10));
                }

                else {
                    for (Integer i : toCheck) {
                        if(i >= 0 && i < 64) {
                            validId.add(i);
                        }
                    }
                }
                break;
        }

        return validId;
    }

    @Override
    public List<Square> possibleFieldsToMove(){
        List<Square> possibleSquares = new ArrayList<>();
        List<Integer> validId = getValidId();
        for (Integer i : validId) {
            if(Chessboard.getSquares(i).getPiece() != null && Chessboard.getSquares(i).getPiece().getColor() == this.getColor())
                continue;
            possibleSquares.add(Chessboard.getSquares(i));
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