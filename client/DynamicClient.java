import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;

public class DynamicClient {
    
    public DynamicClient () throws Exception {
        Properties p = System.getProperties();
        String url = p.getProperty("java.rmi.server.codebase");

        Class<?> ClasseClient = RMIClassLoader.loadClass(url, "GameClientGUI");
        System.out.println("Class loaded: " + ClasseClient.getName());
        
        Constructor<?>[] constructors = ClasseClient.getConstructors();
        
        // Create an instance
        Object guiInstance = constructors[0].newInstance(new Object[]{});
        
        // Make sure it's visible
        Method setVisibleMethod = ClasseClient.getMethod("setVisible", boolean.class);
        setVisibleMethod.invoke(guiInstance, true);
    }

    public static void main(String[] args) {
        System.setSecurityManager(new SecurityManager());
        try {
            new DynamicClient();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace(); // To help debug
        }
    }
}
