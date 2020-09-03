package com.thesis.mygames;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Square implements Comparable<Square> {

    private int id;
    private Piece piece;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Square(int id, Piece piece) {
        this.id = id;
        this.piece = piece;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return id == square.id &&
                Objects.equals(piece, square.piece) &&
                Objects.equals(name, square.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, piece, name);
    }

    @Override
    public int compareTo(@NotNull Square o) {
        return Integer.compare(getId(), o.getId());
    }
}
