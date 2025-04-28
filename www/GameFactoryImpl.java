import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryInterface {
    
    private List<GameImpl> games = new ArrayList<>();
    public GameFactoryImpl() throws RemoteException {
        super();
    }

    // Un joueur veut jouer
    public synchronized GameInterface playGame(String playerName, CallbackInterface playerCallback) throws RemoteException {
       

        // Essayer de rejoindre une partie existante
        for (GameImpl game : games) {
            if (game.addPlayer(playerName, playerCallback)) {
           
                System.out.println("Player " + playerName + " joined an existing game.");
                return game;
            }
        }

        if (games.size()*2 <10) {
            
        
        GameImpl newGame = new GameImpl(playerName, playerCallback);
        games.add(newGame);
      
        System.out.println("Player " + playerName + " created a new game.");
        return newGame;
        }
        throw new RemoteException("Server full: maximum number of players reached.");
    }

    // Un joueur quitte la partie
   

    // Optionnel : méthode pour voir combien de joueurs sont connectés
  
}
