package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;


/**
 * Connect4Server.java
 * Handles client sockets and once a game has enough players, passes these onto a separete threads.
 *
 * @author Kevin Wilkinson
 * @version 1.0
 */
public class Connect4Server extends Application implements Connect4Constants{

    /**
     * Tracks the current amount of sessions that are active.
     */
    private int sessionNumber;

    /**
     * GUI programming for the server application.
     * @param primaryStage boiler plate java GUI attribute.
     */
    @Override
    public void start(Stage primaryStage){
        System.out.println("Game Server Started.");
        TextArea log = new TextArea();

        Scene scene = new Scene(new ScrollPane(log), 450, 200);
        primaryStage.setTitle("Connect4 Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(hostPort);
                Platform.runLater(()-> log.appendText(new Date()+": Server started at socket "+hostPort+"\n"));

                //ServerLoop
                while(true){
                    //no players have joined
                    Platform.runLater(()-> log.appendText(new Date()+": Waiting for players to connect to game "+this.sessionNumber+'\n'));
                    //player 1 has joined
                    Socket player1 = serverSocket.accept();
                    Platform.runLater(()->{
                        log.appendText(new Date()+": Player 1 has joined game "+ this.sessionNumber+'\n');
                        log.appendText(new Date()+": Player 1's IP address "+ player1.getInetAddress().getHostAddress()+'\n');
                    });
                    //let player 1 know he is player1
                    new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

                    //player 2 has joined
                    Socket player2 = serverSocket.accept();
                    Platform.runLater(()->{
                        log.appendText(new Date()+": Player 2 has joined game "+ this.sessionNumber+'\n');
                        log.appendText(new Date()+": Player 2's IP address "+ player2.getInetAddress().getHostAddress()+'\n');
                    });

                    //let player 2 know he is player2
                    new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);

                    //display Session
                    Platform.runLater(() -> log.appendText(new Date() + ": game " + this.sessionNumber++ + "has started."+'\n'));

                    //launch the thread
                    new Thread(new HandleASession(player1, player2)).start();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * HandleASession is a nested class inside the server, which implements
     * runnable that starts a sequential process(thread).     *
     */
    class HandleASession implements Runnable, Connect4Constants{

        private Socket player1;
        private Socket player2;
        private Connect4 game;
        private DataInputStream fromPlayer1;
        private DataInputStream fromPlayer2;
        private DataOutputStream toPlayer1;
        private DataOutputStream toPlayer2;

        public HandleASession(Socket player1, Socket player2){
            this.player1 = player1;
            this.player2 = player2;
            this.game = new Connect4();
        }

        public void run(){
            try{
                fromPlayer1 = new DataInputStream(player1.getInputStream());
                fromPlayer2 = new DataInputStream(player2.getInputStream());
                toPlayer1 = new DataOutputStream(player1.getOutputStream());
                toPlayer2 = new DataOutputStream(player2.getOutputStream());
                //let player 1 know its time!
                toPlayer1.writeInt(7);

                //main game server loop
                while(true){
                    //for player 1
                    System.out.println("game.getCurrentPlayer() "+game.getCurrentPlayer());
                    if(game.getCurrentPlayer()==PLAYER1){
                        int playerMove = fromPlayer1.readInt();
                        //todo remove me
                        System.out.println("FromClient1: "+playerMove);
                        if(game.placePiece(playerMove)){
                            toPlayer1.writeInt(playerMove);       //let player 1 have our movement
                            System.out.println("toPlayer1.writeInt(playerMove)");
                            toPlayer2.writeInt(playerMove);       //let player 2 have our movement

                            game.checkWinner();
                            if(game.hasCurrentPlayerWon){

                                toPlayer1.writeInt(PLAYER1_WON);
                                toPlayer2.writeInt(PLAYER1_WON);
                                break;
                            }
                            else if(game.isBoardFull()){
                                toPlayer1.writeInt(DRAW);
                                toPlayer2.writeInt(DRAW);
                                break;
                            }
                            //let next player play
                            game.setPlayerNext();
                            System.out.println("game.setPlayerNext()");
                            toPlayer1.writeInt(CONTINUE);
                            toPlayer2.writeInt(CONTINUE);


                        }
                        else{
                            toPlayer1.writeInt(INVALID_MOVE);
                        }
                    }
                    //for player 2
                    if(game.getCurrentPlayer()==PLAYER2){
                        int playerMove = fromPlayer2.readInt();
                        //todo remove me
                        System.out.println("FromClient2: "+playerMove);
                        if(game.placePiece(playerMove)){
                            toPlayer1.writeInt(playerMove);       //let player 1 have our movement
                            toPlayer2.writeInt(playerMove);       //let player 2 have our movement
                            game.checkWinner();
                            if(game.hasCurrentPlayerWon){
                                toPlayer1.writeInt(PLAYER2_WON);
                                toPlayer2.writeInt(PLAYER2_WON);
                                break;
                            }
                            else if(game.isBoardFull()){
                                toPlayer1.writeInt(DRAW);
                                toPlayer2.writeInt(DRAW);
                                break;
                            }
                            //let next player play
                            game.setPlayerNext();
                            toPlayer1.writeInt(CONTINUE);
                            toPlayer2.writeInt(CONTINUE);
                        }
                        else{
                            toPlayer2.writeInt(INVALID_MOVE);
                        }
                    }

                }


            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    /**Entry into the server process.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }

}
