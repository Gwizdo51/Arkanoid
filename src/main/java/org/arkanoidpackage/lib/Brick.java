package org.arkanoidpackage.lib;

import javafx.scene.paint.Color;
import org.arkanoidpackage.arkanoid.GameModel;


public class Brick extends Rectangle implements Comparable<Brick> {
    private int lives;
    public int matrixColumnIndex;
    public int matrixRowIndex;

    public Brick(Point origin, double width, double height, int lives, int matrixColumnIndex, int matrixRowIndex) {
        super(origin, width, height);
        this.lives = lives;
        this.matrixColumnIndex = matrixColumnIndex;
        this.matrixRowIndex = matrixRowIndex;
    }

    public int getLives() {
        return this.lives;
    }

    public void update(boolean touchedByBall) {
        // lose a life on contact
        if (touchedByBall) {
            this.lives--;
            if (this.lives < 0) {
                throw new RuntimeException("This brick has a negative amount of lives");
            }
        }
    }

    @Override
    public void render() {
        // render the brick in the color corresponding to the number of lives it has
        Color brickColor = GameModel.brickLivesToColorMap.get(this.lives);
        this.render(brickColor, brickColor.darker());
    }

    // for comparing bricks based on their distance with the ball
    @Override
    public boolean equals(Object o) {
        // "this" is equal to "o" if they reference the same object
        if (this == o) {
            return true;
        }
        // "this" cannot be equal to "null" or an instance of another class
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.getDistanceSquared(GameModel.ball) == ((Brick) o).getDistanceSquared(GameModel.ball);
    }
    @Override
    public int compareTo(Brick other) {
        // returns -1 if this < other
        //          0 if this = other
        //          1 if this > other
        double distanceDiff = this.getDistanceSquared(GameModel.ball) - other.getDistanceSquared(GameModel.ball);
        return distanceDiff < 0 ? -1 : distanceDiff > 0 ? 1 : 0;
    }
}
