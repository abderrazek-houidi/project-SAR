package server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryInterface{
    private List<Game> games = new ArrayList<>();
    private int maxClients = 10;

    public GameFactoryImpl()throws RemoteException{}

    public Game playGame(String playerName)throws RemoteException{
        if (games.size() * 2 >= maxClients) return null;
        for (Game game : games) {
            if (game.addPlayer(playerName)) {
                return game;
            }
        }
        Game newGame = new Game(playerName);
        games.add(newGame);
        return newGame;
    }
}