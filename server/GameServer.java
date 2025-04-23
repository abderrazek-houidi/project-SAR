import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public class GameServer {

    public static void main(String args[]){
        try{
			Registry registry = LocateRegistry.createRegistry(1099);
            GameFactoryImpl factory = new GameFactoryImpl();
            registry.rebind("TicTacToe", factory);

            System.out.println("Serveur pret");
			System.out.println("Attente des invocations...");
        }
        catch (Exception e) {
			System.out.println("Erreur de liaison de l'objet Reverse");
			System.out.println(e.toString());
	    }
    }
}