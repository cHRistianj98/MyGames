package com.thesis.mygames;

import java.util.List;

public abstract class Piece {
    public Piece(boolean color) {
        this.color = color;
    }
//    public Piece(Square square, boolean color, Icon icon) {
//        this.square = square;
//        this.color = color;
//        this.icon = icon;
//    }
//    public Piece(Square square, boolean color, int id, Icon icon) {
//        this.square = square;
//        this.color = color;
//        this.id = id;
//        this.icon = icon;
//    }

    protected Square square;
    protected boolean color;
    protected int id;
    //    protected Icon icon;
//
//    public Icon getIcon() {
//        return icon;
//    }
//    public void setIcon(Icon icon) {
//        this.icon = icon;
//    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public boolean getColor() {
        return color;
    }
    public Square getSquare() {
        return square;
    }
    public void setSquare(Square square) {
        this.square = square;
    }
    //    @Override
//    public String toString() {
//        return "Piece{" +
//                "field=" + square +
//                ", color=" + color +
//                ", id=" + id +
//                ", icon=" + icon +
//                '}';
//    }
//
//    public abstract List<Square> action(JButton []b);
    public abstract List<Square> possibleFieldsToMove();
    public abstract List<Square> possibleFieldsToMoveCheck();
    public abstract List<Integer> getMyPiecesBlocked();
}