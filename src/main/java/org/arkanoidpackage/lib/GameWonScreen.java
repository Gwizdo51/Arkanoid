package org.arkanoidpackage.lib;

import javafx.geometry.VPos;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameController;


public class GameWonScreen extends GameOverScreen {
    private static final Color textFillColor = Color.WHITE;
    private static final Color textContourColor = Color.GRAY;
    private static final String text = "YOU WON";
    // the color vector angle determines the current color
    // red: 0
    // yellow: PI/3
    // green: 2PI/3
    // cyan: PI
    // blue: -2PI/3
    // magenta: -PI/3
    // when progressively rotating the color vector, the current color slides through the colors of the rainbow
    // (for example, orange would be PI/6)
    private final Vector colorVector;
    // time frequency in rad.s^-1 (the speed at which the color vector rotates)
    private static final double colorVectorRotationSpeed = 2 * Math.PI;
    // space frequency in rad.pixel^-1 (the spacial angle offset to give the color vector depending on the position of the current pixel)
    private static final double angleOffsetSpatialFrequency = 2 * Math.PI / 150;
    private static final PixelWriter pixelWriter = GameController.context.getPixelWriter();
    // specific points on the screen
    private static final Point centerScreen = new Point((double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 2);
    private static final Point bottomCenterScreen = new Point((double) Arkanoid.screenWidth / 2, Arkanoid.screenHeight);
    private static final Point halfwayTopCenterScreen = new Point((double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 4);
    private static final Point halfwayBottomCenterScreen = new Point((double) Arkanoid.screenWidth / 2, (double) 3 * Arkanoid.screenHeight / 4);
    private static final Point centerHalfwayLeftScreen = new Point((double) Arkanoid.screenWidth / 4, (double) Arkanoid.screenHeight / 2);
    private static final Point centerHalfwayRightScreen = new Point((double) 3 * Arkanoid.screenWidth / 4, (double) Arkanoid.screenHeight / 2);
    // pixelate pixel size
    private static final int pixelSize = 5;
    // victory screen chosen flavor
    public static final int flavorsCount = 12;
    public static int flavor = 0;

    public GameWonScreen() {
        this.gameWon = true;
        this.colorVector = new Vector(1, 0);
    }

    @Override
    public void update() {
        // rotate the color vector based on its rotation speed
        this.colorVector.rotate(GameWonScreen.colorVectorRotationSpeed * GameController.getDT());
        // System.out.println("color vector angle: " + this.colorVector.getAngle() / Math.PI);
    }

    private static double getRedComponentLinear(double angle) {
        double result;
        if (angle >= 0) {
            result = -3 * angle / Math.PI + 2;
        }
        else {
            result = 3 * angle / Math.PI + 2;
        }
        // System.out.println("color component: " + Utils.clamp(result, 0, 1));
        return Utils.clamp(result, 0, 1);
    }

    private static double getRedComponentSmooth(double angle) {
        double result;
        // ]-PI, -2PI/3] U [2PI/3, PI]
        if (angle <= -2 * Math.PI / 3 || angle >= 2 * Math.PI / 3) {
            result = 0;
        }
        // [-PI/3, PI/3]
        else if (angle >= -Math.PI / 3 && angle <= Math.PI / 3) {
            result = 1;
        }
        // ]-2PI/3, -PI/3[ U ]PI/3, 2PI/3[
        else {
            // result = Math.cos(Math.PI - 3 * angle) / 2 + (double) 1 / 2;
            // by using: cos(x)^2 = 1/2 * (1 + cos(2x))
            result = Math.pow(Math.cos((Math.PI - 3 * angle) / 2), 2);
        }
        // System.out.println("color component: " + result);
        return result;
    }

    @Override
    public void render() {
        // display the background
        // this.renderBackgroundFull();
        this.renderBackgroundPixelate();
        //*
        // display the text
        GameController.context.setFont(new Font(250));
        GameController.context.setTextAlign(TextAlignment.CENTER);
        GameController.context.setTextBaseline(VPos.CENTER);
        // contour
        GameController.context.setStroke(GameWonScreen.textContourColor);
        GameController.context.setLineWidth(10);
        GameController.context.strokeText(GameWonScreen.text, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 3);
        // fill
        GameController.context.setFill(GameWonScreen.textFillColor);
        GameController.context.fillText(GameWonScreen.text, (double) Arkanoid.screenWidth / 2, (double) Arkanoid.screenHeight / 3);
        // display the keys
        GameController.context.setFont(new Font(60));
        GameController.context.setTextAlign(TextAlignment.RIGHT);
        // contour
        GameController.context.strokeText(GameWonScreen.keysText, (double) Arkanoid.screenWidth / 2 - 20, (double) 2 * Arkanoid.screenHeight / 3);
        // fill
        GameController.context.fillText(GameLostScreen.keysText, (double) Arkanoid.screenWidth / 2 - 20, (double) 2 * Arkanoid.screenHeight / 3);
        // display the options
        GameController.context.setTextAlign(TextAlignment.LEFT);
        // contour
        GameController.context.strokeText(GameLostScreen.optionsText, (double) Arkanoid.screenWidth / 2 + 20, (double) 2 * Arkanoid.screenHeight / 3);
        // fill
        GameController.context.fillText(GameLostScreen.optionsText, (double) Arkanoid.screenWidth / 2 + 20, (double) 2 * Arkanoid.screenHeight / 3);
        // */
    }

    private static double getAngleOffset(Point pixel) {
        double angleOffset;
        double angleOffsetCircle1, angleOffsetCircle2, angleOffsetCircle3, angleOffsetCircle4, angleOffsetCircle5;
        double angleOffsetRotation1, angleOffsetRotation2, angleOffsetRotation3, angleOffsetRotation4, angleOffsetRotation5;
        switch (GameWonScreen.flavor) {
            case 0:
                // diagonal translation
                angleOffset = -GameWonScreen.angleOffsetSpatialFrequency * (pixel.x + pixel.y);
                break;
            case 1:
                // center to exterior (circle)
                angleOffset = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerScreen.getDistance(pixel);
                break;
            case 2:
                // center to exterior (oval)
                angleOffset = -GameWonScreen.angleOffsetSpatialFrequency * Math.sqrt(Math.pow(pixel.x - GameWonScreen.centerScreen.x, 2) + 4 * Math.pow(pixel.y - GameWonScreen.centerScreen.y, 2));
                break;
            case 3:
                // rotation from center of screen
                angleOffset = -4 * Vector.fromXY(pixel.x - GameWonScreen.centerScreen.x, pixel.y - GameWonScreen.centerScreen.y).getAngle();
                break;
            case 4:
                // spiral (circle + rotation)
                angleOffsetCircle1 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerScreen.getDistance(pixel);
                angleOffsetRotation1 = -4 * Vector.fromXY(pixel.x - GameWonScreen.centerScreen.x, pixel.y - GameWonScreen.centerScreen.y).getAngle();
                angleOffset = angleOffsetCircle1 + angleOffsetRotation1;
                break;
            case 5:
                // 2 circles
                angleOffsetCircle1 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayTopCenterScreen.getDistance(pixel);
                angleOffsetCircle2 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayBottomCenterScreen.getDistance(pixel);
                angleOffset = angleOffsetCircle1 - angleOffsetCircle2;
                break;
            case 6:
                // 4 circles
                angleOffsetCircle1 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayTopCenterScreen.getDistance(pixel);
                angleOffsetCircle2 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayBottomCenterScreen.getDistance(pixel);
                angleOffsetCircle3 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayLeftScreen.getDistance(pixel);
                angleOffsetCircle4 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayRightScreen.getDistance(pixel);
                angleOffset = angleOffsetCircle1 + angleOffsetCircle2 + angleOffsetCircle3 + angleOffsetCircle4;
                break;
            case 7:
                // 2 spirals
                angleOffsetCircle1 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayTopCenterScreen.getDistance(pixel);
                angleOffsetCircle2 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayBottomCenterScreen.getDistance(pixel);
                angleOffsetRotation1 = Vector.fromXY(pixel.x - GameWonScreen.halfwayTopCenterScreen.x, pixel.y - GameWonScreen.halfwayTopCenterScreen.y).getAngle();
                angleOffsetRotation2 = Vector.fromXY(pixel.x - GameWonScreen.halfwayBottomCenterScreen.x, pixel.y - GameWonScreen.halfwayBottomCenterScreen.y).getAngle();
                angleOffset = angleOffsetCircle1 - angleOffsetCircle2 + angleOffsetRotation1 - angleOffsetRotation2;
                break;
            case 8:
                // 4 spirals
                angleOffsetCircle1 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayTopCenterScreen.getDistance(pixel);
                angleOffsetCircle2 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayBottomCenterScreen.getDistance(pixel);
                angleOffsetCircle3 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayLeftScreen.getDistance(pixel);
                angleOffsetCircle4 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayRightScreen.getDistance(pixel);
                angleOffsetRotation1 = Vector.fromXY(pixel.x - GameWonScreen.halfwayTopCenterScreen.x, pixel.y - GameWonScreen.halfwayTopCenterScreen.y).getAngle();
                angleOffsetRotation2 = Vector.fromXY(pixel.x - GameWonScreen.halfwayBottomCenterScreen.x, pixel.y - GameWonScreen.halfwayBottomCenterScreen.y).getAngle();
                angleOffsetRotation3 = -Vector.fromXY(pixel.x - GameWonScreen.centerHalfwayLeftScreen.x, pixel.y - GameWonScreen.centerHalfwayLeftScreen.y).getAngle();
                angleOffsetRotation4 = -Vector.fromXY(pixel.x - GameWonScreen.centerHalfwayRightScreen.x, pixel.y - GameWonScreen.centerHalfwayRightScreen.y).getAngle();
                angleOffset = angleOffsetCircle1 + angleOffsetCircle2 + angleOffsetCircle3 + angleOffsetCircle4
                        + angleOffsetRotation1 + angleOffsetRotation2 + angleOffsetRotation3 + angleOffsetRotation4;
                break;
            case 9:
                // 5 circles
                angleOffsetCircle1 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayTopCenterScreen.getDistance(pixel);
                angleOffsetCircle2 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayBottomCenterScreen.getDistance(pixel);
                angleOffsetCircle3 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayLeftScreen.getDistance(pixel);
                angleOffsetCircle4 = GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayRightScreen.getDistance(pixel);
                angleOffsetCircle5 = -2 * GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerScreen.getDistance(pixel);
                angleOffset = angleOffsetCircle1 + angleOffsetCircle2 + angleOffsetCircle3 + angleOffsetCircle4 + angleOffsetCircle5;
                break;
            case 10:
                // 5 rotations
                angleOffsetRotation1 = -Vector.fromXY(pixel.x - GameWonScreen.halfwayTopCenterScreen.x, pixel.y - GameWonScreen.halfwayTopCenterScreen.y).getAngle();
                angleOffsetRotation2 = -Vector.fromXY(pixel.x - GameWonScreen.halfwayBottomCenterScreen.x, pixel.y - GameWonScreen.halfwayBottomCenterScreen.y).getAngle();
                angleOffsetRotation3 = -Vector.fromXY(pixel.x - GameWonScreen.centerHalfwayLeftScreen.x, pixel.y - GameWonScreen.centerHalfwayLeftScreen.y).getAngle();
                angleOffsetRotation4 = -Vector.fromXY(pixel.x - GameWonScreen.centerHalfwayRightScreen.x, pixel.y - GameWonScreen.centerHalfwayRightScreen.y).getAngle();
                angleOffsetRotation5 = 4*Vector.fromXY(pixel.x - GameWonScreen.centerScreen.x, pixel.y - GameWonScreen.centerScreen.y).getAngle();
                angleOffset = angleOffsetRotation1 + angleOffsetRotation2 + angleOffsetRotation3 + angleOffsetRotation4 + angleOffsetRotation5;
                break;
            case 11:
                // 5 spirals
                angleOffsetCircle1 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayTopCenterScreen.getDistance(pixel);
                angleOffsetCircle2 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.halfwayBottomCenterScreen.getDistance(pixel);
                angleOffsetCircle3 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayLeftScreen.getDistance(pixel);
                angleOffsetCircle4 = -GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerHalfwayRightScreen.getDistance(pixel);
                angleOffsetCircle5 = 2 * GameWonScreen.angleOffsetSpatialFrequency * GameWonScreen.centerScreen.getDistance(pixel);
                angleOffsetRotation1 = Vector.fromXY(pixel.x - GameWonScreen.halfwayTopCenterScreen.x, pixel.y - GameWonScreen.halfwayTopCenterScreen.y).getAngle();
                angleOffsetRotation2 = Vector.fromXY(pixel.x - GameWonScreen.halfwayBottomCenterScreen.x, pixel.y - GameWonScreen.halfwayBottomCenterScreen.y).getAngle();
                angleOffsetRotation3 = Vector.fromXY(pixel.x - GameWonScreen.centerHalfwayLeftScreen.x, pixel.y - GameWonScreen.centerHalfwayLeftScreen.y).getAngle();
                angleOffsetRotation4 = Vector.fromXY(pixel.x - GameWonScreen.centerHalfwayRightScreen.x, pixel.y - GameWonScreen.centerHalfwayRightScreen.y).getAngle();
                angleOffsetRotation5 = -Vector.fromXY(pixel.x - GameWonScreen.centerScreen.x, pixel.y - GameWonScreen.centerScreen.y).getAngle();
                angleOffset = angleOffsetCircle1 + angleOffsetCircle2 + angleOffsetCircle3 + angleOffsetCircle4 + angleOffsetCircle5
                        + angleOffsetRotation1 + angleOffsetRotation2 + angleOffsetRotation3 + angleOffsetRotation4 + angleOffsetRotation5;
                break;
            default:
                // no spatial angle offset
                angleOffset = 0;
        }
        return angleOffset;
    }

    private Color getPixelColor(Point pixel) {
        double r, g, b;
        double angleOffset = GameWonScreen.getAngleOffset(pixel);
        r = GameWonScreen.getRedComponentSmooth(Vector.rotate(this.colorVector, angleOffset).getAngle());
        g = GameWonScreen.getRedComponentSmooth(Vector.rotate(this.colorVector, -2 * Math.PI / 3 + angleOffset).getAngle());
        b = GameWonScreen.getRedComponentSmooth(Vector.rotate(this.colorVector, 2 * Math.PI / 3 + angleOffset).getAngle());
        return Color.color(r, g, b);
    }

    private void renderBackgroundFull() {
        double r, g, b;
        // display the background, pixel per pixel
        for (int pixelColumn = 0; pixelColumn < Arkanoid.screenWidth; pixelColumn++) {
            for (int pixelRow = 0; pixelRow < Arkanoid.screenHeight; pixelRow++) {
                Point pixel = new Point(pixelColumn, pixelRow);
                GameWonScreen.pixelWriter.setColor(pixelColumn, pixelRow, this.getPixelColor(pixel));
            }
        }
    }

    private void renderBackgroundPixelate() {
        int nColumns = (int) Math.ceil((double) Arkanoid.screenWidth / GameWonScreen.pixelSize);
        int nRows = (int) Math.ceil((double) Arkanoid.screenHeight / GameWonScreen.pixelSize);
        // System.out.printf("%d %d%n", nColumns, nRows);
        for (int pixelColumn = 0; pixelColumn <= nColumns; pixelColumn++) {
            for (int pixelRow = 0; pixelRow <= nRows; pixelRow++) {
                // System.out.printf("%d %d%n", pixelColumn, pixelRow);
                Point pixelCenter = new Point(GameWonScreen.pixelSize * pixelColumn + (double) GameWonScreen.pixelSize / 2, GameWonScreen.pixelSize * pixelRow + (double) GameWonScreen.pixelSize / 2);
                GameController.context.setFill(this.getPixelColor(pixelCenter));
                // System.out.printf("%d %d%n", nColumns / 2, nRows / 2);
                // System.out.printf("%d %d%n", Arkanoid.screenWidth / 2 + 100 * (pixelColumn - nColumns / 2) - pixelSize / 2, Arkanoid.screenHeight / 2 + 10 * (pixelRow - nRows / 2) - pixelSize / 2);
                GameController.context.fillRect(
                        pixelCenter.x - (double) GameWonScreen.pixelSize / 2,
                        pixelCenter.y - (double) GameWonScreen.pixelSize / 2,
                        GameWonScreen.pixelSize,
                        GameWonScreen.pixelSize
                );
            }
        }
    }
}
