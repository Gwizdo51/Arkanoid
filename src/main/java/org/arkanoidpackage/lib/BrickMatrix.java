package org.arkanoidpackage.lib;

import org.arkanoidpackage.arkanoid.Arkanoid;
import org.arkanoidpackage.arkanoid.GameModel;

import java.util.ArrayList;
import java.util.List;


public class BrickMatrix implements GameEntity {
    // size of the matrix
    private static final int nColumns = 14;
    private static final int nRows = 11;
    // the matrix of bricks
    private final Brick[][] matrix = new Brick[BrickMatrix.nColumns][BrickMatrix.nRows];
    // fixed geometric parameters
    private static final int brickWidth = 90;
    private static final int brickHeight = 56;
    private static final Point matrixOrigin = new Point(
            (double) Arkanoid.screenWidth / 2,
            0
    );

    // returns the bricks layout, as a matrix of brick lives
    private static int[][] getBrickLayout(int layoutIndex) {
        int[][] brickLayout = new int[BrickMatrix.nColumns][BrickMatrix.nRows];
        switch (layoutIndex) {
            case -1:
                // generate a random layout
                // choose a random density of brick, from .25 to .5
                // each slot has a probability equal to the density to contain a brick
                // give a random amount of HP to each spawned brick
                double density = Utils.randomDoubleRange(.15, .4);
                for (int[] brickColumn : brickLayout) {
                    for (int indexRow = 0; indexRow < BrickMatrix.nRows; indexRow++) {
                        if (GameModel.rng.nextDouble() <= density) {
                            // spawn a brick there
                            brickColumn[indexRow] = Utils.randomIntRange(1, 6);
                        }
                    }
                }
                break;
            case 0:
                // gradient (5 full rows)
                // top row: 5 lives bricks
                // bottom row: 1 life bricks
                for (int[] brickColumn : brickLayout) {
                    for (int indexRow = 0; indexRow < 5; indexRow++) {
                        brickColumn[indexRow + 2] = 5 - indexRow;
                    }
                }
                break;
            case 1:
                // many columns
                for (int indexColumn = 0; indexColumn < BrickMatrix.nColumns; indexColumn++) {
                    if (indexColumn % 3 == 0) {
                        for (int indexRow = 3; indexRow < BrickMatrix.nRows - 1; indexRow++) {
                            brickLayout[indexColumn][indexRow] = 5;
                        }
                    }
                }
                break;
            case 2:
                // full
                for (int[] brickColumn : brickLayout) {
                    for (int indexRow = 0; indexRow < BrickMatrix.nRows; indexRow++) {
                        brickColumn[indexRow] = 5;
                    }
                }
                break;
            case 3:
                // diamond
                // start from the first 2 columns and expand left and right
                Point startCoordinates = new Point(6, 5);
                int counterColumn = 0;
                while (true) {
                    int currentHP = 5 - counterColumn;
                    if (currentHP == 0) {
                        break;
                    }
                    // middle bricks
                    brickLayout[(int) startCoordinates.x - counterColumn][(int) startCoordinates.y] = currentHP;
                    brickLayout[(int) startCoordinates.x + 1 + counterColumn][(int) startCoordinates.y] = currentHP;
                    // expand up and down
                    currentHP--;
                    int counterRow = 1;
                    while (currentHP != 0) {
                        // left column up
                        brickLayout[(int) startCoordinates.x - counterColumn][(int) startCoordinates.y - counterRow] = currentHP;
                        // left column down
                        brickLayout[(int) startCoordinates.x - counterColumn][(int) startCoordinates.y + counterRow] = currentHP;
                        // right column up
                        brickLayout[(int) startCoordinates.x + 1 + counterColumn][(int) startCoordinates.y - counterRow] = currentHP;
                        // right column down
                        brickLayout[(int) startCoordinates.x + 1 + counterColumn][(int) startCoordinates.y + counterRow] = currentHP;
                        currentHP--;
                        counterRow++;
                    }
                    counterColumn++;
                }
                break;
        }
        return brickLayout;
    }

