package org.arkanoidpackage.lib;


public enum Direction {
    UP_LEFT (-3 * Math.PI / 4),
    UP (-Math.PI / 2),
    UP_RIGHT (-Math.PI / 4),
    RIGHT (0),
    DOWN_RIGHT (Math.PI / 4),
    DOWN (Math.PI / 2),
    DOWN_LEFT (3 * Math.PI / 4),
    LEFT (Math.PI);

    private final double angle;

    Direction(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return this.angle;
    }

    public Vector getUnitVector() {
        return new Vector(1, this.angle);
    }
}
