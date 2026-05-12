package com.github.itmovalesnikov.web3.model;

import java.io.Serializable;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.itmovalesnikov.web3.utils.BigFractionUtils;

/**
 * Represents a point with coordinates (x, y) and radius r, used for hit detection calculations.
 * The class determines whether the point hits a specific target area based on geometric rules.
 */
public class Point implements Serializable {
    /** X coordinate of the point */
    private final BigFraction x;
    
    /** Y coordinate of the point */
    private final BigFraction y;
    
    /** Radius value used in hit detection */
    private final BigFraction r;
    
    /** Result of hit detection calculation */
    private final boolean hit;

    /**
     * Constructs a new Point with given coordinates and radius.
     * Performs hit detection calculation during construction.
     *
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @param r Radius value for hit detection
     */
    public Point(BigFraction x, BigFraction y, BigFraction r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = checkHit(x, y, r);
    }

    /**
     * Determines if a point is within the target area based on geometric rules.
     * The algorithm checks different quadrants and applies different conditions:
     * - First quadrant: Always misses
     * - Second quadrant: Checks if (y/r) - 2*(x/r) <= 1
     * - Third quadrant: Checks if point is inside a circle with radius r/2
     * - Fourth quadrant: Checks if x <= r/2 and y >= -r
     *
     * @param xCoord X coordinate to check
     * @param yCoord Y coordinate to check
     * @param rCoord Radius value for calculations
     * @return true if the point hits the target area, false otherwise
     */
    private static boolean checkHit(BigFraction xCoord, BigFraction yCoord, BigFraction rCoord) {
        BigFraction xDivided = xCoord.divide(rCoord);
        BigFraction yDivided = yCoord.divide(rCoord);

        BigFraction zero = BigFraction.ZERO;
        BigFraction one = BigFraction.ONE;
        BigFraction half = new BigFraction(1, 2);
        BigFraction minusOne = new BigFraction(-1);
        BigFraction two = new BigFraction(2);

        boolean result;
        if (xDivided.compareTo(zero) >= 0 && yDivided.compareTo(zero) >= 0) { // 0-90
            result = false;
        } else if (xDivided.compareTo(zero) < 0 && yDivided.compareTo(zero) > 0) { // 90-180
            BigFraction expr = yDivided.subtract(xDivided.multiply(two));
            result = expr.compareTo(one) <= 0;
        } else if (xDivided.compareTo(zero) < 0 && yDivided.compareTo(zero) <= 0) { // 180-270
            BigFraction xSquared = xDivided.multiply(xDivided);
            BigFraction ySquared = yDivided.multiply(yDivided);
            BigFraction sumSquares = xSquared.add(ySquared);
            result = sumSquares.compareTo(half.multiply(half)) <= 0;
        } else {
            result = xDivided.compareTo(half) <= 0 && yDivided.compareTo(minusOne) >= 0;
        }
        return result;
    }

    /**
     * Returns the X coordinate as a decimal string representation.
     *
     * @return X coordinate as a decimal string
     */
    public String getX() {
        return BigFractionUtils.toDecimalString(x);
    }

    /**
     * Returns the Y coordinate as a decimal string representation.
     *
     * @return Y coordinate as a decimal string
     */
    public String getY() {
        return BigFractionUtils.toDecimalString(y);
    }

    /**
     * Returns the radius as a decimal string representation.
     *
     * @return Radius as a decimal string
     */
    public String getR() {
        return BigFractionUtils.toDecimalString(r);
    }

    /**
     * Returns the result of the hit detection calculation.
     *
     * @return true if the point hits the target area, false otherwise
     */
    public boolean isHit() {
        return hit;
    }
}