import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.Properties;
public class GameServer {
   
                
    public static void main(String args[]){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        SecurityManager s=System.getSecurityManager();
        if ( s!= null) {
            System.out.println("Security Manager is set."+s.toString());
        } else {
            System.out.println("Security Manager is NOT set.");
        }
        try{
          
           
            // Start RMI registry
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Serveur : Construction de l'implementation");

            // Retrieve codebase URL
            
            Properties properties = System.getProperties();
            String codebase = properties.getProperty("java.rmi.server.codebase");
            System.out.println("Codebase: " + codebase);

            if (codebase == null) {
                throw new IllegalArgumentException("java.rmi.server.codebase is not set.");
            }

            // Load the class
            Class<?> serverClass = RMIClassLoader.loadClass(codebase, "GameFactoryImpl");
            System.out.println("Class loaded: " + serverClass.getName());

            // Bind the object to the registry
            registry.rebind("Fabrique Tic Tac Toe", (Remote) serverClass.getDeclaredConstructor().newInstance());
            System.out.println("Objet Fabrique lie dans le RMIregistry");
            System.out.println("Serveur pret.");
            System.out.println("Attente des invocations des clients ...");
			
           
        }
        catch (Exception e) {
			System.out.println("Erreur de liaison de l'objet Fabrique");
			System.out.println(e.toString());
	    }
    }
}