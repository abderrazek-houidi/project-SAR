import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.rmi.registry.*;
import javax.swing.border.EmptyBorder;

public class GameClientGUI extends JFrame {
    private GameInterface game;
    private String playerName;
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;
    private GradientPanel gradientPanel;
    private Color primaryColor =  new Color(156, 39, 176);  // purple
    private Color secondaryColor = new Color(255, 193, 7); // Amber
    private Color darkColor = new Color(38, 50, 56);      // Dark blue-gray
    private Color lightColor = new Color(250, 250, 250);  // Off-white

    public GameClientGUI() {
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 600, 700, 30, 30));
        
        try {
            // Connect to registry
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            GameFactoryInterface factory = (GameFactoryInterface) reg.lookup("TicTacToe");

            // Stylish player name input
            playerName = showMaterialInputDialog("Enter your player name:");
            if (playerName == null || playerName.trim().isEmpty()) {
                showMaterialMessageDialog("Player name cannot be empty!");
                System.exit(0);
            }

            CallbackImpl callback = new CallbackImpl(playerName);
            game = factory.playGame(playerName, callback);

            setupGUI();
            new Timer(500, e -> refreshBoard()).start();

        } catch (Exception e) {
            e.printStackTrace();
            showMaterialMessageDialog("Connection Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupGUI() {
        setTitle("Tic Tac Toe - " + playerName);
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main content panel with gradient
        gradientPanel = new GradientPanel(primaryColor, darkColor);
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(gradientPanel);

        // Title bar with close button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("TIC TAC TOE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(lightColor);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("a"); // Unicode ×
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        closeButton.setContentAreaFilled(false);
            closeButton.setBorderPainted(false);
            closeButton.setForeground(lightColor);
            closeButton.setFocusPainted(false);
            closeButton.setPreferredSize(new Dimension(40, 30));
            // Add action listener to close the window
            closeButton.addActionListener(e -> System.exit(0));
            // Add hover effect
            closeButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    closeButton.setForeground(new Color(255, 83, 73)); // Light red on hover
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    closeButton.setForeground(lightColor); // Back to original color
                }
            });
            titlePanel.add(closeButton, BorderLayout.LINE_END);
        gradientPanel.add(titlePanel, BorderLayout.NORTH);

        // Player info panel
        JPanel playerPanel = new JPanel();
        playerPanel.setOpaque(false);
        playerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel playerLabel = new JLabel("Player: " + playerName);
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playerLabel.setForeground(secondaryColor);
        playerPanel.add(playerLabel);
        gradientPanel.add(playerPanel, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel("Connecting...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(lightColor);
        statusLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        gradientPanel.add(statusLabel, BorderLayout.SOUTH);

        // Game board panel
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setOpaque(false);
        boardPanel.setBorder(new EmptyBorder(20, 50, 50, 50));

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 70);
        Color xColor = new Color(244, 67, 54);    // Red
        Color oColor = new Color(33, 150, 243);   // Blue

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int col = j;
                
                buttons[i][j] = new JButton("") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        if (!isOpaque() && getBackground().getAlpha() < 255) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                              RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(getBackground());
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                            g2.dispose();
                        }
                        super.paintComponent(g);
                    }
                };
                
                buttons[i][j].setFont(buttonFont);
                buttons[i][j].setOpaque(false);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorder(BorderFactory.createEmptyBorder());
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(new Color(255, 255, 255, 30));
                buttons[i][j].addActionListener(e -> makeMove(row, col));
                
                // Hover animation
                buttons[i][j].addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (buttons[row][col].isEnabled()) {
                            buttons[row][col].setBackground(new Color(255, 255, 255, 80));
                            animateButton(buttons[row][col], 1.1f);
                        }
                    }
                    
                    public void mouseExited(MouseEvent e) {
                        buttons[row][col].setBackground(new Color(255, 255, 255, 30));
                        animateButton(buttons[row][col], 1.0f);
                    }
                });
                
                boardPanel.add(buttons[i][j]);
            }
        }

        gradientPanel.add(boardPanel, BorderLayout.CENTER);
    }

    private void animateButton(JComponent component, float scale) {
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            float currentScale = 1.0f;
            public void actionPerformed(ActionEvent e) {
                if ((scale > 1.0f && currentScale < scale) || 
                    (scale < 1.0f && currentScale > scale)) {
                    currentScale += (scale > 1.0f ? 0.05f : -0.05f);
                    component.setFont(component.getFont().deriveFont(currentScale * 70f));
                    component.revalidate();
                    component.repaint();
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    private void makeMove(int row, int col) {
        try {
            if (game.isMyTurn(playerName)) {
                if (game.isValidMove(row, col)) {
                    game.MakeMove(playerName, row, col);
                    buttons[row][col].setText(game.getPlayers(0).equals(playerName) ? "X" : "O");
                    buttons[row][col].setForeground(game.getPlayers(0).equals(playerName) ? 
                          new Color(244, 67, 54) : new Color(33, 150, 243));
                    buttons[row][col].setEnabled(false);
                }
            }
        } catch (Exception e) {
            showMaterialMessageDialog("Move Error: " + e.getMessage());
        }
    }

    private void refreshBoard() {
        try {
            if (game == null) {
                System.err.println("Game instance is null!");
                return;
            }
            String[][] board = game.getBoard();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText(board[i][j]);
                    buttons[i][j].setEnabled(board[i][j].isEmpty());
                }
            }

            if (game.isGameOver()) {
                String winner = game.getWinner();
                String message = winner.equals("Draw") ? 
                    "Game ended in a draw!" : 
                    winner.equals(playerName) ? "You won!" : "You lost!";
                
                statusLabel.setText(message);
                showMaterialMessageDialog(message);
                
                for (JButton[] row : buttons) {
                    for (JButton button : row) {
                        button.setEnabled(false);
                    }
                }
            } else {
                statusLabel.setText(game.isMyTurn(playerName) ? 
                    "Your turn - Make a move!" : "Waiting for opponent...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Material Design input dialog
    private String showMaterialInputDialog(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(lightColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
            new EmptyBorder(5, 5, 5, 5)));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        Object[] options = {"OK", "Cancel"};
        int result = JOptionPane.showOptionDialog(this, panel, "Player Name",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        return result == 0 ? field.getText() : null;
    }

    // Material Design message dialog
    private void showMaterialMessageDialog(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightColor);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Message", 
            JOptionPane.PLAIN_MESSAGE, new ImageIcon());
    }

    // Gradient background panel
    class GradientPanel extends JPanel {
        private Color color1;
        private Color color2;

        public GradientPanel(Color c1, Color c2) {
            color1 = c1;
            color2 = c2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClientGUI client = new GameClientGUI();
            client.setVisible(true);
        });
    }
}