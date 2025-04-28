import java.rmi.*;
import java.rmi.server.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

public class CallbackImpl extends UnicastRemoteObject implements CallbackInterface {
    private String playerName = "";
    private String symbol = "";
    private boolean myTurn = false;
    private GameClientGUI client;

    public CallbackImpl(String playerName, GameClientGUI client) throws RemoteException {
        super();
        this.playerName = playerName;
        this.client = client;
    }

    public void updateBoard(String[][] board) throws RemoteException {
        client.updateBoard();
        System.out.println("Current board:");
        for (String[] row : board) {
            for (String cell : row) {
                System.out.print(cell.isEmpty() ? "-" : cell);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void notifyTurn(boolean isYourTurn) throws RemoteException {
        this.myTurn = isYourTurn;
        System.out.println(isYourTurn ? "Your turn!" : "Opponent's turn...");
    }

    public void notifyError(String message) throws RemoteException {
        System.out.println("Error: " + message);
    }

    public void notifyGameResult(String result) throws RemoteException {
        System.out.println("Game Over: " + result);
    }

    public boolean restartMessage() throws RemoteException {
        final boolean[] result = new boolean[1]; // Array to store the dialog result

        // Run the dialog on the EDT and wait for the result
        try {
            SwingUtilities.invokeAndWait(() -> {
                int response = showRestartDialog(
                    "<html><center><h3>The other player wants a rematch!</h3></center></html>",
                    "Game Over", true
                );
                result[0] = response == JOptionPane.YES_OPTION;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RemoteException("Error showing restart dialog", e);
        }

        return result[0]; // Return true for Restart, false for Exit
    }

    private int showRestartDialog(String message, String title, boolean restartOption) {
        // Define options for the dialog
        Object[] options = restartOption ? new Object[] { "Restart", "Exit" } : new Object[] { "OK" };

        // Show the dialog with no parent component (null)
        return JOptionPane.showOptionDialog(
            null, // No parent component
            message,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
    }
}