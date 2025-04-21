package client;

import java.rmi.*;

public interface CallbackInterface extends Remote {
    void updateBoard(String[][] board) throws RemoteException;
    void notifyTurn(boolean isYourTurn) throws RemoteException;
    void notifyError(String message) throws RemoteException;
    void notifyGameResult(String result) throws RemoteException;
}