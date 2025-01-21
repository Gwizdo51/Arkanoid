package org.arkanoidpackage.lib;

import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameController;
import org.arkanoidpackage.arkanoid.GameModel;


public class Pad extends Rectangle implements GameEntity {
    public Vector speedVector;
    public int moveSpeed;
    private Direction playerInput;

    public Pad(Point origin, double width, double height, int moveSpeed) {
        super(origin, width, height);
        // set initial speed of pad to 0
        this.speedVector = new Vector(0, 0);
        this.moveSpeed = moveSpeed;
        this.playerInput = null;
    }

    /* control the pad:
     * Q -> move left
     * D -> move right
     */
    private void control() {
        // move the pad only if "Q" XOR "D" is pressed
        if (GameController.pressedKeys.contains("Q") ^ GameController.pressedKeys.contains("D")) {
            if (GameController.pressedKeys.contains("Q")) {
                // move pad left
                this.playerInput = Direction.LEFT;
            }
            if (GameController.pressedKeys.contains("D")) {
                // move pad right
                this.playerInput = Direction.RIGHT;
            }
        }
        else {
            this.playerInput = null;
        }
    }

    @Override
    public void update() {
        // bounce the ball if it comes into contact with the pad
        if (this.overlaps(GameModel.ball)) {
            // special case if the ball bounces on the top between the corners
            Point closestPoint = this.getClosestPoint(GameModel.ball);
            if ((closestPoint.y == this.origin.y) && (closestPoint.x > this.origin.x) && (closestPoint.x < this.origin.x + this.width)) {
                // bounce the ball off the surface as usual
                boolean ballHasBounced = GameModel.ball.bounce(this.getVectorToBall(GameModel.ball));
                if (ballHasBounced) {
                    // add a fixed angle to the reflected vector, based on where it landed on the pad:
                    // - if it landed exactly in the middle, don't modify the reflected angle
                    // - if it landed on the left of the pad, add between 0 and -PI/4
                    // - if it landed on the right of the pad, add between 0 and PI/4
                    double rotation = Math.PI / 2 * (((closestPoint.x - this.origin.x) / this.width) - (double) 1 / 2);
                    // clamp the result angle between -3PI/4 and -PI/4
                    double resultAngle = Utils.clamp(
                            GameModel.ball.speedVector.getAngle() + rotation,
                            Direction.UP_LEFT.getAngle(),
                            Direction.UP_RIGHT.getAngle()
                    );
                    GameModel.ball.speedVector.setAngle(resultAngle);
                }
            }
            else {
                // normal bounce
                GameModel.ball.bounce(this.getVectorToBall(GameModel.ball));
            }
        }
        // control the pad based on player input
        this.control();
        // set the pad speed vector based on the player input direction
        if (this.playerInput != null) {
            this.speedVector.set(this.moveSpeed, this.playerInput.getAngle());
        }
        // prevent the pad from moving if it would go out of frame
        Point destination = Point.move(this.origin, Vector.mul(this.speedVector, GameController.getDT()));
        destination.x = Utils.clamp(destination.x, 0, Arkanoid.screenWidth - this.width);
        // update the position of the pad
        this.origin = destination;
        // reset the pad speed vector
        this.speedVector.setLength(0);
    }

    @Override
    public void render() {
        super.render();
    }
}
