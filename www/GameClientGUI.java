import java.rmi.*;
import java.rmi.registry.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class GameClientGUI extends JFrame {
    private GameInterface game;
    private String playerName;
    
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;
    private JPanel boardPanel;
    private JButton restartButton;
    private JPanel headerPanel;
    // Material Design color palette
    private final Color PRIMARY_COLOR = Color.decode("#3F51B5"); // Indigo 500
    private final Color PRIMARY_DARK = Color.decode("#303F9F"); // Indigo 700
    private final Color SECONDARY_COLOR = Color.decode("#FF4081"); // Pink A200
    private final Color BACKGROUND_COLOR = Color.decode("#FAFAFA"); // Light background
    private final Color TEXT_PRIMARY = Color.decode("#212121"); // Dark text
    private final Color TEXT_SECONDARY = Color.decode("#757575"); // Secondary text
    private final Color CARD_COLOR = Color.WHITE;
    private final Color DISABLED_COLOR = Color.decode("#EEEEEE"); // Light gray
    
    // Material Design shadows
    private final Border RAISED_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 0, 0, 20)), 
        BorderFactory.createEmptyBorder(8, 8, 8, 8)
    );
    private final Border PRESSED_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 0, 0, 30)), 
        BorderFactory.createEmptyBorder(8, 8, 8, 8)
    );
    
    // Fonts
    private final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 28);
    private final Font STATUS_FONT = new Font("Roboto", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Roboto", Font.BOLD, 60);

    public GameClientGUI() {
        initializeGUI();
        connectToServer();
    }

    private void initializeGUI() {
        setTitle("Tic Tac Toe - RMI Client");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLocationRelativeTo(null);
        
        // Header panel with Material Design app bar
        headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        
        JLabel titleLabel = new JLabel("TIC TAC TOE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

         restartButton = new JButton("Restart");
    restartButton.setFont(new Font("Arial", Font.BOLD, 18));
    restartButton.setPreferredSize(new Dimension(200, 50));
    restartButton.setBackground(new Color(0x4CAF50)); // Green color for the button
    restartButton.setForeground(Color.WHITE);
    restartButton.setFocusPainted(false);
    restartButton.addActionListener(e -> {
        try {
            game.Restart( playerName);
            updateBoard();
        } catch (RemoteException e1) {
            
            e1.printStackTrace();
        }
    });
    
        // Game board with Material Design card
        boardPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create a card panel to hold the board for shadow effect
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(BACKGROUND_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(new Color(0, 0, 0, 10))
        ));
        cardPanel.add(boardPanel, BorderLayout.CENTER);
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = createMaterialButton();
                final int row = i, col = j;
                buttons[i][j].addActionListener(e -> {
                    try {
                        makeMove(row, col);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                });
                boardPanel.add(buttons[i][j]);
            }
        }
        add(cardPanel, BorderLayout.CENTER);

        // Status bar with Material Design
        statusLabel = new JLabel("Connecting to server...", SwingConstants.CENTER);
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(0, 0, 0, 10)),
            new EmptyBorder(15, 10, 15, 10)
        ));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JButton createMaterialButton() {
        JButton button = new JButton("");
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBackground(CARD_COLOR);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(RAISED_BORDER);
        button.setPreferredSize(new Dimension(100, 100));
        button.setOpaque(true);
        button.setFocusable(false); // Remove the focus outline from buttons
        
        // Add Material Design button effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBorder(PRESSED_BORDER);
                    button.setBackground(TEXT_PRIMARY);
                    button.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBorder(RAISED_BORDER);
                    button.setBackground(CARD_COLOR);
                    button.setForeground(TEXT_PRIMARY);
                }
            }
        });
        
        return button;
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Registry reg = LocateRegistry.getRegistry("localhost", 1099);
                GameFactoryInterface factory = (GameFactoryInterface) reg.lookup("Fabrique Tic Tac Toe");

                int connectedPlayers = factory.getConnectedPlayers();
                if (connectedPlayers >= 10) {
                    SwingUtilities.invokeLater(() -> {
                        showMaterialDialog("Server is full (10 players connected). Try again later.", "Server Full", true);
                        dispose();
                    });
                    return;
                }

                String name = showMaterialInputDialog("Enter your player name:", "Player Name");
                if (name == null || name.trim().isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        showMaterialDialog("Player name cannot be empty!", "Invalid Name", true);
                        dispose();
                    });
                    return;
                }
                playerName = name.trim();

                CallbackImpl callback = new CallbackImpl(playerName,this);
                game = factory.playGame(playerName, callback);

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Waiting for game to start...");
                    statusLabel.setForeground(TEXT_SECONDARY);
                });
                updateBoard();

                // Start game state refresher
                new Timer(500, e -> {
                    
                        try {
                            updateBoard();
                            if (game.isGameOver()) {
                                handleGameEnd();
                                headerPanel.add(restartButton, BorderLayout.EAST);
                                ((Timer) e.getSource()).stop();
                            } 
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                            SwingUtilities.invokeLater(() -> {
                                statusLabel.setText("Error communicating with server.");
                                statusLabel.setForeground(SECONDARY_COLOR);
                            });
                        }
                }).start();

            } catch (ConnectException ce) {
                SwingUtilities.invokeLater(() -> {
                    showMaterialDialog("Could not connect to the server. Ensure it is running.", "Connection Error", true);
                    dispose();
                });
                ce.printStackTrace();
            } catch (NotBoundException | RemoteException e) {
                SwingUtilities.invokeLater(() -> {
                    showMaterialDialog("Error looking up remote object: " + e.getMessage(), "RMI Error", true);
                    dispose();
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private String showMaterialInputDialog(String message, String title) {
        // Create a custom panel with Material Design
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel(message);
        label.setFont(STATUS_FONT);
        label.setForeground(TEXT_PRIMARY);
        
        JTextField textField = new JTextField(20);
        textField.setFont(STATUS_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, TEXT_SECONDARY),
            BorderFactory.createEmptyBorder(0, 0, 5, 0)
        ));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        
        int result = JOptionPane.showOptionDialog(
            this, 
            panel, 
            title, 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE, 
            null, 
            null, 
            null
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        }
        return null;
    }
    private int showRestartDialog(String message, String title, boolean restartOption) {
        Object[] options = restartOption ? new Object[] { "Restart", "Exit" } : new Object[] { "OK" };
        return JOptionPane.showOptionDialog(this, message, title, JOptionPane.DEFAULT_OPTION, 
                                            JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }
    private void showMaterialDialog(String message, String title, boolean isError) {
        JOptionPane pane = new JOptionPane(
            "<html><div style='width:200px;'>" + message + "</div></html>",
            isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
        
        JDialog dialog = pane.createDialog(this, title);
        dialog.setVisible(true);
    }

    private void handleGameEnd() {
        try {
            String winner = game.getWinner();
            System.out.println(winner);
            
    
            SwingUtilities.invokeLater(() -> {
                String message;
                Color messageColor;
                if (winner.equals("Draw")) {
                    message = "Game ended in a draw!";
                    messageColor = TEXT_PRIMARY;
                } else if (winner.equals(playerName)) {
                    message = "Congratulations, " + playerName + "! You won!";
                    messageColor = PRIMARY_DARK;
                } else {
                    message = winner + " won the game! Better luck next time!";
                    messageColor = SECONDARY_COLOR;
                }
    
                statusLabel.setText(message);
                statusLabel.setForeground(messageColor);
    
                // Show Material Design dialog for game over
                showMaterialDialog("<html><center><h3>" + message + "</h3></center></html>", 
                                 "Game Over", 
                                 false);
    
                // Disable all buttons with visual feedback
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        buttons[i][j].setEnabled(false);
                        buttons[i][j].setBackground(DISABLED_COLOR);
                    }
                }
               
    
                // Exit or continue based on the server's decision
               
            });
            
           
        } catch (RemoteException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Error getting game result.");
                statusLabel.setForeground(SECONDARY_COLOR);
            });
        }
    }
    
    private void makeMove(int row, int col) throws RemoteException {
        if (game.isGameOver()) return; // Prevent moves if game is over
    
        new Thread(() -> {
            try {
                // Check if it's the player's turn and the game is still active
                if (game.isMyTurn(playerName) && !game.isGameOver()) {
                    if (game.isValidMove(row, col)) {
                        // Make the move
                        game.MakeMove(playerName, row, col);
                        updateBoard(); // Refresh the board after the move
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Invalid move! Try again.");
                            statusLabel.setForeground(SECONDARY_COLOR);
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("It's not your turn. Waiting...");
                        statusLabel.setForeground(TEXT_SECONDARY);
                    });
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error communicating with server.");
                    statusLabel.setForeground(SECONDARY_COLOR);
                });
            }
        }).start();
    }
    
    public void updateBoard() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            try {
                String[][] board = game.getBoard();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        buttons[i][j].setText(board[i][j]);
                        boolean enabled = board[i][j].isEmpty() && game.isMyTurn(playerName) && !game.isGameOver();
                        buttons[i][j].setEnabled(enabled);
    
                        // Material Design styling
                        if (board[i][j].equals("X")) {
                            buttons[i][j].setForeground(PRIMARY_COLOR);
                        } else if (board[i][j].equals("O")) {
                            buttons[i][j].setForeground(SECONDARY_COLOR);
                        } else {
                            buttons[i][j].setForeground(TEXT_PRIMARY);
                        }
    
                        // Visual feedback for disabled buttons
                        buttons[i][j].setBackground(enabled ? CARD_COLOR : DISABLED_COLOR);
                    }
                }
    
                if (game.isMyTurn(playerName) && !game.isGameOver()) {
                    statusLabel.setText(playerName + "'s turn - Make a move!");
                    statusLabel.setForeground(PRIMARY_DARK);
                } else {
                    statusLabel.setText("Waiting for opponent's move...");
                    statusLabel.setForeground(TEXT_SECONDARY);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                statusLabel.setText("Error updating the board.");
                statusLabel.setForeground(SECONDARY_COLOR);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameClientGUI().setVisible(true));
    }
}