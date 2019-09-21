package test;

import core.Connect4;
import core.Connect4Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Connect4Test implements Connect4Constants {

    Connect4 game;
    @BeforeEach
    void setUp() {
        game = new Connect4();
    }

    @Test
    void setPlayerNext() {
        assertEquals(PLAYER1,game.getCurrentPlayer());
        game.setPlayerNext();
        assertEquals(PLAYER2,game.getCurrentPlayer());
        game.setPlayerNext();
        assertEquals(PLAYER1,game.getCurrentPlayer());
        game.setPlayerNext();
        assertEquals(PLAYER2,game.getCurrentPlayer());


    }

    @Test
    void validateMove() {
        for(int i = -99;i<1;i++){
            assertFalse(game.placePiece(i));
        }
        for(int i = 8;i<100;i++){
            assertFalse(game.placePiece(i));
        }
        for(int i = 1;i<=7;i++){
            assertTrue(game.placePiece(i));
        }

    }

    @Test
    void placePiece() {
        for(int i = 0;i<game.rows;i++){
            game.placePiece(1);
        }
        assertFalse(game.placePiece(1));

        for(int i = 0;i<game.rows;i++){
            assertTrue(game.placePiece(2));
        }

        for(int i = 0;i<game.rows;i++){
            game.placePiece(3);
        }
        assertFalse(game.placePiece(3));

        for(int i = 0;i<game.rows;i++){
            assertTrue(game.placePiece(4));
        }

        for(int i = 0;i<game.rows;i++){
            game.placePiece(5);
        }
        assertFalse(game.placePiece(5));

        for(int i = 0;i<game.rows;i++){
            assertTrue(game.placePiece(6));
        }
        for(int i = 0;i<game.rows;i++){
            game.placePiece(7);
        }
        assertFalse(game.placePiece(7));




    }

    @Test
    void isBoardFull() {
        assertFalse(game.isBoardFull());
        for(int i = 0;i<game.rows;i++){
            game.placePiece(1);
            assertFalse(game.isBoardFull());
        }
        for(int i = 0;i<game.rows;i++){
            game.placePiece(2);
            assertFalse(game.isBoardFull());
        }
        for(int i = 0;i<game.rows;i++){
            game.placePiece(3);
            assertFalse(game.isBoardFull());
        }
        for(int i = 0;i<game.rows;i++){
            game.placePiece(4);
            assertFalse(game.isBoardFull());
        }
        for(int i = 0;i<game.rows;i++){
            game.placePiece(5);
            assertFalse(game.isBoardFull());
        }
        for(int i = 0;i<game.rows;i++){
            game.placePiece(6);
            assertFalse(game.isBoardFull());
        }
        for(int i = 0;i<game.rows-1;i++){
            game.placePiece(7);
            assertFalse(game.isBoardFull());
        }
        game.placePiece(7);
        assertTrue(game.isBoardFull());

    }

    @Test
    void getCurrentPlayer() {
        assertEquals(game.getCurrentPlayer(),PLAYER1);
        game.setPlayerNext();
        assertEquals(game.getCurrentPlayer(),PLAYER2);
        game.setPlayerNext();
        assertEquals(game.getCurrentPlayer(),PLAYER1);
        game.setPlayerNext();
        assertEquals(game.getCurrentPlayer(),PLAYER2);
    }

    @Test
    void checkWinner() {
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(1);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(2);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(3);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(4);
        game.checkWinner();
        assertTrue(game.hasCurrentPlayerWon);

        game = new Connect4();
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(1);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(1);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(1);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(1);
        game.checkWinner();
        assertTrue(game.hasCurrentPlayerWon);

        game = new Connect4();
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(2);
        game.placePiece(3);
        game.placePiece(3);
        game.placePiece(4);
        game.placePiece(4);
        game.placePiece(4);
        assertFalse(game.hasCurrentPlayerWon);
        game.setPlayerNext();
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(1);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(2);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(3);
        assertFalse(game.hasCurrentPlayerWon);
        game.placePiece(4);
        game.checkWinner();
        assertTrue(game.hasCurrentPlayerWon);


    }
}