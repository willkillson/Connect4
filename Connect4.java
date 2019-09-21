package core;

import java.util.Scanner;

/**Connect4.java (Game Logic Module)
 *
 * @author Kevin Wilkinson
 * @version 3.0
 *
 */
public class Connect4 implements Connect4Constants{

    /**
     * Board array that contains all player data information. A value of 0 indicates that there is not
     *  a piece currently occupying that space in the 2d array. A value of 1 indicates a player 1 piece,
     *  and a value of 2 indicates a player 2 piece.
     * */
    public int [][]board;

    /**
     * This information will allow us to know if the current player has won. True indicates the current player is
     * a winner, and false indicates that the current player is not a winner.
     */
    public boolean hasCurrentPlayerWon;

    /**
     * A value of 1 indicates player 1 is the current player. A value of 2 indicates player 2 is the current player.
     */
    private int currentPlayer;

    /**
     * Hardcoded value of total rows.
     */
    public final int rows = 6;

    /**
     * Hardcoded value of  total columns.
     */
    public final int columns = 7;

    /**
     * decision will be either p for player vs player game, or c for player vs computer game.
     */
    public char decision;

    /**
     * Initializes the two deminisional game board to all zeros indicating there is no
     * pieces on the board. Then sets the player order, and the current winner to be false.
     */
    public Connect4() {
        this.board = new int[this.rows][this.columns];
        for(int r = 0;r< rows;r++){
            for(int c = 0;c<columns;c++){
                this.board[r][c]= 0;
            }
        }
        setPlayerOrder();
        this.hasCurrentPlayerWon = false;
    }

    /**
     * Sets the next player's turn, which should be called after a movement is validated.
     */
    public void setPlayerNext(){
        switch(this.currentPlayer){
            case PLAYER1:
                this.currentPlayer = PLAYER2;
                break;
            case PLAYER2:
                this.currentPlayer = PLAYER1;
                break;
        }

    }

    /**
     * Validates input of an int.
     * @param pPosition as an int.
     * @return returns true if pPosition is valid
     */
    public boolean validateMove(int pPosition){

        //Check if move is outside the index;
        if(!(pPosition>=1 &&pPosition<=7)){
            return false;
        }
        //Check if there is room to put the piece.
        for(int r = 0;r<this.rows;r++){
            if(this.board[r][pPosition-1]==0){
                return true;
            }
        }
        return false;
    }

    /**
     * Validates and places a piece into the board array.
     * @param pPosition is the position that the piece will go.
     * @return returns true if the method was a success.
     */
    public boolean placePiece(int pPosition){

        if(this.validateMove(pPosition)){
            for(int r = this.rows-1;r>=0;r--){
                if(this.board[r][pPosition-1]==0){
                    this.board[r][pPosition-1]= this.currentPlayer;
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks whether the board is full, which is important to check
     * if there is a cats game.
     * @return returns whether or not there is an open space in the board.
     */
    public boolean isBoardFull(){
        for(int r = 0;r< this.rows;r++){
            for(int c = 0;c<this.columns;c++){
                if(this.board[r][c]==0){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Accessor for current player.
     * @return Returns current player.
     */
    public int getCurrentPlayer(){
        return this.currentPlayer;
    }

    /**
     * Displays prompt for player vs computer or player vs player
     * @return c for computer or p for player
     */
    public char prompt_PvP_or_PvC(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to play against another (p)layer, or a (c)omputer?");
        System.out.println("Enter p or c: ");

        while(true){
            String input = scanner.next();
            if(input.charAt(0)=='p'){
                return input.charAt(0);
            }
            if(input.charAt(0)=='c'){
                return input.charAt(0);
            }
            System.out.println(input.charAt(0)+" is invalid.");
            System.out.println("Enter p or c: ");
        }

    }

    /**
     * Announces the current game's winner.
     */
    public void announceWinner(){
        switch(getCurrentPlayer()){
            case PLAYER1:
                System.out.print("Player 1 ");
                break;
            case PLAYER2:
                System.out.print("Player 2 ");
                break;
        }
        System.out.println("Won the Game");
    }

    /**
     * Sets the current player order. As per the requirements, player 1 will start.
     */
    private void setPlayerOrder(){
        this.currentPlayer = PLAYER1;
    }

    /**
     * Master check winner function. Uses checkHorizontal(),
     * checkVertical(), checkDiagBlToTR(), and checkDiagBrToTl() to indicate
     * whether the current player has won.
     */
    public void checkWinner(){
        checkHorizontal();
        checkVertical();
        checkDiagBlToTr();
        checkDiagBrToTl();
    }

    private void checkHorizontal(){

        for(int r = 0;r< rows;r++){
            for(int c = 0;c<columns;c++){
                if(this.board[r][c]== this.currentPlayer){
                    int total = 0;

                    for(int i = 0;i<4;i++){
                        if(i+c>this.columns-1){//don't want to go past our bounds
                            break;
                        }
                        if(this.board[r][i+c]==this.currentPlayer){
                            total++;
                        }
                    }
                    if(total==4){
                        hasCurrentPlayerWon = true;
                    }
                }
            }
        }
    }

    private void checkVertical(){
        for(int r = 0;r< rows;r++) {
            for (int c = 0; c < columns; c++) {
                if(this.board[r][c]==this.currentPlayer){
                    int total = 0;
                    for(int j = 0;j<4;j++){
                        if(r+j>this.rows-1){//don't want to go past our bounds
                            break;
                        }
                        if(this.board[r+j][c]==this.currentPlayer){
                            total++;
                        }
                    }
                    if(total==4){
                        hasCurrentPlayerWon = true;
                    }
                }
            }
        }

    }

    private void checkDiagBlToTr(){

        for(int r = rows-1;r>=0 ;r--){
            for(int c = 0;c<columns;c++){

                if(this.board[r][c]== this.currentPlayer){
                    int total = 0;

                    for(int i = 0;i<4;i++){
                        if(i+c>this.columns-1||!((r-i)>=0)){//don't want to go past our bounds
                            break;
                        }
                        if(this.board[r-i][c+i]==this.currentPlayer){
                            total++;
                        }
                    }
                    if(total==4){
                        hasCurrentPlayerWon = true;
                    }
                }
            }
        }

    }

    private void checkDiagBrToTl(){
        for(int r = 0;r< rows;r++){
            for(int c = 0;c<columns;c++){
                if(this.board[r][c]== this.currentPlayer){
                    int total = 0;

                    for(int i = 0;i<4;i++){
                        if(i+c>this.columns-1||(i+r)>this.rows-1){//don't want to go past our bounds
                            break;
                        }
                        if(this.board[r+i][c+i]==this.currentPlayer){
                            total++;
                        }
                    }
                    if(total==4){
                        hasCurrentPlayerWon = true;
                    }
                }
            }
        }
    }

}
