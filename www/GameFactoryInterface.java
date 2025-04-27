import java.rmi.*;

public interface GameFactoryInterface extends Remote {
    public GameInterface playGame(String playerName,CallbackInterface playerCallback)throws RemoteException;
    public void close(String playerName)throws RemoteException;
    public int getConnectedPlayers() throws RemoteException ;
}