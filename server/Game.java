package server;

public class Game {
    private String[] Players = new String[2];
    private String[][] board = new String[3][3];
    private int movesMade = 0;
    private int currentPlayer = 0;
    private boolean gameEnded = false;

    public Game(String playerName)
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = "";
        Players[0] = playerName;
    }
    public Boolean addPlayer(String playerName)
    {
        if (Players[1] == "")
        {
            Players[1] = playerName;
            return true;
        }
        return false;
    }
    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col].isEmpty();
    }
    public boolean executeMove(int row, int col, String symbol) {
        if (!isValidMove(row, col)) return false;
        board[row][col] = symbol;
        movesMade++;
        return true;
    }
    public String checkWinner() {
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
    public Boolean MakeMove(String playerName, int row, int col){
        if(gameEnded || playerName != Players[currentPlayer] || !isValidMove(row, col)){
            return false;
        }
        String res = checkWinner();
        if (!res.isEmpty()){
           gameEnded = true;
           
        }
        else{
            currentPlayer++;
        }
        return true;
    }
}