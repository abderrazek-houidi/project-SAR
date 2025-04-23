import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryInterface{
    private List<GameImpl> games = new ArrayList<>();
    private int maxClients = 10;

    public GameFactoryImpl()throws RemoteException{}

    public synchronized GameImpl playGame(String playerName ,CallbackInterface playerCallback)throws RemoteException{
        if (games.size() * 2 >= maxClients) return null;
        for (GameImpl game : games) {
            if (game.addPlayer(playerName,playerCallback)) {
                System.out.println("Player " + playerName + " joined existing game");
                return game;
            }
        }
        GameImpl newGame = new GameImpl(playerName,playerCallback);
        games.add(newGame);
        System.out.println("Player " + playerName + " created new game");
        return newGame;
    }
}