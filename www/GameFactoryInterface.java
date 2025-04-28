import java.rmi.*;

public interface GameFactoryInterface extends Remote {
    public GameInterface playGame(String playerName,CallbackInterface playerCallback)throws RemoteException;

}