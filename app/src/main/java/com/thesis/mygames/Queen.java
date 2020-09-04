package com.thesis.mygames;

import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.thesis.mygames.Chessboard.getSquares;

public class Queen extends Piece{

    public Queen(boolean color){
        super(color);
    }

    public Queen(Square square, boolean color, int icon) {
        super(square, color, icon);
    }

    public Queen(Square square, boolean color, int id, int icon) {
        super(square, color, id, icon);
    }

    @Override
    public List<Square> possibleFieldsToMove(){
        List<Square> possibleSquares = new ArrayList<>();

        getPossibleHorizontalSquaresToRight(possibleSquares);
        getPossibleHorizontalSquaresToLeft(possibleSquares);
        getPossibleVerticalSquaresToUp(possibleSquares);
        getPossibleVerticalSquaresToDown(possibleSquares);
        getPossibleSquaresOnDiagonals(possibleSquares);

        Collections.sort(possibleSquares);
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

    private void getPossibleSquaresOnDiagonals(List<Square> possibleSquares) {
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
    public List<Integer> getMyPiecesBlocked() {
        List<Integer> toRemove = new ArrayList<>();

        int currField = this.square.getId();

        for (int i = currField + 1; i < 64; i++) {
            if(currField % 8 == 7)
                break;
            if(Chessboard.getSquares(currField + 1).getPiece() != null && Chessboard.getSquares(currField+1).getPiece().getColor() != this.getColor())
                break;
            if(Chessboard.getSquares(currField + 1).getPiece() != null && Chessboard.getSquares(currField+1).getPiece().getColor() == this.getColor()) {
                toRemove.add(currField+1);
                break;
            }
            if(currField != 62 && Chessboard.getSquares(i+1).getPiece() != null && Chessboard.getSquares(i+1).getPiece().getColor() != this.getColor())
                break;
            if(currField != 62 && Chessboard.getSquares(i+1).getPiece() != null && Chessboard.getSquares(i+1).getPiece().getColor() == this.getColor()) {
                toRemove.add(i+1);
                break;
            }
            if((currField + 1) % 8 == 7)
                break;
            if((i+1) % 8 == 7)
                break;
        }

        for (int i = currField - 1; i >= 0; i--) {
            if(currField % 8 == 0)
                break;
            if(Chessboard.getSquares(currField - 1).getPiece() != null && Chessboard.getSquares(currField-1).getPiece().getColor() != this.getColor())
                break;
            if(Chessboard.getSquares(currField - 1).getPiece() != null && Chessboard.getSquares(currField-1).getPiece().getColor() == this.getColor()) {
                toRemove.add(currField-1);
                break;
            }
            if(currField != 1 && Chessboard.getSquares(i-1).getPiece() != null && Chessboard.getSquares(i-1).getPiece().getColor() != this.getColor())
                break;
            if(currField != 1 && Chessboard.getSquares(i-1).getPiece() != null && Chessboard.getSquares(i-1).getPiece().getColor() == this.getColor()) {
                toRemove.add(i-1);
                break;
            }
            if((currField - 1) % 8 == 0)
                break;
            if((i-1) % 8 == 0)
                break;
        }

        for (int i = currField - 8; i >= 0 ; i = i - 8) {
            if(Chessboard.getSquares(i).getPiece() != null && Chessboard.getSquares(i).getPiece().getColor() != this.getColor())
                break;
            if(Chessboard.getSquares(i).getPiece() != null && Chessboard.getSquares(i).getPiece().getColor() == this.getColor()) {
                toRemove.add(i);
                break;
            }
        }

        for (int i = currField + 8; i < 64 ; i = i + 8) {
            if(Chessboard.getSquares(i).getPiece() != null && Chessboard.getSquares(i).getPiece().getColor() != this.getColor())
                break;
            if(Chessboard.getSquares(i).getPiece() != null && Chessboard.getSquares(i).getPiece().getColor() == this.getColor()) {
                toRemove.add(Chessboard.getSquares(i).getId());
                break;
            }
        }

        Integer[] borderId = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7,
                15, 23, 31, 39, 47, 55,
                56, 57, 58, 59, 60, 61, 62, 63,
                8, 16, 24, 32, 40, 48};

        Integer[] leftBorderId = new Integer[] { 0, 8, 16, 24, 32, 40, 48, 56 };
        Integer[] rightBorderId = new Integer[] { 7, 15, 23, 31, 39, 47, 55, 63};

        List<Integer> list = Arrays.asList(borderId);
        List<Integer> leftList = Arrays.asList(leftBorderId);
        List<Integer> rightList = Arrays.asList(rightBorderId);

        currField = this.square.getId();
        do {
            if(leftList.contains(currField))
                break;
            currField -= 9;
            if(currField < 0 || currField > 63)
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color != Chessboard.getSquares(currField).getPiece().getColor())
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color == Chessboard.getSquares(currField).getPiece().getColor()) {
                toRemove.add(currField);
                break;
            }
            if(list.contains(currField))
                break;
        } while (currField < 64);

        currField = this.square.getId();

        do {
            if(rightList.contains(currField))
                break;
            currField += 9;
            if(currField < 0 || currField > 63)
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color != Chessboard.getSquares(currField).getPiece().getColor())
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color == Chessboard.getSquares(currField).getPiece().getColor()) {
                toRemove.add(currField);
                break;
            }
            if(list.contains(currField))
                break;
        } while (currField < 64);

        currField = this.square.getId();

        do {
            if(rightList.contains(currField))
                break;
            currField -= 7;
            if(currField < 0 || currField > 63)
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color != Chessboard.getSquares(currField).getPiece().getColor())
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color == Chessboard.getSquares(currField).getPiece().getColor()) {
                toRemove.add(currField);
                break;
            }
            if(list.contains(currField))
                break;
        } while (currField < 64);

        currField = this.square.getId();

        do {
            if(leftList.contains(currField))
                break;
            currField += 7;
            if(currField < 0 || currField > 63)
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color != Chessboard.getSquares(currField).getPiece().getColor())
                break;
            if(Chessboard.getSquares(currField).getPiece() != null && this.color == Chessboard.getSquares(currField).getPiece().getColor()) {
                toRemove.add(currField);
                break;
            }
            if(list.contains(currField))
                break;
        } while (currField < 64);
        Collections.sort(toRemove);
        return toRemove;
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