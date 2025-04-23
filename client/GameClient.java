import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

public class GameClient{
    public static void main(String args[]){
        try {
            Registry reg = LocateRegistry.getRegistry("localhost",1099);
            GameFactoryInterface factory = (GameFactoryInterface)reg.lookup("TicTacToe");
            Scanner scanner = new Scanner(System.in);
            String playerName;
            System.out.print("give your player name: ");
            playerName = scanner.nextLine();
            CallbackImpl callback = new CallbackImpl(playerName);
            GameInterface game =factory.playGame(playerName,callback);
            System.out.println(game.getPlayers());
            while (true) {
                if (game.isMyTurn(playerName)) {
                    System.out.print("Enter row (0-2) and column (0-2): ");
                    int row = scanner.nextInt();
                    int col = scanner.nextInt();
                    try {
                        game.MakeMove(playerName, row, col);
                    } catch (RemoteException e) {
                        System.err.println("Error making move: " + e.getMessage());
                    }
                }
                try {
                    Thread.sleep(100); // Avoid busy-waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}