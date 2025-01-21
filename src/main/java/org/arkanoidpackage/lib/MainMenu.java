package org.arkanoidpackage.lib;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameController;
import org.arkanoidpackage.arkanoid.GameModel;


public class MainMenu {
    private static final String title = "ARKANOID";
    private static final Color fillColor = Color.WHITE;
    private static final Color titleContourColor = Color.GRAY;
    private static final Color levelContourColor = Color.RED.darker().darker();
    private static final String previousLevelText = "<\n[Q]";
    private static final String nextLevelText = ">\n[D]";
    private static final String selectedLevelTopText = "Select a level";
    private static final String selectedLevelBottomText = "%d";
    private static final String randomLevelText = "?"; // 0
    private static final String emptyLevelText = "!"; // -1
    private static final String keysText = "[SPACE]\n[ESCAPE]";
    private static final String optionsText = "Play\nExit";

    public static void update() {
        // if the left (-) or right (+) arrow is pressed this frame, select a different level
        if (GameController.pressedKeysThisFrame.contains("Q")) {
            // select previous level
            GameModel.currentLevel = Math.max(GameModel.currentLevel - 1, -2);
        }
        if (GameController.pressedKeysThisFrame.contains("D")) {
            // select next level
            GameModel.currentLevel = Math.min(GameModel.currentLevel + 1, GameModel.levelsCount - 1);
        }
    }

    // display the main menu
    public static void render() {
        GameController.context.setFill(MainMenu.fillColor);
        // game title
        GameController.context.setFont(new Font(200));
        GameController.context.setTextAlign(TextAlignment.CENTER);
        GameController.context.setTextBaseline(VPos.CENTER);
        // contour
        GameController.context.setStroke(MainMenu.titleContourColor);
        GameController.context.setLineWidth(10);
        GameController.context.strokeText(MainMenu.title, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 4);
        // fill
        GameController.context.fillText(MainMenu.title, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 4);
        // selected level
        // top text
        GameController.context.setFont(new Font(50));
        GameController.context.fillText(MainMenu.selectedLevelTopText, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 2);
        // level text
        GameController.context.setFont(new Font(90));
        GameController.context.setStroke(MainMenu.levelContourColor);
        GameController.context.setLineWidth(5);
        String currentLevelText;
        if (GameModel.currentLevel == -1) {
            currentLevelText = MainMenu.randomLevelText;
        }
        else if (GameModel.currentLevel == -2) {
            currentLevelText = MainMenu.emptyLevelText;
        }
        else {
            currentLevelText = String.format(MainMenu.selectedLevelBottomText, GameModel.currentLevel + 1);
        }
        GameController.context.strokeText(currentLevelText, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 2 + 100);
        GameController.context.fillText(currentLevelText, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 2 + 100);
        // previous level
        GameController.context.setFont(new Font(40));
        GameController.context.fillText(MainMenu.previousLevelText, (double) Arkanoid.screenWidth / 2 - 100, (double) Arkanoid.screenHeight / 2 + 130);
        // next level
        GameController.context.fillText(MainMenu.nextLevelText, (double) Arkanoid.screenWidth / 2 + 100, (double) Arkanoid.screenHeight / 2 + 130);
        // keys
        GameController.context.setTextAlign(TextAlignment.RIGHT);
        GameController.context.fillText(MainMenu.keysText, (double) Arkanoid.screenWidth / 2 - 15, (double) 5 * Arkanoid.screenHeight / 6);
        // options
        GameController.context.setTextAlign(TextAlignment.LEFT);
        GameController.context.fillText(MainMenu.optionsText, (double) Arkanoid.screenWidth / 2 + 15, (double) 5 * Arkanoid.screenHeight / 6);
    }
}
