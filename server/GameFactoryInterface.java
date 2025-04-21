package server;

import java.rmi.*;

public interface GameFactoryInterface extends Remote {
    public Game playGame(String playerName)throws RemoteException;
}