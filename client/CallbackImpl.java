package client;

import java.rmi.RemoteException;
import java.rmi.server.*;

public class CallbackImpl extends UnicastRemoteObject implements CallbackInterface{
    private String playerName = "";
    private String symbol = "";
    private boolean myTurn = false;
    
    public CallbackImpl(String playerName,String symbol,boolean myTurn)throws RemoteException
    {
        this.playerName = playerName;
        this.symbol = symbol;
        this.myTurn = myTurn;
    }

    public void updateBoard(String[][] board) throws RemoteException {
        System.out.println("Current board:");
        for (String[] row : board) {
            for (String cell : row) {
                System.out.print(cell.isEmpty() ? "-" : cell);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    public void notifyTurn(boolean isYourTurn) throws RemoteException {
        this.myTurn = isYourTurn;
        System.out.println(isYourTurn ? "Your turn!" : "Opponent's turn...");
    }
    public void notifyError(String message) throws RemoteException {
        System.out.println("Error: " + message);
    }
    public void notifyGameResult(String result) throws RemoteException {
        System.out.println("Game Over: " + result);
    }
}