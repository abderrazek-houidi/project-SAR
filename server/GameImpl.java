import java.rmi.*;
import java.rmi.server.*;

public class GameImpl extends UnicastRemoteObject implements GameInterface {
    private String[] Players = new String[2];
    private CallbackInterface[] playersCallback = new CallbackInterface[2];
    private String[] symbols = new String[2];
    private String[][] board = new String[3][3];
    private int movesMade = 0;
    private int currentPlayer = 0;
    private boolean gameEnded = false;
    private String winner = "";

    public GameImpl(String playerName, CallbackInterface playerCallback) throws RemoteException {
        super(); // Important for RMI
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
        Players[0] = playerName;
        playersCallback[0] = playerCallback;
        Players[1] = "";
        symbols[0] = "X";
        symbols[1] = "O"; // Changed from "0" to "O" for clarity
    }

    @Override
    public synchronized Boolean addPlayer(String playerName, CallbackInterface playerCallback) throws RemoteException {
        if (Players[1] == null || Players[1].isEmpty()) {
            Players[1] = playerName;
            playersCallback[1] = playerCallback;
            notifyAllPlayers();
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean isValidMove(int row, int col) throws RemoteException {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col].isEmpty();
    }

    @Override
    public synchronized void executeMove(int row, int col, String symbol) throws RemoteException {
        if (isValidMove(row, col)) {
            board[row][col] = symbol;
            movesMade++;
        }
    }

    @Override
    public synchronized String checkWinner() throws RemoteException {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                return board[i][0];
            }
        }
        
        // Check columns
        for (int j = 0; j < 3; j++) {
            if (!board[0][j].isEmpty() && board[0][j].equals(board[1][j]) && board[1][j].equals(board[2][j])) {
                return board[0][j];
            }
        }
        
        // Check diagonals
        if (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            return board[0][0];
        }
        if (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            return board[0][2];
        }
        
        return movesMade == 9 ? "Draw" : "";
    }

    @Override
    public synchronized Boolean MakeMove(String playerName, int row, int col) throws RemoteException {
        if (gameEnded || !playerName.equals(Players[currentPlayer]) ){
            return false;
        }

        if (!isValidMove(row, col)) {
            playersCallback[currentPlayer].notifyError("Invalid move!");
            return false;
        }

        executeMove(row, col, symbols[currentPlayer]);
        String result = checkWinner();
        
        if (!result.isEmpty()) {
            gameEnded = true;
            winner = result;
            String message = result.equals("Draw") ? "Draw" : result + " wins!";
            playersCallback[0].notifyGameResult(message);
            playersCallback[1].notifyGameResult(message);
        } else {
            currentPlayer = 1 - currentPlayer;
            notifyAllPlayers();
        }
        
        return true;
    }

    @Override
    public synchronized Boolean isMyTurn(String playerName) throws RemoteException {
        return !gameEnded && playerName.equals(Players[currentPlayer]);
    }

    @Override
    public String[] getPlayers() throws RemoteException {
        return Players.clone();
    }

    @Override
    public String getPlayers(int i) throws RemoteException {
        if (i >= 0 && i < Players.length) {
            return Players[i];
        }
        return "";
    }

    @Override
    public String[][] getBoard() throws RemoteException {
        String[][] copy = new String[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 3);
        }
        return copy;
    }

    @Override
    public boolean isGameOver() throws RemoteException {
        return gameEnded;
    }

    @Override
    public String getWinner() throws RemoteException {
        return winner;
    }

    private void notifyAllPlayers() throws RemoteException {
        for (int i = 0; i < 2; i++) {
            if (playersCallback[i] != null) {
                playersCallback[i].updateBoard(getBoard());
                playersCallback[i].notifyTurn(isMyTurn(Players[i]));
            }
        }
    }
}