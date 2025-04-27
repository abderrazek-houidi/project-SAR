import java.rmi.*;

public interface GameInterface extends Remote{
    public Boolean addPlayer(String playerName,CallbackInterface playerCallback)throws RemoteException;
    public boolean isValidMove(int row, int col) throws RemoteException;
    public void executeMove(int row, int col, String symbol)throws RemoteException;
    public String checkWinner() throws RemoteException;
    public Boolean MakeMove(String playerName, int row, int col)throws RemoteException;
    public Boolean isMyTurn(String playerName)throws RemoteException;
    public String[] getPlayers()throws RemoteException;
    public String getPlayers(int i)throws RemoteException;
    public String[][] getBoard() throws RemoteException;
    public boolean isGameOver() throws RemoteException;
    public String getWinner() throws RemoteException;


}
