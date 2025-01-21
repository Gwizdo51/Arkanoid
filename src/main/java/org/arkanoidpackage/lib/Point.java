package org.arkanoidpackage.lib;


public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(Vector displacementVector) {
        this.x += displacementVector.getX();
        this.y += displacementVector.getY();
    }
    public static Point move(Point point, Vector displacementVector) {
        return new Point(point.x + displacementVector.getX(), point.y + displacementVector.getY());
    }

    public double getDistanceSquared(Point other) {
        return Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2);
    }

    public double getDistance(Point other) {
        return Math.sqrt(this.getDistanceSquared(other));
    }
}
