package test;

import core.Connect4;

import core.Connect4ComputerPlayer;
import org.junit.Assert;

class Connect4ComputerPlayerTest {

    Connect4 game = null;
    Connect4ComputerPlayer com1 = null;
    Connect4ComputerPlayer com2 = null;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        game = new Connect4();
        com1 = new Connect4ComputerPlayer();
        com2 = new Connect4ComputerPlayer();
    }

    @org.junit.jupiter.api.Test
    void getBasicDecision() {
        for(int i = 0;i<21;i++){
            Assert.assertTrue(game.placePiece(com1.getBasicDecision(game)));
            Assert.assertTrue(game.placePiece(com2.getBasicDecision(game)));
        }
        Assert.assertFalse(game.placePiece(com1.getBasicDecision(game)));
        Assert.assertFalse(game.placePiece(com2.getBasicDecision(game)));

}
}