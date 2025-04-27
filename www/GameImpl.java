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

    public GameImpl(String playerName,CallbackInterface playerCallback) throws RemoteException{
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = "";
        Players[0] = playerName;
        playersCallback[0] = playerCallback;
        Players[1] = "";
        symbols[0] = "X";
        symbols[1] = "0";
        movesMade = 0;
        currentPlayer = 0;
        gameEnded = false;
    }
    public synchronized Boolean addPlayer(String playerName,CallbackInterface playerCallback) throws RemoteException{
        if (Players[1].equals(""))
        {
            Players[1] = playerName;
            playersCallback[1] = playerCallback;
            notifyAllPlayers();
            return true;
        }
        return false;
    }
    public synchronized Boolean isMyTurn(String playerName)throws RemoteException{
        return(playerName.equals(Players[currentPlayer]));
    }
    public synchronized boolean isValidMove(int row, int col) throws RemoteException{
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col].isEmpty();
    }
    public synchronized void executeMove(int row, int col, String symbol) {
        board[row][col] = symbol;
        movesMade++;
    }
    public synchronized String checkWinner() throws RemoteException{
        // Check rows, columns, diagonals
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]))
                return board[i][0];
            if (!board[0][i].isEmpty() && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]))
                return board[0][i];
        }
        if (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
            return board[0][0];
        if (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]))
            return board[0][2];
        return movesMade == 9 ? "Draw" : "";
    }
    public synchronized Boolean MakeMove(String playerName, int row, int col)throws RemoteException{
        if(gameEnded || !playerName.equals(Players[currentPlayer]) || !isValidMove(row, col)){
            if(playerName.equals(Players[currentPlayer])){
                playersCallback[1-currentPlayer].notifyError("Invalid move! Try again.");
            }
            else{
                playersCallback[currentPlayer].notifyError("Invalid move! Try again.");
            }
            return false;
        }
        executeMove(row, col, symbols[currentPlayer]);
        notifyAllPlayers();
        String res = checkWinner();
        if (!res.isEmpty()){
            playersCallback[0].notifyGameResult(res.equals("Draw") ? "Draw" : res + " wins!");
            playersCallback[1].notifyGameResult(res.equals("Draw") ? "Draw" : res + " wins!");
           gameEnded = true;
        }
        else{
            currentPlayer = 1 - currentPlayer;
            notifyAllPlayers();
        }
        return true;
    }
    public String[] getPlayers()throws RemoteException{
        return Players;
    }
    public String getPlayers(int i)throws RemoteException{
        return Players[i];
    }
    private void notifyAllPlayers() throws RemoteException {
        for (int i = 0; i<2; i++) {
            CallbackInterface callback = playersCallback[i];
            callback.updateBoard(board);
            callback.notifyTurn(i == currentPlayer);
        }
    }
}