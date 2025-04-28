import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.registry.*;

public class GameClientGUI extends JFrame {
    private GameInterface game;
    private String playerName;
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;
    private JPanel boardPanel;
    private JButton restartButton;
    private JPanel headerPanel;
    private CallbackImpl callback;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel launcherPanel;
    private JPanel gamePanel;
    private JTextField nameField;

    private final Color PRIMARY_COLOR = Color.decode("#3F51B5");
    private final Color PRIMARY_DARK = Color.decode("#303F9F");
    private final Color SECONDARY_COLOR = Color.decode("#FF4081");
    private final Color BACKGROUND_COLOR = Color.decode("#FAFAFA");
    private final Color TEXT_PRIMARY = Color.decode("#212121");
    private final Color TEXT_SECONDARY = Color.decode("#757575");
    private final Color CARD_COLOR = Color.WHITE;
    private final Color DISABLED_COLOR = Color.decode("#EEEEEE");

    private final Border RAISED_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 0, 0, 20)),
        BorderFactory.createEmptyBorder(8, 8, 8, 8)
    );
    private final Border PRESSED_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0, 0, 0, 30)),
        BorderFactory.createEmptyBorder(8, 8, 8, 8)
    );

    private final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 28);
    private final Font STATUS_FONT = new Font("Roboto", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Roboto", Font.BOLD, 60);

    public GameClientGUI() {
        SwingUtilities.invokeLater(() -> {
            initializeGUI();
        showLauncher();
            this.setVisible(true);
        });
        
    }

    private void initializeGUI() {
        setTitle("Tic Tac Toe - RMI Client");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (game == null || playerName == null) {
                    System.exit(0);
                } else {
                    try {
                        game.notifyPlayerDeclinedRestart(playerName);
                    } catch (RemoteException ex) {
                        showMaterialDialog("Error notifying server of exit: " + ex.getMessage(), "Exit Error", true);
                    }
                    // *** Redirection to launcher ***
                    resetToInitialState();
                }
            }
        });

        // Main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        // Launcher panel
        launcherPanel = new JPanel();
        launcherPanel.setBackground(BACKGROUND_COLOR);
        launcherPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Enter your player name:");
        nameLabel.setFont(STATUS_FONT);
        nameLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        launcherPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(STATUS_FONT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, TEXT_SECONDARY),
            BorderFactory.createEmptyBorder(0, 0, 5, 0)
        ));
        gbc.gridy = 1;
        launcherPanel.add(nameField, gbc);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(STATUS_FONT);
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startGame());
        gbc.gridy = 2;
        launcherPanel.add(startButton, gbc);

        // Game panel
        gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBackground(BACKGROUND_COLOR);

        headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 70));

        JLabel titleLabel = new JLabel("TIC TAC TOE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Roboto", Font.BOLD, 18));
        restartButton.setPreferredSize(new Dimension(200, 50));
        restartButton.setBackground(new Color(0x4CAF50));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(e -> requestRestart());

        gamePanel.add(headerPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

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
                        showMaterialDialog("Error making move: " + e1.getMessage(), "Move Error", true);
                    }
                });
                boardPanel.add(buttons[i][j]);
            }
        }
        gamePanel.add(cardPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Connecting to server...", SwingConstants.CENTER);
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(0, 0, 0, 10)),
            new EmptyBorder(15, 10, 15, 10)
        ));
        gamePanel.add(statusLabel, BorderLayout.SOUTH);

        // Add panels to CardLayout
        mainPanel.add(launcherPanel, "Launcher");
        mainPanel.add(gamePanel, "Game");
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
        button.setFocusable(false);

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

    private void showLauncher() {
        cardLayout.show(mainPanel, "Launcher");
        nameField.setText("");
        nameField.requestFocus();
    }

    private void startGame() {
        playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            showMaterialDialog("Player name cannot be empty!", "Invalid Name", true);
            return;
        }
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Registry reg = LocateRegistry.getRegistry("localhost", 1099);
                GameFactoryInterface factory = (GameFactoryInterface) reg.lookup("Fabrique Tic Tac Toe");

                callback = new CallbackImpl(playerName, this);
                game = factory.playGame(playerName, callback);

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Waiting for game to start...");
                    statusLabel.setForeground(TEXT_SECONDARY);
                    cardLayout.show(mainPanel, "Game");
                    try {
                        updateBoard();
                    } catch (RemoteException e) {
                        showMaterialDialog("Error updating board: " + e.getMessage(), "Update Error", true);
                    }
                });

            } catch (ConnectException ce) {
                SwingUtilities.invokeLater(() -> {
                    showMaterialDialog("Could not connect to the server. Ensure it is running.", "Connection Error", true);
                    // *** Redirection to launcher ***
                    resetToInitialState();
                });
                ce.printStackTrace();
            } catch (NotBoundException | RemoteException e) {
                SwingUtilities.invokeLater(() -> {
                    showMaterialDialog("Error looking up remote object: " + e.getMessage(), "RMI Error", true);
                    // *** Redirection to launcher ***
                    resetToInitialState();
                });
                e.printStackTrace();
            }
        }).start();
    }

    public void showMaterialDialog(String message, String title, boolean isError) {
        JOptionPane pane = new JOptionPane(
            "<html><div style='width:200px;'>" + message + "</div></html>",
            isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
        JDialog dialog = pane.createDialog(this, title);
        dialog.setVisible(true);
    }

    public void handleGameEnd(String winner) {
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

            showMaterialDialog("<html><center><h3>" + message + "</h3></center></html>",
                "Game Over",
                false);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setEnabled(false);
                    buttons[i][j].setBackground(DISABLED_COLOR);
                }
            }

            headerPanel.add(restartButton, BorderLayout.EAST);
            headerPanel.revalidate();
            headerPanel.repaint();
        });
    }

    private void makeMove(int row, int col) throws RemoteException {
        if (game.isGameOver()) return;

        new Thread(() -> {
            try {
                if (game.isMyTurn(playerName) && !game.isGameOver()) {
                    if (game.isValidMove(row, col)) {
                        game.MakeMove(playerName, row, col);
                        updateBoard();
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
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error communicating with server.");
                    statusLabel.setForeground(SECONDARY_COLOR);
                });
            }
        }).start();
    }

    private void requestRestart() {
        new Thread(() -> {
            try {
                game.Restart(playerName);
                SwingUtilities.invokeLater(() -> {
                    removeRestartButton();
                    try {
                        updateBoard();
                    } catch (RemoteException e) {
                        showMaterialDialog("Error updating board: " + e.getMessage(), "Update Error", true);
                    }
                });
            } catch (RemoteException e) {
                SwingUtilities.invokeLater(() -> {
                    showMaterialDialog("Error requesting restart: " + e.getMessage(), "Restart Error", true);
                    // *** Redirection to launcher ***
                    resetToInitialState();
                });
            }
        }).start();
    }

    public void removeRestartButton() {
        headerPanel.remove(restartButton);
        headerPanel.revalidate();
        headerPanel.repaint();
    }

    public void resetToInitialState() {
        SwingUtilities.invokeLater(() -> {
            // *** Redirection to launcher ***
            // Clear game state
            game = null;
            playerName = null;
            callback = null;

            // Clear board
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText("");
                    buttons[i][j].setEnabled(true);
                    buttons[i][j].setBackground(CARD_COLOR);
                    buttons[i][j].setForeground(TEXT_PRIMARY);
                }
            }

            // Reset status label
            statusLabel.setText("Connecting to server...");
            statusLabel.setForeground(TEXT_SECONDARY);

            // Remove restart button
            removeRestartButton();

            // Show launcher panel
            showLauncher();
        });
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

                        if (board[i][j].equals("X")) {
                            buttons[i][j].setForeground(PRIMARY_COLOR);
                        } else if (board[i][j].equals("O")) {
                            buttons[i][j].setForeground(SECONDARY_COLOR);
                        } else {
                            buttons[i][j].setForeground(TEXT_PRIMARY);
                        }

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
                statusLabel.setText("Error updating the board.");
                statusLabel.setForeground(SECONDARY_COLOR);
            }
        });
    }

    public GameInterface getGame() {
        return game;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClientGUI client = new GameClientGUI();
            client.setVisible(true);
        });
    }
}