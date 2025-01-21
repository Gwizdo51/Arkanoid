package org.arkanoidpackage.arkanoid;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.arkanoidpackage.lib.*;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;


public class GameModel {
    // ball
    public static Ball ball;
    public static final int ballRadius = 15;
    // move speed in pixel.s^-1
    public static final int ballInitialMS = 400;
    // brick matrix
    private static BrickMatrix brickMatrix;
    // levels
    public static int currentLevel;
    public static final int levelsCount = 4;
    // player pad
    public static Pad playerPad;
    public static final int padWidth = 200;
    public static final int padHeight = 20;
    public static final int padInitialMS = 500;
    // game over screen
    private static GameOverScreen gameOverScreen;
    // RNG
    public static final Random rng = new Random();
    // game phase
    private enum GameState {
        MAIN_MENU,
        WAITING, // waiting to start
        WAITING_PAUSED, // paused while waiting to start
        RUNNING,
        RUNNING_PAUSED, // paused while running
        OVER
    }
    private static GameState gameState;
    // private static boolean gamePaused;
    // "number of lives" to "color" mapping for the bricks
    public static Map<Integer, Color> brickLivesToColorMap;
    private static int playerLives;
    private static final int playerInitialLives = 3;

    public static void start() {
        // init the lives to color mapping for the colors of the bricks (green to red)
        // 5 lives: white
        // 4 lives: green
        // 3 lives: blue
        // 2 lives: magenta
        // 1 life: red
        GameModel.brickLivesToColorMap = new Hashtable<>();
        GameModel.brickLivesToColorMap.put(5, Color.color(1, 1, 1));
        GameModel.brickLivesToColorMap.put(4, Color.color(0, 1, 0));
        GameModel.brickLivesToColorMap.put(3, Color.color(0, 0, 1));
        GameModel.brickLivesToColorMap.put(2, Color.color(1, 0, 1));
        GameModel.brickLivesToColorMap.put(1, Color.color(1, 0, 0));
        // set the starting level
        GameModel.currentLevel = 0;
        // init the game state
        GameModel.gameState = GameState.MAIN_MENU;
        // GameModel.gameState = GameState.WAITING;
        // GameModel.gameState = GameState.OVER;
        // GameModel.gamePaused = false;
        // GameWonScreen.flavor = GameModel.rng.nextInt(GameWonScreen.flavorsCount + 1);
        // GameModel.gameOverScreen = new GameWonScreen();
        // GameModel.gameOverScreen = new GameLostScreen();
    }

    // load (or reload) the selected level
    private static void loadLevel() {
        // init the player lives
        GameModel.playerLives = GameModel.playerInitialLives;
        // ball
        // choose a random starting angle for the ball speed vector, between -3PI/4 and -PI/4
        double ballSpeedVectorAngle = Utils.randomDoubleRange(
                Direction.UP_LEFT.getAngle(),
                Direction.UP_RIGHT.getAngle()
        );
        // create a ball with a speed vector of length 1 and the generated angle
        GameModel.ball = new Ball(
                new Point(0, 0),
                GameModel.ballRadius,
                new Vector(GameModel.ballInitialMS, ballSpeedVectorAngle),
                // new Vector(GameModel.ballInitialMS, -Math.PI/2),
                true
        );
        // matrix of bricks
        GameModel.brickMatrix = new BrickMatrix(GameModel.currentLevel);
        // player pad
        GameModel.playerPad = new Pad(
                new Point((double) (Arkanoid.screenWidth - GameModel.padWidth) / 2, Arkanoid.screenHeight - GameModel.padHeight - 10),
                GameModel.padWidth,
                GameModel.padHeight,
                GameModel.padInitialMS
        );
        // snap the ball to the middle of the player pad
        GameModel.ball.snapToPad();
        // transition to WAITING
        GameModel.gameState = GameState.WAITING;
    }

