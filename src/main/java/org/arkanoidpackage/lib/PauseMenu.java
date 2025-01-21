package org.arkanoidpackage.lib;


import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameController;

public class PauseMenu {
    private static final Point windowOrigin = new Point((double) Arkanoid.screenWidth / 4, (double) Arkanoid.screenHeight / 4);
    private static final double windowWidth = (double) Arkanoid.screenWidth / 2;
    private static final double windowHeight = (double) Arkanoid.screenHeight / 2;
    private static final Color windowBackgroundColor = Color.GRAY;
    private static final Color windowContourColor = Color.WHITE;
    private static final Color textColor = Color.WHITE;
    private static final String title = "GAME PAUSED";
    private static final String keysText = "[ESCAPE]\n[R]\n[M]\n[E]";
    private static final String optionsText = "Resume\nReset\nMain Menu\nExit";

    public static void render() {
        // display the menu background in grey
        // contour
        GameController.context.setStroke(PauseMenu.windowContourColor);
        GameController.context.setLineWidth(25);
        GameController.context.strokeRect(PauseMenu.windowOrigin.x, PauseMenu.windowOrigin.y, PauseMenu.windowWidth, PauseMenu.windowHeight);
        // background
        GameController.context.setFill(PauseMenu.windowBackgroundColor);
        GameController.context.fillRect(PauseMenu.windowOrigin.x, PauseMenu.windowOrigin.y, PauseMenu.windowWidth, PauseMenu.windowHeight);
        // display the title
        GameController.context.setFont(new Font(80));
        GameController.context.setTextAlign(TextAlignment.CENTER);
        GameController.context.setTextBaseline(VPos.CENTER);
        GameController.context.setFill(PauseMenu.textColor);
        GameController.context.fillText(PauseMenu.title, PauseMenu.windowOrigin.x + PauseMenu.windowWidth / 2, PauseMenu.windowOrigin.y + PauseMenu.windowHeight / 4);
        // display the keys
        GameController.context.setFont(new Font(40));
        GameController.context.setTextAlign(TextAlignment.RIGHT);
        GameController.context.fillText(PauseMenu.keysText, PauseMenu.windowOrigin.x + PauseMenu.windowWidth / 2 - 15, PauseMenu.windowOrigin.y + 2 * PauseMenu.windowHeight / 3);
        // display the options
        GameController.context.setTextAlign(TextAlignment.LEFT);
        GameController.context.fillText(PauseMenu.optionsText, PauseMenu.windowOrigin.x + PauseMenu.windowWidth / 2 + 15, PauseMenu.windowOrigin.y + 2 * PauseMenu.windowHeight / 3);
    }
}
