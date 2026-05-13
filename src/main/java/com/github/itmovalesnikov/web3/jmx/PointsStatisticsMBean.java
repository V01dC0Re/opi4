package com.github.itmovalesnikov.web3.jmx;

/**
 * Interface for exposing points statistics metrics via JMX.
 * Defines methods to retrieve various statistics about points.
 */
public interface PointsStatisticsMBean {
    /**
     * Gets the total number of points recorded.
     *
     * @return the total number of points
     */
    long getTotalPoints();

    /**
     * Gets the total number of missed points recorded.
     *
     * @return the total number of missed points
     */
    long getMissedPoints();

    /**
     * Gets the maximum number of consecutive misses across all users.
     *
     * @return the maximum number of consecutive misses
     */
    int getConsecutiveMisses();

    /**
     * Resets all statistics to zero.
     */
    void reset();
}