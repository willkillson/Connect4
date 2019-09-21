package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import core.Connect4Constants;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;


/**Connect4TextConsole.java  (Console-based UI to test the game)
 *
 * @author Kevin Wilkinson
 * @version 2.0
 *
 */

public class Connect4TextConsole implements Connect4Constants{

    private int playerNumber;
    private boolean myTurn = false;
    private boolean continueToPlay = true;
    private boolean waiting = true;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private Connect4 game;
    private int currentDecision;

    /**
     * Default class constructor.
     */
    public Connect4TextConsole(){
        game = new Connect4();
    }

    /**
     * Main method for Connect4TextConsole
     */
    public void run(){
        game.decision = this.game.prompt_PvP_or_PvC();
        if(game.decision == 'p'){
            gameLoopPvP();
        }
        if(game.decision =='c'){
            gameLoopPvC();
        }
    }

    private void gameLoopPvC(){

        System.out.println("Player verses Computer, begin Game.");
        Scanner sc = new Scanner(System.in);
        Connect4ComputerPlayer computer = new Connect4ComputerPlayer();
        String choice = null;
        try{
            while(this.game.hasCurrentPlayerWon==false&&this.game.isBoardFull()==false)
            {
                displayGameBoard();
                displayPlayerTurn();
                do{
                    
                    switch (game.getCurrentPlayer()){
                        case PLAYER2:
                            choice = Integer.toString(computer.getBasicDecision(game));
                            break;
                        case PLAYER1:
                            while(true){
                                choice = sc.next();
                                System.out.println(choice);
                                if(choice.charAt(0)>='1'&&choice.charAt(0)<='7'){
                                    break;
                                }
                                System.out.println("Invalid choice: "+ choice);
                                System.out.println("Enter [1,7]");
                                break;
                            }
                    }
                }while(this.game.placePiece(Character.getNumericValue(choice.charAt(0)))!=true);
                
                this.game.checkWinner();
                if(this.game.isBoardFull()==true||this.game.hasCurrentPlayerWon==true){
                    break;
                }
                this.game.setPlayerNext();
            }
            displayGameBoard();
            if(this.game.isBoardFull()==true&&this.game.hasCurrentPlayerWon==false){
                System.out.println("Draw detected.");
            }
            else{
                game.announceWinner();
            }

        }
        catch (InputMismatchException ex2){
            System.out.println("InputMismatchException: Invalid input type.");
        }
    }

    private void gameLoopPvP(){
        System.out.println("Player verses Player, begin Game.");

        try {
            Socket socket = new Socket(hostIP, hostPort);
            this.fromServer = new DataInputStream(socket.getInputStream());
            this.toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e){
                e.printStackTrace();
        }

        //This thread polls the keyboard.
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while(true){
                String input = scanner.next();
                int intInput = Character.getNumericValue(input.charAt(0));
                if(intInput>=1&&intInput<=7){
                    currentDecision = intInput;
                    waiting= false;
                }
            }

        }).start();

        //This thread handles the game.
        new Thread(()->{

            try{
                this.myTurn = false;
                this.playerNumber = fromServer.readInt();
                switch(this.playerNumber){
                    case PLAYER1:
                        System.out.println(new Date()+" You are player 1.");
                        System.out.println(new Date()+" Waiting for player 2.");
                        fromServer.readInt();//Start
                        this.myTurn = true;
                        currentDecision = -1;//default
                        break;
                    case PLAYER2:
                        System.out.println(new Date()+": You are player 2.");
                        System.out.println(new Date()+": Waiting for player 1 to move.");
                        currentDecision = -1;//default
                        break;
                }
                while(continueToPlay){
                    if(this.myTurn){
                        displayGameBoard();
                        System.out.println(new Date()+": Your turn.");
                        currentDecision = -1;
                        waitForPlayerAction();
                        if(currentDecision!=-1){
                            toServer.writeInt(this.currentDecision);
                            serverUpdate();//receive our movement
                        }
                    }
                    else{
                        displayGameBoard();
                        System.out.println(new Date()+": Waiting for opponent.");
                        serverUpdate();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }).start();


    }

    private void waitForPlayerAction() throws InterruptedException{
        while(waiting){
            Thread.sleep(100);
        }
        waiting = true;
    }

    private void serverUpdate() throws IOException {



        int serverData = fromServer.readInt();
        processMove(serverData);//deals with the movement
        if(serverData!=INVALID_MOVE){
            serverData = fromServer.readInt();//deals with the game condition
        }

        switch(serverData){
            case INVALID_MOVE:
                break;
            case PLAYER1_WON:
                continueToPlay = false;
                if(this.playerNumber==PLAYER1){
                    System.out.println("I have won!");
                }
                if(this.playerNumber==PLAYER2){
                    System.out.println("Player 1 has won!");
                    processMove(serverData);
                }
                displayGameBoard();
                break;
            case PLAYER2_WON:
                continueToPlay = false;
                if(this.playerNumber==PLAYER1){
                    System.out.println("Player 2 has won!");
                    processMove(serverData);
                }
                if(this.playerNumber==PLAYER2){
                    System.out.println("I have won!");
                }
                displayGameBoard();
                break;
            case DRAW:
                continueToPlay = false;
                System.out.println("Draw!");
                if(this.playerNumber==Connect4Constants.PLAYER2){
                    processMove(serverData);
                }
                displayGameBoard();
                break;
        }

    }

    private void displayGameBoard(){
        for(int r = 0;r<this.game.rows;r++){
            for(int c = 0;c<this.game.columns;c++){

                System.out.print('|');
                switch(this.game.board[r][c]){
                    case 0:
                        System.out.print(' ');
                        break;
                    case Connect4Constants.PLAYER1:
                        System.out.print('X');
                        break;
                    case Connect4Constants.PLAYER2:
                        System.out.print('O');
                        break;
                }

            }
            System.out.print('|');
            System.out.println();
        }


    }

    private void displayPlayerTurn(){
        switch (game.getCurrentPlayer()){
            case PLAYER1:
                System.out.println ("Player 1's turn.");
                break;
            case PLAYER2:
                System.out.println ("Player 2's turn.");
                break;
        }
    }

    private void processMove(int data) throws IOException {
        if(data==-1){
            //we have a bad move
        }
        else{
            //we have a good move
            game.placePiece(data);
            game.setPlayerNext();

        }
        if(game.getCurrentPlayer()==this.playerNumber){
            this.myTurn=true;
        }
        else
        {
            this.myTurn=false;
        }
    }

}