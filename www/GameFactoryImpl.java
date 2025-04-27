import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryInterface {
    
    private List<GameImpl> games = new ArrayList<>();
    private static int nbClient = 0;
    private static final int maxClients = 10;

    public GameFactoryImpl() throws RemoteException {
        super();
    }

    // Un joueur veut jouer
    public synchronized GameInterface playGame(String playerName, CallbackInterface playerCallback) throws RemoteException {
        if (nbClient >= maxClients) {
            throw new RemoteException("Server full: maximum number of players reached.");
        }

        // Essayer de rejoindre une partie existante
        for (GameImpl game : games) {
            if (game.addPlayer(playerName, playerCallback)) {
                nbClient++;
                System.out.println("Player " + playerName + " joined an existing game.");
                return game;
            }
        }

        // Sinon, créer une nouvelle partie
        GameImpl newGame = new GameImpl(playerName, playerCallback);
        games.add(newGame);
        nbClient++;
        System.out.println("Player " + playerName + " created a new game.");
        return newGame;
    }

    // Un joueur quitte la partie
    public synchronized void close(String playerName)throws RemoteException  {
        if (nbClient > 0) {
            nbClient--;
            System.out.println("Player " + playerName + " left. Remaining players: " + nbClient);
        }
    }

    // Optionnel : méthode pour voir combien de joueurs sont connectés
    public synchronized int getConnectedPlayers() throws RemoteException  {
        return nbClient; 
    }
}
