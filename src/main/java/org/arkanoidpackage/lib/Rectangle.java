package org.arkanoidpackage.lib;

import javafx.scene.paint.Color;
import org.arkanoidpackage.arkanoid.GameController;


public abstract class Rectangle {
    public Point origin;
    public double width;
    public double height;

    public Rectangle(Point origin, double width, double height) {
        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    protected void render(Color color) {
        GameController.context.setFill(color);
        GameController.context.fillRect(
                this.origin.x,
                this.origin.y,
                this.width,
                this.height
        );
    }

    protected void render(Color innerColor, Color outerColor) {
        GameController.context.setFill(outerColor);
        GameController.context.fillRect(
                this.origin.x,
                this.origin.y,
                this.width,
                this.height
        );
        GameController.context.setFill(innerColor);
        GameController.context.fillRect(
                this.origin.x + 5,
                this.origin.y + 5,
                this.width - 10,
                this.height - 10
        );
    }

    public void render() {
        // render the rectangle in white by default
        this.render(Color.WHITE);
    }

    // returns the point within the rectangle that is the closest to the ball center
    protected Point getClosestPoint(Ball ball) {
        return new Point(
                Utils.clamp(ball.center.x, this.origin.x, this.origin.x + this.width),
                Utils.clamp(ball.center.y, this.origin.y, this.origin.y + this.height)
        );
    }

    public double getDistance(Ball ball) {
        return this.getClosestPoint(ball).getDistance(ball.center);
    }

    public double getDistanceSquared(Ball ball) {
        return this.getClosestPoint(ball).getDistanceSquared(ball.center);
    }

    // check if the rectangle overlaps with a Ball
    public boolean overlaps(Ball ball) {
        // https://stackoverflow.com/a/1879223/16509326
        // find the point within the rectangle that is the closest to the ball center
        Point closestPoint = this.getClosestPoint(ball);
        // if the distance between this point and the center of the ball is less than the ball's radius, there is a collision
        return closestPoint.getDistanceSquared(ball.center) < Math.pow(ball.radius, 2);
    }

    // get the vector that would be needed to move the closest point in the rectangle to the center of the ball
    public Vector getVectorToBall(Ball ball) {
        Point closestPoint = this.getClosestPoint(ball);
        return Vector.fromXY(
                ball.center.x - closestPoint.x,
                ball.center.y - closestPoint.y
        );
    }
}
