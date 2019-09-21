package core;

import ui.Connect4GUI;
import ui.Connect4TextConsole;
import java.util.Scanner;


/**
 * Connect4Client.java
 *
 * This class contains our java main method, which then launches different parts of the program
 * depending on what the user decides.
 *
 * Launches Connect4GUI().main();
 *  or
 * Launches Connect4TextConsole().ruin();
 *
 * @author Kevin Wilkinson
 * @version 1.0
 */
public class  Connect4Client{

    /**
     * Main method for program entry.
     * @param args unused
     */
    public static void main(String[] args){

        System.out.println("Would you like a (G)UI or (c)onsole-based UI");
        System.out.print("Enter g, or c: ");
        Scanner scanner = new Scanner(System.in);

        while(true){
            String choiceUI = scanner.next();
            if(choiceUI.charAt(0)=='g'){
                new Connect4GUI().main();
                break;
            }
            if(choiceUI.charAt(0)=='c'){
                new Connect4TextConsole().run();

                break;
            }
            System.out.println(choiceUI.charAt(0)+" is invalid.");
            System.out.println("Enter g or c: ");
        }
    }
}
