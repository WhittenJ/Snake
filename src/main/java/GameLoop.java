public class GameLoop implements Runnable {
    private SnakeGame game;

    public GameLoop(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            if (game.gameOver) {
                // show the game over splash screen
                game.gameOverPanel.setVisible(true);
            } else {
                game.move();
                game.repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // handle the exception here
                }
            }
        }
    }
}
