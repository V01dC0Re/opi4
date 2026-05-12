package com.github.itmovalesnikov.web3.model;

import java.io.Serializable;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.itmovalesnikov.web3.utils.BigFractionConverter;
import com.github.itmovalesnikov.web3.utils.BigFractionUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a persistent point entity with coordinates (x, y), radius r, and hit status.
 * This entity is mapped to the 'points' table in the database and includes
 * functionality for hit detection calculations.
 */
@Entity
@Table(name = "points")
public class BPoint implements Serializable {

    /** Unique identifier for the point entity */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** X coordinate of the point */
    @Convert(converter = BigFractionConverter.class)
    @Column(nullable = false)
    private BigFraction x;

    /** Y coordinate of the point */
    @Convert(converter = BigFractionConverter.class)
    @Column(nullable = false)
    private BigFraction y;

    /** Radius value used in hit detection */
    @Convert(converter = BigFractionConverter.class)
    @Column(nullable = false)
    private BigFraction r;

    /** Result of hit detection calculation */
    @Column(nullable = false)
    private boolean hit;

    /** Session identifier associated with this point */
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    /**
     * Protected no-argument constructor required by JPA.
     */
    protected BPoint() {
    }

    /**
     * Constructs a new BPoint with given coordinates, radius, and session ID.
     * Performs hit detection calculation during construction.
     *
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @param r Radius value for hit detection
     * @param sessionId Session identifier associated with this point
     */
    public BPoint(BigFraction x, BigFraction y, BigFraction r, String sessionId) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = checkHit(x, y, r);
        this.sessionId = sessionId;
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
     * Returns the session identifier associated with this point.
     *
     * @return Session identifier string
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Returns the unique identifier for this point entity.
     *
     * @return Unique ID of the point
     */
    public Long getId() {
        return id;
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