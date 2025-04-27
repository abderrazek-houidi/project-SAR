import java.lang.reflect.Constructor;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;

public class DynamicClient {
    
    public DynamicClient (String[] args) throws Exception{
        Properties p = System.getProperties();
        String url = p.getProperty("java.rmi.server.codebase");

        Class<?> ClasseClient = RMIClassLoader.loadClass(url , "GameClientGUI");
        System.out.println("Class loaded: " + ClasseClient.getName());
        Constructor<?> [] C = ClasseClient.getConstructors();
        
        C[0].newInstance(new Object[]{args});
    }


    public static void main(String [] args) {
        System.setSecurityManager(new SecurityManager());
        try {
            new DynamicClient(args);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        
    }
}
