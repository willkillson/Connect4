package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import core.Connect4Constants;
import javafx.application.Application;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.*;
import java.util.Date;


/**
 * Provides graphical user interface for Connect4 game.
 */
public class Connect4GUI extends Application implements Connect4Constants {

    private int playerNumber;
    private boolean myTurn = false;
    private boolean continueToPlay = true;
    private boolean waiting = true;
    private Connect4 game;
    private int currentDecision;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private Circle[][] circles;
    private Connect4ComputerPlayer computer = new Connect4ComputerPlayer();
    private int radius = 40;
    private int x = 0;
    private int y = 0;


    /**
     * Main method for Connect4GUI.
     * @param primaryStage is the primary stage.
     */
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage){
        game = new Connect4();
        circles = new Circle[this.game.rows][this.game.columns];
        game.decision = this.game.prompt_PvP_or_PvC();

        if(game.decision=='p'){
            playerVsPlayer(primaryStage);
        }
        if(game.decision=='c'){
            playerVsComputer(primaryStage);
        }
    }

    private void playerVsPlayer(Stage primaryStage){

        for(int r = 0;r< game.rows;r++){
            for(int c = 0;c<game.columns;c++){
                circles[r][c] = new Circle(x + radius, y + radius, radius);
                circles[r][c].setFill(Color.DIMGRAY);
                x += radius * 2;
            }
            y+=radius*2;
            x = 0;
        }

        Pane pane = new Pane();
        Scene scene = new Scene(pane,280*2,240*2);
        scene.setFill(Color.YELLOW);

        for(int r = 0;r< game.rows;r++){
            for(int c = 0;c<game.columns;c++){
            pane.getChildren().add(circles[r][c]);
            }
        }

        pane.setOnMouseClicked(e->{
            double xPos = e.getX();
            int x = ((int)xPos/80)+1;
            currentDecision = x;
            waiting = false;
        });

        primaryStage.setTitle("Connect4 Player vs Player");
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();          //ConnectToServer

    }

    private void updateGamePvP_singlePlayer(double xPos){
        game.checkWinner();
        int x = (int)xPos/80;
        x++;
        try{

            if(game.hasCurrentPlayerWon==false){
                if(game.placePiece(x)){
                    game.checkWinner();
                    if(game.hasCurrentPlayerWon==false){
                        game.setPlayerNext();
                        System.out.println("Player "+game.getCurrentPlayer()+"'s turn.");
                    }
                }
                else
                {
                    System.out.println("Invalid movement.");
                }
            }
            if(game.hasCurrentPlayerWon){
                System.out.println("Player "+game.getCurrentPlayer() + " has won!");
            }
            if(game.hasCurrentPlayerWon==false&&game.isBoardFull()){
                System.out.println("Draw.");
            }

        }
        catch (Exception e){
            System.out.println("Invalid movement: "+e.getMessage());
        }

    }

    private void playerVsComputer(Stage primaryStage){

        int radius = 40;
        int x = 0;
        int y = 0;

        for(int r = 0;r< game.rows;r++){
            for(int c = 0;c<game.columns;c++){
                circles[r][c] = new Circle(x + radius, y + radius, radius);
                circles[r][c].setFill(Color.DIMGRAY);
                x += radius * 2;
            }
            y+=radius*2;
            x = 0;
        }

        Pane pane = new Pane();
        Scene scene = new Scene(pane,280*2,240*2);
        scene.setFill(Color.YELLOW);

        for(int r = 0;r< game.rows;r++){
            for(int c = 0;c<game.columns;c++){
                pane.getChildren().add(circles[r][c]);
            }
        }

        pane.setOnMouseClicked(e->{

            double xPos = e.getX();

            updateGamePvC(xPos);
            updateGraphics();

        });

        primaryStage.setTitle("Connect4 Player vs Computer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateGamePvC(double xPos){
        game.checkWinner();
        int x = (int)xPos/80;
        x++;
        if(game.hasCurrentPlayerWon==false){
            if(game.placePiece(x)){
                game.checkWinner();
                if(game.hasCurrentPlayerWon==false){
                    game.setPlayerNext();
                    //get computer input

                    int computerMove = computer.getBasicDecision(game);
                    System.out.println("Computer play's "+computerMove);
                    game.placePiece(computerMove);
                    game.checkWinner();
                    if(game.hasCurrentPlayerWon==false){
                        game.setPlayerNext();
                    }


                }
            }
        }
        if(game.hasCurrentPlayerWon){
            if(game.getCurrentPlayer()==Connect4Constants.PLAYER1){
                System.out.println("Player "+ 1  + " has won!");
            }
            if(game.getCurrentPlayer()==Connect4Constants.PLAYER2){
                System.out.println("The computer has won!");
            }
        }
        else if(game.hasCurrentPlayerWon==false&&game.isBoardFull()){
            System.out.println("Draw.");
        }
        else
        {
            if(game.getCurrentPlayer()==PLAYER1){
                System.out.println("Player "+1+"'s turn.");
            }

        }

    }

    private void updateGraphics(){
        for(int r = 0;r< game.rows;r++){
            for(int c = 0;c<game.columns;c++){
                switch(this.game.board[r][c]){
                    case 0:
                        circles[r][c].setFill(Color.DIMGREY);
                        break;
                    case PLAYER1:
                        circles[r][c].setFill(Color.RED);
                        break;
                    case PLAYER2:
                        circles[r][c].setFill(Color.BLACK);
                        break;
                }
            }
        }
    }

    private void connectToServer(){
        try{
            Socket socket = new Socket(hostIP, hostPort);
            //open communication channels
            this.fromServer = new DataInputStream(socket.getInputStream());
            this.toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //Thread to handle the game
        new Thread(()->{
            try{
                this.myTurn = false;
                this.playerNumber = fromServer.readInt();
                switch(this.playerNumber){
                    case PLAYER1:
                        System.out.println(new Date()+": You are player 1.");
                        System.out.println(new Date()+": Waiting for player 2.");
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
                        System.out.println(new Date()+": Your turn.");
                        currentDecision = -1;
                        waitForPlayerAction();
                        if(currentDecision!=-1){
                            toServer.writeInt(this.currentDecision);
                            serverUpdate();//receive our movement
                        }
                    }
                    else{
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

    private void serverUpdate() throws IOException{

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
                break;
            case DRAW:
                continueToPlay = false;
                System.out.println("Draw!");
                if(this.playerNumber==Connect4Constants.PLAYER2){
                    processMove(serverData);
                }
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
        updateGraphics();
    }

    public void main(){
        Application.launch();
    }

}