    private static void transitionState() {
        switch (GameModel.gameState) {
            case MAIN_MENU:
                // load the level and transition to WAITING when "SPACE" is pressed
                if (GameController.pressedKeysThisFrame.contains("SPACE")) {
                    GameModel.loadLevel();
                }
                // exit the game when "ESCAPE" is pressed
                else if (GameController.pressedKeysThisFrame.contains("ESCAPE")) {
                    javafx.application.Platform.exit();
                }
                break;
            case WAITING:
                // transition to WAITING_PAUSED when "ESCAPE" is pressed
                if (GameController.pressedKeysThisFrame.contains("ESCAPE")) {
                    GameModel.gameState = GameState.WAITING_PAUSED;
                }
                // transition to RUNNING when "SPACE" is pressed
                else if (GameController.pressedKeysThisFrame.contains("SPACE")) {
                    // hide the ball angle indicator
                    if (!Arkanoid.debugMode) {
                        GameModel.ball.displayAngle = false;
                    }
                    GameModel.gameState = GameState.RUNNING;
                }
                break;
            case WAITING_PAUSED:
                // exit the game when "E" is pressed
                if (GameController.pressedKeysThisFrame.contains("E")) {
                    javafx.application.Platform.exit();
                }
                // transition to WAITING when "ESCAPE" is pressed
                else if (GameController.pressedKeysThisFrame.contains("ESCAPE")) {
                    GameModel.gameState = GameState.WAITING;
                }
                // reset the level and transition to WAITING when "R" is pressed
                else if (GameController.pressedKeysThisFrame.contains("R")) {
                    GameModel.loadLevel();
                }
                // return to the main menu when "M" is pressed
                else if (GameController.pressedKeysThisFrame.contains("M")) {
                    GameModel.gameState = GameState.MAIN_MENU;
                }
                break;
            case RUNNING:
                // transition to RUNNING_PAUSED when "ESC" is pressed
                if (GameController.pressedKeysThisFrame.contains("ESCAPE")) {
                    GameModel.gameState = GameState.RUNNING_PAUSED;
                }
                // transition to WAITING (new ball) when losing a life and some lives are left
                // transition to OVER (lose) when losing a life and all lives are lost
                else if (GameModel.ball.center.y - GameModel.ball.radius > Arkanoid.screenHeight) {
                    GameModel.playerLives--;
                    if (GameModel.playerLives == 0) {
                        GameModel.gameOverScreen = new GameLostScreen();
                        GameModel.gameState = GameState.OVER;
                    }
                    else {
                        // display the ball angle indicator
                        GameModel.ball.displayAngle = true;
                        // resetLevel the ball speed vector angle
                        GameModel.ball.speedVector.setAngle(Utils.randomDoubleRange(
                                Direction.UP_LEFT.getAngle(),
                                Direction.UP_RIGHT.getAngle()
                        ));
                        // snap the ball to the middle of the player pad
                        GameModel.ball.snapToPad();
                        GameModel.gameState = GameState.WAITING;
                    }
                }
                // transition to OVER (win) if all bricks are destroyed
                else if (GameModel.brickMatrix.isGameWon()) {
                    // choose a random win screen
                    GameWonScreen.flavor = GameModel.rng.nextInt(GameWonScreen.flavorsCount);
                    GameModel.gameOverScreen = new GameWonScreen();
                    GameModel.gameState = GameState.OVER;
                }
                break;
            case RUNNING_PAUSED:
                // exit the game when exit
                if (GameController.pressedKeysThisFrame.contains("E")) {
                    javafx.application.Platform.exit();
                }
                // transition to RUNNING when unpaused
                else if (GameController.pressedKeysThisFrame.contains("ESCAPE")) {
                    GameModel.gameState = GameState.RUNNING;
                }
                // transition to WAITING (new game) when resetLevel
                else if (GameController.pressedKeysThisFrame.contains("R")) {
                    GameModel.loadLevel();
                }
                // return to the main menu when "M" is pressed
                else if (GameController.pressedKeysThisFrame.contains("M")) {
                    GameModel.gameState = GameState.MAIN_MENU;
                }
                break;
            case OVER:
                // exit the game when exit
                if (GameController.pressedKeysThisFrame.contains("E")) {
                    javafx.application.Platform.exit();
                }
                // transition to WAITING (new game) when resetLevel
                else if (GameController.pressedKeysThisFrame.contains("R")) {
                    GameModel.loadLevel();
                }
                // return to the main menu when "M" is pressed
                else if (GameController.pressedKeysThisFrame.contains("M")) {
                    GameModel.gameState = GameState.MAIN_MENU;
                }
                break;
        }
    }

