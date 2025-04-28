import java.rmi.*;

public interface CallbackInterface extends Remote {
    public void updateBoard(String[][] board) throws RemoteException;
    public void notifyTurn(boolean isYourTurn) throws RemoteException;
    public void notifyError(String message) throws RemoteException;
    public void notifyGameResult(String result) throws RemoteException;
    public boolean restartMessage() throws RemoteException;
    public void notifyOpponentLeft(String message) throws RemoteException;
}