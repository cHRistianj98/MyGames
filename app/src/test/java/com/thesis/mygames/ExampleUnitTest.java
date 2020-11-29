package com.thesis.mygames;

import com.thesis.mygames.game.Piece;
import com.thesis.mygames.pieces.Pawn;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String s = "ala";
        Class c = s.getClass();
        System.out.println(c.getName());
        assertEquals(4, 2 + 2);
    }
}