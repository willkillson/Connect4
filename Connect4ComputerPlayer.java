package core;

/**
 * Connect4ComputerPlayer.java
 * Tracks and gives a computer decision
 * @author Kevin Wilkinson
 * @version 1.0
 */
public class Connect4ComputerPlayer {

    private Connect4 game;

    /**
     * Constructor for Connect4ComputerPlayer class.
     *
     */
    public Connect4ComputerPlayer() {

        this.game = new Connect4();
    }

    /**
     * Gets basic decision based on the board input.
     *
     * @param game Must receive  input so a decision can be made.
     * @return Best decision [1,7]
     */
    public int getBasicDecision(Connect4 game) {
        this.game.board = game.board;

        int decision = 0;

        for (int c = 0; c < this.game.board[0].length; c++) {
            int currentTally = 0;
            for (int r = 0; r < this.game.board.length; r++) {
                if (this.game.board[r][c] == 0) {
                    currentTally++;
                }
            }
            if (currentTally >= 1) {
                decision = c;
            }
        }

        return decision + 1;

    }

}