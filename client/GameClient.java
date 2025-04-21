package client;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

public class GameClient{
    public static void main(String args[]){
        Registry reg = LocateRegistry.getRegistry("localhost",1099);
        GameFactoryImpl factory = (GameFactoryImpl) reg.lookup("TicTacToe");
        Scanner scanner = new Scanner(System.in);
        String playerName;
        System.out.print("give your player name");
        playerName = scanner.nextLine();
        CallbackImpl callback = new CallbackImpl(playerName);
        factory.playGame(playerName);
    }
}