import javax.swing.*;
import java.rmi.*;
import java.rmi.server.*;
import java.lang.reflect.InvocationTargetException;

public class CallbackImpl extends UnicastRemoteObject implements CallbackInterface {
    private String playerName;
    private String symbol = "";
    private boolean myTurn = false;
    private GameClientGUI client;

    public CallbackImpl(String playerName, GameClientGUI client) throws RemoteException {
        super();
        this.playerName = playerName;
        this.client = client;
    }

    @Override
    public void updateBoard(String[][] board) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            try {
                client.updateBoard();
            } catch (RemoteException e) {
                client.showMaterialDialog("Error updating board: " + e.getMessage(), "Update Error", true);
            }
        });
    }

    @Override
    public void notifyTurn(boolean isYourTurn) throws RemoteException {
        this.myTurn = isYourTurn;
        SwingUtilities.invokeLater(() -> {
            try {
                client.updateBoard();
            } catch (RemoteException e) {
                client.showMaterialDialog("Error updating turn: " + e.getMessage(), "Turn Error", true);
            }
        });
    }

    @Override
    public void notifyError(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            client.showMaterialDialog("Error: " + message, "Error", true);
        });
    }

    @Override
    public void notifyGameResult(String result) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            client.handleGameEnd(result);
        });
    }

    @Override
    public boolean restartMessage() throws RemoteException {
        final boolean[] result = new boolean[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                int response = showRestartDialog(
                    "<html><center><h3>The other player wants a rematch!</h3></center></html>",
                    "Game Over",
                    true
                );
                result[0] = response == JOptionPane.YES_OPTION;
                
                if (result[0]) {
                    client.removeRestartButton();
                } 
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RemoteException("Error showing restart dialog", e);
        }
        return result[0];
    }

    @Override
    public void notifyOpponentLeft(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            client.showMaterialDialog(message, "Opponent Left", true);
            // *** Redirection to launcher ***
            client.resetToInitialState();
        });
    }

    private int showRestartDialog(String message, String title, boolean restartOption) {
        Object[] options = restartOption ? new Object[]{"Restart", "Exit"} : new Object[]{"OK"};
        return JOptionPane.showOptionDialog(
            client,
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