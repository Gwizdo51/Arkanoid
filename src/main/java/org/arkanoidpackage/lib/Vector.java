package org.arkanoidpackage.lib;


// Helper class to handle vectors and related operations
public class Vector {
    private double length;
    private double angle;

    public Vector(double length, double angle) {
        this.set(length, angle);
    }

    public static Vector fromXY(double x, double y) {
        return new Vector(
                Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)),
                Math.atan2(y, x)
        );
    }

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
        Vector other = (Vector) o;
        // for the null vector, only check the length
        boolean result;
        if (this.length == 0) {
            result = other.length == 0;
        }
        else {
            result = (this.length == other.length) && (this.angle == other.angle);
        }
        return result;
    }
    public static boolean equals(Vector u, Vector v) {
        return u.equals(v);
    }

    public double getLength() {
        return this.length;
    }

    public Vector setLength(double length) {
        if (length < 0) {
            throw new ArithmeticException("Cannot set a negative length");
        }
        this.length = length;
        return this;
    }

    public double getAngle() {
        return this.angle;
    }

    public static double processAngle(double angle) {
        // keep the angle between -PI excluded and PI included
        double processedAngle = angle % (2 * Math.PI);
        if (processedAngle > Math.PI) {
            processedAngle -= 2 * Math.PI;
        }
        else if (processedAngle <= -Math.PI) {
            processedAngle += 2 * Math.PI;
        }
        return processedAngle;
    }

    public Vector setAngle(double angle) {
        this.angle = Vector.processAngle(angle);
        return this;
    }

    public Vector set(double length, double angle) {
        this.setLength(length);
        this.setAngle(angle);
        return this;
    }

    public double getX() {
        return this.length * Math.cos(this.angle);
    }

    public Vector setX(double x) {
        Vector tempVector = Vector.fromXY(x, this.getY());
        this.set(tempVector.length, tempVector.angle);
        return this;
    }

    public double getY() {
        return this.length * Math.sin(this.angle);
    }

    public Vector setY(double y) {
        Vector tempVector = Vector.fromXY(this.getX(), y);
        this.set(tempVector.length, tempVector.angle);
        return this;
    }

    public Vector copy() {
        return new Vector(this.length, this.angle);
    }

    // change vector in place
    public Vector add(Vector other) {
        // https://math.stackexchange.com/a/1365938
        double newLength = Math.sqrt(Math.pow(this.length, 2) + Math.pow(other.length, 2) + 2 * this.length * other.length * Math.cos(other.angle - this.angle));
        double newAngle = this.angle + Math.atan2(other.length * Math.sin(other.angle - this.angle), this.length + other.length * Math.cos(other.angle - this.angle));
        this.set(newLength, newAngle);
        return this;
    }
    // return a new vector
    public static Vector add(Vector vector1, Vector vector2) {
        Vector result = vector1.copy();
        result.add(vector2);
        return result;
    }

    // change vector in place
    public Vector mul(double k) {
        double newLength, newAngle;
        if (k < 0) {
            newAngle = this.angle + Math.PI;
            newLength = -k * this.length;
        }
        else {
            newAngle = this.angle;
            newLength = k * this.length;
        }
        this.set(newLength, newAngle);
        return this;
    }
    // return a new vector
    public static Vector mul(Vector vector, double k) {
        Vector result = vector.copy();
        result.mul(k);
        return result;
    }

    // change vector in place
    public Vector rotate(double angle) {
        this.setAngle(this.angle + angle);
        return this;
    }
    // return a new vector
    public static Vector rotate(Vector vector, double angle) {
        return new Vector(vector.length, vector.angle + angle);
    }

    // returns the angle needed to rotate this vector to get it point in the same direction as another vector
    public double getRelativeAngle(Vector other) {
        // keep the relative angle between -PI excluded and PI included
        return Vector.processAngle(other.angle - this.angle);
    }

    // reflect the vector off of a surface specified by a normal vector
    public boolean reflect(Vector normal, boolean fixAngle) {
        // only reflect if the absolute value of the relative angle between this vector and the normal vector is more than PI/2
        // (i.e. if this vector points towards the surface specified by the normal vector)
        // and if the normal vector is not the null vector
        // if fixAngle is true, prevent reflected vectors from being too horizontal
        boolean isReflected = false;
        double relativeAngle = this.getRelativeAngle(normal);
        if (normal.getLength() > 0 && Math.abs(relativeAngle) > Math.PI / 2) {
            this.mul(-1);
            this.rotate(2 * relativeAngle);
            isReflected = true;
            if (fixAngle) {
                double relativeAngleRight = this.getRelativeAngle(new Vector(1, 0));
                double relativeAngleLeft = this.getRelativeAngle(new Vector(1, Math.PI));
                double flatestAbsoluteAngle = Math.PI / 20;
                if (Math.abs(relativeAngleRight) < flatestAbsoluteAngle) {
                    if (relativeAngleRight >= 0) {
                        this.setAngle(-flatestAbsoluteAngle);
                    }
                    else {
                        this.setAngle(flatestAbsoluteAngle);
                    }
                }
                else if (Math.abs(relativeAngleLeft) < flatestAbsoluteAngle) {
                    if (relativeAngleLeft >= 0) {
                        this.setAngle(Math.PI - flatestAbsoluteAngle);
                    }
                    else {
                        this.setAngle(Math.PI + flatestAbsoluteAngle);
                    }
                }
            }
        }
        return isReflected;
    }
    public static Vector reflect(Vector vector, Vector normal, boolean fixAngle) {
        Vector result = vector.copy();
        result.reflect(normal, fixAngle);
        return result;
    }
}