    public static void update() {
        // pressed keys
        // System.out.println("pressed keys last frame: " + GameController.pressedKeysLastFrame);
        // System.out.println("pressed keys this frame: " + GameController.pressedKeys);
        // game won
        // System.out.println("Game is won: " + GameModel.brickMatrix.isGameWon());
        // update the game according to its state
        switch (GameModel.gameState) {
            case MAIN_MENU:
                MainMenu.update();
                break;
            case WAITING:
                GameModel.updateWaiting();
                break;
            case RUNNING:
                GameModel.updateRunning();
                break;
            case OVER:
                GameModel.updateGameOver();
                break;
        }
        // transition the game state
        GameModel.transitionState();
    }

    private static void updateWaiting() {
        // update the pad position
        GameModel.playerPad.update();
        // snap the ball to the middle of the player pad
        GameModel.ball.snapToPad();
    }

    private static void updateRunning() {
        // for each of the 3 sides of the screen (up, left and right),
        // if the ball intersects with the side,
        // reflect the ball speed vector using the normal vector of the side
        if (GameModel.ball.center.y - GameModel.ball.radius < 0) {
            // ball intersects with top wall
            GameModel.ball.bounce(Direction.DOWN.getUnitVector());
        }
        if (GameModel.ball.center.x - GameModel.ball.radius < 0) {
            // ball intersects with left wall
            GameModel.ball.bounce(Direction.RIGHT.getUnitVector());
        }
        if (GameModel.ball.center.x + GameModel.ball.radius > Arkanoid.screenWidth) {
            // ball intersects with right wall
            GameModel.ball.bounce(Direction.LEFT.getUnitVector());
        }
        // update the matrix of bricks
        GameModel.brickMatrix.update();
        // update the pad position
        GameModel.playerPad.update();
        // update the ball position
        GameModel.ball.update();
    }

    private static void updateGameOver() {
        // update the game over screen animation
        GameModel.gameOverScreen.update();
    }

    private static void renderPlayerLives() {
        String text = String.format("LIVES: %d", GameModel.playerLives);
        // display the text
        GameController.context.setFont(new Font(50));
        GameController.context.setTextAlign(TextAlignment.LEFT);
        GameController.context.setTextBaseline(VPos.BASELINE);
        GameController.context.setFill(Color.color(0,.20,.40));
        GameController.context.fillText(text, 25, Arkanoid.screenHeight - 100);
    }

    private static void renderControlsHelp() {
        String keys = "[QD]\n[SPACE]\n[ESCAPE]";
        String options = "Move the pad\nLaunch the ball\nPause the game";
        GameController.context.setFont(new Font(35));
        GameController.context.setTextBaseline(VPos.BASELINE);
        GameController.context.setFill(Color.GRAY);
        // display keys
        GameController.context.setTextAlign(TextAlignment.RIGHT);
        GameController.context.fillText(keys, (double) Arkanoid.screenWidth / 2 - 10, Arkanoid.screenHeight - 225);
        // display options
        GameController.context.setTextAlign(TextAlignment.LEFT);
        GameController.context.fillText(options, (double) Arkanoid.screenWidth / 2 + 10, Arkanoid.screenHeight - 225);
    }

    public static void render() {
        if (GameModel.gameState == GameState.MAIN_MENU) {
            MainMenu.render();
        }
        else if (Arrays.asList(new GameState[] {GameState.WAITING, GameState.RUNNING, GameState.WAITING_PAUSED, GameState.RUNNING_PAUSED}).contains(GameModel.gameState)) {
            GameModel.renderPlayerLives();
            GameModel.brickMatrix.render();
            GameModel.playerPad.render();
            GameModel.ball.render();
            if (Arrays.asList(new GameState[] {GameState.WAITING_PAUSED, GameState.RUNNING_PAUSED}).contains(GameModel.gameState)) {
                // render the pause menu
                PauseMenu.render();
            }
            else if (GameModel.gameState == GameState.WAITING) {
                // display controls help
                GameModel.renderControlsHelp();
            }
        }
        else {
            GameModel.gameOverScreen.render();
        }
    }
}
