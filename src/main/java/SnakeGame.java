import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class SnakeGame extends JPanel {

    JPanel gameOverPanel;
    private GameLoop gameLoop;

    private static final long serialVersionUID = 1L;
    private static final int CELL_SIZE = 20;

    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;

    private static final int INITIAL_LENGTH = 5;

    private int x = INITIAL_LENGTH;
    private int y = 0;

    private int[] xPositions = new int[WIDTH * HEIGHT];
    private int[] yPositions = new int[WIDTH * HEIGHT];

    private int fruitX;
    private int fruitY;

    private boolean movingRight = true;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingUp = false;

    boolean gameOver = false;

    private List<Integer> snakeX = new ArrayList<>();
    private List<Integer> snakeY = new ArrayList<>();

    public static void main(String[] args) {
        SnakeGame game = new SnakeGame();

        // start the game loop
        new Thread(game.gameLoop).start();
    }

    public SnakeGame() {
        JFrame frame = new JFrame();
        frame.setTitle("Snake");
        frame.setResizable(false);
        frame.setSize(WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                System.out.println("Key pressed: " + key);
                if (key == KeyEvent.VK_LEFT && !movingRight) {
                    movingLeft = true;
                    movingUp = false;
                    movingDown = false;
                }
                if (key == KeyEvent.VK_RIGHT && !movingLeft) {
                    movingRight = true;
                    movingUp = false;
                    movingDown = false;
                }
                if (key == KeyEvent.VK_UP && !movingDown) {
                    movingUp = true;
                    movingRight = false;
                    movingLeft = false;
                }
                if (key == KeyEvent.VK_DOWN && !movingUp) {
                    movingDown = true;
                    movingRight = false;
                    movingLeft = false;
                }
            }
        });
        frame.setFocusable(true);
        frame.setVisible(true);

        for (int i = 0; i < INITIAL_LENGTH; i++) {
            snakeX.add(i);
            snakeY.add(0);
        }

        placeFruit();

        // initialize the game over panel
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BorderLayout());

        // add the game over message
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverPanel.add(gameOverLabel, BorderLayout.NORTH);

        // add the exit button
        JButton exitButton = createExitButton();
        gameOverPanel.add(exitButton, BorderLayout.WEST);

        // add the retry button
        JButton retryButton = createRetryButton();
        gameOverPanel.add(retryButton, BorderLayout.EAST);

        // add the game over panel to the main panel
        frame.add(gameOverPanel, BorderLayout.CENTER);

        // set the game over panel to be not visible initially
        gameOverPanel.setVisible(false);

        // create the game loop
        gameLoop = new GameLoop(this);
    }

    // method to create the exit button
    private JButton createExitButton() {
        JButton button = new JButton("Exit");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // exit the game when the button is clicked
                System.exit(0);
            }
        });
        return button;
    }

    // method to create the retry button
    private JButton createRetryButton() {
        JButton button = new JButton("Retry");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reset the game when the button is clicked
                resetGame();
            }
        });
        return button;
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);

        g.setColor(Color.BLACK);
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            int x = xPositions[i];
            int y = yPositions[i];
            g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        g.setColor(Color.ORANGE);
        int x = snakeX.get(snakeX.size() - 1);
        int y = snakeY.get(snakeY.size() - 1);
        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        g.setColor(Color.GREEN);
        for (int i = 0; i < snakeX.size() - 1; i++) {
            x = snakeX.get(i);
            y = snakeY.get(i);
            g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(fruitX * CELL_SIZE, fruitY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private void placeFruit() {
        fruitX = (int) (Math.random() * WIDTH);
        fruitY = (int) (Math.random() * HEIGHT);

        for (int i = 0; i < snakeX.size(); i++) {
            if (fruitX == snakeX.get(i) && fruitY == snakeY.get(i)) {
                placeFruit();
                return;
            }
        }
    }


    public void move() {
        xPositions[0] = x;
        yPositions[0] = y;

        if (movingRight) {
            x++;
        } else if (movingDown) {
            y++;
        } else if (movingLeft) {
            x--;
        } else if (movingUp) {
            y--;
        }

        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            System.out.println("gameOver = true 1");
            setGameOverCondition();
            return;
        }

        if (x == fruitX && y == fruitY) {
            snakeX.add(fruitX);
            snakeY.add(fruitY);
            placeFruit();
        } else {
            snakeX.add(x);
            snakeY.add(y);

            snakeX.remove(0);
            snakeY.remove(0);
        }

        for (int i = 1; i < snakeX.size() - 1; i++) {
            if (x == snakeX.get(i) && y == snakeY.get(i)) {
                System.out.println("gameOver = true 2");
                setGameOverCondition();
                break;
            }
        }

        for (int i = 0; i < snakeX.size(); i++) {
            xPositions[i] = snakeX.get(i);
            yPositions[i] = snakeY.get(i);
        }
    }

    public void setGameOverCondition() {
        gameOver = true;
        gameOverPanel.setVisible(true);
        gameOverPanel.setOpaque(true);
        setVisible(false);
        repaint();
    }


    private void resetGame() {
        // reset the game state here
        gameOver = false;
        snakeX.clear();
        snakeY.clear();
        xPositions = new int[WIDTH * HEIGHT];
        yPositions = new int[WIDTH * HEIGHT];
        x = WIDTH / 2;
        y = HEIGHT / 2;
        snakeX.add(x);
        snakeY.add(y);
        xPositions[0] = x;
        yPositions[0] = y;
        placeFruit();

        // make the game over panel not visible
        gameOverPanel.setVisible(false);

        // start the game loop
        new Thread(gameLoop).start();
    }


}