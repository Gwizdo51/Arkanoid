package org.arkanoidpackage.arkanoid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class Arkanoid extends Application {
    // main stage
    private static Stage mainWindow;
    // scene
    private static Scene gameScene;
    // controller
    private static GameController gameController;
    // other
    public static final int screenWidth = 1280;
    public static final int screenHeight = 960;
    private static double sessionTimer = 0;
    public static final boolean debugMode = false;
    // public static final boolean debugMode = true;

    public static double getSessionTimer() {
        return Arkanoid.sessionTimer;
    }

    public static void updateSessionTimer() {
        Arkanoid.sessionTimer += GameController.getDT();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // init the window
        Arkanoid.mainWindow = stage;
        Arkanoid.mainWindow.setTitle("Arkanoid");
        Arkanoid.mainWindow.setResizable(false);
        // add an icon
        // https://www.svgrepo.com/svg/384228/break-brick-breaker-gaming-retro
        Image icon = new Image("file:images/icon.png");
        Arkanoid.mainWindow.getIcons().add(icon);
        // load the scene from the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Arkanoid.class.getResource("game-view.fxml"));
        Arkanoid.gameScene = new Scene(fxmlLoader.load());
        // get the scene controller
        Arkanoid.gameController = fxmlLoader.getController();
        // set the scene
        Arkanoid.mainWindow.setScene(Arkanoid.gameScene);
        // add event listeners to the scene
        Arkanoid.gameScene.setOnKeyPressed(GameController::onKeyPressed);
        Arkanoid.gameScene.setOnKeyReleased(GameController::onKeyReleased);
        // start the game
        GameController.startGame();
        // show the window
        Arkanoid.mainWindow.show();
    }

    public static void main(String[] args) {
        /* tests
        double[] doubleArray = new double[3];
        doubleArray[0] = 5;
        doubleArray[1] = 7;
        doubleArray[2] = 8;
        System.out.println(Arrays.stream(doubleArray).average().orElse(0));
        // */
        Arkanoid.launch();
    }

    public static Stage getMainWindow() {
        return Arkanoid.mainWindow;
    }

    public static Scene getGameScene() {
        return Arkanoid.gameScene;
    }

    public static GameController getGameController() {
        return Arkanoid.gameController;
    }
}
