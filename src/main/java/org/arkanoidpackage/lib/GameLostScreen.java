package org.arkanoidpackage.lib;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameController;


public class GameLostScreen extends GameOverScreen {
    private static final Color textColor = Color.RED;
    private static final String text = "YOU LOST";

    public GameLostScreen() {
        this.gameWon = false;
    }

    @Override
    public void update() {}

    @Override
    public void render() {
        // System.out.println("YOU LOST");
        // display the text
        GameController.context.setFont(new Font(250));
        GameController.context.setTextAlign(TextAlignment.CENTER);
        GameController.context.setTextBaseline(VPos.CENTER);
        // contour
        GameController.context.setStroke(GameLostScreen.textColor.darker().darker());
        GameController.context.setLineWidth(10);
        GameController.context.strokeText(GameLostScreen.text, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 3);
        // fill
        GameController.context.setFill(GameLostScreen.textColor);
        GameController.context.fillText(GameLostScreen.text, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 3);
        // display the keys
        GameController.context.setFont(new Font(60));
        GameController.context.setTextAlign(TextAlignment.RIGHT);
        GameController.context.fillText(GameLostScreen.keysText, (double) Arkanoid.screenWidth / 2 - 20, (double) 2 * Arkanoid.screenHeight / 3);
        // display the options
        GameController.context.setTextAlign(TextAlignment.LEFT);
        GameController.context.fillText(GameLostScreen.optionsText, (double) Arkanoid.screenWidth / 2 + 20, (double) 2 * Arkanoid.screenHeight / 3);
    }
}
