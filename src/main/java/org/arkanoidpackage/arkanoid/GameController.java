package org.arkanoidpackage.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class GameController {
    // canvas + graphics context
    @FXML
    private Canvas canvas;
    public static GraphicsContext context;
    // pressed keys set
    public static final Set<String> pressedKeys = new HashSet<>();
    public static final Set<String> pressedKeysThisFrame = new HashSet<>();
    // game loop
    private static AnimationTimer animationTimer;
    private static long currentFrameTimestamp = 0;
    private static double dt;
    // FPS
    private static final double[] pastFPS = new double[60];
    private static int indexCurrentFPS = 0;

    public static double getDT() {
        return GameController.dt;
        // return (double) 1 / 60;
    }

    @FXML
    private void initialize() {
        // resize the canvas to the screen size
        this.canvas.setWidth(Arkanoid.screenWidth);
        this.canvas.setHeight(Arkanoid.screenHeight);
        // get the canvas' graphics context
        GameController.context = this.canvas.getGraphicsContext2D();
    }

    private static void showFPS() {
        if (GameController.indexCurrentFPS == GameController.pastFPS.length) {
            // compute and show the mean FPS for the all the past FPS in the array
            System.out.printf("FPS : %.0f%n", Arrays.stream(GameController.pastFPS).average().orElse(Double.NaN));
            // reset the current FPS index
            GameController.indexCurrentFPS = 0;
        }
        else {
            // add the current FPS to the array
            GameController.pastFPS[GameController.indexCurrentFPS] = 1 / GameController.dt;
            // increment the current FPS index
            GameController.indexCurrentFPS++;
        }
    }

    private static void gameLoop(long now) {
        // don't update the game on the first call
        if (GameController.currentFrameTimestamp != 0) {
            // compute the time passed since the last frame
            GameController.dt = (now - GameController.currentFrameTimestamp) * 1E-9;
            // System.out.println(dt);
            // add dt to the session timer
            Arkanoid.updateSessionTimer();
            // System.out.printf("time passed: %.2f%n", Arkanoid.sessionTimer);
            // System.out.printf("FPS: %.0f%n", 1 / dt); // -> insane frequencies (2869 FPS ?)
            // GameController.showFPS();
            // cap dt to keep the game stable in case of graphic lag
            GameController.dt = Math.min(GameController.dt, (double) 1 / 60);
            // update the game
            GameModel.update();
        }
        // render the game
        GameController.render();
        // update the timestamp
        GameController.currentFrameTimestamp = now;
        // clear the set of keys pressed this frame
        GameController.pressedKeysThisFrame.clear();
    }

    public static void startGame() {
        // start the game
        GameModel.start();
        // create the animation timer
        GameController.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GameController.gameLoop(now);
            }
        };
        // start the animation timer
        GameController.animationTimer.start();
    }

    public static void render() {
        // clear the previous screen
        GameController.context.clearRect(0, 0, Arkanoid.screenWidth, Arkanoid.screenHeight);
        // set the background to black
        GameController.context.setFill(Color.BLACK);
        GameController.context.fillRect(0, 0, Arkanoid.screenWidth, Arkanoid.screenHeight);
        // render the game
        GameModel.render();
    }

    public static void onKeyPressed(KeyEvent event) {
        // add the pressed key to the set of pressed keys, if it isn't already in it
        GameController.pressedKeys.add(event.getCode().toString());
        // add the pressed key to the set of pressed keys this frame
        GameController.pressedKeysThisFrame.add(event.getCode().toString());
    }

    public static void onKeyReleased(KeyEvent event) {
        // remove the released key from the set of pressed keys
        GameController.pressedKeys.remove(event.getCode().toString());
    }
}