    public BrickMatrix(int layoutIndex) {
        // init the matrix
        int[][] brickLayout = BrickMatrix.getBrickLayout(layoutIndex);
        // System.out.println(Arrays.deepToString(brickLayout));
        for (int indexColumn = 0; indexColumn < BrickMatrix.nColumns; indexColumn++) {
            for (int indexRow = 0; indexRow < BrickMatrix.nRows; indexRow++) {
                int brickLives = brickLayout[indexColumn][indexRow];
                if (brickLives != 0) {
                    double brickOriginX = BrickMatrix.matrixOrigin.x + BrickMatrix.brickWidth * (indexColumn - (BrickMatrix.nColumns >> 1));
                    double brickOriginY = BrickMatrix.matrixOrigin.y + BrickMatrix.brickHeight * indexRow;
                    this.matrix[indexColumn][indexRow] = new Brick(
                            new Point(brickOriginX, brickOriginY),
                            BrickMatrix.brickWidth,
                            BrickMatrix.brickHeight,
                            brickLives,
                            indexColumn,
                            indexRow
                    );
                }
            }
        }
    }

    // comparator to sort the list of bricks by distance from the ball
    // private static int brickDistanceToBallComparator(Brick brick1, Brick brick2) {
    //     // returns 1 if brick1 > brick2
    //     //        -1 if brick1 < brick2
    //     //         0 if brick1 = brick2
    //     double distanceDiff = brick1.getDistance(GameModel.ball) - brick2.getDistance(GameModel.ball);
    //     return distanceDiff > 0 ? 1 : distanceDiff < 0 ? -1 : 0;
    // }

    /* pseudocode:
    put all bricks that intersect with the ball in a list
    sort the list by the distance between the closest point and the ball (from shortest to longest)
    bounce the ball off of each brick in this list in order
    */
    @Override
    public void update() {
        // put all bricks that intersect with the ball in a list
        List<Brick> intersectedBricks = new ArrayList<>();
        for (Brick[] currentColumn : this.matrix) {
            for (Brick currentBrick : currentColumn) {
                if (currentBrick != null && currentBrick.overlaps(GameModel.ball)) {
                    intersectedBricks.add(currentBrick);
                }
            }
        }
        // if some bricks are intersected ...
        if (!intersectedBricks.isEmpty()) {
            // System.out.println("bouncing off brick(s) ...");
            // sort the list by the distance between the closest point and the ball (from shortest to longest)
            // intersectedBricks.sort(BrickMatrix::brickDistanceToBallComparator);
            intersectedBricks.sort(null);
            // bounce the ball off of each brick in the list in order
            for (Brick currentBrick : intersectedBricks) {
                // System.out.printf("bouncing off brick (%d, %d)%n", currentBrick.matrixColumnIndex, currentBrick.matrixRowIndex);
                boolean ballHasBounced = GameModel.ball.bounce(currentBrick.getVectorToBall(GameModel.ball));
                // update the HP of the brick based on whether the ball hit it or not
                currentBrick.update(ballHasBounced);
                // delete the brick from the matrix if it has no HP
                if (currentBrick.getLives() == 0) {
                    this.matrix[currentBrick.matrixColumnIndex][currentBrick.matrixRowIndex] = null;
                }
            }
        }
    }

    // returns whether there are any bricks left in the matrix
    public boolean isGameWon() {
        boolean gameWon = true;
        outerLoop: for (Brick[] currentColumn : this.matrix) {
            for (Brick currentBrick : currentColumn) {
                if (currentBrick != null) {
                    gameWon = false;
                    break outerLoop;
                }
            }
        }
        return gameWon;
    }

    // render each brick of the matrix
    @Override
    public void render() {
        for (Brick[] currentColumn : this.matrix) {
            for (Brick currentBrick : currentColumn) {
                if (currentBrick != null) {
                    currentBrick.render();
                }
            }
        }
    }
}
