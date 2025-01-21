package org.arkanoidpackage.lib;

import javafx.scene.paint.Color;
import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameController;
import org.arkanoidpackage.arkanoid.GameModel;

import java.util.ArrayList;
import java.util.Arrays;


public class Ball implements GameEntity {
    // geometric parameters
    public Point center;
    public double radius;
    public Vector speedVector;
    // controls the speed at which the speed vector grows or shrinks (pixel.second^-2)
    private static final double accelerationRate = 200;
    // controls the speed at which the angle of the speed vector changes (rad.s^-1)
    // private static final double turnSpeed = Math.PI / 2;
    private static final double turnSpeed = Math.PI;
    // angle indicator
    public boolean displayAngle;
    public Color angleIndicatorColor;
    private static final double angleIndicatorWidth = 3;
    // ball color
    private static final Color[] possibleColors = {
            Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE
    };
    private Color currentColor = Color.WHITE;
    public boolean changeColor;
    // flags
    private static final int flagRight = 1;
    private static final int flagLeft = 1 << 1;
    private static final int flagDown = 1 << 2;
    private static final int flagUp = 1 << 3;

    public Ball(Point center, double radius, Vector speedVector, boolean displayAngle) {
        this.center = center;
        this.radius = radius;
        this.speedVector = speedVector;
        this.changeColor = false;
        this.displayAngle = displayAngle;
        this.angleIndicatorColor = Color.GRAY;
    }

    public boolean bounce(Vector normal) {
        return this.speedVector.reflect(normal, true);
    }

    /* (debug) control the ball with tank controls:
     * UP -> accelerate
     * DOWN -> decelerate
     * LEFT -> turn left
     * RIGHT -> turn right
     */
    private void controlTank() {
        if (GameController.pressedKeys.contains("UP")) {
            // accelerate
            // add (accelerationRate * dt) to the ball speedVector
            this.speedVector.setLength(this.speedVector.getLength() + Ball.accelerationRate * GameController.getDT());
        }
        if (GameController.pressedKeys.contains("DOWN")) {
            // decelerate (cannot go backwards)
            this.speedVector.setLength(Math.max(this.speedVector.getLength() - Ball.accelerationRate * GameController.getDT(), 0));
        }
        if (GameController.pressedKeys.contains("LEFT")) {
            // turn left
            this.speedVector.rotate(-Ball.turnSpeed * GameController.getDT());
        }
        if (GameController.pressedKeys.contains("RIGHT")) {
            // turn right
            this.speedVector.rotate(Ball.turnSpeed * GameController.getDT());
        }
    }

    private int computePlayerInputFlags() {
        int playerInputFlags = 0;
        playerInputFlags |= GameController.pressedKeys.contains("UP") ? Ball.flagUp : 0;
        playerInputFlags |= GameController.pressedKeys.contains("DOWN") ? Ball.flagDown : 0;
        playerInputFlags |= GameController.pressedKeys.contains("LEFT") ? Ball.flagLeft : 0;
        playerInputFlags |= GameController.pressedKeys.contains("RIGHT") ? Ball.flagRight : 0;
        return playerInputFlags;
    }

