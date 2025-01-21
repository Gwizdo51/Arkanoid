package org.arkanoidpackage.lib;

import org.arkanoidpackage.arkanoid.GameModel;


public class Utils {

    // returns "value" if it is between "min" and "max"
    // returns "min" or "max" otherwise
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    // returns a random double between 2 bounds
    public static double randomDoubleRange(double lowerBound, double upperBound) {
        return (upperBound - lowerBound) * GameModel.rng.nextDouble() + lowerBound;
    }

    // lower bound included, upper bound excluded
    public static int randomIntRange(int lowerBound, int upperBound) {
        return GameModel.rng.nextInt(upperBound - lowerBound) + lowerBound;
    }
}