    // figure out the direction the player wishes the ball to go based on input
    private Direction figureOutWhishedDirection() {
        /* UP DOWN LEFT RIGHT = 15 => null
         * UP DOWN LEFT       = 14 => LEFT
         * UP DOWN      RIGHT = 13 => RIGHT
         * UP DOWN            = 12 => null
         * UP      LEFT RIGHT = 11 => UP
         * UP      LEFT       = 10 => UP_LEFT
         * UP           RIGHT = 9  => UP_RIGHT
         * UP                 = 8  => UP
         *    DOWN LEFT RIGHT = 7  => DOWN
         *    DOWN LEFT       = 6  => DOWN_LEFT
         *    DOWN      RIGHT = 5  => DOWN_RIGHT
         *    DOWN            = 4  => DOWN
         *         LEFT RIGHT = 3  => null
         *         LEFT       = 2  => LEFT
         *              RIGHT = 1  => RIGHT
         *                    = 0  => null
         *
         * UP_LEFT => 10
         * UP_RIGHT => 9
         * DOWN_LEFT => 6
         * DOWN_RIGHT => 5
         * UP => 11 8
         * DOWN => 7 4
         * LEFT => 14 2
         * RIGHT => 13 1
         * null => 15 12 3 0
         */
        Direction result;
        switch (this.computePlayerInputFlags()) {
            case 0: case 3: case 12: case 15:
                result = null;
                break;
            case 1: case 13:
                result = Direction.RIGHT;
                break;
            case 2: case 14:
                result = Direction.LEFT;
                break;
            case 4: case 7:
                result = Direction.DOWN;
                break;
            case 8: case 11:
                result = Direction.UP;
                break;
            case 5:
                result = Direction.DOWN_RIGHT;
                break;
            case 6:
                result = Direction.DOWN_LEFT;
                break;
            case 9:
                result = Direction.UP_RIGHT;
                break;
            case 10:
                result = Direction.UP_LEFT;
                break;
            default:
                throw new RuntimeException("This exception should never be thrown");
        }
        return result;
    }

    // (debug) control the ball with absolute controls
    private void controlAbsolute() {
        // rotation
        // figure out the direction the player wishes the ball to go based on his input
        Direction wishedDirection = this.figureOutWhishedDirection();
        // get the vector that points towards the wished direction
        // if the wished direction is null, make it equal to the current speed vector
        Vector WishedDirectionVector = wishedDirection == null ? this.speedVector : wishedDirection.getUnitVector();
        // turn the speed vector towards the wished direction vector, using its turn speed
        double maxAngle = Ball.turnSpeed * GameController.getDT();
        this.speedVector.rotate(Utils.clamp(this.speedVector.getRelativeAngle(WishedDirectionVector), -maxAngle, maxAngle));
        // acceleration
        if (GameController.pressedKeys.contains("ADD")) {
            // accelerate
            this.speedVector.setLength(this.speedVector.getLength() + Ball.accelerationRate * GameController.getDT());
        }
        if (GameController.pressedKeys.contains("SUBTRACT")) {
            // decelerate (cannot go backwards)
            this.speedVector.setLength(Math.max(this.speedVector.getLength() - Ball.accelerationRate * GameController.getDT(), 0));
        }
    }

    @Override
    public void update() {
        // control the ball (debug)
        if (Arkanoid.debugMode) {
            // this.controlTank();
            this.controlAbsolute();
        }
        // update the position of the ball based on its speed vector
        this.center.move(Vector.mul(this.speedVector, GameController.getDT()));
        if (this.changeColor) {
            // choose a random different color from the available colors
            ArrayList<Color> currentPossibleColors = new ArrayList<>(Arrays.asList(Ball.possibleColors));
            currentPossibleColors.remove(this.currentColor);
            this.currentColor = currentPossibleColors.get(GameModel.rng.nextInt(currentPossibleColors.size()));
            this.changeColor = false;
        }
    }

    // snap the ball to the middle of the player pad
    public void snapToPad() {
        this.center.x = GameModel.playerPad.origin.x + GameModel.playerPad.width / 2;
        this.center.y = GameModel.playerPad.origin.y - this.radius;
    }

    @Override
    public void render() {
        // render the angle indicator
        if (this.displayAngle) {
            GameController.context.setStroke(this.angleIndicatorColor);
            GameController.context.setLineWidth(Ball.angleIndicatorWidth);
            // A is 3 radii away from the center, in the direction of its speed vector
            Point A = Point.move(this.center, this.speedVector.copy().setLength(3 * this.radius));
            // draw the line between the ball center and A
            GameController.context.strokeLine(this.center.x, this.center.y, A.x, A.y);
        }
        // render the ball
        GameController.context.setFill(this.currentColor);
        GameController.context.fillOval(
                this.center.x - this.radius,
                this.center.y - this.radius,
                2 * this.radius,
                2 * this.radius
        );
    }
}
